package com.sc.excel.util;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

import java.util.Map;

/**
 *
 */
public class StyleUtil {

    /**
     *
     * @param workbook
     * @return
     */
    public static CellStyle buildDefaultCellStyle(Workbook workbook) {
        CellStyle newCellStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("宋体");
        font.setFontHeightInPoints((short)14);
        font.setBold(true);
        newCellStyle.setFont(font);
        newCellStyle.setWrapText(true);
        newCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        newCellStyle.setAlignment(HorizontalAlignment.CENTER);
        newCellStyle.setLocked(true);
        newCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        newCellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        newCellStyle.setBorderBottom(BorderStyle.THIN);
        newCellStyle.setBorderLeft(BorderStyle.THIN);
        return newCellStyle;
    }

    /**
     *
     * @param workbook
     * @param f
     * @param indexedColors
     * @return
     */
    public static CellStyle buildCellStyle(Workbook workbook, com.sc.excel.metadata.Font f,
                                           IndexedColors indexedColors) {
        CellStyle cellStyle = buildDefaultCellStyle(workbook);
        if (f != null) {
            Font font = workbook.createFont();
            font.setFontName(f.getFontName());
            font.setFontHeightInPoints(f.getFontHeightInPoints());
            font.setBold(f.isBold());
            cellStyle.setFont(font);
        }
        if (indexedColors != null) {
            cellStyle.setFillForegroundColor(indexedColors.getIndex());
        }
        return cellStyle;
    }

    public static Sheet buildSheetStyle(Sheet currentSheet, Map<Integer, Integer> sheetWidthMap){
        currentSheet.setDefaultColumnWidth(20);
        for (Map.Entry<Integer, Integer> entry : sheetWidthMap.entrySet()) {
            currentSheet.setColumnWidth(entry.getKey(), entry.getValue());
        }
        return currentSheet;
    }


}
