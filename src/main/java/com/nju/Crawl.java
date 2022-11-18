package com.nju;

import com.nju.consts.Crawls;
import com.nju.utils.CrawlStrategy;
/**
 * @description
 * @date:2022/11/8 16:07
 * @author: qyl
 */
public class Crawl {
    public static void main(String[] args) {
        while (true) {
            CrawlStrategy.crawl(Crawls.GITEEOPENINFOCRAWL);
        }
    }
}
