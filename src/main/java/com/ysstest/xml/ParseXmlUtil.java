package com.ysstest.xml;

import com.alibaba.fastjson.JSON;
import com.ysstest.file.EncodingDetect;
import com.ysstest.xmltojson.CDL;
import com.ysstest.xmltojson.JSONArray;
import com.ysstest.xmltojson.JSONObject;
import com.ysstest.xmltojson.XML;
import org.apache.commons.io.FileUtils;
import org.dom4j.*;
import org.dom4j.io.SAXReader;
import org.json4s.jackson.Json;

import java.io.*;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.List;

/**
 * @author wangshuai
 * @version 2018-11-01 10:35
 * describe:
 * 目标文件：
 * 目标表：
 */
public class ParseXmlUtil {
    private StringBuffer dataBuffer = new StringBuffer();
    private StringBuffer headbuffer = new StringBuffer();
    //    private static SAXReader saxReader = new SAXReader();
//    private Document document;
    private long line = 1;
    private boolean headFile;
//    private Element rootElement;


    public ParseXmlUtil(File file, boolean headFile) {
        this.headFile = headFile;

    }

    private void readXml(String secondaryNode, String threeNode, Element rootElement) {
//        List<Element> secondaryElements = rootElement.elements(secondaryNode);
        List<Element> secondaryElements = rootElement.elements();
        for (Element secondaryElement : secondaryElements) {
            List<Element> secondaryList = secondaryElement.elements();
            for (Element secondaryData : secondaryList) {
                if (secondaryData.getName().equalsIgnoreCase(threeNode)) {
                    for (Element threeData : secondaryData.elements()) {
                        if (line == 1) {
                            headbuffer.append(threeData.getName().replaceAll("\n", ""));
                            headbuffer.append(",");
                        }
                        dataBuffer.append(threeData.getText().replaceAll("\n", ""));
                        dataBuffer.append(",");
                    }
                } else {
                    if (line == 1) {
                        headbuffer.append(secondaryData.getName().replaceAll("\n", ""));
                        headbuffer.append(",");
                    }
                    dataBuffer.append(secondaryData.getText().replaceAll("\n", ""));
                    dataBuffer.append(",");
                }
            }
            line++;
//            headbuffer.replace(dataBuffer.length() - 1, dataBuffer.length(), "\n");
            dataBuffer.replace(dataBuffer.length() - 1, dataBuffer.length(), "\n");

        }
//        System.out.println(headbuffer.toString().replaceAll(" ", ""));
        System.out.println(dataBuffer.toString().replaceAll(" ", ""));
    }


    private void readXmltest(Element rootElement) {
        List<Element> secondaryElements = rootElement.elements();
        for (Element secondaryElement : secondaryElements) {
            List<Element> secondaryList = secondaryElement.elements();
            if (secondaryList.isEmpty()) {
                dataBuffer.append(secondaryElement.getName().replaceAll("\n", "") + "---" + secondaryElement.getText().replaceAll("\n", ""));
                dataBuffer.append(",");
            } else {
                readXmltest(secondaryElement);
            }
        }
    }

    private void readXmlt(Element rootElement) {
        List<Element> secondaryElements = rootElement.elements();
        Iterator<Element> iterator = secondaryElements.iterator();
        while (iterator.hasNext()) {
            Element next = iterator.next();
            List<Element> elements = next.elements();
            if (elements.isEmpty()) {
                dataBuffer.append(next.getName().replaceAll("\n", "") + "---" + next.getText().replaceAll("\n", ""));
                dataBuffer.append(",");
            } else {
                readXmltest(next);
            }
            if (dataBuffer.length() > 1) {
                dataBuffer.replace(dataBuffer.length() - 1, dataBuffer.length(), "\n");
            }
        }
        System.out.println(dataBuffer.toString().replaceAll(" ", ""));
    }


    public static void main(String[] args) throws DocumentException {
//        "C:\\Users\\lxd\\Desktop\\securities_20180730.XML"

        File file = new File("C:\\Users\\lxd\\Desktop\\securities_20180730.XML");


        ParseXmlUtil parseXmlUtil = new ParseXmlUtil(file, true);
        SAXReader saxReader = new SAXReader();
        Document read = saxReader.read(file);
        Element element = read.getRootElement();
        parseXmlUtil.readXmlt(element);


    }

}
