package com.ruoyi.common.utils;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * @Author hsj
 * @Date 2022/8/4 9:04
 * @Description
 **/
public class ExcelUtils {

    //设置单元格边框为实线
    public static CellStyle Border(Workbook workbook){
        CellStyle cellStyle = workbook.createCellStyle();
        //下边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        //左边框
        cellStyle.setBorderLeft(BorderStyle.THIN);
        //上边框
        cellStyle.setBorderTop(BorderStyle.THIN);
        //右边框
        cellStyle.setBorderRight(BorderStyle.THIN);

        cellStyle.setAlignment(HorizontalAlignment.LEFT);
        return cellStyle;
    }




}
