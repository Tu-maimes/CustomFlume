package com.ysstest;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author wangshuai
 * @version 2018-10-24 16:22
 * describe:
 * 目标文件：
 * 目标表：
 */
public class FilterFile {

    public static boolean filtration(String fileName, List<String> prefixList) {
        for (String prefix : prefixList) {
            if (fileName.toLowerCase().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    public static List<String> assemblePrefix(String prefixStr) {
        String[] split = prefixStr.toLowerCase().split(",");
        return new ArrayList<String>(Arrays.asList(split));
    }


    private List<File> getChildDirectoryFiles(File directory, FileFilter filter) {
        List<File> candidateFiles = new ArrayList<File>();
        if (directory == null || !directory.isDirectory()) {
            return candidateFiles;
        }
        for (File file : directory.listFiles(filter)) {
            if (file.isDirectory()) {
                candidateFiles.addAll(getChildDirectoryFiles(file, filter));
            } else {
                candidateFiles.add(file);
            }
        }
        return candidateFiles;
    }


    public static void main(String[] args) {
        FilterFile killFile = new FilterFile();
//        String str = "gzlx,zqye,qtsl,zqbd,zqjsxx,hk_jsmx,hk_tzxx,hk_zqbd,hk_zqye,hk_ckhl,abcsj,ywhb,zjhz,wdq,hk_zjbd,hk_zjhz,hk_zjye,jsmxjs,op_bzjzh";
        String str ="doesntfdjfskfksdfkjsfsfskjkfsjdfsj";
        FileFilter fileFilter = new FileFilter() {
            public boolean accept(File candidate) {
                if (candidate.isDirectory()) {
                    String directoryName = candidate.getName();
                    if ((directoryName.startsWith("."))) {
                        return false;
                    }
                    return true;
                } else {
                    String fileName = candidate.getName();
                    if (!Pattern.compile("(.*\\.(DBF|dbf|xml|XML|tsv|TSV|txt|TXT)$)").matcher(fileName).matches() && !killFile.filtration(fileName, killFile.assemblePrefix(str))) {
                        return false;
                    }
                }
                return true;
            }
        };


        List<File> childDirectoryFiles = killFile.getChildDirectoryFiles(new File("C:\\Users\\lxd\\Desktop\\文件前缀文件类型整理数据样例汇总--big"), fileFilter);


        for (File childDirectoryFile : childDirectoryFiles) {
            System.out.println(childDirectoryFile.getName());
        }


    }
}
