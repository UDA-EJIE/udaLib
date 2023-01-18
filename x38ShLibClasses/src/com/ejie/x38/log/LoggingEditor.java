package com.ejie.x38.log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Service;

import com.ejie.x38.dto.TableRequestDto;
import com.ejie.x38.dto.TableResourceResponseDto;
import com.ejie.x38.dto.TableRowDto;
import com.ejie.x38.generic.model.SelectGeneric;
import com.ejie.x38.generic.model.SelectGenericPKs;
import com.ejie.x38.log.model.LogModel;
import com.ejie.x38.util.ResourceUtils;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

/**
 * Gestion del cambio de logs en runtime.
 *
 * @author Urko Guinea
 * 
 */
@Service(value = "LoggingEditor")
public class LoggingEditor {
	private static final String LOGBACK_CLASSIC = "ch.qos.logback.classic";
    private static final String LOGBACK_CLASSIC_LOGGER = "ch.qos.logback.classic.Logger";
	private static final String LOGBACK_CLASSIC_LEVEL = "ch.qos.logback.classic.Level";
	private static final Logger logger = LoggerFactory.getLogger(LoggingEditor.class);
	private static List<TableRowDto<LogModel>> listReorderSelection = new ArrayList<TableRowDto<LogModel>>();
	private static LogModel lastFilterLogModel = new LogModel();
	private static int lastRowsNumber = 10;
	private static int lastPageNumber = 0;

