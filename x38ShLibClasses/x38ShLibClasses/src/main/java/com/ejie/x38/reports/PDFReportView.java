package com.ejie.x38.reports;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

import net.sf.jasperreports.engine.JasperPrint;

import org.springframework.web.servlet.view.jasperreports.JasperReportsPdfView;

public class PDFReportView extends JasperReportsPdfView{

	@Override
	protected JasperPrint fillReport(Map<String, Object> model) throws Exception {
		
		//Tipo y Nombre Fichero
		Properties headers = new Properties();
		headers.setProperty("Content-type", "application/pdf"); //por defecto
		if (model.get("isInline")==null || !((Boolean) model.get("isInline")).booleanValue()){
			headers.setProperty("Content-Disposition", "attachment; filename=\"" + model.get("fileName") + ".pdf\"");
		}
		this.setHeaders(headers);
		
		return super.fillReport(model);
	}

	@Override
	protected void writeToResponse(HttpServletResponse response, ByteArrayOutputStream baos) throws IOException {
		
		//Gestión de cookie (determina el final de la generación del fichero)
		Cookie cookie = new Cookie("fileDownload", "true");
		cookie.setPath("/");
		response.addCookie(cookie); 
		
		super.writeToResponse(response, baos);
	}
}