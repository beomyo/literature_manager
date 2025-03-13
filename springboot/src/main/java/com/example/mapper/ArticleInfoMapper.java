package com.example.mapper;

import com.example.entity.ArticleInfo;
import java.util.List;

public interface ArticleInfoMapper {

    int insert(ArticleInfo articleInfo);

    int deleteById(Integer id);

    int deleteByTitle(String title);

    int updateById(ArticleInfo articleInfo);

    ArticleInfo selectById(Integer id);

    List<ArticleInfo> selectAll(ArticleInfo articleInfo);

    int deleteSummaryByTitle(String title);
}