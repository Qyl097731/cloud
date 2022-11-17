package com.nju;

import com.nju.pipeline.GiteeOpenInfoPipeline;
import com.nju.pipeline.GiteePipeline;
import com.nju.pipeline.PaperLine;
import com.nju.processor.GiteeOpenInfoProcessor;
import com.nju.processor.GiteeProcessor;
import com.nju.processor.PaperProcessor;
import com.nju.utils.ThreadPoolUtil;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.nju.consts.UrlConstant.GITEE_URL;
import static com.nju.consts.UrlConstant.IEEE_URL;

/**
 * @description
 * @date:2022/11/8 16:07
 * @author: qyl
 */
public class Crawl {
    private static final ExecutorService POOL = ThreadPoolUtil.getExecutorService();
    private static final int pStart = 1;
    private static final int pEnd = 10;

    public static void main(String[] args) {
        crawlGiteeOpenInfo();
    }

    static void crawlGiteeOpenInfo() {
        try {
            while (true) {
                long start = System.currentTimeMillis();
                crawlGiteeOpenInfo(pStart, pEnd, GITEE_URL);
                TimeUnit.SECONDS.sleep(1);
                long end = System.currentTimeMillis();
                System.out.println((end - start) / 1_000);
            }
        } catch (Exception e) {
        }
    }

    static void crawlGitee() {
        try {
            int pStart = 1;
            while (pStart <= 100) {
                String target = String.format(GITEE_URL, pStart);
                Request request = new Request(target);
                request.setMethod(HttpConstant.Method.GET);
                //执行爬取任务
                Spider spider = Spider.create(new GiteeProcessor());
                //调用接口地址 在Pipeline中进行数据处理
                spider.addRequest(request).addPipeline(new GiteePipeline()).run();
                pStart++;
            }
        } catch (Exception e) {
        }
    }

    static void crawlIeee() {
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

    static Stream<CompletableFuture<Void>> crawlIeee(int start, int end, String url) {
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

    static void crawlGiteeOpenInfo(int start, int end, String url) {
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
                .thread(POOL, 10).run();
    }
}
