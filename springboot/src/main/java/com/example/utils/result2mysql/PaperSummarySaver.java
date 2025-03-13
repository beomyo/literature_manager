package com.example.utils.result2mysql;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.utils.Config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaperSummarySaver {
    private static final String DB_URL = Config.MYSQL_LINK;
    private static final String USER = Config.MYSQL_USERNAME;
    private static final String PASS = Config.MYSQL_PASSWORD;

    public static void saveSummary(String model,String title,String input,String ifteacher) {
        Connection conn = null;
        try {
            // 提取有效JSON部分
            String jsonString = extractJson(input);
            if (jsonString == null) {
                throw new IllegalArgumentException("Invalid input - no JSON found");
            }

            // 解析JSON
            ObjectMapper mapper = new ObjectMapper();
            ArticleSummary summary = mapper.readValue(jsonString, ArticleSummary.class);

            // 数据库连接
            conn = DriverManager.getConnection(DB_URL, USER, PASS);

            // 准备SQL语句
            String sql = "INSERT INTO article_summary (" +
                    "model, title, summary,short1, short2, short3, short4, short5, short6, " +
                    "mid1, mid2, mid3, mid4, mid5, mid6, long1, long2, long3, long4, " +
                    "long5, long6, target, algmid1, algmid2, algmid3, algmid4, alglong1, " +
                    "alglong2, alglong3, alglong4, environment, tools, datas, standard, " +
                    "result, future, weekpoint,keyword,ifteacher) VALUES (" +
                    "?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ";

            PreparedStatement pstmt = conn.prepareStatement(sql);

            // 设置参数
            int index = 1;
            pstmt.setString(index++, model);
            pstmt.setString(index++, title);
            pstmt.setString(index++, summary.summary);
            pstmt.setString(index++, summary.short1);
            pstmt.setString(index++, summary.short2);
            pstmt.setString(index++, summary.short3);
            pstmt.setString(index++, summary.short4);
            pstmt.setString(index++, summary.short5);
            pstmt.setString(index++, summary.short6);
            pstmt.setString(index++, summary.mid1);
            pstmt.setString(index++, summary.mid2);
            pstmt.setString(index++, summary.mid3);
            pstmt.setString(index++, summary.mid4);
            pstmt.setString(index++, summary.mid5);
            pstmt.setString(index++, summary.mid6);
            pstmt.setString(index++, summary.long1);
            pstmt.setString(index++, summary.long2);
            pstmt.setString(index++, summary.long3);
            pstmt.setString(index++, summary.long4);
            pstmt.setString(index++, summary.long5);
            pstmt.setString(index++, summary.long6);
            pstmt.setString(index++, summary.target);
            pstmt.setString(index++, summary.algmid1);
            pstmt.setString(index++, summary.algmid2);
            pstmt.setString(index++, summary.algmid3);
            pstmt.setString(index++, summary.algmid4);
            pstmt.setString(index++, summary.alglong1);
            pstmt.setString(index++, summary.alglong2);
            pstmt.setString(index++, summary.alglong3);
            pstmt.setString(index++, summary.alglong4);
            pstmt.setString(index++, summary.environment);
            pstmt.setString(index++, summary.tools);
            pstmt.setString(index++, summary.datas);
            pstmt.setString(index++, summary.standard);
            pstmt.setString(index++, summary.result);
            pstmt.setString(index++, summary.future);
            pstmt.setString(index++, summary.weekpoint);
            pstmt.setString(index++, summary.keyword);
            pstmt.setString(index++, ifteacher);

            // 执行插入
            pstmt.executeUpdate();
            pstmt.close();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) conn.close();
            } catch (SQLException se) {
                se.printStackTrace();
            }
        }
    }

    private static String extractJson(String input) {
        // 使用正则匹配最外层{}
        Pattern pattern = Pattern.compile("\\{.*\\}", Pattern.DOTALL);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(0);
        }
        return null;
    }

    // 内部类对应JSON结构
    private static class ArticleSummary {
        //public String model;
        //public String title;
        public String summary;
        public String short1;
        public String short2;
        public String short3;
        public String short4;
        public String short5;
        public String short6;
        public String mid1;
        public String mid2;
        public String mid3;
        public String mid4;
        public String mid5;
        public String mid6;
        public String long1;
        public String long2;
        public String long3;
        public String long4;
        public String long5;
        public String long6;
        public String target;
        public String algmid1;
        public String algmid2;
        public String algmid3;
        public String algmid4;
        public String alglong1;
        public String alglong2;
        public String alglong3;
        public String alglong4;
        public String environment;
        public String tools;
        public String datas;
        public String standard;
        public String result;
        public String future;
        public String weekpoint;
        public String keyword;
    }

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}