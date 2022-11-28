package com.nju.pipeline;

import com.nju.utils.CsvUtils;
import us.codecraft.webmagic.ResultItems;
import us.codecraft.webmagic.Task;
import us.codecraft.webmagic.pipeline.Pipeline;

import java.util.List;

import static com.nju.consts.SchemasConst.SCHEMA;
import static com.nju.crawls.CrawlMethod.REPO_HEADER;
import static com.nju.crawls.CrawlMethod.dest;
import static com.nju.utils.SparkUtils.upload;


/**
 * @description
 * @date:2022/11/16 23:21
 * @author: qyl
 */
public class RepoPipeline implements Pipeline {
//    public static final String PATH = "data/distribute/";

    public static final String PATH = "/root/test/data/distribute/";
    @Override
    public void process(ResultItems resultItems, Task task) {
        List<Object> data = resultItems.get ("data");
        String filename = data.get (0).toString ( );
        CsvUtils.createCSVFile (REPO_HEADER, data, PATH, filename);
        upload (PATH, filename, dest, SCHEMA);
    }
}
