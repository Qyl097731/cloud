package com.nju.crawls;

import com.nju.consts.CrawlMethod;
import com.nju.pipeline.GiteePipeline;
import com.nju.processor.GiteeProcessor;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

import static com.nju.consts.UrlConstant.GITEE_URL;

/**
 * @description
 * @date:2022/11/18 18:58
 * @author: qyl
 */
public class GiteeRepoInfoCrawl implements CrawlMethod {
    @Override
    public void crawl() {
        crawlGiteeWithSpider();
    }

    /**
     * 用Spider爬gitee具体仓库
     */
    private void crawlGiteeWithSpider() {
        try {
            int pStart = PSTART;
            while (pStart <= PEND) {
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
}