	/**
	 * Cambio en runtime del nivel de logger.
	 *
	 * @param loggerName Nombre del logger a cambiar su nivel. Si blank, se usara el root logger.
	 * @param logLevel Uno de los niveles de logs soportado: TRACE, DEBUG, INFO, WARN, ERROR, FATAL, OFF. {@code null}  se considera 'OFF'.
	 * 
	 * @return boolean
	 */
	public static boolean setLogLevel(String loggerName, String logLevel) {
		String logLevelUpper = (logLevel == null) ? "OFF" : logLevel.toUpperCase();
	
		try {
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
		catch (Exception e) {
			logger.warn("No se pudo asignar el log level a {} para el logger '{}'", logLevelUpper, loggerName);
			return false;
		}
	}
	
	/**
	 * Obtener un logger en concreto.
	 * 
	 * @param loggerName Nombre del log a obtener.
	 * 
	 * @return LogModel
	 */
	public static LogModel getLogger(final String loggerName) {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		ch.qos.logback.classic.Logger aux;
		LogModel result = new LogModel();
		
		aux = lc.getLogger(loggerName);
		result.setLevelLog(aux.getEffectiveLevel().toString());
		result.setNameLog(aux.getName());
		
		return result;
	}
	
	/**
	 * Devuelve todos los loggers configurados.
	 * 
	 * @param showAll Devuelve todos los loggers, no solo los configurados.
	 * 
	 * @return List<LogModel>
	 */
	public static List<LogModel> getLoggers(final boolean showAll) {
		final LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
		final List<LogModel> loggers = new ArrayList<LogModel>();
		LogModel aux;
		
		for (ch.qos.logback.classic.Logger log : lc.getLoggerList()) {
			if(!showAll) {
				if(log.getLevel() != null || hasAppenders(log)) {
					aux = new LogModel();
					aux.setLevelLog(log.getEffectiveLevel().toString());
					aux.setNameLog(log.getName());
					loggers.add(aux);
				}
			} else {
				aux = new LogModel();
				aux.setLevelLog(log.getEffectiveLevel().toString());
				aux.setNameLog(log.getName());
				loggers.add(aux);
			}
		}

		return loggers;
	}
	
	/** 
	 * Comprueba si el logger seleccionado tiene appender.
	 * 
	 * @param logger Logger a probar.
	 * 
	 * @return boolean True si el logger tiene appenders.
	 */
	public static boolean hasAppenders(Logger logger) {
		Iterator<Appender<ILoggingEvent>> it =((ch.qos.logback.classic.Logger) logger).iteratorForAppenders();
		return it.hasNext();
	}

	private static Object getFieldVaulue(String fullClassName, String fieldName) {
		try {
			Class<?> clazz = Class.forName(fullClassName);
			Field field = clazz.getField(fieldName);
			return field.get(null);
		}
		catch (Exception ex) {
			return null;
		}
	}
	
	/** 
	 * Devuelve los logs filtrados.
	 *
	 * @param filterLogModel Filtro a aplicar enviado por el cliente.
	 * @param tableRequestDto DTO que contiene los parámetros de configuración propios del RUP_TABLE a aplicar en el filtrado.
	 * 
	 * @return TableResourceResponseDto<LogModel>
	 */
	public static TableResourceResponseDto<LogModel> getLoggersFiltered(LogModel filterLogModel, TableRequestDto tableRequestDto) {
		TableResourceResponseDto<LogModel> resultado = new TableResourceResponseDto<LogModel>();
		List<LogModel> listalogs = getLoggers(true);
		List<LogModel> resulList = new ArrayList<LogModel>();
		LogModel model;
		
		// Paginación
		int startPosition = (int) (tableRequestDto.getRows() * (tableRequestDto.getPage() - 1));
		long iterations = (listalogs.size() - startPosition) > tableRequestDto.getRows() ? tableRequestDto.getRows() : listalogs.size() - startPosition;
		
		for (int i = startPosition; i < startPosition + iterations; i++) {	
			if (filterLogModel.getLevelLog() == null && filterLogModel.getNameLog() == null) {
				model = new LogModel();						
				model.setNameLog(listalogs.get(i).getNameLog());
				model.setLevelLog(listalogs.get(i).getLevelLog());
				resulList.add(model);
			} else if (filterLogModel.getLevelLog() != null && filterLogModel.getNameLog() == null) {
				if (filterLogModel.getLevelLog().equalsIgnoreCase(listalogs.get(i).getLevelLog())) {
					model = new LogModel();						
					model.setNameLog(listalogs.get(i).getNameLog());
					model.setLevelLog(listalogs.get(i).getLevelLog());
					resulList.add(model);
				}
			} else if (filterLogModel.getLevelLog() == null && filterLogModel.getNameLog() != null) {
				if (listalogs.get(i).getNameLog().toLowerCase().contains(filterLogModel.getNameLog().toLowerCase()) || listalogs.get(i).getNameLog().toLowerCase().contains(filterLogModel.getNameLog().toLowerCase())) {
					model = new LogModel();						
					model.setNameLog(listalogs.get(i).getNameLog());
					model.setLevelLog(listalogs.get(i).getLevelLog());
					resulList.add(model);
				}	
			} else if (filterLogModel.getLevelLog() != null && filterLogModel.getNameLog() != null) {
				if (listalogs.get(i).getNameLog().toLowerCase().contains(filterLogModel.getNameLog().toLowerCase()) && filterLogModel.getLevelLog().equalsIgnoreCase(listalogs.get(i).getLevelLog())) {
					model = new LogModel();						
					model.setNameLog(listalogs.get(i).getNameLog());
					model.setLevelLog(listalogs.get(i).getLevelLog());
					resulList.add(model);
				}
			}
		}
		
		// NOTA: las siguientes gestiones son experimentales y podrían no funcionar perfectamente
		// Cuando el filtro actual es diferente al nuevo, se deseleccionan todos los registros seleccionados. En realidad, existe la posibilidad de mantenerlos, pero habría que hacer muchas comparaciones y sería costoso en cuanto a memoria se refiere.
		if (filterLogModel.compare(lastFilterLogModel)) {
			// Añadir seleccionado o seleccionados (dependiendo de si es selección simple o múltiple)
			List<String> selectedIds = tableRequestDto.getMultiselection().getSelectedIds();
			if (selectedIds != null) {
				// Al cambiar el número de filas por página, hay que actualizar la ubicaci�n de los registros seleccionados
				if (lastRowsNumber != tableRequestDto.getRows()) {
					for (TableRowDto<LogModel> selectedItem : listReorderSelection) {
						// Comprueba si el registro está en la página actual para poder obtener la ubicación de una manera más sencilla
						boolean inTheSamePage = false;
						int rowIndex = 0;
						for (LogModel log : resulList) {
							if (log.getNameLog().equals(selectedItem.getModel().getNameLog())) {
								selectedItem.setPage(tableRequestDto.getPage().intValue());
								selectedItem.setPageLine(rowIndex);
								selectedItem.setTableLine(rowIndex);
								
								inTheSamePage = true;
								break;
							}
							rowIndex++;
						}
						
						// En caso de no estar el registro en la página actual, se calcula su posición
						if (!inTheSamePage) {
							int newPageNumber = (int) ((selectedItem.getPage() * lastRowsNumber) / tableRequestDto.getRows());
							int newPageLineNumber = 0;
							
							// Gestiona los cambios ascendentes o descendentes de la cantidad de registros a mostrar por página
							if (lastRowsNumber < tableRequestDto.getRows()) {
								newPageLineNumber = (int) ((((selectedItem.getPage() - 1) * lastRowsNumber) + selectedItem.getPageLine()) - (newPageNumber * tableRequestDto.getRows()));
								
								if (newPageLineNumber > tableRequestDto.getRows()) {
									newPageLineNumber = (int) (newPageLineNumber - tableRequestDto.getRows());
								} else if (newPageLineNumber < 0) {
									newPageLineNumber = (int) (newPageLineNumber + tableRequestDto.getRows());
								} else {
									newPageNumber = newPageNumber + 1;
								}
							} else {
								newPageLineNumber = (int) (((selectedItem.getPage() * lastRowsNumber) + selectedItem.getPageLine()) - (newPageNumber * tableRequestDto.getRows()));
								
								if (newPageLineNumber > tableRequestDto.getRows()) {
									newPageLineNumber = (int) (newPageLineNumber - tableRequestDto.getRows());
								} else {
									newPageNumber = newPageNumber - 1;
								}
							}
							
							selectedItem.setPage((int) (newPageNumber <= 0 ? 1 : newPageNumber));
							selectedItem.setPageLine((int) (newPageLineNumber < 0 ? (newPageNumber * tableRequestDto.getRows()) + newPageLineNumber : newPageLineNumber));
							selectedItem.setTableLine((int) (newPageLineNumber < 0 ? (newPageNumber * tableRequestDto.getRows()) + newPageLineNumber : newPageLineNumber));
						}
					}
				}
				
				if (selectedIds.size() == 1) {
					// Selección o multiselección con un registro seleccionado
					String rowID = tableRequestDto.getMultiselection().getSelectedIds().get(0);
					
					if (listReorderSelection.size() > 0) {
						if (!rowID.equals(listReorderSelection.get(0).getModel().getNameLog())) {
							setSelectionReorder(listalogs, rowID, tableRequestDto);
						}
					} else {
						setSelectionReorder(listalogs, rowID, tableRequestDto);
					}
				} else {
					// Multiselección
					setMultiselectionReorder(listalogs, selectedIds, tableRequestDto);
				}
			} else {
				// Nada seleccionado
				listReorderSelection.clear();
			}
		} else {
			// Como el filtro ha cambiado, se deselecciona todo
			listReorderSelection.clear();
		}
		
		resultado.setReorderedSelection(listReorderSelection);
		resultado.addAdditionalParam("reorderedSelection", listReorderSelection);
		resultado.addAdditionalParam("selectedAll", tableRequestDto.getMultiselection().getSelectedAll());
		
		// Añadir información necesaria para la tabla
		resultado.setRows(resulList);
		resultado.setRecords(listalogs.size());
		resultado.setPage(tableRequestDto.getPage().toString());
		resultado.setTotal(new Long(listalogs.size()), new Long(tableRequestDto.getRows()));
		
		// Guardar número de filas por página, número de la página actual y filtrado para poder obtenerlo en el siguiente filtrado
		lastRowsNumber = tableRequestDto.getRows().intValue();
		lastPageNumber = tableRequestDto.getPage().intValue();
		lastFilterLogModel = filterLogModel;
		
		return resultado;
	}

	/** 
	 * Devuelve los nombres disponibles.
	 *
	 * @param q String enviado por el cliente para la bússqueda de resultados.
	 * 
	 * @return List<Resource<SelectGenericPKs>>
	 */
	public static List<Resource<SelectGenericPKs>> getNames(String q) {		
		List<LogModel> listalogs = getLoggers(true);
		List<SelectGenericPKs> columnValues = new ArrayList<SelectGenericPKs>();
		
		if(q != null) {
			q = Normalizer.normalize(q, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		} else {
			q = "";
		}
		
		for (int i = 0; i < listalogs.size(); i++){	
			if (listalogs.get(i).getNameLog() != null) {
				String name = listalogs.get(i).getNameLog();
				name = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
				
				if (q.equals("") || name.indexOf(q) >= 0) {
					columnValues.add(new SelectGenericPKs(name, name));
				}
			}
		}
		
		return ResourceUtils.fromListToResource(columnValues);
	}

	/** 
	 * Devuelve los niveles disponibles.
	 *
	 * @return List<Resource<SelectGeneric>>
	 */
	public static List<Resource<SelectGeneric>> getLevels() {		
		List<SelectGeneric> columnValues = new ArrayList<SelectGeneric>();
		columnValues.add(new SelectGeneric("TRACE", "TRACE"));
		columnValues.add(new SelectGeneric("DEBUG", "DEBUG"));
		columnValues.add(new SelectGeneric("INFO", "INFO"));
		columnValues.add(new SelectGeneric("WARN", "WARN"));
		columnValues.add(new SelectGeneric("ERROR", "ERROR"));
		
		return ResourceUtils.fromListToResource(columnValues);
	}

	/** 
	 * Gestiona la selección cuando se pagina.
	 *
	 * @param listalogs Lista que contiene los logs.
	 * @param rowID Identificador del registro seleccionado.
	 * @param tableRequestDto DTO que contiene los parámetros de configuración propios del RUP_TABLE a aplicar en el filtrado.
	 */
	private static void setSelectionReorder(List<LogModel> listalogs, String rowID, TableRequestDto tableRequestDto) {
		Map<String, String> pkMap = new HashMap<String, String>();
		pkMap.put(tableRequestDto.getCore().getPkNames().get(0), rowID);
		
		TableRowDto<LogModel> reorderSelection = new TableRowDto<LogModel>();
		reorderSelection.setPage(lastPageNumber);
		reorderSelection.setModel(new LogModel(rowID, null));
		reorderSelection.setPkMap(pkMap);
		
		int index = 0;
		for (LogModel log : listalogs) {
			if (log.getNameLog().equals(rowID)) {
				reorderSelection.setPageLine((int) (index - ((lastPageNumber - 1) * tableRequestDto.getRows())));
				reorderSelection.setTableLine((int) (index - ((lastPageNumber - 1) * tableRequestDto.getRows())));
			}
			index++;
		}
		
		// Vaciar la lista que contiene el elemento anteriormente seleccionado
		listReorderSelection.clear();
		
		// Añadir redorderSelection a la lista
		listReorderSelection.add(reorderSelection);
	}

	/** 
	 * Gestiona la multiselección cuando se pagina.
	 *
	 * @param listalogs Lista que contiene los logs.
	 * @param rowID Identificadores de registros seleccionados.
	 * @param tableRequestDto DTO que contiene los parámetros de configuración propios del RUP_TABLE a aplicar en el filtrado.
	 */
	private static void setMultiselectionReorder(List<LogModel> listalogs, List<String> selectedIds, TableRequestDto tableRequestDto) {
		// Eliminar los elementos que hayan sido deseleccionados
		Iterator<TableRowDto<LogModel>> selectedItemIterator = listReorderSelection.iterator();
		while (selectedItemIterator.hasNext()) {
			TableRowDto<LogModel> item = selectedItemIterator.next();
			boolean alreadySelected = false;
			
			for (String rowID : selectedIds) {
				if (rowID.equals(item.getModel().getNameLog())) {
					alreadySelected = true;
					break;
				}
			}
			
			if (!alreadySelected) {
				selectedItemIterator.remove();
			}
		}
		
		for (String rowID : selectedIds) {
			boolean alreadySelected = false;
			
			for (TableRowDto<LogModel> selectedItem : listReorderSelection) {
				if (rowID.equals(selectedItem.getModel().getNameLog())) {
					alreadySelected = true;
					break;
				}
			}
			
			if (!alreadySelected) {
				Map<String, String> pkMap = new HashMap<String, String>();
				pkMap.put(tableRequestDto.getCore().getPkNames().get(0), rowID);
				
				TableRowDto<LogModel> reorderSelection = new TableRowDto<LogModel>();
				reorderSelection.setPage(lastPageNumber);
				reorderSelection.setModel(new LogModel(rowID, null));
				reorderSelection.setPkMap(pkMap);
				
				int logIndex = 0;
				for (LogModel log : listalogs) {
					if (log.getNameLog().equals(rowID)) {
						reorderSelection.setPageLine((int) (logIndex - ((lastPageNumber - 1) * tableRequestDto.getRows())));
						reorderSelection.setTableLine((int) (logIndex - ((lastPageNumber - 1) * tableRequestDto.getRows())));
					}
					logIndex++;
				}
				
				// Añadir redorderSelection a la lista
				listReorderSelection.add(reorderSelection);
			}
		}
	}
}
