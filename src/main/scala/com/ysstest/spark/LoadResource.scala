package com.ysstest.spark

import java.io.InputStream
import java.util.Properties

/**
  * @author wangshuai
  * @version 2018-11-15 10:48
  *          describe: 
  *          目标文件：
  *          目标表：
  */
object LoadResource {
  private val pro = new Properties()
  pro.load(LoadResource.getClass.getResourceAsStream("/basic.properties"))
  val namenodePath = pro.getProperty("namenode_path")
  val gzInterfaceDir = pro.getProperty("gz_interfacedir")
  val gzOutputDir = pro.getProperty("gz_outputdir")
  val gzBasicList = pro.getProperty("gz_basic_list")
  val user = pro.getProperty("user")
  val password = pro.getProperty("password")
  val driver = pro.getProperty("driver")
  val jdbc = pro.getProperty("jdbc")
  val masterType = pro.getProperty("master_type")
  val properties = pro
}
