package com.nju.pipeline;

import com.nju.processor.RepoProcessor;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.concurrent.ExecutorService;

import static com.nju.consts.UrlConstant.GITEE_REPO_URL;
import static com.nju.utils.ThreadPoolUtil.getExecutorService;

/**
 * @description
 * @date:2022/11/16 21:53
 * @author: qyl
 */
public class GiteePipeline implements Pipeline {
    private static final ExecutorService POOL = getExecutorService();

    static void crawlRepoInfo(List<String> suffixs) {
        Request[] requests = suffixs.stream().map(suffix ->
                new Request(GITEE_REPO_URL + suffix).setMethod(HttpConstant.Method.GET)
        ).toArray(Request[]::new);
        //执行爬取任务
        Spider spider = Spider.create(new RepoProcessor());
        //调用接口地址 在Pipeline中进行数据处理
        spider.addRequest(requests).addPipeline(new RepoPipeline())
                .thread(POOL, requests.length).run();
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<String> urls = resultItems.get("data");
        if (!CollectionUtils.isEmpty(urls)) {
            try {
                crawlRepoInfo(urls);
            } catch (Exception e) {
            }
        }
    }
}
