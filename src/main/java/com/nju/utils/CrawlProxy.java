package com.nju.utils;

import com.nju.consts.Crawls;
import com.nju.crawls.CrawlMethod;

/**
 * @description 爬虫类
 * @date:2022/11/18 18:25
 * @author: qyl
 */
public class CrawlProxy {
    private CrawlProxy() {
    }

    public static void crawl(String name) {
        CrawlMethod delegator = Crawls.of (name);
        if (delegator != null) {
            delegator.crawl ();
        }
    }
}
