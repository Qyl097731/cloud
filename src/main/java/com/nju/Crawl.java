package com.nju;

import com.nju.consts.Crawls;
import com.nju.crawls.CrawlMethod;
import com.nju.utils.CrawlStrategy;
import com.nju.utils.CsvUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @description
 * @date:2022/11/8 16:07
 * @author: qyl
 */
public class Crawl {
    public static void main(String[] args) {
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor ( );
        try {
            executor.scheduleWithFixedDelay (() -> {
                long start = System.currentTimeMillis ( );
                CrawlStrategy.crawl (Crawls.GITEEOPENINFOSPIDER);
//                upload ( );
                CsvUtils.createCSVFile2 (CrawlMethod.REPO_HEADER, CrawlMethod.data, "data/", "all_data");
                closeGracefully ( );
                long end = System.currentTimeMillis ( );
                System.out.println ((end - start) / 1_000);
            }, 0, 10000, TimeUnit.SECONDS);
        } catch (Exception e) {
        }
    }

    private static void closeGracefully() {
        CrawlMethod.urls.clear ( );
        CrawlMethod.data.clear ( );
    }

    private static void upload() {
        System.setProperty ("HADOOP_USER_NAME", "root");
        SparkConf conf = new SparkConf ( ).setAppName ("crawl").setMaster ("local");
        SparkSession spark = SparkSession.builder ( ).config (conf).getOrCreate ( );

        StructType schema = new StructType ( )
                .add ("symbol", DataTypes.StringType, true)
                .add ("author", DataTypes.StringType, true)
                .add ("repo", DataTypes.StringType, true)
                .add ("watch", DataTypes.StringType, true)
                .add ("star", DataTypes.StringType, true)
                .add ("fork", DataTypes.StringType, true)
                .add ("desc", DataTypes.StringType, true)
                .add ("language", DataTypes.StringType, true)
                .add ("time", DataTypes.StringType, true);

        Dataset<Row> csv = spark
                .read ( )
                .schema (schema)
                .option ("header", true)
                .csv ("data/");
        csv.coalesce (1)
                .write ( )
                .mode (SaveMode.Overwrite)
                .option ("header", true)
                .csv ("hdfs://master:9000/cloud/gitee/all_data.csv");
    }
}
