package com.nju.crawls.impl;

import com.nju.crawls.CrawlMethod;
import com.nju.pipeline.GiteePipeline;
import com.nju.pipeline.RepoPipeline;
import com.nju.processor.GiteeProcessor;
import com.nju.processor.RepoProcessor;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.Arrays;
import java.util.stream.IntStream;

import static com.nju.consts.UrlConstant.GITEE_REPO_URL;
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
        crawlRepoUrls ( );
        crawlRepoInfo ( );
    }

    /**
     * 用Spider爬gitee开源仓库详细信息
     */
    private void crawlRepoInfo() {
        Request[] requests = CrawlMethod.urls.stream ( ).map (url ->
                new Request (GITEE_REPO_URL + url).setMethod (HttpConstant.Method.GET)).toArray (Request[]::new);
        int n = requests.length;
        int start = 0;
        while (start < n) {
            Request[] subRequests = Arrays.copyOfRange (requests, start, Math.min (start + POOLSIZE, n));
            //执行爬取任务
            Spider spider = Spider.create (new RepoProcessor ( ));
            //调用接口地址 在Pipeline中进行数据处理
            spider.addRequest (subRequests).addPipeline (new RepoPipeline ( ))
                    .thread (POOL, subRequests.length).run ( );
            start += POOLSIZE;
        }
    }

    /**
     * 用Spider爬gitee开源仓库地址
     */
    private void crawlRepoUrls() {
        try {
            Request[] requests = IntStream.rangeClosed (PSTART, PEND)
                    .mapToObj (suffix -> {
                        String target = String.format (GITEE_URL, suffix);
                        return new Request (target).setMethod (HttpConstant.Method.GET);
                    }).toArray (Request[]::new);
            int n = requests.length;
            int start = 0;
            while (start < n) {
                Request[] subRequests = Arrays.copyOfRange (requests, start, Math.min (start + POOLSIZE, n));
                //执行爬取任务
                Spider spider = Spider.create (new GiteeProcessor ( ));
                //调用接口地址 在Pipeline中进行数据处理
                spider.addRequest (subRequests).addPipeline (new GiteePipeline ( ))
                        .thread (POOL, subRequests.length).run ( );
                start += POOLSIZE;
            }
        } catch (Exception e) {
        }
    }
}
