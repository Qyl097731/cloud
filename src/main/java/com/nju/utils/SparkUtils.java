package com.nju.utils;

import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.StructType;

import java.io.File;

/**
 * @description
 * @date:2022/11/27 23:15
 * @author: qyl
 */
public class SparkUtils {

    public static void upload(String src, String filename, String dest, StructType schema) {
        System.setProperty ("HADOOP_USER_NAME", "root");
        SparkConf conf = new SparkConf ( ).setAppName ("cloud").setMaster ("local");
        SparkSession spark = SparkSession.builder ( ).config (conf).getOrCreate ( );

        String path = src + File.separator + filename + ".csv";
        Dataset<Row> csv = spark
                .read ( )
                .schema (schema)
                .option ("header", true)
                .csv (path);
        csv.coalesce (1)
                .write ( )
                .mode (SaveMode.Overwrite)
                .option ("header", true)
                .csv (dest + filename);
    }
}
