package com.nju.pipeline;

import com.nju.utils.CsvUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
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
    public static DateTimeFormatter FORMAT_YMDH = DateTimeFormatter.ofPattern("yyyyMMddHH");

    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Object> data = resultItems.get("data");
        CsvUtils.createCSVFile(HEADER, data, "data/",
                LocalDateTime.now(ZoneId.systemDefault()).format(FORMAT_YMDH));
    }
}
