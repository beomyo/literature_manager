package com.example.utils.txt2mysql;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CNKIFileProcessor {
    public static void main(String[] args) {
        String inputDir = "D:\\Program Files\\Desktop\\CNKI"; // 修改为你的输入目录
        String encoding = "UTF-8";

        try {
            // 创建数据库表并加载现有记录的 Title-Author 组合
            DatabaseHelper.createTableIfNotExists();
            DatabaseHelper.loadExistTitleAuthorCombinations();

            // 遍历目录下的所有TXT文件
            File dir = new File(inputDir);
            File[] files = dir.listFiles((dir1, name) -> name.toLowerCase().endsWith(".txt"));
            if (files == null) {
                System.out.println("No files found in directory: " + inputDir);
                return;
            }

            List<Record> allRecords = new ArrayList<>();
            for (File file : files) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        new FileInputStream(file), encoding));
                StringBuilder content = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
                reader.close();

                List<Record> records = parseContent(content.toString());
                allRecords.addAll(records);
            }

            // 插入数据到数据库
            for (Record record : allRecords) {
                DatabaseHelper.insertRecord(record);
            }

            System.out.println("Data insertion completed successfully!");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<Record> parseContent(String content) {
        List<Record> records = new ArrayList<>();
        String[] blocks = content.split("\n\n"); // 使用 "\n\n" 匹配空行

        for (String block : blocks) {
            block = block.trim();
            if (block.isEmpty()) continue;

            Record record = new Record();

            String[] lines = block.split("\n");
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;

                if (line.contains(":")) {
                    String[] parts = line.split(":", 2);
                    String key = parts[0].trim();
                    String value = parts.length > 1 ? parts[1].trim() : "";

                    switch (key) {
                        case "SrcDatabase-来源库":
                            record.setSrcDatabase(value);
                            break;
                        case "Title-题名":
                            record.setTitle(value);
                            break;
                        case "Author-作者":
                            record.setAuthor(value);
                            break;
                        case "Organ-单位":
                            record.setOrgan(value);
                            break;
                        case "Source-文献来源":
                            record.setSource(value);
                            break;
                        case "Keyword-关键词":
                            record.setKeyword(value);
                            break;
                        case "Summary-摘要":
                            record.setSummary(value);
                            break;
                        case "PubTime-发表时间":
                            record.setPubTime(value);
                            break;
                        case "FirstDuty-第一责任人":
                            record.setFirstDuty(value);
                            break;
                        case "Fund-基金":
                            record.setFund(value);
                            break;
                        case "Year-年":
                            record.setYear(value);
                            break;
                        case "PageCount-页码":
                            record.setPageCount(value);
                            break;
                        case "CLC-中图分类号":
                            record.setCLC(value);
                            break;
                        case "URL-网址":
                            record.setURL(value);
                            break;
                        case "DOI-DOI":
                            record.setDOI(value);
                            break;
                    }
                }
            }
            records.add(record);
        }

        return records;
    }
}