package com.nju.processor;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Strings;
import org.apache.commons.lang3.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author qyl
 * @program WaterQtyStationProcessor.java
 * @createTime 2022-08-25 17:13
 */
public class PaperProcessor implements PageProcessor {
    private static final String BEGIN = "xplGlobal.document.metadata";
    private static final String END = "xplGlobal.document.userLoggedIn";
    private Site site;

    public PaperProcessor() {
        setSite();
    }

    private void setSite() {
        // 设置站点信息，模拟浏览器
        site = Site.me().setRetryTimes(30).setSleepTime(1000).setTimeOut(20000);
    }

    @Override
    public void process(Page page) {
        String content = page.getHtml().css("script").regex(".*" + BEGIN + ".*").get();
        int end = content.indexOf(END);
        content = content.substring(content.indexOf(BEGIN) + BEGIN.length() + 1, end == -1 ? content.length() : end);
        if (Strings.isNullOrEmpty(content)) {
            return;
        }
        Map map = (Map) JSON.parse(content.substring(0, content.lastIndexOf(";")));
        if (map != null) {
            try {
                List<Object> date = new ArrayList<>();
                date.add(map.get("sourcePdf").toString().split("\\.")[0]);
                date.add(map.get("title"));
                date.add(map.get("publicationDate"));
                Map authors = (Map) JSON.parse((((List) map.get("authors")).get(0)).toString());
                date.add(authors.get("name"));
                date.add(getKeyWords((JSONArray) map.get("keywords")));
                page.putField("date", date);
            }catch (Exception e){
            }
        }
    }

    private Object getKeyWords(JSONArray keywords) {
        return ((Map) JSON.parse(keywords.get(1).toString())).get("kwd");
    }

    @Override
    public Site getSite() {
        return site;
    }
}
