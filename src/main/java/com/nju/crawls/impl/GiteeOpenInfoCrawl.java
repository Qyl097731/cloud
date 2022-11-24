package com.nju.crawls.impl;

import com.nju.crawls.CrawlMethod;
import com.nju.utils.CsvUtils;
import com.nju.utils.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.IntStream;

import static com.nju.consts.UrlConstant.GITEE_REPO_URL;
import static com.nju.consts.UrlConstant.GITEE_URL;
import static com.nju.utils.ThreadPoolUtil.POOLSIZE;

/**
 * @description
 * @date:2022/11/18 18:43
 * @author: qyl
 */
public class GiteeOpenInfoCrawl implements CrawlMethod {
    List<Object> HEADER = Arrays.asList (
            "author",
            "repo",
            "level",
            "watch",
            "star",
            "fork",
            "labels",
            "desc",
            "language",
            "type",
            "time"
    );

    @Override
    public void crawl() {
        crawlGiteeOpenInfoWithJsoup ( );
    }


    /**
     * 爬取指定页数数据，并把数据收集起来
     */
    private void crawlGiteeOpenInfoWithJsoup() {
        try {
            List<List<Object>> data = new ArrayList<> ( );
            int pStart = PSTART;
            while (pStart < PEND) {
                data.addAll (crawlGiteeOpenInfoWithJsoupConcurrently (pStart, Math.min (pStart + POOLSIZE, PEND)));
                pStart = Math.min (pStart + POOLSIZE, PEND);
            }
            CsvUtils.createCSVFile2 (HEADER, data, "data/", "all_data");
        } catch (Exception e) {
            e.printStackTrace ( );
        }
    }

    /**
     * 爬取多页数据集
     *
     * @param start
     * @param end
     * @return
     */
    private List<List<Object>> crawlGiteeOpenInfoWithJsoupConcurrently(int start, int end) {
        List<List<Object>> data = new ArrayList<> ( );

        CompletableFuture[] tasks = IntStream
                .rangeClosed (start, end).mapToObj (
                        pid -> CompletableFuture.runAsync (() -> {
                            String target = String.format (GITEE_URL, pid);
                            String page = HttpUtils.getHtml (target);
                            if (!StringUtils.isEmpty (page)) {
                                Document html = Jsoup.parse (page, Parser.htmlParser ( ));
                                data.addAll (encapsulateData (html));
                            }
                        }, POOL)).toArray (CompletableFuture[]::new);
        CompletableFuture.allOf (tasks).join ( );
        return data;
    }

    /**
     * 解析每一页数据，并封装返回
     *
     * @param html
     * @return
     */
    private List<List<Object>> encapsulateData(Document html) {
        List<List<Object>> data = new ArrayList<> ( );

        Elements items = html.select (".ui.relaxed.divided.items.explore-repo__list > .item");
        try {
            CompletableFuture[] tasks = items.stream ( ).map (content ->
                    CompletableFuture.runAsync (() -> {
                        List<Object> row = new ArrayList<> ( );

                        Elements namespace = content.select (".title.project-namespace-path");
                        String title = namespace.text ( );
                        if (!StringUtils.isEmpty (title)) {
                            String[] strs = title.split ("/");
                            row.add (strs[0]);
                            row.add (strs[1]);
                        }

                        String repo = namespace.attr ("href");
                        String target = GITEE_REPO_URL + repo;
                        String page = HttpUtils.getHtml (target);
                        Document repoPage = Jsoup.parse (page, Parser.htmlParser ( ));
                        row.addAll (repoPage.select (".ui.button.action-social-count").eachAttr ("title"));

                        String level = content.select (".iconfont.icon-recommended.js-popup-default").attr ("title");
                        row.add (level);

                        List<String> labels = content.select (".project-label-item").eachText ( );
                        row.add (StringUtils.join (labels, ","));

                        String desc = content.select (".project-desc.mb-1").text ( );
                        row.add (desc);

                        String language = content.select (".d-flex-center > .project-language.project-item-bottom__item").text ( );
                        row.add (language);

                        String type = content.select (".d-align-center.ml-2.project-class > .project-item-bottom__item").text ( );
                        row.add (type);

                        String time = content.select (".text-muted.project-item-bottom__item.d-flex-center").attr ("title");
                        row.add (time);
                        data.add (row);
                    }, POOL)).toArray (CompletableFuture[]::new);

            for (CompletableFuture task : tasks) {
                task.join ( );
            }
        } catch (Exception e) {
            e.printStackTrace ( );
        }
        return data;
    }
}
