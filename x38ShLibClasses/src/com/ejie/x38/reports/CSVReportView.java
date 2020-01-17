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

import com.ejie.x38.dto.JerarquiaDto;
 
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
		cookie.setSecure(true);
		response.addCookie(cookie); 
		
		//Token
		String token =  model.get("separator")!=null?(String)model.get("separator"):";";
		
		//Datos a procesar
		ReportData reportData = (ReportData) model.get("reportData");
		boolean isJerarquia = reportData.isJerarquia();
		boolean isGrouping = reportData.isGrouping();
		
		//Metadatos jerarquia
		JerarquiaMetadata jmd = reportData.getJerarquiaMetadada();
		
		//Salida
		BufferedWriter writer = new BufferedWriter(response.getWriter());
			
		//Columnas
		LinkedHashMap<String, String> dataHeader = (LinkedHashMap<String, String>) reportData.getHeaderNames();
		if (reportData.isShowHeaders()){
			//Columna para la agrupacion
			if (isGrouping){
				writer.write(token);
			}
			//Columna para marcar los elementos que cumplen filtro en Jerarquia
			if (isJerarquia && jmd.isShowFiltered()){ 
				if (!"".equals(jmd.getFilterHeaderName())){
					writer.write(jmd.getFilterHeaderName());
				}
				writer.write(token); 
			}
			for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
				//Si tiene agrupacion, no debe mostrarse la columna del grupo y es la columna del grupo comprobar => pasar al siguiente
				if (isGrouping && !reportData.isShowGroupColumng() && entry.getKey().equals(reportData.getGroupColumnName())){
					continue;
				}
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
		String prevGroupValue="", groupValue = "";
		for (Object object : modelData) {
			int level = 0;
			boolean hasChildren = false;
			//Si tiene agrupación => comprobar el cambio de grupo
			if (isGrouping){
				groupValue = parseValue(BeanUtils.getProperty(((JerarquiaDto)object).getModel(), reportData.getGroupColumnName()), token);
				if (!groupValue.equals(prevGroupValue)){
					prevGroupValue = groupValue;
					writer.write(groupValue);
					writer.newLine();
				}
				writer.write(token); //Espacio para evitar columna agrupación
			}
			//Si es jerarquia => comprobar si debe mostrar filtro y obtener atributo + parsear objeto de iteracion
			if (isJerarquia){
				if (jmd.isShowFiltered()){//Mostrar si cumple filtro
					if (((JerarquiaDto) object).isFilter()){ //Cumple filtro
						writer.write(jmd.getFilterToken());
					}
					writer.write(token); 
				}
				level = ((JerarquiaDto) object).getLevel();
				hasChildren = ((JerarquiaDto) object).isHasChildren();
				object = ((JerarquiaDto)object).getModel(); //Obtener objeto de negocio
			}
			for (Map.Entry<String, String> entry : dataHeader.entrySet()) {
				//Si tiene agrupacion, no debe mostrarse la columna del grupo y es la columna del grupo comprobar => pasar al siguiente
				if (isGrouping && !reportData.isShowGroupColumng() && entry.getKey().equals(reportData.getGroupColumnName())){
					continue;
				}
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