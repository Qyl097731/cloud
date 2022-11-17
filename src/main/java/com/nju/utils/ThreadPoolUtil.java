package com.nju.utils;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import java.util.concurrent.*;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;

/**
 * @author asus
 */
public class ThreadPoolUtil {
    private static ExecutorService executor;
    private static final int POOLSIZE = 2 * Runtime.getRuntime().availableProcessors() + 1;

    private ThreadPoolUtil() {
    }

    public static ExecutorService getExecutorService() {
        if (null == executor || executor.isShutdown() || executor.isTerminated()) {
            ThreadFactory namedThreadFactory =
                    (new ThreadFactoryBuilder()).setNameFormat("nju-pool-%d").setDaemon(true).build();
            executor = new ThreadPoolExecutor(POOLSIZE, 100, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue(1048576), namedThreadFactory, new AbortPolicy());
        }

        return executor;
    }

    public static void closeThreadPool() {
        executor.shutdown();
    }

    static {
        getExecutorService();
    }
}
