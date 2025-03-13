package com.example.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class TaskService {

    // 使用单线程池确保任务按顺序执行
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * 异步执行任务
     * @param task 要执行的任务
     */
    @Async
    public void executeAsync(Runnable task) {
        executor.submit(task);
    }

    /**
     * 关闭线程池（可选，在应用关闭时调用）
     */
    public void shutdown() {
        executor.shutdown();
    }
}