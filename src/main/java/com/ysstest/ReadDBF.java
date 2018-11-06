package com.ysstest;

import com.google.common.collect.Lists;
import com.linuxense.javadbf.DBFReader;
import com.ysstest.xmltojson.CDL;
import com.ysstest.xmltojson.JSONArray;
import org.codehaus.jettison.json.JSONException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * @author wangshuai
 * @version 2018-09-13 14:39
 * describe:
 * 目标文件：
 * 目标表：
 */
public class ReadDBF {
    public static List<String> readDBF(String path) {
        InputStream fis = null;
        List<String> eventList = Lists.newLinkedList();

        try {
            // 读取文件的输入流
            fis = new FileInputStream(path);
            // 根据输入流初始化一个DBFReader实例，用来读取DBF文件信息
            DBFReader reader = new DBFReader(fis);
            // 调用DBFReader对实例方法得到path文件中字段的个数
            int fieldsCount = reader.getFieldCount();
            Object[] rowValues;

            // 一条条取出path文件中记录
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < fieldsCount; i++) {
                stringBuffer.append(reader.getField(i).getName());
                stringBuffer.append(",");
            }
            stringBuffer.delete(stringBuffer.length() - 1, stringBuffer.length());
            eventList.add(stringBuffer.toString());
//            System.out.println(stringBuffer.toString());
            while ((rowValues = reader.nextRecord()) != null) {
                System.out.println(((FileInputStream) fis).getChannel().position());

                StringBuffer rowBuffer = new StringBuffer();
//                JSONObject jsonObject = new JSONObject();
                for (int i = 0; i < rowValues.length; i++) {
                    rowBuffer.append(rowValues[i]);
                    rowBuffer.append(",");
//                    jsonObject.put(reader.getField(i).getName(), rowValues[i]);

                }
//                System.out.println(rowBuffer.toString());
                eventList.add(rowBuffer.toString());
//                eventList.add(EventBuilder.withBody(jsonObject.toString(), Charset.forName("utf-8")));
            }
            return eventList;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fis.close();
            } catch (Exception e) {
            }
        }
        return null;
    }

    public static void main(String[] args) throws JSONException, IOException {
        List<String> events = readDBF("C:\\Users\\lxd\\Desktop\\dgh2250120180418.dbf");
        File file = new File("C:\\Users\\lxd\\Desktop\\dgh2250120180418.dbf");
        System.out.println(file.length());
        int a = 0;
        for (String event : events) {
//            System.out.println(event.getBytes().length);
        }
    }

    public static String Json2Csv(String json) throws JSONException {
        JSONArray jsonArray = new JSONArray("[" + json + "]");
        String csv = CDL.toString(jsonArray);
        String[] split = csv.split("\n");
        return split[1];
    }


}

