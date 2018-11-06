package com.ysstest.test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wangshuai
 * @version 2018-10-24 16:22
 * describe:
 * 目标文件：
 * 目标表：
 */
public class FilterFile {
    /**
     * @param [fileName, prefixList]
     * @return boolean
     * @author wangshuai
     * @date 2018/10/29 13:33
     * @description 通过前缀判断是否是DBF文件
     */
    public static boolean filtrationDbf(String fileName, List<String> prefixList) {
        for (String prefix : prefixList) {
            if (fileName.toLowerCase().startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param [prefixStr]
     * @return java.util.List<java.lang.String>
     * @author wangshuai
     * @date 2018/10/29 13:34
     * @description 分隔字符串
     */
    public static List<String> assemblePrefix(String prefixStr) {

        String[] split = prefixStr.toLowerCase().split(",");
        return new ArrayList<String>(Arrays.asList(split));
    }

    /**
     * @param [regex, fileName]
     * @return boolean
     * @author wangshuai
     * @date 2018/10/29 14:24
     * @description 创建正则实例
     */
    public static boolean filtrationTxt(String regex, String fileName) {
        Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = p.matcher(fileName);
        return matcher.matches();
    }

    private List<File> getChildDirectoryFiles(File directory) {
        List<File> candidateFiles = new ArrayList<File>();
        if (directory == null || !directory.isDirectory()) {
            return candidateFiles;
        }
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                candidateFiles.addAll(getChildDirectoryFiles(file));
            } else {
                candidateFiles.add(file);
            }

        }
        return candidateFiles;
    }

    public static void main(String[] args) {
//        String regex ="(^\\d*otherfund\\d*.txt$)|(^\\d*fundchg\\d*.txt$)|(^\\d*cusfund\\d*.txt$)";
//        String regex = "^\\d*(delivdetails|holddata|trddata|otherfund|fundchg|cusfund)\\d*.txt$";
//        String regex ="^FSD_\\d*_\\d*_\\d*_JY.TXT$";
        String regex ="^RZRQLX_\\d*\\.dbf";
//        String regex ="^FSD_\\d*_\\d*_\\d*_06_\\d*_\\d*.txt$";
//        String regex ="^FSD_\\d*_\\d*_\\d*_04_\\d*_\\d*.txt$";
//        String regex ="(^[a-z].*\\d{6}[a-z].*\\d*00(SPOTMATCH|DEFERMATCH|DEFERDELIVERYAPPMATCH|CLIENTSTORAGEFLOW|ETFAPPLY|MEMBERSEATCAPICATL|CLIENTMISFEEDETAIL|CLIENTSTORAGE|LARGEAMOUNTMATCH)\\.txt$)|(^hkex(clpr|reff)04_\\d*\\.txt$)|(^reff0[4|3]\\d*\\.txt$)|(^cpxx\\d*\\.txt$)";

        Pattern matcher = Pattern.compile(regex);
        List<File> childDirectoryFiles = new FilterFile().getChildDirectoryFiles(new File("C:\\Users\\lxd\\Desktop\\文件前缀文件类型整理数据样例汇总--big\\上传文件样例--big"));
        for (File childDirectoryFile : childDirectoryFiles) {
//            System.out.println(childDirectoryFile.getName());
            if (filtrationTxt(regex, childDirectoryFile.getName())) {
                System.out.println(childDirectoryFile.getName());
            }
//            System.out.println(childDirectoryFile.getName());

        }

    }
}
