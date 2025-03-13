package com.example.controller;

import com.example.common.Result;
import com.example.common.enums.ResultCodeEnum;
import com.example.entity.Account;
import com.example.entity.ArticleInfo;
import com.example.service.impl.ArticleInfoService;
import com.example.utils.TokenUtils;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/articleInfo")
public class ArticleInfoController {

    @Resource
    private ArticleInfoService articleInfoService;

    @PostMapping("/add")
    public Result add(@RequestBody ArticleInfo articleInfo) {
        articleInfoService.add(articleInfo);
        return Result.success();
    }

    //删除
    @DeleteMapping("/delete/{id}")
    public Result deleteById(@PathVariable Integer id) {
        articleInfoService.deleteById(id);
        return Result.success();
    }

    //批量删除
    @DeleteMapping("/delete/batch")
    public Result deleteBatch(@RequestBody List<Integer> ids) {
        articleInfoService.deleteBatch(ids);
        return Result.success();
    }

    @PutMapping("/update")
    public Result updateById(@RequestBody ArticleInfo articleInfo) {
        articleInfoService.updateById(articleInfo);
        return Result.success();
    }

    @GetMapping("/selectById/{id}")
    public Result selectById(@PathVariable Integer id) {
        ArticleInfo articleInfo = articleInfoService.selectById(id);
        return Result.success(articleInfo);
    }

    @GetMapping("/selectAll")
    public Result selectAll(ArticleInfo articleInfo) {
        List<ArticleInfo> list = articleInfoService.selectAll(articleInfo);
        return Result.success(list);
    }

    @GetMapping("/selectPage")
    public Result selectPage(ArticleInfo articleInfo,
                             @RequestParam(defaultValue = "1") Integer pageNum,
                             @RequestParam(defaultValue = "10") Integer pageSize) {
        PageInfo<ArticleInfo> page = articleInfoService.selectPage(articleInfo, pageNum, pageSize);
        return Result.success(page);
    }

    @GetMapping("/selectByUserId")
    public Result selectByUserId(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title) { // 新增 title 参数
        Account currentUser = TokenUtils.getCurrentUser();
        if (currentUser.getId() == null) {
            return Result.error(ResultCodeEnum.USER_NOT_LOGIN);
        }
        ArticleInfo articleInfo = new ArticleInfo();
        articleInfo.setUserid(currentUser.getId().toString());
        if (title != null && !title.trim().isEmpty()) { // 检查 title 是否非空
            articleInfo.setTitle(title);
        }
        PageInfo<ArticleInfo> page = articleInfoService.selectPage(articleInfo, pageNum, pageSize);
        return Result.success(page);
    }
}