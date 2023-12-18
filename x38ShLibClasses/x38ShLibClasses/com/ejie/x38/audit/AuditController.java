package com.ejie.x38.audit;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

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
	public void audit(@RequestBody final AuditModel data, final HttpServletRequest request, final HttpServletResponse response) {

		Logger logger = LoggerFactory.getLogger("com.ejie.x38.audit.AuditController");

		// Cargamos las propiedades
		// Properties props = new Properties();

		// InputStream in = null;
		// try {
		// logger.debug("Loading properties from: "+StaticsContainer.webAppName+"/"+StaticsContainer.webAppName+".properties");
		// in =
		// this.getClass().getClassLoader().getResourceAsStream(StaticsContainer.webAppName+"/"+StaticsContainer.webAppName+".properties");
		// props.load(in);
		//
		// //Creamos el traceClient
		// W43taMomoTraceClient mtc = W43taMomoTraceClient.getInstance(
		// props.getProperty(Constants.PROPS_MOMO_SERVICIO),
		// Constants.MOMO_APP,
		// Constants.MOMO_SEC_TOKEN,
		// props.getProperty(Constants.PROPS_MOMO_URI_ENDPOINT),
		// Integer.parseInt(props.getProperty(Constants.PROPS_MOMO_PUERTO_ENDPOINT)),
		// false);
		//
		// //Obtenemos el timestamp y lo convertimos a tiempo utc
		// SimpleDateFormat utcFormat = new SimpleDateFormat("yyyy-MMM-dd HH:mm:ss.SSS");
		// utcFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		// Date dataDate = new Date(data.getTimeStamp().getTime());
		// //Añaidmos las trazas
		// W43taMomoCustomMap customData = mtc.getNewCustomDataMap();
		//
		// customData.add(Constants.MOMO_LABEL_URL, data.getUrl());
		// customData.add(Constants.MOMO_LABEL_COD_APP, data.getCodApp());
		// customData.add(Constants.MOMO_LABEL_VERSION, data.getVersionRup());
		// customData.add(Constants.MOMO_LABEL_COMPONENTE, data.getNombreComponente());
		// customData.add(Constants.MOMO_LABEL_AUDITING, data.getAuditing());
		// customData.add(Constants.MOMO_LABEL_TIMESTAMP, dataDate);
		// String now = new java.util.Date().toString();
		// String msgTraza = "##|AUDIT ~~ "+Constants.MOMO_APP+" ~~ "+now+" ~~ "+data.getNombreComponente()+" ~~
		// "+data.getAuditing()+"|##";
		// //Escribimos los datos en PIB
		// mtc.info(msgTraza, customData);

		// }
		// catch (Exception e) {
		// logger.error(StackTraceManager.getStackTrace(e));
		// }
		// finally {
		// try {
		// in.close();
		// }
		// catch (IOException e) {
		// logger.error("ERROR al cerrar el inputStream en AuditController:", e);
		// }
		// }
	}
}
