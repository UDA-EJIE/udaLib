package com.ejie.x38.audit;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Properties;
import java.util.TimeZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.ejie.w43ta.clients.W43taMomoCustomMap;
import com.ejie.w43ta.clients.W43taMomoTraceClient;
import com.ejie.x38.util.StackTraceManager;
import com.ejie.x38.util.StaticsContainer;
import com.ejie.x38.util.Constants;

@Controller
@RequestMapping(value = "/audit")
public class AuditController {

	/**
	 * Método que realiza el registro de la audición.
	 * @param AuditModel data
	 * @param HttpServletRequest request
	 * @param HttpServletResponse response
	 */
	@RequestMapping(method = RequestMethod.POST)
	public void audit(@RequestBody AuditModel data,
						HttpServletRequest request, HttpServletResponse response) {
		
		Logger logger =  LoggerFactory.getLogger("com.ejie.x38.audit.AuditController");
		
		//Cargamos las propiedades
		Properties props = new Properties();
		
		InputStream in = null;
		try{
			logger.debug("Loading properties from: "+StaticsContainer.webAppName+"/"+StaticsContainer.webAppName+".properties");
			in = this.getClass().getClassLoader().getResourceAsStream(StaticsContainer.webAppName+"/"+StaticsContainer.webAppName+".properties");
			props.load(in);
			
			//Creamos el traceClient
			W43taMomoTraceClient mtc = W43taMomoTraceClient.getInstance(
					props.getProperty(Constants.PROPS_MOMO_SERVICIO),
					Constants.MOMO_APP,
					Constants.MOMO_SEC_TOKEN,
					props.getProperty(Constants.PROPS_MOMO_SALIDA_ESTANDAR),
					Integer.parseInt(Constants.PROPS_MOMO_PUERTO_ENDPOINT),
					false);
			
			//Obtenemos el timestamp y lo convertimos a tiempo utc
			SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS");
			utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
			Date dataDate = new Date(data.getTimeStamp().getTime());
			//Añaidmos las trazas
			W43taMomoCustomMap customData = mtc.getNewCustomDataMap();
			
			customData.add(Constants.MOMO_LABEL_URL, data.getUrl());
			customData.add(Constants.MOMO_LABEL_COD_APP, data.getCodApp());
			customData.add(Constants.MOMO_LABEL_VERSION, data.getVersionRup());
			customData.add(Constants.MOMO_LABEL_COMPONENTE, data.getNombreComponente());
			customData.add(Constants.MOMO_LABEL_AUDITING, data.getAuditing());
			customData.add(Constants.MOMO_LABEL_TIMESTAMP, dataDate);
			
			//Escribimos los datos en PIB
			mtc.info("trazaUDA", customData);
			
		}catch(Exception e){
			logger.error(StackTraceManager.getStackTrace(e));
		}
		finally {
			try {
				in.close();
			} catch (IOException e) {
				logger.error("ERROR al cerrar el inputStream en AuditController:",e);
			}
		}
	}
}
