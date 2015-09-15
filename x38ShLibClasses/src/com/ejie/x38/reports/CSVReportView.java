package com.ejie.x38.reports;

import java.io.BufferedWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.beanutils.BeanUtils;
import org.springframework.web.servlet.view.AbstractView;

import com.ejie.x38.dto.Jerarquia;
 
public class CSVReportView extends AbstractView{

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void renderMergedOutputModel(Map<String, Object> model,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		//Tipo y Nombre Fichero
		response.setHeader("Content-type", "application/octet-stream; charset=ISO-8859-1"); 
		response.setHeader("Content-Disposition", "attachment; filename=\"" + model.get("fileName") + ".csv\"");
		
		//Gestión de cookie (determina el final de la generación del fichero)
		Cookie cookie = new Cookie("fileDownload", "true");
		cookie.setPath("/");
		response.addCookie(cookie); 
		
		//Token
		String token =  model.get("separator")!=null?(String)model.get("separator"):";";
		
		//Datos a procesar
		ReportData reportData = (ReportData) model.get("reportData");
		boolean isJerarquia = reportData.isJerarquia();
		
		//Metadatos jerarquia
		JerarquiaMetadata jmd = reportData.getJerarquiaMetadada();
		
		//Salida
		BufferedWriter writer = new BufferedWriter(response.getWriter());
			
		//Columnas
		LinkedHashMap<String, String> dataHeader = (LinkedHashMap<String, String>) reportData.getHeaderNames();
		if (reportData.isShowHeaders()){
			//Columna para marcar los elementos que cumplen filtro en Jerarquia
			if (isJerarquia && jmd.isShowFiltered()){ 
				if (!"".equals(jmd.getFilterHeaderName())){
					writer.write(jmd.getFilterHeaderName());
				}
				writer.write(token); 
			}
			for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
			    writer.write(parseValue(entry.getValue(), token));
				writer.write(token);
			}
			writer.newLine();
		}
		
		//Datos
		List<Object> modelData;
		if (!isJerarquia){
			modelData = reportData.getModelData();
		} else {
			modelData = reportData.getModelDataJerarquia();
		}
		for (Object object : modelData) {
			int level = 0;
			boolean hasChildren = false;
			if (isJerarquia){
				if (jmd.isShowFiltered()){//Mostrar si cumple filtro
					if (((Jerarquia) object).isFilter()){ //Cumple filtro
						writer.write(jmd.getFilterToken());
					}
					writer.write(token); 
				}
				level = ((Jerarquia) object).getLevel();
				hasChildren = ((Jerarquia) object).isHasChildren();
				object = ((Jerarquia)object).getModel(); //Obtener objeto de negocio
			}
			for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
				String columnValue = parseValue(BeanUtils.getProperty(object, entry.getKey()), token); 
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
				writer.write(columnValue);
				writer.write(token);
			}
			writer.newLine(); 
		}
		
		//Escribir/cerrar salida
		writer.flush(); 
		writer.close();
	}
	
	
	private String parseValue (String value, String token){
		//Forzar nulos a vacíos
		value = value!=null?value:"";

		//Escapar valor que contiene token
		if (value.indexOf(token)!=-1){
			value = "\"" + value + "\"";
		}
		return value;
	}
}