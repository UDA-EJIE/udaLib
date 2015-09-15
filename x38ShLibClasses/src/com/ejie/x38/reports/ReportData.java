package com.ejie.x38.reports;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.ejie.x38.dto.Jerarquia;
import com.ejie.x38.json.JSONArray;

public class ReportData<T> implements java.io.Serializable {

	private static final long serialVersionUID = 2127819481595995328L;
	
	private String sheetName;
	private boolean showHeaders = true; 
	private Map<String, String> headerNames = new LinkedHashMap<String, String>();

	//Normal
	private List<T> modelData = new ArrayList<T>();

	//Jerarquia
	boolean jerarquia;
	private List<Jerarquia<T>> modelDataJerarquia = new ArrayList<Jerarquia<T>>();
	private JerarquiaMetadata jerarquiaMetadada = new JerarquiaMetadata();
	
	/**
	 * @return the sheetName
	 */
	public String getSheetName() {
		return sheetName;
	}
	/**
	 * @param sheetName the sheetName to set
	 */
	public void setSheetName(String sheetName) {
		this.sheetName = sheetName;
	}
	/**
	 * @return the headerNames
	 */
	public Map<String, String> getHeaderNames() {
		return headerNames;
	}
	/**
	 * @param headerNames the headerNames to set
	 */
	public void setHeaderNames(Map<String, String> headerNames) {
		this.headerNames = headerNames;
	}
	/**
	 * @return the modelData
	 */
	public List<?> getModelData() {
		return modelData;
	}
	/**
	 * @return the modelDataJerarquia
	 */
	protected List<Jerarquia<T>> getModelDataJerarquia() {
		return modelDataJerarquia;
	}
	/**
	 * @param modelData the modelData to set
	 */
	@SuppressWarnings("unchecked")
	public void setModelData(List<?> modelData) {
		if (modelData.get(0) instanceof Jerarquia){
			this.modelDataJerarquia = (List<Jerarquia<T>>) modelData;
			this.setJerarquia(true);
		} else {
			this.modelData = (List<T>) modelData;
			this.setJerarquia(false);
		}
	}
	/**
	 * @return the showHeaders
	 */
	public boolean isShowHeaders() {
		return showHeaders;
	}
	/**
	 * @param showHeaders the showHeaders to set
	 */
	public void setShowHeaders(boolean showHeaders) {
		this.showHeaders = showHeaders;
	}
	
	
	
	/**
	 * @return the jerarquia
	 */
	public boolean isJerarquia() {
		return jerarquia;
	}
	/**
	 * @param jerarquia the jerarquia to set
	 */
	public void setJerarquia(boolean jerarquia) {
		this.jerarquia = jerarquia;
	}
	/**
	 * @return the jerarquiaMetadada
	 */
	public JerarquiaMetadata getJerarquiaMetadada() {
		return jerarquiaMetadada;
	}
	/**
	 * @param jerarquiaMetadada the jerarquiaMetadada to set
	 */
	public void setJerarquiaMetadada(JerarquiaMetadata jerarquiaMetadada) {
		this.jerarquiaMetadada = jerarquiaMetadada;
	}
	
	@Override
	public String toString() {
		return "ReportData [sheetName=" + sheetName + ", showHeaders="
				+ showHeaders + ", headerNames=" + headerNames + ", modelData="
				+ modelData + ", jerarquia=" + jerarquia
				+ ", jerarquiaMetadada=" + jerarquiaMetadada + "]";
	}
	
	
	public static LinkedHashMap<String, String> parseColumns (String columns){
		return ReportData.parseColumns(columns, new HashMap<String, String>());
	}
	public static LinkedHashMap<String, String> parseColumns (String columns, Map<String, String> columnsMatch){
		LinkedHashMap<String, String> parsedColumns = new LinkedHashMap<String, String>();
		JSONArray jsonArr = new JSONArray(columns);
		for (int i = 0; i < jsonArr.length(); i++) {
			JSONArray column = (JSONArray) jsonArr.get(i);
			String key = (String)column.get(0);
			String name = columnsMatch.containsKey(key)?columnsMatch.get(key):key;
			if ("".equals(name)){
				continue;
			} 
			parsedColumns.put(name,(String)column.get(1));
		}
		return parsedColumns;
	}
	
}