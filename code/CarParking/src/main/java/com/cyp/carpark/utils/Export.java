package com.cyp.carpark.utils;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.util.CellRangeAddress;


public class Export {

	public String createExcel(HttpServletResponse response) throws IOException {

		HSSFWorkbook wb = new HSSFWorkbook();
		HSSFSheet sheet = wb.createSheet("收入表");

		HSSFRow row1 = sheet.createRow(0);

		HSSFCell cell = row1.createCell(0);

		cell.setCellValue("CellValue");

		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 3));

		HSSFRow row2 = sheet.createRow(1);

		row2.createCell(0).setCellValue("����");
		row2.createCell(1).setCellValue("�༶");
		row2.createCell(2).setCellValue("���Գɼ�");
		row2.createCell(3).setCellValue("���Գɼ�");

		HSSFRow row3 = sheet.createRow(2);
		row3.createCell(0).setCellValue("����");
		row3.createCell(1).setCellValue("As178");
		row3.createCell(2).setCellValue(87);
		row3.createCell(3).setCellValue(78);

		OutputStream output = response.getOutputStream();
		response.reset();
		response.setHeader("Content-disposition", "attachment; filename=details.xls");
		response.setContentType("application/msexcel");
		wb.write(output);
		output.close();
		return null;
	}
}
