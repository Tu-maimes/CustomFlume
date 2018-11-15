package com.ysstest;

import com.ysstest.resource.LoadResource;

/**
 * @author wangshuai
 * @version 2018-09-28 09:31
 * describe:
 * 目标文件：
 * 目标表：
 */
public class RunMain {
    public static void main(String[] args) throws Exception {


        LoadResource loadResource = new LoadResource("/basic.properties");

        String jdbc = loadResource.getJdbc();
        System.out.println(jdbc);
    }
}
