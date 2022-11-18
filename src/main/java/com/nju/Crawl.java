package com.nju;

import com.nju.consts.Crawls;
import com.nju.utils.CrawlStrategy;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.SparkSession;

/**
 * @description
 * @date:2022/11/8 16:07
 * @author: qyl
 */
public class Crawl {
    public static void main(String[] args) {
        while (true) {
            long start = System.currentTimeMillis();
            CrawlStrategy.crawl(Crawls.GITEEOPENINFOCRAWL);
            upload();
            long end = System.currentTimeMillis();
            System.out.println((end - start) / 1_000);
        }
    }

    private static void upload() {
        SparkConf conf = new SparkConf().setAppName("crawl").setMaster("master");
        SparkSession spark = SparkSession.builder().config(conf).getOrCreate();
        Dataset<Gitee> csv = spark.read().csv("/usr/local/data/").as(Encoders.bean(Gitee.class));
        csv.write()
                .mode("Overwrite")
                .format("csv")
                .option("header", true)
                .option("path", "hdfs://master:9000/cloud/gitee/data/all_data.csv");
    }

    static class Gitee {
        String author;
        String repo;
        String level;
        String star;
        String labels;
        String desc;
        String language;
        String type;
        String time;
    }
}
