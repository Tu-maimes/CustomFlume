package com.ysstest;

import com.linuxense.javadbf.DBFField;
import com.linuxense.javadbf.DBFReader;
import com.linuxense.javadbf.DBFWriter;
import com.ysstest.file.EncodingDetect;
import com.ysstest.source.utils.ReadDbf;
import net.sf.jmimemagic.Magic;
import net.sf.jmimemagic.MagicMatch;
import org.apache.flume.Event;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

/**
 * @author wangshuai
 * @version 2018-09-28 09:31
 * describe:
 * 目标文件：
 * 目标表：
 */
public class RunMain {
    public static void writeDBF(String path) throws IOException {
        OutputStream fos = new FileOutputStream(path);
        DBFField[] fields = new DBFField[3];
        DBFWriter writer = new DBFWriter();
        fields[0] = new DBFField();
        // fields[0].setFieldName("emp_code");
        fields[0].setName("semp_code");
        fields[0].setDataType(DBFField.FIELD_TYPE_C);
        fields[0].setFieldLength(10);
        fields[1] = new DBFField();
        // fields[1].setFieldName("emp_name");
        fields[1].setName("emp_name");
        fields[1].setDataType(DBFField.FIELD_TYPE_C);
        fields[1].setFieldLength(20);
        fields[2] = new DBFField();
        // fields[2].setFieldName("salary");
        fields[2].setName("salary");
        fields[2].setDataType(DBFField.FIELD_TYPE_N);
        fields[2].setFieldLength(12);
        fields[2].setDecimalCount(2);
        writer.setFields(fields);
        for (int a = 0; a < 500000; a++) {
            try {
                // 定义DBF文件字段

                // 分别定义各个字段信息，setFieldName和setName作用相同,
                // 只是setFieldName已经不建议使用

                // DBFWriter writer = new DBFWriter(new File(path));
                // 定义DBFWriter实例用来写DBF文件

                // 把字段信息写入DBFWriter实例，即定义表结构

                // 一条条的写入记录
                Object[] rowData = new Object[3];
                rowData[0] = "1000";
                rowData[1] = "John";
                rowData[2] = new Double(5000.00);
                writer.addRecord(rowData);
                rowData = new Object[3];
                rowData[0] = "1001";
                rowData[1] = "Lalit";
                rowData[2] = new Double(3400.00);
                writer.addRecord(rowData);
                rowData = new Object[3];
                rowData[0] = "1002";
                rowData[1] = "Rohit";
                rowData[2] = new Double(7350.00);
                writer.addRecord(rowData);
                // 定义输出流，并关联的一个文件

                // 写入数据

                // writer.write();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        writer.write(fos);
        fos.close();
    }

    private static final HashMap<String, String> mFileTypes = new HashMap<String, String>();

    static {
        mFileTypes.put("FFD8FFE000104A464946", "jpg"); //JPEG (jpg)
        mFileTypes.put("89504E470D0A1A0A0000", "png"); //PNG (png)
        mFileTypes.put("47494638396126026F01", "gif"); //GIF (gif)
        mFileTypes.put("49492A00227105008037", "tif"); //TIFF (tif)
        mFileTypes.put("424D228C010000000000", "bmp"); //16色位图(bmp)
        mFileTypes.put("424D8240090000000000", "bmp"); //24位位图(bmp)
        mFileTypes.put("424D8E1B030000000000", "bmp"); //256色位图(bmp)
        mFileTypes.put("41433130313500000000", "dwg"); //CAD (dwg)
        mFileTypes.put("3C21444F435459504520", "html"); //HTML (html)
        mFileTypes.put("3C21646F637479706520", "htm"); //HTM (htm)
        mFileTypes.put("48544D4C207B0D0A0942", "css"); //css
        mFileTypes.put("696B2E71623D696B2E71", "js"); //js
        mFileTypes.put("7B5C727466315C616E73", "rtf"); //Rich Text Format (rtf)
        mFileTypes.put("38425053000100000000", "psd"); //Photoshop (psd)
        mFileTypes.put("46726F6D3A203D3F6762", "eml"); //Email [Outlook Express 6] (eml)
        mFileTypes.put("D0CF11E0A1B11AE10000", "doc"); //MS Excel 注意：word、msi 和 excel的文件头一样
        mFileTypes.put("D0CF11E0A1B11AE10000", "vsd"); //Visio 绘图
        mFileTypes.put("5374616E64617264204A", "mdb"); //MS Access (mdb)
        mFileTypes.put("252150532D41646F6265", "ps");
        mFileTypes.put("255044462D312E350D0A", "pdf"); //Adobe Acrobat (pdf)
        mFileTypes.put("2E524D46000000120001", "rmvb"); //rmvb/rm相同
        mFileTypes.put("464C5601050000000900", "flv"); //flv与f4v相同
        mFileTypes.put("00000020667479706D70", "mp4");
        mFileTypes.put("49443303000000002176", "mp3");
        mFileTypes.put("000001BA210001000180", "mpg"); //
        mFileTypes.put("3026B2758E66CF11A6D9", "wmv"); //wmv与asf相同
        mFileTypes.put("52494646E27807005741", "wav"); //Wave (wav)
        mFileTypes.put("52494646D07D60074156", "avi");
        mFileTypes.put("4D546864000000060001", "mid"); //MIDI (mid)
        mFileTypes.put("504B0304140000000800", "zip");
        mFileTypes.put("526172211A0700CF9073", "rar");
        mFileTypes.put("235468697320636F6E66", "ini");
        mFileTypes.put("504B03040A0000000000", "jar");
        mFileTypes.put("4D5A9000030000000400", "exe");//可执行文件
        mFileTypes.put("3C25402070616765206C", "jsp");//jsp文件
        mFileTypes.put("4D616E69666573742D56", "mf");//MF文件
        mFileTypes.put("3C3F786D6C2076657273", "xml");//xml文件
        mFileTypes.put("494E5345525420494E54", "sql");//xml文件
        mFileTypes.put("7061636B616765207765", "java");//java文件
        mFileTypes.put("406563686F206F66660D", "bat");//bat文件
        mFileTypes.put("1F8B0800000000000000", "gz");//gz文件
        mFileTypes.put("6C6F67346A2E726F6F74", "properties");//bat文件
        mFileTypes.put("CAFEBABE0000002E0041", "class");//bat文件
        mFileTypes.put("49545346030000006000", "chm");//bat文件
        mFileTypes.put("04000000010000001300", "mxp");//bat文件
        mFileTypes.put("504B0304140006000800", "docx");//docx文件
        mFileTypes.put("D0CF11E0A1B11AE10000", "wps");//WPS文字wps、表格et、演示dps都是一样的
        mFileTypes.put("6431303A637265617465", "torrent");
        mFileTypes.put("6D6F6F76", "mov"); //Quicktime (mov)
        mFileTypes.put("FF575043", "wpd"); //WordPerfect (wpd)
        mFileTypes.put("CFAD12FEC5FD746F", "dbx"); //Outlook Express (dbx)
        mFileTypes.put("2142444E", "pst"); //Outlook (pst)
        mFileTypes.put("AC9EBD8F", "qdf"); //Quicken (qdf)
        mFileTypes.put("E3828596", "pwl"); //Windows Password (pwl)
        mFileTypes.put("2E7261FD", "ram"); //Real Audio (ram)
    }

    /**
     * @param filePath 文件路径
     * @return 文件头信息
     * @author wlx
     * <p>
     * 方法描述：根据文件路径获取文件头信息
     */
    public static String getFileType(String filePath) {
//		System.out.println(getFileHeader(filePath));
//		System.out.println(mFileTypes.get(getFileHeader(filePath)));
        return mFileTypes.get(getFileHeader(filePath));
    }

    /**
     * @param filePath 文件路径
     * @return 文件头信息
     * @author wlx
     * <p>
     * 方法描述：根据文件路径获取文件头信息
     */
    public static String getFileHeader(String filePath) {
        FileInputStream is = null;
        String fileCode = null;
        try {
            is = new FileInputStream(filePath);
            byte[] b = new byte[10];
            /*
             * int read() 从此输入流中读取一个数据字节。int read(byte[] b) 从此输入流中将最多 b.length
             * 个字节的数据读入一个 byte 数组中。 int read(byte[] b, int off, int len)
             * 从此输入流中将最多 len 个字节的数据读入一个 byte 数组中。
             */
            is.read(b, 0, b.length);
            fileCode = bytesToHexString(b);
            Iterator<String> keys = mFileTypes.keySet().iterator();
            while (keys.hasNext()) {
                String key = keys.next();
                if (key.startsWith(fileCode)) {
                    fileCode = key;
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return fileCode;
    }

    /**
     * @param src 要读取文件头信息的文件的byte数组
     * @return 文件头信息
     * @author wlx
     * <p>
     * 方法描述：将要读取文件头信息的文件的byte数组转换成string类型表示
     */
    private static String bytesToHexString(byte[] src) {
        StringBuilder builder = new StringBuilder();
        if (src == null || src.length <= 0) {
            return null;
        }
        String hv;
        for (byte aSrc : src) {
            // 以十六进制（基数 16）无符号整数形式返回一个整数参数的字符串表示形式，并转换为大写
            hv = Integer.toHexString(aSrc & 0xFF).toUpperCase();
            if (hv.length() < 2) {
                builder.append(0);
            }
            builder.append(hv);
        }
//		System.out.println(builder.toString());
        return builder.toString();
    }


    public static void main(String[] args) throws Exception {
        FileInputStream fileInputStream = new FileInputStream("E:\\work\\document\\project_doc\\估值\\数据\\szhk_tzxx0922.dbf");
        String javaEncode = EncodingDetect.getJavaEncode("E:\\work\\document\\project_doc\\估值\\数据\\szhk_tzxx0922.dbf");
        ReadDbf dd = new ReadDbf(fileInputStream, "dd", "\t", 50, false);
        Event event = dd.readDBFFile();
        System.out.println(new String(event.getBody()));

    }
}
