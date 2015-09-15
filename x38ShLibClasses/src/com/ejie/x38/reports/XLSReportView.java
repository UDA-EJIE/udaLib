package com.ejie.x38.reports;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;

public class XLSReportView extends AbstractPOIExcelView{

	@Override
	protected Workbook createWorkbook() {
		return new HSSFWorkbook();
	}
	
	@Override
	protected String getFileExtension(){
		return ".xls";
	}
	
}