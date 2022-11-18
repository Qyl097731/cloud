package com.nju.consts;

import com.nju.utils.ThreadPoolUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

/**
 * @description
 * @date:2022/11/18 18:39
 * @author: qyl
 */
@FunctionalInterface
public interface CrawlMethod {
    ExecutorService POOL = ThreadPoolUtil.getExecutorService();
    int PSTART = 1;
    int PEND = 10;
    List<Object> HEADER = Arrays.asList(
            "author",
            "repo",
            "level",
            "star",
            "labels",
            "desc",
            "language",
            "type",
            "time"
    );

    void crawl();
}
