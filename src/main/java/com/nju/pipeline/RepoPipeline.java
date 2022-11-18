package com.nju.pipeline;

import com.nju.utils.CsvUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.Arrays;
import java.util.List;


/**
 * @description
 * @date:2022/11/16 23:21
 * @author: qyl
 */
public class RepoPipeline implements Pipeline {
    private static final List<Object> HEADER = Arrays.asList(
            "author",
            "repo",
            "labels",
            "watch",
            "star",
            "fork",
            "desc",
            "language"
    );

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Object> data = resultItems.get("data");
        CsvUtils.createCSVFile(HEADER, data, "/usr/local/data/",
                data.get(0).toString());
    }
}
