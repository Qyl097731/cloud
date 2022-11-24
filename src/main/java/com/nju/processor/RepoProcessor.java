package com.nju.processor;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.ArrayList;
import java.util.List;

/**
 * @description
 * @date:2022/11/16 23:20
 * @author: qyl
 */
public class RepoProcessor implements PageProcessor {
    private static final Logger log = Logger.getLogger(RepoProcessor.class);
    private Site site;

    public RepoProcessor() {
        setSite();
    }

    private void setSite() {
        // 设置站点信息，模拟浏览器
        site = Site.me().setRetryTimes(30).setSleepTime(1000).setTimeOut(20000);
    }

    @Override
    public void process(Page page) {
        try {
            Html content = page.getHtml();
            if (content == null) {
                log.info("fail to connect");
                return;
            }
            List<Object> data = encapsulateData(content);
            page.putField("data", data);
        } catch (Exception e) {
        }
    }

    private List<Object> encapsulateData(Html content) {
        List<Object> data = new ArrayList<> ( );
        String author = content.css (".author", "text").get ( );
        data.add (author);
        String repo = content.css (".repository", "text").get ( );
        data.add (repo);
        List<String> labels = content.css (".project-label-item", "text").all ( );
        data.add (StringUtils.join (labels, ","));
        List<String> social = content.css (".ui.button.action-social-count", "title").all ( );
        data.addAll (social);
        String desc = content.css (".git-project-desc-text", "text").get ( );
        data.add (desc);
        String language = content.css (".summary-languages", "text").get ( );
        data.add (language);
        String time = content.css (".timeago", "title").get ( );
        data.add (time);
        return data;
    }


    @Override
    public Site getSite() {
        return site;
    }
}
