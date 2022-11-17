package com.nju.processor;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;

/**
 * @description
 * @date:2022/11/17 16:56
 * @author: qyl
 */
public class GiteeOpenInfoProcessor implements PageProcessor {
    private static final Logger log = Logger.getLogger(GiteeOpenInfoProcessor.class);
    private Site site;

    public GiteeOpenInfoProcessor() {
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
            List<List<Object>> data = encapsulateData(content);
            page.putField("data", data);
        } catch (Exception e) {
        }
    }

    private List<List<Object>> encapsulateData(Html html) {
        List<List<Object>> data = new ArrayList<>();

        List<Selectable> items = html.css(".ui.relaxed.divided.items.explore-repo__list > .item").nodes();
        for (Selectable content : items) {
            List<Object> row = new ArrayList<>();

            String repo = content.css(".title.project-namespace-path", "text").get();
            if (!StringUtils.isEmpty(repo)) {
                String[] strs = repo.split("/");
                row.add(strs[0]);
                row.add(strs[1]);
            }

            String level = content.css(".iconfont.icon-recommended.js-popup-default", "title").get();
            row.add(level);

            String stars = content.css(".stars-count", "text").get();
            row.add(stars);

            List<String> labels = content.css(".project-label-item", "text").all();
            row.add(StringUtils.join(labels, ","));

            String desc = content.css(".project-desc.mb-1", "text").get();
            row.add(desc);

            String language = content.css(".d-flex-center > .project-language.project-item-bottom__item", "text").get();
            row.add(language);

            String type = content.css(".d-align-center.ml-2.project-class > .project-item-bottom__item", "text").get();
            row.add(type);

            String time = content.css(".text-muted.project-item-bottom__item.d-flex-center", "title").get();
            row.add(time);

            data.add(row);
        }
        return data;
    }


    @Override
    public Site getSite() {
        return site;
    }
}
