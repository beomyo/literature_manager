// E:\manager\springboot\src\main\java\com\example\service\ArticleInfoService.java
package com.example.service.impl;

import cn.hutool.core.io.FileUtil;
import com.example.entity.ArticleInfo;
import com.example.mapper.ArticleInfoMapper;
import com.example.utils.Config; // 假设这是定义 UPLOAD_PATH 的类
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Service
public class ArticleInfoService {

    @Resource
    private ArticleInfoMapper articleInfoMapper;

    private static final Logger log = LoggerFactory.getLogger(ArticleInfoService.class);

    public void add(ArticleInfo articleInfo) {
        articleInfoMapper.insert(articleInfo);
    }

    @Transactional
    public void deleteById(Integer id) {
        ArticleInfo articleInfo = articleInfoMapper.selectById(id);
        if (articleInfo != null) {
            // 删除本地文件
            deleteLocalFiles(articleInfo);

            // 执行原有删除逻辑
            String title = articleInfo.getTitle();
            articleInfoMapper.deleteById(id);
            articleInfoMapper.deleteSummaryByTitle(title);
        }
    }

    /**
     * 删除文章相关的本地文件
     */
    private void deleteLocalFiles(ArticleInfo articleInfo) {
        // 获取上传目录的基础路径
        String basePath = Config.UPLOAD_PATH; // 假设基础路径在 Config 中定义
        if (!basePath.endsWith(File.separator)) {
            basePath += File.separator;
        }

        // 删除 patha 文件（主文件）
        if (articleInfo.getPatha() != null && !articleInfo.getPatha().isEmpty()) {
            String fullPath = resolveFullPath(basePath, articleInfo.getPatha());
            if (FileUtil.del(fullPath)) {
                log.info("Deleted file: {}", fullPath);
            } else {
                log.warn("Failed to delete file: {}", fullPath);
            }
        }

        // 删除 pathb 文件（附件，可能有多个，以分号分隔）
        if (articleInfo.getPathb() != null && !articleInfo.getPathb().isEmpty()) {
            String[] attachmentPaths = articleInfo.getPathb().split(";");
            for (String path : attachmentPaths) {
                if (!path.isEmpty()) {
                    String fullPath = resolveFullPath(basePath, path);
                    if (FileUtil.del(fullPath)) {
                        log.info("Deleted attachment: {}", fullPath);
                    } else {
                        log.warn("Failed to delete attachment: {}", fullPath);
                    }
                }
            }
        }

        // 删除 pathdocx 文件
        if (articleInfo.getPathdocx() != null && !articleInfo.getPathdocx().isEmpty()) {
            String fullPath = resolveFullPath(basePath, articleInfo.getPathdocx());
            if (FileUtil.del(fullPath)) {
                log.info("Deleted docx file: {}", fullPath);
            } else {
                log.warn("Failed to delete docx file: {}", fullPath);
            }
        }

        // 删除 pathtxt 文件
        if (articleInfo.getPathtxt() != null && !articleInfo.getPathtxt().isEmpty()) {
            String fullPath = resolveFullPath(basePath, articleInfo.getPathtxt());
            if (FileUtil.del(fullPath)) {
                log.info("Deleted txt file: {}", fullPath);
            } else {
                log.warn("Failed to delete txt file: {}", fullPath);
            }
        }

        // 删除 pathpdf 文件
        if (articleInfo.getPathpdf() != null && !articleInfo.getPathpdf().isEmpty()) {
            String fullPath = resolveFullPath(basePath, articleInfo.getPathpdf());
            if (FileUtil.del(fullPath)) {
                log.info("Deleted pdf file: {}", fullPath);
            } else {
                log.warn("Failed to delete pdf file: {}", fullPath);
            }
        }
    }

    /**
     * 解析完整文件路径
     */
    private String resolveFullPath(String basePath, String relativePath) {
        // 如果 relativePath 已经是绝对路径，直接返回
        if (FileUtil.isAbsolutePath(relativePath)) {
            return relativePath;
        }
        // 否则，拼接基础路径和相对路径
        return basePath + relativePath.replace("/", File.separator);
    }

    @Transactional
    public void deleteBatch(List<Integer> ids) {
        for (Integer id : ids) {
            deleteById(id);
        }
    }

    public void updateById(ArticleInfo articleInfo) {
        articleInfoMapper.updateById(articleInfo);
    }

    public ArticleInfo selectById(Integer id) {
        return articleInfoMapper.selectById(id);
    }

    public List<ArticleInfo> selectAll(ArticleInfo articleInfo) {
        return articleInfoMapper.selectAll(articleInfo);
    }

    public PageInfo<ArticleInfo> selectPage(ArticleInfo articleInfo, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        List<ArticleInfo> list = articleInfoMapper.selectAll(articleInfo);
        return PageInfo.of(list);
    }
}