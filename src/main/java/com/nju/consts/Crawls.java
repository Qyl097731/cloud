package com.nju.consts;

import com.nju.crawls.CrawlMethod;

/**
 * @description 所有的爬虫类
 * @date:2022/11/18 18:33
 * @author: qyl
 */
public class Crawls {
    public static final String GITEEOPENINFOSPIDER = "GiteeOpenInfoSpider";

    private static volatile CrawlMethod crawl = null;

    private Crawls() {
    }

    public static CrawlMethod of(String name) {
        try {
            Class<?> clazz = CrawlMethod.class.getClassLoader ().loadClass ("com.nju.crawls.impl." + name);
            if (crawl == null) {
                synchronized (Crawls.class) {
                    if (crawl == null) {
                        crawl = (CrawlMethod) clazz.newInstance ();
                    }
                }
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace ( );
        }
        return crawl;
    }
}
