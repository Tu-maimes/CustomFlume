package com.ysstest;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.read.biff.BiffException;

/**
 * @author wangshuai
 * @version 2018-10-23 17:43
 * describe:
 * 目标文件：
 * 目标表：
 */
public class ReadXls {
    private static StringBuffer buffer = new StringBuffer();
    private static int row = 0;

    public static String readXls(String fileName) throws IOException, BiffException {
        File file = new File(fileName);
        WorkbookSettings setEncode = new WorkbookSettings();
        setEncode.setEncoding("GB2312");
        Workbook wb = Workbook.getWorkbook(file, setEncode);
        Sheet sheet = wb.getSheet(0);
        if (row < sheet.getRows()) {
            for (int j = 0; j < sheet.getColumns(); j++) {
                Cell cell = sheet.getCell(j, row);
                buffer.append(cell.getContents().replaceAll("\n", " ") + ",");
            }
            buffer.delete(buffer.length() - 1, buffer.length());
//            buffer += "\n";
            System.out.println(buffer.toString());
            buffer.setLength(0);
            row++;
        }
        return null;
    }

    public static void main(String[] args) throws IOException, BiffException {
        String fileName = "C:\\Users\\lxd\\Desktop\\新建 XLS 工作表.xls";
        for (int a = 0; a < 10; a++) {
            readXls(fileName);
        }
    }

}
