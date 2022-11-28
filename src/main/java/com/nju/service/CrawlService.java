package com.nju.service;

import com.nju.consts.Crawls;
import com.nju.crawls.CrawlMethod;
import com.nju.utils.CrawlStrategy;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description
 * @date:2022/11/8 16:07
 * @author: qyl
 */
public class CrawlService {
    public static void run() {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor ( );
        try {
            executor.scheduleWithFixedDelay (() -> {
                CrawlStrategy.crawl (Crawls.GITEEOPENINFOSPIDER);
                closeGracefully ( );
            }, 0, 10, TimeUnit.MINUTES);
        } catch (Exception e) {
        }
    }

    private static void closeGracefully() {
        CrawlMethod.urls.clear ( );
        CrawlMethod.data.clear ( );
    }
}
