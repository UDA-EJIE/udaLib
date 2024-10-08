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

import com.ejie.x38.dto.JerarquiaDto;

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
			
			//Metadatos
			boolean isJerarquia = dataSheet.isJerarquia();
			boolean isGrouping = dataSheet.isGrouping();
			JerarquiaMetadata jmd = dataSheet.getJerarquiaMetadada();
			
			LinkedHashMap<String, String> dataHeader = (LinkedHashMap<String, String>) dataSheet.getHeaderNames();
			List<Object> modelData;
			if (!isJerarquia){
				modelData = dataSheet.getModelData();
			} else {
				modelData = dataSheet.getModelDataJerarquia();
			}
			
			//Hoja
			OdfTable table = OdfTable.newTable(osd, modelData.size()+1, dataHeader.size());
			table.setTableName(dataSheet.getSheetName());
			
			//Cabeceras Hoja
			OdfTableRow tablerow = table.getRowByIndex(0);
			int cellNum = 0;
			int rowNum = 0;
			if (dataSheet.isShowHeaders()){
				//Columna para la agrupacion
				if (isGrouping){
					tablerow.getCellByIndex(cellNum++).setStringValue("");
				}
				//Columna para marcar los elementos que cumplen filtro en Jerarquia
				if (isJerarquia && jmd.isShowFiltered()){ 
					if (!"".equals(jmd.getFilterHeaderName())){
						tablerow.getCellByIndex(cellNum++).setStringValue(jmd.getFilterHeaderName());
					} else {
						tablerow.getCellByIndex(cellNum++).setStringValue("");
					}
				}
				for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
					//Si tiene agrupacion, no debe mostrarse la columna del grupo y es la columna del grupo comprobar => pasar al siguiente
					if (isGrouping && !dataSheet.isShowGroupColumng() && entry.getKey().equals(dataSheet.getGroupColumnName())){
						continue;
					}
					tablerow.getCellByIndex(cellNum++).setStringValue(entry.getValue());
				}
				rowNum++;
			}
			
			//Datos Hoja
			String prevGroupValue="", groupValue = "";
			for (Object object : modelData) {
				//Gestión filas-columnas
				tablerow = table.getRowByIndex(rowNum++);
				cellNum = 0;
				
				//jerarquia
				int level = 0;
				boolean hasChildren = false;
				
				//Si tiene agrupación => comprobar el cambio de grupo
				if (isGrouping){
					groupValue = BeanUtils.getProperty(((JerarquiaDto)object).getModel(), dataSheet.getGroupColumnName());
					if (!groupValue.equals(prevGroupValue)){
						prevGroupValue = groupValue;
						tablerow.getCellByIndex(cellNum++).setStringValue(groupValue);
						tablerow = table.getRowByIndex(rowNum++);
						cellNum = 0;
					}
					tablerow.getCellByIndex(cellNum++).setStringValue(""); //Espacio para evitar columna agrupación
				}
				//Si es jerarquia => comprobar si debe mostrar filtro y obtener atributo + parsear objeto de iteracion
				if (isJerarquia){
					if (jmd.isShowFiltered()){//Mostrar si cumple filtro
						if (((JerarquiaDto) object).isFilter()){ //Cumple filtro
							tablerow.getCellByIndex(cellNum++).setStringValue(jmd.getFilterToken());
						} else {
							tablerow.getCellByIndex(cellNum++).setStringValue("");
						}
					}
					level = ((JerarquiaDto) object).getLevel();
					hasChildren = ((JerarquiaDto) object).isHasChildren();
					object = ((JerarquiaDto)object).getModel(); //Obtener objeto de negocio
				}
				for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
					//Si tiene agrupacion, no debe mostrarse la columna del grupo y es la columna del grupo comprobar => pasar al siguiente
					if (isGrouping && !dataSheet.isShowGroupColumng() && entry.getKey().equals(dataSheet.getGroupColumnName())){
						continue;
					}
					String columnValue = BeanUtils.getProperty(object, entry.getKey()); 
					//Si es Jerarquia...
					if (isJerarquia){
						//Si está activados los iconos y la columna es la indicada => aplicar iconos
						if (jmd.isShowIcon() && entry.getKey().equals(jmd.getIconColumnName())){  
							if (jmd.getIconCollapsedList().contains(BeanUtils.getProperty(object, jmd.getIconBeanAtribute()))){
								columnValue = jmd.getIconUnexpanded() + columnValue;
							} else {
								if (hasChildren){
									columnValue = jmd.getIconExpanded() + columnValue;
								} else {
									columnValue = jmd.getIconNoChild() + columnValue;
								}
							}
						}
						//Si está activada la tabulación y la columna es la indicada => aplicar tabulación
						if (jmd.isShowTabbed() && entry.getKey().equals(jmd.getTabColumnName())){  
							columnValue = jmd.getTab(level) + columnValue;
						}
					}
					tablerow.getCellByIndex(cellNum++).setStringValue(columnValue);
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