package com.ysstest;

import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;

import java.io.File;
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
    //    private static List<Integer> listFour = Arrays.asList(16, 8, 3, 16, 8, 16, 8, 16, 6, 8, 4, 17, 9, 3, 12, 16, 1, 4, 8, 10, 10, 16, 7, 9, 10, 16, 1, 16, 20, 16, 16, 10, 1, 1, 10, 16, 1, 24, 16, 16, 12, 12, 12, 18, 18);

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
                    Event event = EventBuilder.withBody(Transcoding.gbkToUTF(rowData),Charset.forName("utf-8"));
                    event.getHeaders().put(currentRecord, String.valueOf(ROW));
                    contentData.setLength(0);
                    return event;
                } else {
                    for (String g : fieldByteList) {
                        int i = Integer.parseInt(g);
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
        Event event = EventBuilder.withBody(Transcoding.gbkToUTF(contentData.toString()),Charset.defaultCharset());
        event.getHeaders().put(currentRecord, String.valueOf(ROW));
        contentData.setLength(0);
        return event;
    }

    public Event readFSDFour(List<String> fieldByteList) throws IOException {
        if (ROW == 0) {
            if (head) {
                Event event = EventBuilder.withBody(Transcoding.gbkToUTF(readHead()),Charset.defaultCharset());
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

    public static void main(String[] args) throws IOException {

//        StringBuffer stringBuffer = new StringBuffer();
        String s = "24,24,8,3,16,16,6,1,8,6,4,17,9,16,16,3,12,20,1,5,4,10,10,7,9,24,8,10,1,16,9,16,20,60,9,9,17,4,1,16,10,16,16,7,8,1,16,7,7,16,16,8,10,1,1,1,1,8,1,6,10,12,2,9,1,1,1,2,2,5,6,7,16,16,16,16,1,20,16,1,16,16,16,60,16,16,12,12,12,18,18";
        List<String> strings = Arrays.asList(s.split(","));
        List<Integer> fourList = Arrays.asList(24, 24, 8, 3, 16, 16, 6, 1, 8, 6, 4, 17, 9, 16, 16, 3, 12, 20, 1, 5, 4, 10, 10, 7, 9, 24, 8, 10, 1, 16, 9, 16, 20, 60, 9, 9, 17, 4, 1, 16, 10, 16, 16, 7, 8, 1, 16, 7, 7, 16, 16, 8, 10, 1, 1, 1, 1, 8, 1, 6, 10, 12, 2, 9, 1, 1, 1, 2, 2, 5, 6, 7, 16, 16, 16, 16, 1, 20, 16, 1, 16, 16, 16, 60, 16, 16, 12, 12, 12, 18, 18);
//        List<Integer> jYList = Arrays.asList(64, 3, 24, 24, 12, 12, 12, 1, 8, 6, 1, 1, 80, 6, 16, 16, 3, 9, 9, 17, 12, 2, 60, 6, 1, 1, 1, 1, 1, 6, 120, 80, 30, 80, 20, 22, 24, 40, 24, 20, 30, 1, 8, 120, 6, 22, 24, 40, 24, 20, 30, 1, 8, 120, 6, 120, 80, 30, 60, 20, 22, 24, 40, 24, 20, 18, 17, 12, 8, 24, 8, 20, 1, 2, 40, 3, 4, 1, 60, 20, 20, 30, 16, 16);
//        List<Integer> sixList = Arrays.asList(16, 8, 3, 16, 8, 16, 8, 16, 6, 8, 4, 17, 9, 3, 12, 16, 1, 4, 8, 10, 10, 16, 7, 9, 10, 16, 1, 16, 20, 16, 16, 10, 1, 1, 10, 16, 1, 24, 16, 16, 12, 12, 12, 18, 18);
//        File file = new File("C:\\Users\\lxd\\Desktop\\20180110\\FSD_100_102000000002_20180110_JY.TXT");
        File file = new File("C:\\Users\\lxd\\Desktop\\文件前缀文件类型整理数据样例汇总--big\\上传文件样例--big\\FSD_100_10101_20180125_04_98_30101.txt");
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
//        while (randomAccessFile.getFilePointer() != file.length()) {
//            String dataLine = randomAccessFile.readLine();
//            StringBuffer buffer = new StringBuffer();
//            if (row == 10) {
//                Integer integer = Integer.valueOf(dataLine);
//                for (int a = 0; a < integer; a++) {
//                    dataLine = randomAccessFile.readLine();
//                    buffer.append(dataLine);
//                    buffer.append(",");
////                    System.out.println(dataLine);
//                }
//                System.out.println(buffer.toString());
//            } else if (row == 11) {
//                Integer integer = Integer.valueOf(dataLine);
//                for (int a = 0; a < integer; a++) {
//                    int b = 0;
//                    String s = new String(randomAccessFile.readLine().getBytes("iso-8859-1"), Charset.forName("gbk"));
////                    System.out.println("FSD_100_10101_20180125_04_98_30101.txt    每行数据的长度:    "+s.getBytes().length);
//                    byte[] gbks = s.getBytes("gbk");
////System.out.println(gbks.length);
////System.out.println(s.length());
//                    for (Integer i : jYList) {
//                        byte[] bytes = new byte[i];
//                        if (b <= gbks.length) {
//                            System.arraycopy(gbks, b, bytes, 0, i);
//                            stringBuffer.append(new String(bytes, Charset.forName("gbk")));
//                            stringBuffer.append(",");
//                            b += i;
//                        } else {
//                            stringBuffer.append(",");
//                        }
//                    }
//                    System.out.println(stringBuffer.toString());
//                    stringBuffer.setLength(0);
//                }
//            }
//            row++;
//        }

        ReadFsdTxt dd = new ReadFsdTxt(randomAccessFile, "dd", ",", 1, true);


        for (int a = 0; a < 15; a++) {
            Event event = dd.readFSDFour(strings);
            if (event != null) {
                System.out.println(new String(event.getHeaders().get("dd")) + new String(event.getBody()));
            } else {
                break;
            }
        }


    }
}
