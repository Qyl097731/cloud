package com.nju.pipeline;

import com.nju.processor.RepoProcessor;
import com.nju.utils.ThreadPoolUtil;
import org.apache.commons.collections.CollectionUtils;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.utils.HttpConstant;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Stream;

import static com.nju.consts.UrlConstant.GITEE_REPO_URL;

/**
 * @description
 * @date:2022/11/16 21:53
 * @author: qyl
 */
public class GiteePipeline implements Pipeline {
    private static final ExecutorService POOL = ThreadPoolUtil.getExecutorService();

    static Stream<CompletableFuture<Void>> crawlRepoInfo(List<String> suffixs) {
        return suffixs.stream().map(suffix ->
                CompletableFuture.runAsync(() -> {
                    Request request = new Request(GITEE_REPO_URL + suffix);
                    request.setMethod(HttpConstant.Method.GET);
                    //执行爬取任务
                    Spider spider = Spider.create(new RepoProcessor());
                    //调用接口地址 在Pipeline中进行数据处理
                    spider.addRequest(request).addPipeline(new RepoPipeline()).run();
                }, POOL));
    }

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<String> urls = resultItems.get("data");
        if (!CollectionUtils.isEmpty(urls)) {
            try {
                CompletableFuture[] tasks = crawlRepoInfo(urls).toArray(CompletableFuture[]::new);
                CompletableFuture.allOf(tasks).join();
            } catch (Exception e) {
            }
        }
    }
}
