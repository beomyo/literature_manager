package com.example.controller;

import cn.hutool.core.io.FileUtil;
import com.example.common.Result;
import com.example.entity.ArticleInfo;
import com.example.entity.ArticleSummary;
import com.example.service.ArticleService;
import com.example.service.impl.TaskService;
import com.example.utils.AfterUpload;
import com.example.utils.Config;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

import static com.example.utils.neo4jloader.Neo4jLoader.runNeo4jLoader;

@RestController
@RequestMapping("/article")
public class ArticleController {

    @Resource
    private ArticleService articleService;

    @Resource
    private TaskService taskService;

    @Autowired
    private AfterUpload afterUpload;

    private final Consumer<ArticleInfo> func;

    public ArticleController(ArticleService articleService, TaskService taskService, AfterUpload afterUpload) {
        this.articleService = articleService;
        this.taskService = taskService;
        this.afterUpload = afterUpload;
        this.func = afterUpload::file_task;
    }

    @PostMapping("/search")
    public Result<PageInfo<ArticleInfo>> search(@RequestBody ArticleInfo articleInfo,
                                                @RequestParam(defaultValue = "1") Integer pageNum,
                                                @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<ArticleInfo> page = articleService.selectPage(articleInfo, pageNum, pageSize);
        return Result.success(page);
    }

    @PostMapping("/summary/{title}")
    public Result<List<ArticleSummary>> getSummary(@PathVariable String title) {
        List<ArticleSummary> summaries = articleService.selectSummariesByTitle(title);
        return Result.success(summaries); // 修改为返回列表
    }

    // 其他方法保持不变
    @PostMapping("/upload")
    public Result<String> uploadArticle(
            @RequestParam("token") String token,
            ArticleInfo articleInfo,
            @RequestParam("paperFile") MultipartFile paperFile,
            @RequestParam(value = "attachment", required = false) MultipartFile attachment) { // 修改为单个 MultipartFile
        try {
            String userData = TokenUtils.verifyToken(token);
            if (userData == null) {
                return Result.error("401", "无效的token，请重新登录");
            }
            String userId = userData.split("-")[0];
            articleInfo.setUserid(userId);

            String paperFilePath = saveFile(paperFile, "paper");
            articleInfo.setPatha(paperFilePath);

            if (attachment != null) { // 判断单个附件是否存在
                String attachmentPath = saveFile(attachment, "attachment");
                articleInfo.setPathb(attachmentPath); // 直接设置单个路径，不需要拼接
            }

            taskService.executeAsync(() -> func.accept(articleInfo));
            return Result.success("提交成功");
        } catch (Exception e) {
            return Result.error("500", "文件上传失败：" + e.getMessage());
        }
    }

    private String saveFile(MultipartFile file, String type) throws IOException {
        String uploadPath = Config.UPLOAD_PATH;
        if (!FileUtil.isDirectory(uploadPath)) {
            FileUtil.mkdir(uploadPath);
        }
        String originalFilename = file.getOriginalFilename();
        String ext = FileUtil.getSuffix(originalFilename);
        String fileName = type + "_" + UUID.randomUUID() + "." + ext;
        String filePath = uploadPath + "/" + fileName;
        FileUtil.writeBytes(file.getBytes(), filePath);
        return filePath;
    }

    @GetMapping("/file-paths/{title}")
    public Result<Map<String, String>> getFilePaths(@PathVariable String title) {
        Map<String, String> filePaths = articleService.getFilePathsByTitle(title);
        return Result.success(filePaths);
    }

    @GetMapping("/download/{title}/{field}")
    public void downloadFile(@PathVariable String title, @PathVariable String field, HttpServletResponse response) {
        try {
            Map<String, String> filePaths = articleService.getFilePathsByTitle(title);
            String filePath = filePaths.get(field);
            if (filePath == null || !FileUtil.exist(filePath)) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            String fileName = title + "_" + field + "." + FileUtil.getSuffix(filePath);
            response.setHeader("Content-Disposition", "attachment; filename=" + java.net.URLEncoder.encode(fileName, "UTF-8"));
            response.setContentType("application/octet-stream");

            byte[] fileBytes = Files.readAllBytes(Paths.get(filePath));
            OutputStream os = response.getOutputStream();
            os.write(fileBytes);
            os.flush();
            os.close();
        } catch (IOException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            e.printStackTrace();
        }
    }

    @PostMapping("/rebuild")
    public Result<String> rebuildGraph() {
        try {
            runNeo4jLoader(true, "前面为true时后面的字符串失效");
            return Result.success("图谱重建成功");
        } catch (Exception e) {
            return Result.error("500", "图谱重建失败：" + e.getMessage());
        }
    }
}