package com.nju.service;

import com.nju.utils.CsvUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.nju.consts.SchemasConst.SCHEMA;
import static com.nju.consts.SchemasConst.SOCIAL_SCHEMA;
import static com.nju.utils.SparkUtils.upload;

/**
 * @description 分析最多人看的数据
 * @date:2022/11/27 19:54
 * @author: qyl
 */
public class WatchingAnalyzerService {
    private static SparkSession session;
    private static Dataset<Row> data;
    private static ScheduledExecutorService service;
    private static Set<Integer>[] nums = new Set[3];
    private static Map<Integer, Integer>[] map = new Map[3];
    private static String src = "/root/test/data/output/ranks/";
    private static String filename = "ranks";
    private static String dest = "hdfs://master:9000/cloud/";

    public static void run() {
        init ( );
        service.scheduleWithFixedDelay (WatchingAnalyzerService::analysisSocials
                , 0, 10, TimeUnit.SECONDS);
    }

    private static void init() {
        System.setProperty ("HADOOP_USER_NAME", "root");
        SparkConf conf = new SparkConf ( ).setAppName ("watching").setMaster ("local[*]");
        session = SparkSession.builder ( ).config (conf).getOrCreate ( );
        session.sparkContext ( ).setLogLevel ("ERROR");
        data = getData ( );
        service = Executors.newSingleThreadScheduledExecutor ( );
        for (int i = 0; i < 3; i++) {
            nums[i] = new HashSet<> ( );
            map[i] = new HashMap<> ( );
        }
    }

    private static void analysisSocials() {
        clear ( );
        List<Row> rows = data.collectAsList ( );
        for (Row row : rows) {
            for (int i = 1; i <= 3; i++) {
                nums[i - 1].add (row.getInt (i));
            }
        }
        for (int i = 0; i < 3; i++) {
            int pos = 0;
            for (int num : nums[i]) {
                map[i].put (num, pos++);
            }
        }
        List<List<Object>> data = rows.stream ( ).map (row -> {
            List<Object> content = new ArrayList<> ( );
            content.add (row.get (0));
            for (int i = 0; i < 3; i++) {
                content.add (map[i].get (row.getInt (i + 1)));
            }
            return content;
        }).collect (Collectors.toList ( ));
        CsvUtils.createCSVFile2 (Arrays.asList ("title", "watch", "stars", "fork"), data, src, filename);
        upload (src, filename, dest, SOCIAL_SCHEMA);
    }

    private static void clear() {
        for (int i = 0; i < 3; i++) {
            nums[i].clear ( );
            map[i].clear ( );
        }
    }

    private static Dataset<Row> getData() {
        return session.read ( )
                .schema (SCHEMA)
                .option ("header", true)
                .csv ("hdfs://master:9000/cloud/stream/*/")
//                .csv ("data/distribute/")
                .select ("title", "watch", "stars", "fork");
    }
}
