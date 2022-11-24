package com.nju.pipeline;

import com.nju.crawls.CrawlMethod;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

/**
 * @description
 * @date:2022/11/16 21:53
 * @author: qyl
 */
public class GiteePipeline implements Pipeline {
    @Override
    public void process(ResultItems resultItems, Task task) {
        List<String> urls = resultItems.get("data");
        if (!CollectionUtils.isEmpty(urls)) {
            try {
                CrawlMethod.urls.addAll (urls);
            } catch (Exception e) {
            }
        }
    }
}
