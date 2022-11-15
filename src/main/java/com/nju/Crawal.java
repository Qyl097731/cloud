package com.nju;

import com.nju.pipeline.PaperLine;
import com.nju.processor.PaperProcessor;
import com.nju.utils.ThreadPoolUtil;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.utils.HttpConstant;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @description
 * @date:2022/11/8 16:07
 * @author: qyl
 */
public class Crawal {
    private static final ExecutorService POOL = ThreadPoolUtil.getExecutorService();

    public static void main(String[] args) throws IOException {
        String url = "https://ieeexplore.ieee.org/document/%d";
        int pStart = 8000000;
        try {
            CompletableFuture[] tasks;
            while (pStart < 8009000) {
                tasks = crawl(pStart, pStart + 16, url).toArray(CompletableFuture[]::new);
                CompletableFuture.anyOf(tasks).join();
                pStart += 16;
            }

        } catch (Exception e) {
        }
    }

    private static void upload() throws IOException {
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS","hdfs://master:9000");
        FileSystem fs = FileSystem.get(conf);
        Path src = new Path("C:\\Users\\asus\\Desktop\\data1");
        Path dst = new Path("/");
        fs.copyFromLocalFile(src,dst);
        FileStatus[] files = fs.listStatus(dst);
        for (FileStatus file : files) {
            System.out.println(file.getPath());
        }
    }

    static Stream<CompletableFuture<Void>> crawl(int start, int end, String url) {
        return IntStream.rangeClosed(start, end).boxed().map(pid ->
                CompletableFuture.runAsync(() -> {
                    String target = String.format(url, pid);
                    Request request = new Request(target);
                    request.setMethod(HttpConstant.Method.GET);
                    //执行爬取任务
                    Spider spider = Spider.create(new PaperProcessor());
                    //调用接口地址 在Pipline中进行数据处理
                    spider.addRequest(request).addPipeline(new PaperLine()).run();
                }, POOL));
    }


}
