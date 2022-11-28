package com.nju;

import com.nju.service.CrawlService;
import com.nju.service.DividerAnalyzerService;
import com.nju.service.WatchingAnalyzerService;

/**
 * @description
 * @date:2022/11/8 16:07
 * @author: qyl
 */
public class CloudServer {
    public static void main(String[] args) {
        new Thread (CrawlService::run).start ( );
        new Thread (DividerAnalyzerService::run).start ( );
        new Thread (WatchingAnalyzerService::run).start ( );
    }
}
