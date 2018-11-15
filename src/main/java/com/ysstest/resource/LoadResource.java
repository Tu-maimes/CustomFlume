package com.ysstest.resource;

import com.google.common.base.Preconditions;

import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author wangshuai
 * @version 2018-11-13 17:49
 * describe:
 * 目标文件：
 * 目标表：
 */
public class LoadResource {
    private static Logger logger = Logger.getLogger(LoadResource.class);
    public String namenodePath;
    private String gzInterfaceDir;
    private String gzOutputDir;
    private String gzBasicList;
    private String user;
    private String password;
    private String driver;
    private String jdbc;
    private String masterType;
    private Properties properties;

    /**
     * @param [fileName] 相对Resources的相对路径
     * @return
     * @author wangshuai
     * @date 2018/11/14 8:57
     * @description 通过构造方法初始化
     */
    public LoadResource(String fileName) {
        Preconditions.checkState(fileName != null, "配置文件不允许为空!");
        InputStream resourceAsStream = LoadResource.class.getResourceAsStream(fileName);
        Properties pro = new Properties();
        try {
            pro.load(resourceAsStream);
            logger.info("加载基础配置文件!");
        } catch (IOException e) {
            logger.error("加载基础配置文件出错!   " + e);
        }
        this.namenodePath = pro.getProperty("namenode_path");
        this.gzInterfaceDir = pro.getProperty("gz_interfacedir");
        this.gzOutputDir = pro.getProperty("gz_outputdir");
        this.gzBasicList = pro.getProperty("gz_basic_list");
        this.user = pro.getProperty("user");
        this.password = pro.getProperty("password");
        this.driver = pro.getProperty("driver");
        this.jdbc = pro.getProperty("jdbc");
        this.masterType = pro.getProperty("master_type");
        this.properties = pro;
    }

    public String getNamenodePath() {
        return namenodePath;
    }

    public String getGzInterfaceDir() {
        return gzInterfaceDir;
    }

    public String getGzOutputDir() {
        return gzOutputDir;
    }

    public String getGzBasicList() {
        return gzBasicList;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }

    public String getDriver() {
        return driver;
    }

    public String getJdbc() {
        return jdbc;
    }

    public String getMasterType() {
        return masterType;
    }

    public Properties getProperties() {
        return properties;
    }
}
