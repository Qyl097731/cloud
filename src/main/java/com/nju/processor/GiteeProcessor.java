package com.nju.processor;

import org.apache.commons.collections.CollectionUtils;
import org.apache.log4j.Logger;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * @description
 * @date:2022/11/16 21:53
 * @author: qyl
 */
public class GiteeProcessor implements PageProcessor {
    private static final Logger log = Logger.getLogger(GiteeProcessor.class);
    private Site site;

    public GiteeProcessor() {
        setSite();
    }

    private void setSite() {
        // 设置站点信息，模拟浏览器
        site = Site.me().setRetryTimes(30).setSleepTime(1000).setTimeOut(20000);
    }

    @Override
    public void process(Page page) {
        try {
            List<String> urls = page.getHtml().css(".title.project-namespace-path", "href").all();
            if (!CollectionUtils.isEmpty(urls)) {
                page.putField("data", urls);
            }
        } catch (Exception e) {
        }
    }

    @Override
    public Site getSite() {
        return site;
    }
}
