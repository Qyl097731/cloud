package com.nju.crawls;

import com.nju.consts.CrawlMethod;
import com.nju.pipeline.PaperLine;
import com.nju.processor.PaperProcessor;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.nju.consts.UrlConstant.IEEE_URL;

/**
 * @description
 * @date:2022/11/18 18:59
 * @author: qyl
 */
public class IeeeSrawlWithSpider implements CrawlMethod {

    /**
     * 用Spider爬 IEEE
     */
    public static void crawlIeeeWithSpider() {
        try {
            int pStart = 8000000;
            CompletableFuture[] tasks;
            while (pStart < 8009000) {
                tasks = crawlIeee(pStart, pStart + 16, IEEE_URL).toArray(CompletableFuture[]::new);
                CompletableFuture.anyOf(tasks).join();
                pStart += 16;
            }

        } catch (Exception e) {
        }
    }

    public static Stream<CompletableFuture<Void>> crawlIeee(int start, int end, String url) {
        return IntStream.rangeClosed(start, end).boxed().map(pid ->
                CompletableFuture.runAsync(() -> {
                    String target = String.format(url, pid);
                    Request request = new Request(target);
                    request.setMethod(HttpConstant.Method.GET);
                    //执行爬取任务
                    Spider spider = Spider.create(new PaperProcessor());
                    //调用接口地址 在Pipeline中进行数据处理
                    spider.addRequest(request).addPipeline(new PaperLine()).run();
                }, POOL));
    }

    @Override
    public void crawl() {
        crawlIeeeWithSpider();
    }
}
