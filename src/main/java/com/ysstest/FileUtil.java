package com.ysstest;

import java.io.File;

/**
 * @author wangshuai
 * @version 2018-10-19 17:38
 * describe:
 * 目标文件：
 * 目标表：
 */
public class FileUtil {
    public static boolean deleteDir(String path) {
        File dir = new File(path);
        if (dir.exists()) {
            for (File f : dir.listFiles()) {
                if (f.isDirectory()) {
                    deleteDir(f.getName());
                } else {
                    f.delete();
                }
            }
            dir.delete();
            return true;
        } else {
            System.out.println("文件(夹)不存在!");
            return false;
        }
    }
}