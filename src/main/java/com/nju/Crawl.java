package com.nju;

import com.nju.consts.Crawls;
import com.nju.utils.CrawlStrategy;
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
        executor.scheduleWithFixedDelay (() -> {
            try {
                long start = System.currentTimeMillis ( );
                CrawlStrategy.crawl (Crawls.GITEEOPENINFOCRAWL);
//            upload();
                long end = System.currentTimeMillis ( );
                System.out.println ((end - start) / 1_000);

            } catch (Exception e) {
            }
        }, 0, 10, TimeUnit.SECONDS);
    }

    private static void upload() {
        SparkConf conf = new SparkConf ( ).setAppName ("crawl").setMaster ("local");
        SparkSession spark = SparkSession.builder ( ).config (conf).getOrCreate ( );

        StructType schema = new StructType ( )
                .add ("author", DataTypes.StringType, true)
                .add ("repo", DataTypes.StringType, true)
                .add ("level", DataTypes.StringType, true)
                .add ("star", DataTypes.StringType, true)
                .add ("labels", DataTypes.StringType, true)
                .add ("desc", DataTypes.StringType, true)
                .add ("language", DataTypes.StringType, true)
                .add ("type", DataTypes.StringType, true)
                .add ("time", DataTypes.StringType, true);

        Dataset<Row> csv = spark
                .read ( )
                .schema (schema)
                .option ("header", true)
                .csv ("data/");
        csv.coalesce (1)
                .write ( )
                .mode (SaveMode.Append)
                .format ("csv")
                .option ("header", true)
                .option ("path", "hdfs://master:9000/cloud/qyl/all_data.csv");
    }
}
