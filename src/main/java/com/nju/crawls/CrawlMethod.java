package com.nju.crawls;

import com.nju.utils.ThreadPoolUtil;

import java.util.ArrayList;
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
    ExecutorService POOL = ThreadPoolUtil.getExecutorService ( );
    List<List<Object>> data = new ArrayList<> ( );
    List<String> urls = new ArrayList<> ( );
    int PSTART = 1;
    int PEND = 100;
    List<Object> HEADER = Arrays.asList (
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

    String dest = "hdfs://master:9000/cloud/stream/";

    List<Object> REPO_HEADER = Arrays.asList (
            "title",
            "author",
            "language",
            "divider",
            "watch",
            "stars",
            "fork",
            "abstract",
            "key_words",
            "time"
    );

    void crawl();
}
