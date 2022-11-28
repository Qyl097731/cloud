package com.nju.service;

import com.nju.consts.SchemasConst;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.StreamingQueryException;
import org.apache.spark.sql.streaming.Trigger;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

/**
 * @description 实时统计过去24小时内的最热分类
 * @author: aus
 */
public class DividerAnalyzerService {

    private static SparkSession session;
    private static Dataset<Row> ds;

    public static void run() {
        init ( );
        try {
            analysisHotDivider ( );
        } catch (TimeoutException | StreamingQueryException e) {
            e.printStackTrace ( );
        }
    }

    private static void init() {
        System.setProperty ("HADOOP_USER_NAME", "root");
        SparkConf conf = new SparkConf ( ).setMaster ("local[*]").setAppName ("divider");
        session = SparkSession
                .builder ( )
                .config (conf)
                .getOrCreate ( );
        ds = getInputStream ( );
    }

    private static void analysisHotDivider() throws TimeoutException, StreamingQueryException {
        ds = dataClean (ds);
        ds = aggData (ds);
//        ds.printSchema ( );
//        display (ds);
        save (ds);
    }

    private static void display(Dataset<Row> ds) throws TimeoutException, StreamingQueryException {
        ds.sort (col ("start").asc ( ), col ("count").desc ( )).coalesce (1)
                .coalesce (1)
                .writeStream ( )
                .outputMode (OutputMode.Complete ( ))
                .format ("console")
                .trigger (Trigger.ProcessingTime (2, TimeUnit.MINUTES))
                .start ( )
                .awaitTermination ( );
    }

    private static void save(Dataset<Row> ds) throws TimeoutException, StreamingQueryException {
        ds.coalesce (1)
                .writeStream ( )
                .outputMode (OutputMode.Append ( ))
                .format ("csv")
                .option ("path", "hdfs://master:9000/cloud/data/output/divider/")
                .option ("checkpointLocation", "hdfs://master:9000/cloud/data/output/checkout/")
//                .option ("path", "data/output/divider/")
//                .option ("checkpointLocation", "data/output/checkout/")
                .trigger (Trigger.ProcessingTime (1, TimeUnit.MINUTES))
                .start ( )
                .awaitTermination ( );
        session.close ( );
    }

    private static Dataset<Row> aggData(Dataset<Row> ds) {
        return ds.withWatermark ("time", "1 seconds")
                .groupBy (window (col ("time"), "1 hours", "20 minutes").as ("time")
                        , col ("divider"))
                .agg (count (ds.col ("divider")).as ("count"))
                .withColumn ("start", date_format (col ("time").getField ("start"), "yyyy/MM/dd HH:mm:ss"))
                .withColumn ("end", date_format (col ("time").getField ("end"), "yyyy/MM/dd HH:mm:ss"))
                .drop ("time");
    }

    private static Dataset<Row> dataClean(Dataset<Row> ds) {
        ds.createOrReplaceTempView ("gitee");
        return session.sql ("select divider,time from gitee")
                .where (col ("time").isNotNull ( ))
                .where (col ("time").notEqual ("time"))
                .where ((col ("divider")).isNotNull ( ))
                .where ((col ("divider")).notEqual ("None"))
                .withColumn ("divider",
                        explode (split (col ("divider"), "/")))
                .withColumn ("time", to_timestamp (col ("time")))
                .where (ds.col ("time").between (date_sub (ds.col ("time"), 1), col ("time")));
    }

    private static Dataset<Row> getInputStream() {
        return session.readStream ( )
                .schema (SchemasConst.SCHEMA)
                .option ("delimiter", ",")
                .format ("csv")
                .csv ("hdfs://master:9000/cloud/stream/*/")
//                .csv ("data/distribute/")
                .select ("divider", "time");
    }
}
