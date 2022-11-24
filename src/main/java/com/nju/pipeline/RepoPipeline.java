package com.nju.pipeline;

import com.nju.consts.CrawlMethod;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;


/**
 * @description
 * @date:2022/11/16 23:21
 * @author: qyl
 */
public class RepoPipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        CrawlMethod.data.add (resultItems.get ("data"));
    }
}
