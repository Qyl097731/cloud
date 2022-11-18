package com.nju.pipeline;

import com.nju.utils.CsvUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.ArrayList;
import java.util.List;

/**
 * 数据管道处理
 *
 * @author qyl
 */
public class PaperLine implements Pipeline {
    static List<Object> header = new ArrayList<Object>() {
        {
            add("title");
            add("date");
            add("authors");
            add("keyword");
        }
    };

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Object> date = resultItems.get("data");
        CsvUtils.createCSVFile(header, date.subList(1, date.size()), "/usr/local/data/",
                date.get(0).toString() + "test");
    }
}
