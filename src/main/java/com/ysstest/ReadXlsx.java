package com.ysstest;

import org.apache.flume.Event;
import org.apache.flume.event.EventBuilder;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.time.LocalDateTime;

/**
 * @author wangshuai
 * @version 2018-10-24 09:00
 * describe:
 * 目标文件：
 * 目标表：
 */
public class ReadXlsx {
    private int RowIndex;
    private static StringBuffer buffer = new StringBuffer();
    private String currentRecord;
    private String csvSeparator;
    private Sheet sheet;
    private int eventLines;
    private final int lastRowIndex;

    public ReadXlsx(Workbook wb, String currentRecord, String csvSeparator, int eventLines, Boolean head) {
        this.currentRecord = currentRecord;
        this.csvSeparator = csvSeparator;
        this.eventLines = eventLines;
        this.sheet = wb.getSheetAt(0);
        this.lastRowIndex = this.sheet.getLastRowNum();
        //ture取头数据   false 不取头数据
        if (head) {
            this.RowIndex = this.sheet.getFirstRowNum();
        } else {
            this.RowIndex = this.sheet.getFirstRowNum() + 1;
        }

    }

    public void readRow() {
        if (RowIndex <= lastRowIndex) {
            Row row = sheet.getRow(RowIndex);
            if (row != null) {
                int firstCellIndex = row.getFirstCellNum();
                int lastCellIndex = row.getLastCellNum();
                for (int cIndex = firstCellIndex; cIndex < lastCellIndex; cIndex++) {   //遍历列
                    Cell cell = row.getCell(cIndex);
                    if (cell != null) {
                        buffer.append(cell.toString().replaceAll("\n", " "));
                        buffer.append(csvSeparator);
                    } else {
                        buffer.append(csvSeparator);
                    }
                }
                buffer.replace(buffer.length() - 1, buffer.length(), "\n");
            }
        }
        RowIndex++;
    }


    public Event readRows() {
        if (RowIndex == 0) {
            readRow();
            if (buffer.length() > 1) {
                buffer.delete(buffer.length() - 1, buffer.length());
            } else {
                System.out.println(LocalDateTime.now() + "    空白文件!");
                return null;
            }
            Event event = EventBuilder.withBody(buffer.toString(), Charset.forName("utf-8"));
            event.getHeaders().put(currentRecord, String.valueOf(RowIndex));
            buffer.setLength(0);
            return event;
        } else {
            for (int a = 0; a < eventLines; a++) {
                readRow();
            }
            if (buffer.length() > 1) {
                buffer.delete(buffer.length() - 1, buffer.length());
            } else {
                return null;
            }
            Event event = EventBuilder.withBody(buffer.toString(), Charset.forName("utf-8"));
            event.getHeaders().put(currentRecord, String.valueOf(RowIndex));
            buffer.setLength(0);
            return event;
        }
    }

    public static void main(String[] args) throws IOException {
        FileInputStream fiStream = null;
        File excel = new File("C:\\Users\\lxd\\Desktop\\新建 XLS 工作表.xls");
        String[] split = excel.getName().split("\\.");  //.是特殊字符，需要转义！
        Workbook wb = null;
        //根据文件后缀（xls/xlsx）进行判断
        if ("xls".equals(split[1])) {
            fiStream = new FileInputStream(excel);   //文件流对象
            wb = new HSSFWorkbook(fiStream);
        } else {
            wb = new XSSFWorkbook(new FileInputStream(excel));
        }
        ReadXlsx readXlsx = new ReadXlsx(wb, "dd", "\t", 20, true);
        for (int a = 0; a < 5; a++) {
            if (fiStream != null) {


            }

            Event event = readXlsx.readRows();
            if (event != null) {
                int length = new String(event.getBody(), Charset.forName("utf-8")).length();
//                System.out.println("每次读取的数据量" + length);
                System.out.println(new String(event.getBody()));
            } else {
                System.out.println(excel.length());
            }

        }

    }
}