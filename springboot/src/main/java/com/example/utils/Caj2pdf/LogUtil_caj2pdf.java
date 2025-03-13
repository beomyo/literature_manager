package com.example.utils.Caj2pdf;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LogUtil_caj2pdf {
    private static final String LOG_DIR = "log";
    private static final String LOG_FILE = "caj2pdf.log";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static synchronized void log(String message) {
        File logDir = new File(LOG_DIR);
        if (!logDir.exists()) {
            logDir.mkdirs(); // 创建日志目录
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(new File(logDir, LOG_FILE), true))) {
            String timestamp = DATE_FORMAT.format(new Date());
            writer.println("[" + timestamp + "] " + message);
        } catch (IOException e) {
            System.err.println("无法写入日志: " + e.getMessage());
        }
    }
}