package com.pazz.framework.web.view.document;

import com.pazz.framework.util.ReflectionUtils;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.document.AbstractXlsView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author: 彭坚
 * @create: 2018/11/19 15:29
 * @description: Excele view
 */
public class XlsView extends AbstractXlsView {

    public static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    private String fileName;
    private CellStyle style;
    private Map maps;
    private List list;

    /**
     * @param map      映射
     * @param list     数据源
     * @param fileName 文件名
     */
    public XlsView(Map map, List list, String fileName) {
        this.maps = map;
        this.list = list;
        this.fileName = fileName;
    }

    /**
     * @param map      映射
     * @param list     数据源
     * @param fileName 文件名
     * @param style    表头样式
     */
    public XlsView(Map map, List list, String fileName, CellStyle style) {
        this.maps = map;
        this.list = list;
        this.fileName = fileName;
        this.style = style;
    }


    @Override
    protected void buildExcelDocument(Map<String, Object> model, Workbook workbook, HttpServletRequest httpServletRequest, HttpServletResponse response) throws Exception {
        if (this.style == null) {
            setStyle(workbook);
        }
        response.setHeader("Content-Disposition", "attachment; filename=" + new String(fileName.getBytes(), "iso-8859-1"));
        Sheet sheet = workbook.createSheet("sheet1");
        sheet.setDefaultColumnWidth(30);
        Row header = sheet.createRow(0);
        Set sets = maps.keySet();
        int i = 0;
        for (Iterator it = sets.iterator(); it.hasNext(); ) {
            String key = (String) it.next();
            header.createCell(i).setCellValue((String) maps.get(key));
            header.getCell(i).setCellStyle(style);
            i++;
        }
        int rowCount = 1;
        for (int j = 0; j < list.size(); j++) {
            Row userRow = sheet.createRow(rowCount++);
            Object p = list.get(j);
            int index = 0;
            for (Iterator it = sets.iterator(); it.hasNext(); ) {
                String key = (String) it.next();
                Field field = ReflectionUtils.findField(p.getClass(), key);
                field.setAccessible(true);
                Object value = field.get(p);
                if ((value instanceof Date)) {
                    value = df.format(value);
                }
                userRow.createCell(index).setCellValue(value == null ? "" : value.toString());
                index++;
            }
        }
    }

    private void setStyle(Workbook workbook) {
        style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontName("Arial");
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        font.setBoldweight((short) 1);
        font.setColor(HSSFColor.WHITE.index);
        style.setFont(font);
    }

}
