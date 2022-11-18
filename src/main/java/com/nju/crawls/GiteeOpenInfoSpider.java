package com.nju.crawls;

import com.nju.consts.CrawlMethod;
import com.nju.pipeline.GiteeOpenInfoPipeline;
import com.nju.processor.GiteeOpenInfoProcessor;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static com.nju.consts.UrlConstant.GITEE_URL;
import static com.nju.utils.ThreadPoolUtil.POOLSIZE;

/**
 * @description
 * @date:2022/11/18 18:56
 * @author: qyl
 */
public class GiteeOpenInfoSpider implements CrawlMethod {
    @Override
    public void crawl() {
        crawlGiteeOpenInfoWithSpider();
    }

    /**
     * 用Spider爬gitee开源
     */
    private void crawlGiteeOpenInfoWithSpider() {
        try {
            long start = System.currentTimeMillis();
            crawlGiteeOpenInfoWithSpiderConcurrently(PSTART, PEND, GITEE_URL);
            TimeUnit.SECONDS.sleep(1);
            long end = System.currentTimeMillis();
            System.out.println((end - start) / 1_000);
        } catch (Exception e) {
        }
    }

    private void crawlGiteeOpenInfoWithSpiderConcurrently(int start, int end, String url) {
        Request[] requests = IntStream.rangeClosed(start, end)
                .boxed()
                .map(suffix -> {
                    String target = String.format(url, suffix);
                    return new Request(target).setMethod(HttpConstant.Method.GET);
                })
                .toArray(Request[]::new);
        //执行爬取任务
        Spider spider = Spider.create(new GiteeOpenInfoProcessor());
        //调用接口地址 在Pipeline中进行数据处理
        spider.addRequest(requests).addPipeline(new GiteeOpenInfoPipeline())
                .thread(POOL, POOLSIZE).run();
    }
}
