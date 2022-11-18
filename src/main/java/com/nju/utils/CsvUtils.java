package com.nju.utils;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * @description:
 * @author: qyl
 * @create: 2022-03-04 09:53
 */
public class CsvUtils {

    protected static Logger logger = LoggerFactory.getLogger(CsvUtils.class);

    /**
     * CSV文件生成方法
     *
     * @param head       文件头
     * @param dataList   数据列表
     * @param outPutPath 文件输出路径
     * @param filename   文件名
     * @return
     */
    public static File createCSVFile(List<Object> head, List<Object> dataList, String outPutPath, String filename) {
        File csvFile = null;
        BufferedWriter csvWriter = null;
        try {
            csvFile = new File(outPutPath + File.separator + filename + ".csv");
            File parent = csvFile.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();
            csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), UTF_8), 1024);
            // 写入文件头部
            writeRow(head, csvWriter);
            // 写入文件内容
            writeRow(dataList, csvWriter);
            csvWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (csvWriter != null) {
                    csvWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    /**
     * CSV文件生成方法
     * 追加数据
     *
     * @param dataList   数据列表
     * @param outPutPath 文件输出路径
     * @param filename   文件名
     * @return
     */
    public static File createCSVFile2(List<Object> header, List<List<Object>> dataList, String outPutPath,
                                      String filename) {
        File csvFile = null;
        BufferedWriter csvWriter = null;
        try {
            csvFile = new File(outPutPath + File.separator + filename + ".csv");
            File parent = csvFile.getParentFile();
            if (!parent.exists()) {
                parent.mkdirs();
            }
            csvFile.createNewFile();
            csvWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(
                    csvFile), UTF_8), 1024);
            writeRow(header, csvWriter);
            // 写入文件内容
            for (List<Object> row : dataList) {
                writeRow(row, csvWriter);
            }
            csvWriter.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (csvWriter != null) {
                    csvWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return csvFile;
    }

    /**
     * 写一行数据方法
     *
     * @param row
     * @param csvWriter
     * @throws IOException
     */
    private static void writeRow(List<Object> row, BufferedWriter csvWriter) throws IOException {
        // 写入文件头部
        for (Object data : row) {
            StringBuilder sb = new StringBuilder();
            if (data instanceof JSONArray) {
                data = StringUtils.join(((JSONArray) data).toArray(new Object[0]), ",");
            }
            String rowStr = sb.append("\"").append(data).append("\",").toString();
            csvWriter.write(rowStr);
        }
        csvWriter.newLine();
    }

    /**
     * @param path
     * @return List<String>
     * @description 读取csv文件
     * @author qyl
     * @date 2022/8/11 14:58
     */
    public static List<String[]> readCsvFile(String path) {
        File file = new File(path);
        List<String[]> csvList = new ArrayList<>();
        if (!file.exists()) {
            return null;
        }

        try (BufferedReader in = new BufferedReader(new FileReader(file))) {
            String line = null;
            while ((line = in.readLine()) != null) {
                csvList.add(line.split(","));
            }
        } catch (IOException e) {
            logger.error(e.getMessage());
        }

        return csvList;
    }
}

