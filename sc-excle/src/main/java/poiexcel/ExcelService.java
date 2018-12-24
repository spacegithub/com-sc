package poiexcel;

import com.sc.excel.util.ObjectUtils;

import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ExcelService {

    private static CellStyle getCellStyle(Workbook work) {
        CellStyle cellStyle = work.createCellStyle();
        //垂直水平居中
        cellStyle.setAlignment(HorizontalAlignment.CENTER);
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        //上下左右边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        //背景填充方式
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置字体样式
        //Font font = work.createFont();
        //font.setFontName("宋体");
        //font.setFontHeightInPoints((short) 12);//设置字体大小
        //font.setBold(true);
        //cellStyle.setFont(font);
        return cellStyle;
    }

    /**
     * 将输入写入到2007excle 数据区域包括标题,默认写到第一个sheet中
     */
    public static void writerExcel4TargetDispatch(OutputStream outputStream, List<List<Object>> data) throws Exception {
        if (ObjectUtils.isEmpty(data)) {
            return;
        }
        Workbook work = new XSSFWorkbook();
        // 生成工作表
        Sheet sheet = work.createSheet();
        //设置列宽为10
        sheet.setColumnWidth(0, 2560);
        //设置标题头
        Row row = sheet.createRow(0);

        for (int i = 0; i < data.get(0).size(); i++) {
            Cell cell = row.createCell(i, CellType.STRING);
            row.setHeight((short) 400);
            cell.setCellValue(String.valueOf(data.get(0).get(i)));
            CellStyle cellStyle = getCellStyle(work);
            cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            cell.setCellStyle(cellStyle);
        }
        //设置内容
        for (int j = 1; j < data.size(); j++) {
            row = sheet.createRow(j);
            row.setHeight((short) 300);
            for (int k = 0; k < data.get(j).size(); k++) {
                Cell cell = row.createCell(k, CellType.STRING);
                cell.setCellValue(String.valueOf(data.get(j).get(k)));
                //设置样式
                CellStyle cellStyle = getCellStyle(work);
                cell.setCellStyle(cellStyle);
            }
        }
        work.write(outputStream);
    }

    /**
     * 获取IO流中的数据，组装成List<List<Object>>对象
     *
     * @param inputStream
     * @return
     */
    public static List<List<Object>> readExcel4TargetDispatch(InputStream inputStream) throws Exception {
        return readExcel4TargetDispatch(inputStream, null);
    }

    /**
     * 获取IO流中的数据，组装成List<List<Object>>对象
     *
     * @param inputStream
     * @param excleValidate
     *         校验excle的值
     * @return
     */
    public static List<List<Object>> readExcel4TargetDispatch(InputStream inputStream, ExcleValidate excleValidate) throws Exception {
        Workbook work = WorkbookFactory.create(inputStream);
        // 遍历Excel中所有的sheet
        Sheet sheet = work.getSheetAt(0);
        if (sheet == null) {
            throw new Exception("sheet不存在!");
        }
        if (excleValidate != null && !excleValidate.validate(sheet)) {
            throw new Exception("excle未通过校验!");
        }
        // sheet总行数
        int linCount = sheet.getLastRowNum();
        List<List<Object>> list = new LinkedList<List<Object>>();
        for (int j = 1; j <= linCount; j++) {
            Row row = sheet.getRow(j);
            if (row == null || row.getFirstCellNum() == j) {
                continue;
            }
            // 遍历所有的列
            List<Object> columns = new LinkedList<Object>();
            columns.add(j);
            for (int y = 0; y < row.getLastCellNum(); y++) {
                Cell cell = row.getCell(y);
                columns.add(getCellValue(cell));
            }
            list.add(columns);
        }
        return list;
    }

    /**
     * 描述：对表格中数值进行格式化
     *
     * @param cell
     * @return
     */
    private static Object getCellValue(Cell cell) {
        Object value = null;
        DecimalFormat df = new DecimalFormat("0");  //格式化number String字符
        switch (cell.getCellTypeEnum()) {
            case STRING:
                value = cell.getRichStringCellValue().getString().trim();
                break;
            case NUMERIC:
                if ("General".equals(cell.getCellStyle().getDataFormatString())) {
                    value = df.format(cell.getNumericCellValue());
                } else if (HSSFDateUtil.isCellDateFormatted(cell)) {
                    value = cell.getDateCellValue();
                } else {
                    value = cell.getNumericCellValue();
                }
                break;
            case BOOLEAN:
                value = cell.getBooleanCellValue();
                break;
            case BLANK:
                value = "";
                break;
            default:
                break;
        }
        return value;
    }

    public static void main(String[] args) throws Exception {
        List<Object> title = new ArrayList<>();
        title.add("标题ssssssssssssssssssssssssssssssssssssssssssssssssss1");
        title.add("标题2");
        title.add("标题2");
        title.add("标题2");
        title.add("标题2");
        title.add("标题2");
        title.add("标题2");
        title.add("标题2");
        title.add("标题ssssssssssssssssssssssssssssssssssssssssssssssssss2");


        List<Object> body = new ArrayList<>();
        body.add("身体1");
        body.add(2);
        body.add(null);
        body.add(null);
        body.add(null);
        body.add(null);

        body.add(3);
        List<List<Object>> listList = new ArrayList<>();
        listList.add(title);
        listList.add(body);
        FileOutputStream fileOutputStream = new FileOutputStream("F:\\a.xlsx");
        ExcelService.writerExcel4TargetDispatch(fileOutputStream, listList);

    }
}
