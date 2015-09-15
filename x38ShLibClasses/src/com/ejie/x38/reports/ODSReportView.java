package com.ejie.x38.reports;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.odftoolkit.odfdom.doc.OdfSpreadsheetDocument;
import org.odftoolkit.odfdom.doc.table.OdfTable;
import org.odftoolkit.odfdom.doc.table.OdfTableRow;
import org.springframework.web.servlet.view.AbstractView;

public class ODSReportView extends AbstractView {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {

		//Tipo y Nombre Fichero
		response.setHeader("Content-type", "application/vnd.oasis.opendocument.spreadsheet");
		response.setHeader("Content-Disposition", "attachment; filename=\"" + model.get("fileName") + ".ods\"");
		
		//Salida
		OdfSpreadsheetDocument osd = (OdfSpreadsheetDocument) OdfSpreadsheetDocument.newSpreadsheetDocument();
		osd.getOrCreateDocumentStyles();
		
		//Eliminar contenido por defecto
        osd.getTableByName("Sheet1").remove();
		
		//Procesar Hojas
		List<ReportData> reportData = (List<ReportData>) model.get("reportData"); 
		for (ReportData dataSheet : reportData) {
			
			LinkedHashMap<String, String> dataHeader = (LinkedHashMap<String, String>) dataSheet.getHeaderNames();
			List<Object> modelData = dataSheet.getModelData();
			
			//Hoja
			OdfTable table = OdfTable.newTable(osd, modelData.size()+1, dataHeader.size());
			table.setTableName(dataSheet.getSheetName());
			
			//Cabeceras Hoja
			OdfTableRow tablerow = table.getRowByIndex(0);
			int cellNum = 0;
			int rowNum = 0;
			if (dataSheet.isShowHeaders()){
				for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
					tablerow.getCellByIndex(cellNum++).setStringValue(entry.getValue());
				}
				rowNum++;
			}
			
			//Datos Hoja
			for (Object object : modelData) {
				tablerow = table.getRowByIndex(rowNum++);
				cellNum = 0;
				for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
				    tablerow.getCellByIndex(cellNum++).setStringValue(BeanUtils.getProperty(object, entry.getKey()));
				}
			}
		}
		
		//Gestión de cookie (determina el final de la generación del fichero)
		Cookie cookie = new Cookie("fileDownload", "true");
		cookie.setPath("/");
		response.addCookie(cookie); 
		
		//Escribir datos
		osd.save(response.getOutputStream());
	}
}