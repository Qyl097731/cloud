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
 * @date:2022/11/17 16:56
 * @author: qyl
 */
public class GiteeOpenInfoPipeline implements Pipeline {
    private static final List<Object> HEADER = Arrays.asList(
            "author",
            "repo",
            "level",
            "star",
            "labels",
            "desc",
            "language",
            "type",
            "time"
    );
    public static DateTimeFormatter FORMAT_YMDH = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");


    @Override
    public void process(ResultItems resultItems, Task task) {
        List<List<Object>> data = resultItems.get("data");
        CsvUtils.createCSVFile2(HEADER, data, "data/",
                LocalDateTime.now(ZoneId.systemDefault()).format(FORMAT_YMDH));
    }
}
