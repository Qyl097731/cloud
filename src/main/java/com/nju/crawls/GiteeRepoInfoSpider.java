package com.nju.crawls;

import com.nju.consts.CrawlMethod;
import com.nju.pipeline.GiteePipeline;
import com.nju.processor.GiteeProcessor;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.stream.IntStream;

import static com.nju.consts.UrlConstant.GITEE_URL;

/**
 * @description
 * @date:2022/11/18 18:58
 * @author: qyl
 */
public class GiteeRepoInfoSpider implements CrawlMethod {
    private static final int PEND = 10;

    @Override
    public void crawl() {
        crawlGiteeWithSpider ( );
    }

    /**
     * 用Spider爬gitee具体仓库
     */
    private void crawlGiteeWithSpider() {
        try {
            Request[] requests = IntStream.rangeClosed(PSTART, PEND).mapToObj(pid -> {
                String target = String.format(GITEE_URL, pid);
                return new Request(target).setMethod(HttpConstant.Method.GET);
            }).toArray(Request[]::new);
            //执行爬取任务
            Spider spider = Spider.create(new GiteeProcessor());
            //调用接口地址 在Pipeline中进行数据处理
            spider.addRequest(requests).addPipeline(new GiteePipeline())
                    .thread(POOL, requests.length).run();
        } catch (Exception e) {
        }
    }
}
