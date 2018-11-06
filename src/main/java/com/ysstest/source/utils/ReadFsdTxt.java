package com.ysstest.source.utils;

import com.ysstest.file.EncodingDetect;
import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.Charset;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author wangshuai
 * @version 2018-10-30 10:14
 * describe:
 * 目标文件：
 * 目标表：
 */
public class ReadFsdTxt {
    private long ROW = 0;
    private RandomAccessFile randomAccessFile;
    private StringBuffer contentData = new StringBuffer();
    private String currentRecord;
    private String csvSeparator;
    private int eventLines;
    private boolean head;

    public ReadFsdTxt(RandomAccessFile randomAccessFile, String currentRecord, String csvSeparator, int eventLines, boolean head) {
        this.randomAccessFile = randomAccessFile;
        this.currentRecord = currentRecord;
        this.csvSeparator = csvSeparator;
        this.eventLines = eventLines;
        this.head = head;
    }

    /**
     * @param []
     * @return void
     * @author wangshuai
     * @date 2018/10/30 17:33
     * @description 排除前9行数据
     */
    private void excludeLineFileHead() {
        for (int a = 1; a < 10; a++) {
            try {
                randomAccessFile.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param []
     * @return java.lang.String
     * @author wangshuai
     * @date 2018/10/30 17:46
     * @description 读取字段信息
     */
    private String readHead() throws IOException {
        excludeLineFileHead();
        String dataLine = randomAccessFile.readLine();
        Integer integer = Integer.valueOf(dataLine);
        for (int a = 0; a < integer; a++) {
            contentData.append(randomAccessFile.readLine());
            contentData.append(csvSeparator);
        }
        if (contentData.length() > 1) {
            contentData.delete(contentData.length() - 1, contentData.length());
        } else {
            System.out.println(LocalDateTime.now() + "    空白文件!");
            return null;
        }
        ROW++;
        return contentData.toString();
    }

    private Event readContent(List<String> fieldByteList) throws IOException {
        for (int a = 0; a < eventLines; a++) {
            int b = 0;
            String rowData = randomAccessFile.readLine();
            if (rowData != null) {
                rowData = new String(rowData.getBytes("iso-8859-1"), Charset.forName("gbk"));
                byte[] gbks = rowData.getBytes("gbk");
                if (gbks.length == 8) {
                    if (contentData.length() > 1) {
                        contentData.delete(contentData.length() - 1, contentData.length());
                    } else {
                        return null;
                    }
//                    contentData.append(rowData);
                    Event event = EventBuilder.withBody(Transcoding.gbkToUTF(contentData.toString()));
                    event.getHeaders().put(currentRecord, String.valueOf(ROW));
                    contentData.setLength(0);
                    return event;
                } else {
                    for (String f : fieldByteList) {
                        int i = Integer.parseInt(f);
                        byte[] bytes = new byte[i];
                        if (b <= gbks.length) {
                            System.arraycopy(gbks, b, bytes, 0, i);
                            contentData.append(new String(bytes, Charset.forName("gbk")));
                            contentData.append(csvSeparator);
                            b += i;
                        } else {
                            contentData.append(csvSeparator);
                        }
                    }
                    contentData.replace(contentData.length() - 1, contentData.length(), "\n");
                    ROW++;
                }

            } else {
                System.out.println("文件读取结束");
                break;
            }
        }
        if (contentData.length() > 1) {
            contentData.delete(contentData.length() - 1, contentData.length());
        } else {
            return null;
        }
        Event event = EventBuilder.withBody(Transcoding.gbkToUTF(contentData.toString()));
        event.getHeaders().put(currentRecord, String.valueOf(ROW));
        contentData.setLength(0);
        return event;
    }

    public Event readFSDFour(List<String> fieldByteList) throws IOException {
        if (ROW == 0) {
            if (head) {
                Event event = EventBuilder.withBody(Transcoding.gbkToUTF(readHead()));
                event.getHeaders().put(currentRecord, String.valueOf(ROW));
                //跳过数据记录数
                randomAccessFile.readLine();
                contentData.setLength(0);
                return event;
            } else {
                readHead();
                randomAccessFile.readLine();
                contentData.setLength(0);
                return readContent(fieldByteList);
            }
        } else {
            return readContent(fieldByteList);
        }
    }

    public Event readTxt(String defaultSeparator) throws IOException {
        for (int a = 0; a < eventLines; a++) {
            String data = randomAccessFile.readLine();
            if (data != null) {
                contentData.append(data);
                contentData.append("\n");
            } else {
                break;
            }
        }
        if (contentData.length() > 1) {
            contentData.delete(contentData.length() - 1, contentData.length());
        } else {
            return null;
        }
        Event event = EventBuilder.withBody(Transcoding.transcodByte(contentData.toString().replaceAll(defaultSeparator, csvSeparator)));
        event.getHeaders().put(currentRecord, String.valueOf(ROW));
        contentData.setLength(0);
        return event;
    }


    public static void main(String[] args) throws IOException {
        RandomAccessFile randomAccessFile = new RandomAccessFile("C:\\Users\\lxd\\Desktop\\文件前缀文件类型整理数据样例汇总--big\\上传文件样例--big\\I016101S17031000DEFERMATCH.TXT", "r");
        ReadFsdTxt dd = new ReadFsdTxt(randomAccessFile, "dd", ",", 9, true);
        String s = "24,24,8,3,16,16,6,1,8,6,4,17,9,16,16,3,12,20,1,5,4,10,10,7,9,24,8,10,1,16,9,16,20,60,9,9,17,4,1,16,10,16,16,7,8,1,16,7,7,16,16,8,10,1,1,1,1,8,1,6,10,12,2,9,1,1,1,2,2,5,6,7,16,16,16,16,1,20,16,1,16,16,16,60,16,16,12,12,12,18,18";
        String fileEncode = EncodingDetect.getJavaEncode("C:\\Users\\lxd\\Desktop\\文件前缀文件类型整理数据样例汇总--big\\上传文件样例--big\\FSD_100_102000000002_20180124_JY.TXT");
        System.out.println(fileEncode);
        while (true) {
            String s1 = randomAccessFile.readLine();
            System.out.println(s1);
            if (s1 == null) {
                break;
            }
        }

//        randomAccessFile.seek(randomAccessFile.length() - 10);
//        String s1 = randomAccessFile.readLine();
//        System.out.println(s1);


//        for (int a = 0; a < 15; a++) {
//            Event event = dd.readFSDFour(Arrays.asList(s.split(",")));
//            if (event != null) {
//                System.out.println(new String(event.getHeaders().get("dd")) + new String(event.getBody()));
//            } else {
//                break;
//            }
//        }

    }
}
