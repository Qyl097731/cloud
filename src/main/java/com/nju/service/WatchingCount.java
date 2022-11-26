package com.nju.service;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.function.FilterFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.streaming.OutputMode;
import org.apache.spark.sql.streaming.Trigger;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.apache.spark.sql.functions.*;

/**
 * @description 大家都在看
 * @author: aus
 */
public class WatchingCount {
    static StructType schema = new StructType ( )
            .add ("title", DataTypes.StringType, true)
            .add ("author", DataTypes.StringType, true)
            .add ("language", DataTypes.StringType, true)
            .add ("divider", DataTypes.StringType, true)
            .add ("watch", DataTypes.IntegerType, true)
            .add ("stars", DataTypes.IntegerType, true)
            .add ("fork", DataTypes.IntegerType, true)
            .add ("abstract", DataTypes.StringType, true)
            .add ("key_words", DataTypes.StringType, true)
            .add ("time", DataTypes.StringType, true);
    private static SparkSession session;

    static {
        SparkConf conf = new SparkConf ( ).setMaster ("local").setAppName ("cloud");
        session = SparkSession
                .builder ( )
                .config (conf)
                .getOrCreate ( );
        session.sparkContext ( ).setLogLevel ("ERROR");
    }

    DateFormat format = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) throws TimeoutException {
        System.setProperty ("HADOOP_USER_NAME", "root");
        Dataset<Row> ds = getInputStream ( );
        ds = dataClean (ds);
        ds = aggData (ds);
        display (ds);
    }

    private static void display(Dataset<Row> ds) throws TimeoutException {
        ds.coalesce (1)
                .writeStream ( )
                .outputMode (OutputMode.Complete ( ))
                .format ("console")
                .trigger (Trigger.ProcessingTime (2, TimeUnit.SECONDS))
                .start ( );
    }

    private static Dataset<Row> aggData(Dataset<Row> ds) {
        return ds.withWatermark ("time", "1 seconds")
                .groupBy (window (ds.col ("time"), "1 hours", "2 seconds")
                                .alias ("time")
                        , ds.col ("divider")
                ).agg (count (ds.col ("divider")).alias ("count"));
    }


    private static Dataset<Row> dataClean(Dataset<Row> ds) {
        ds = ds.filter ((FilterFunction<Row>) row -> !row.getString (0).equals ("divider"));

        ds.createOrReplaceTempView ("gitee");
        return session.sql ("select divider,time from gitee")
                .where (ds.col ("time").isNotNull ( ))
                .where ((ds.col ("divider")).isNotNull ( ))
                .where ((ds.col ("divider")).notEqual ("None"))
                .withColumn ("divider",
                        explode (split (ds.col ("divider"), "/")))
                .withColumn ("time", to_timestamp (ds.col ("time")));
    }

    private static Dataset<Row> getInputStream() {
        return session.readStream ( )
                .schema (schema)
                .option ("delimiter", ",")
                .csv ("hdfs://master:9000/cloud/gitee/gitee_data.csv")
                .select ("divider", "time");
    }
}
