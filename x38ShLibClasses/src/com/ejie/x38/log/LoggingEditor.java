package com.ejie.x38.log;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.ejie.x38.dto.TableResponseDto;
import com.ejie.x38.log.model.LogModel;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

/**
 * Gestion del cambio de logs en runtime 
 *
 * @author Urko Guinea
 * 
 */

@Service(value = "LoggingEditor")
public  class LoggingEditor
{
	private static final String LOGBACK_CLASSIC        = "ch.qos.logback.classic";
    private static final String LOGBACK_CLASSIC_LOGGER = "ch.qos.logback.classic.Logger";
	private static final String LOGBACK_CLASSIC_LEVEL  = "ch.qos.logback.classic.Level";
	private static  final Logger logger                 = LoggerFactory.getLogger(LoggingEditor.class);


	/**
	 * Cambio en runtime del nivel de logger
	 *
	 * @param loggerName Nombre del logger a cambiar su nivel. Si blank, se usara el root logger.
	 * @param logLevel   Uno de los niveles de logs soportado: TRACE, DEBUG, INFO, WARN, ERROR, FATAL,
	 *                      OFF. {@code null}  se considera 'OFF'.
	 */
	public static boolean setLogLevel(String loggerName, String logLevel)
	{
		String logLevelUpper = (logLevel == null) ? "OFF" : logLevel.toUpperCase();
	

		
		try
		{
			Package logbackPackage = Package.getPackage(LOGBACK_CLASSIC);
			if (logbackPackage == null)
			{
				logger.info("Logback no esta en el classpath!");
				return false;
			}

			// usa el root logger en caso de que llegue vacio
			if ((loggerName == null) || loggerName.trim().isEmpty())
			{
				loggerName = (String) getFieldVaulue(LOGBACK_CLASSIC_LOGGER, "ROOT_LOGGER_NAME");
			}

			// Obtener el logger por su nombre
			Logger loggerObtained = LoggerFactory.getLogger(loggerName);
			if (loggerObtained == null)
			{
				// I don't know if this case occurs
				logger.warn("No existe un logger con tal nombre: {}", loggerName);
				return false;
			}

			Object logLevelObj = getFieldVaulue(LOGBACK_CLASSIC_LEVEL, logLevelUpper);
			if (logLevelObj == null)
			{
				logger.warn("No existe tal log level: {}", logLevelUpper);
				return false;
			}

			Class<?>[] paramTypes =  { logLevelObj.getClass() };
			Object[]   params     =  { logLevelObj };

			Class<?> clz    = Class.forName(LOGBACK_CLASSIC_LOGGER);
			Method   method = clz.getMethod("setLevel", paramTypes);
			method.invoke(loggerObtained, params);

			logger.debug("Log level cambiado a  {} para el logger '{}'", logLevelUpper, loggerName);
			
			return true;
		}
		catch (Exception e)
		{
			logger.warn("No se pudo asignar el log level a {} para el logger '{}'", logLevelUpper, loggerName);
			return false;
		}
	}
	
	/**
	 * Obtener un logger concreto
	 * 
	 * @return Logger
	 */
	public static LogModel getLogger(final String loggerName) {

		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger aux;
		LogModel result = new LogModel();
		
		aux= lc.getLogger(loggerName);
		result.setLevelLog(aux.getEffectiveLevel().toString());
		result.setNameLog(aux.getName());
		
		return result;
		
	}
	
	
	
	/**
	 * Devuelto todos los loggers configurados. 
	 * 
	 * @param showAll para devolver todos los loggers, no solo los configurados.
	 * @return list de LogModel
	 */
	public static List<LogModel> getLoggers(final LoggerContext logContext ,final boolean showAll) {

		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		final List<LogModel> loggers = new ArrayList<LogModel>();
		LogModel aux;
		
		for (ch.qos.logback.classic.Logger log : lc.getLoggerList()) {
			if(showAll == false) {
				if(log.getLevel() != null || hasAppenders(log)) {
					aux= new LogModel();
					aux.setLevelLog(log.getEffectiveLevel().toString());
					aux.setNameLog(log.getName());
					loggers.add(aux);
				}
			} else {
				aux= new LogModel();
				aux.setLevelLog(log.getEffectiveLevel().toString());
				aux.setNameLog(log.getName());
				loggers.add(aux);
			}
		}

		return loggers;
	}
	
	/** 
	 * Comprueba si el logger seleccionado tiene appender 
	 * 
	 * @param logger el logger a probar
	 * @return true si el logger tiene appenders.
	 */
	public static boolean hasAppenders(Logger logger) {
		Iterator<Appender<ILoggingEvent>> it =((ch.qos.logback.classic.Logger) logger).iteratorForAppenders();
		return it.hasNext();
	}



	private  static Object getFieldVaulue(String fullClassName, String fieldName)
	{
		try
		{
			Class<?> clazz = Class.forName(fullClassName);
			Field    field = clazz.getField(fieldName);
			return field.get(null);
		}
		catch (Exception ex)
		{
			return null;
		}
	}

	
	public static  TableResponseDto<LogModel>  getLoggersFiltered(LogModel filterLogModel
			) {
		
		
		
		TableResponseDto<LogModel> resultado= new TableResponseDto<LogModel>();
		
		List<LogModel> listalogs=getLoggers((LoggerContext) LoggerFactory.getILoggerFactory() ,false);
		
		List<LogModel> resulList= new ArrayList<LogModel>();
		LogModel model;
		for (int i=0;i<listalogs.size();i++){
			
			
			
			
		if (filterLogModel.getLevelLog()==null && filterLogModel.getNameLog()==null){
			model= new LogModel();						
			model.setNameLog(listalogs.get(i).getNameLog());
			model.setLevelLog(listalogs.get(i).getLevelLog());
			resulList.add(model);
		}else if (filterLogModel.getLevelLog()!=null && filterLogModel.getNameLog()==null){
				if(filterLogModel.getLevelLog().equalsIgnoreCase(listalogs.get(i).getLevelLog())){
					model= new LogModel();						
					model.setNameLog(listalogs.get(i).getNameLog());
					model.setLevelLog(listalogs.get(i).getLevelLog());
					resulList.add(model);
				}
		}else if(filterLogModel.getLevelLog()==null && filterLogModel.getNameLog()!=null){
				if 	(listalogs.get(i).getNameLog().toLowerCase().contains(filterLogModel.getNameLog().toLowerCase()) ||   listalogs.get(i).getNameLog().toLowerCase().contains(filterLogModel.getNameLog().toLowerCase())){
					model= new LogModel();						
					model.setNameLog(listalogs.get(i).getNameLog());
					model.setLevelLog(listalogs.get(i).getLevelLog());
					resulList.add(model);
				}
				
		}else if (filterLogModel.getLevelLog()!=null && filterLogModel.getNameLog()!=null){
			if (listalogs.get(i).getNameLog().toLowerCase().contains(filterLogModel.getNameLog().toLowerCase()) && filterLogModel.getLevelLog().equalsIgnoreCase(listalogs.get(i).getLevelLog())){
				model= new LogModel();						
				model.setNameLog(listalogs.get(i).getNameLog());
				model.setLevelLog(listalogs.get(i).getLevelLog());
				resulList.add(model);
			}
		}
		}			
			
			
				
		
			
		
		resultado.setRows(resulList);
		resultado.setRecords(resulList.size());
		resultado.setTotal(new Long(resulList.size()), new Long(resulList.size()));
		
		
		
	return resultado;
	}


	


}
