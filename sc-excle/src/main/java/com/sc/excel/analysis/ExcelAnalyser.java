package com.sc.excel.analysis;

import com.sc.excel.metadata.Sheet;

import java.util.List;

/**
 * Excel file analyser
 *
 *
 */
public interface ExcelAnalyser {

    /**
     * parse one sheet
     *
     * @param sheetParam
     */
    void analysis(Sheet sheetParam);

    /**
     * parse all sheets
     */
    void analysis();

    /**
     * get all sheet of workbook
     *
     * @return all sheets
     */
    List<Sheet> getSheets();

}
