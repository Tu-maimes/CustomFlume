package com.ysstest.test;

import com.ysstest.source.utils.ReadFsdTxt;
import com.ysstest.source.utils.RenameDir;
import org.apache.commons.lang3.StringUtils;
import org.apache.flume.Event;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.HashMap;

/**
 * @author wangshuai
 * @version 2018-11-05 09:10
 * describe:
 * 目标文件：
 * 目标表：
 */
public class RegexTest {
    public static void main(String[] args) throws IOException {
//        RandomAccessFile randomAccessFile = new RandomAccessFile("C:\\Users\\lxd\\Desktop\\文件前缀文件类型整理数据样例汇总--big\\上传文件样例--big\\FSD_100_10101_20180125_04_98_30101.txt", "r");
//        ReadFsdTxt readDbf = new ReadFsdTxt(randomAccessFile, "dd", ",", 10, true);
//        String st = "24,24,8,3,16,16,6,1,8,6,4,17,9,16,16,3,12,20,1,5,4,10,10,7,9,24,8,10,1,16,9,16,20,60,9,9,17,4,1,16,10,16,16,7,8,1,16,7,7,16,16,8,10,1,1,1,1,8,1,6,10,12,2,9,1,1,1,2,2,5,6,7,16,16,16,16,1,20,16,1,16,16,16,60,16,16,12,12,12,18,18";
//        Event event = readDbf.readFSDFour(Arrays.asList(st.split(",")));
//        String d = event.getHeaders().get("dd");
//        System.out.println(d);
//       new RenameDir().rename(event, "2018/FSD_100_10101_20180125_04_98_30101.txt", "bb");
//        String s = event.getHeaders().get("bb");
//        System.out.println(s);
//        boolean f = FilterFile.filtrationTxt("^FSD_\\d*_\\d*_\\d*_04_\\d*_\\d*\\.txt$", "FSD_100_10101_20180125_04_98_30101.txt");
//        System.out.println(f);
        String s = "\\data\\gz_interface\\20181105";
        File file = new File(s);
        String parent = file.getParent();
        System.out.println(parent);
        String s1 = file.getPath().replace(parent, "");
        System.out.println(s1.split("\\\\").length);
        System.out.println(s1);


    }
}
