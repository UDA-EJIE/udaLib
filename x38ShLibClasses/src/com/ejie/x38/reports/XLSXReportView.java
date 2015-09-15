package com.ejie.x38.reports;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XLSXReportView extends AbstractPOIExcelView{

	@Override
	protected Workbook createWorkbook() {
		return new XSSFWorkbook();
	}
	
	@Override
	protected String getFileExtension(){
		return ".xlsx";
	}
	
}