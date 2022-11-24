package com.nju.consts;

import com.nju.crawls.CrawlMethod;

/**
 * @description
 * @date:2022/11/18 18:33
 * @author: qyl
 */
public class Crawls {
    public static final String GITEEOPENINFOCRAWL = "GiteeOpenInfoCrawl";
    public static final String GITEEOPENINFOSPIDER = "GiteeOpenInfoSpider";
    public static final String GITEEREPOINFOSPIDER = "GiteeRepoInfoSpider";
    public static final String IEEESRAWLWITHSPIDER = "IeeeSrawlWithSpider";
    private static CrawlMethod crawl;

    private Crawls() {
    }

    public static CrawlMethod of(String name) {
        try {
            Class<?> clazz = CrawlMethod.class.getClassLoader ( ).loadClass ("com.nju.crawls.impl." + name);
            if (clazz != null) {
                crawl = (CrawlMethod) clazz.newInstance ( );
            }
            return crawl;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace ( );
        }
        return null;
    }

}
