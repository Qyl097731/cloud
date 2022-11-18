package com.nju.consts;

/**
 * @description
 * @date:2022/11/18 18:33
 * @author: qyl
 */
public class Crawls {
    public static final String GITEEOPENINFOCRAWL = "GiteeOpenInfoCrawl";
    public static final String GITEEOPENINFOSPIDER = "GiteeOpenInfoSpider";
    public static final String GITEEREPOINFOCRAWL = "GiteeRepoInfoCrawl";
    public static final String IEEESRAWLWITHSPIDER = "IeeeSrawlWithSpider";

    private Crawls() {
    }

    public static CrawlMethod of(String name) {
        try {
            Class<?> clazz = Crawls.class.getClassLoader().loadClass(name);
            return (CrawlMethod) clazz.newInstance();
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return null;
    }

}
