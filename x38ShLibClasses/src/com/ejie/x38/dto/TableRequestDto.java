/*
* Copyright 2011 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.dto;

import java.util.ArrayList;
import java.util.List;

import org.hdiv.services.SecureIdContainer;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

/**
 * 
 * @author UDA
 *
 */
public class TableRequestDto implements java.io.Serializable, SecureIdContainer {

	private static final long serialVersionUID = 2127819481595995328L;
	
	//jqGrid -> Table
	private Long rows;
	private Long page;
	private String sidx;
	private String sord;
	
	// Core
	private TableRequestDto.Core core = new TableRequestDto.Core();
	
	//Jerarquia
	private TableRequestDto.Jerarquia jerarquia = new TableRequestDto.Jerarquia();
	
	//Ordenacion
	private TableRequestDto.Multiselection multiselection = new TableRequestDto.Multiselection();
	
	//Ordenacion
	private TableRequestDto.Multiselection seeker= new TableRequestDto.Multiselection();
	
	
	public static final String SORT_ASC = "ASC";
	public static final String SORT_DESC = "DESC";
	
	
	public TableRequestDto(){}
	public TableRequestDto(Long rows, Long page, String sidx, String sord){
		this.rows = rows;
		this.page = page;
		this.sidx = sidx;
		this.setSord(sord);
	}
	public TableRequestDto(Long rows, Long page, String sidx, String sord, String multiselectionIds, Boolean selectAll){
		this.rows = rows;
		this.page = page;
		this.sidx = sidx;
		this.setSord(sord);
//		this.multiselectionIds = multiselectionIds;
//		this.selectedAll = selectAll;
	}
	
	public Long getRows() {
		return rows;
	}
	public void setRows(Long rows) {
		this.rows = rows;
	}
	public Long getPage() {
		return page;
	}
	public void setPage(Long page) {
		this.page = page;
	}
	public String getSidx() {
		return sidx;
	}
	public void setSidx (String sidx) {
		if (!"".equals(sidx)){ //Posible vacío en petición Ajax (jQuery >= 1.8)
			this.sidx = sidx;
		}
	}
	public String getSord() {
		return sord;
	}
	public void setSord (String sord) {
		this.sord = TableRequestDto.SORT_DESC.equals(sord.trim().toUpperCase())?TableRequestDto.SORT_DESC:TableRequestDto.SORT_ASC;
	}
	
	
	public TableRequestDto.Core getCore() {
		return core;
	}
	public void setCore(TableRequestDto.Core core) {
		this.core = core;
	}
	
	public TableRequestDto.Multiselection getMultiselection() {
		return multiselection;
	}
	public void setMultiselection(TableRequestDto.Multiselection multiselection) {
		this.multiselection = multiselection;
	}
	
	public TableRequestDto.Multiselection getSeeker() {
		return seeker;
	}
	public void setSeeker(TableRequestDto.Multiselection seeker) {
		this.seeker = seeker;
	}
	public TableRequestDto.Jerarquia getJerarquia() {
		return jerarquia;
	}
	public void setJerarquia(TableRequestDto.Jerarquia jerarquia) {
		this.jerarquia = jerarquia;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append(this.getClass().getName()).append(" Object {");
		result.append(" [ rows: ").append(this.rows).append(" ]");
		result.append(" [ page: ").append(this.page).append(" ]");
		result.append(" [ sidx: ").append(this.sidx).append(" ]");
		result.append(" [ sord: ").append(this.sord).append(" ]");
		result.append("}");
		return result.toString();
	}

	//CLASES INTERNAS
	
	public class Core{
		private List<String> pkNames;
		private String pkToken;

		public List<String> getPkNames() {
			return pkNames;
		}

		public void setPkNames(List<String> pkNames) {
			this.pkNames = pkNames;
		}

		public String getPkToken() {
			return pkToken;
		}

		public void setPkToken(String pkToken) {
			this.pkToken = pkToken;
		}
	}
	
	public class Multiselection {
		private List<String> selectedIds;
		private Boolean selectedAll;
		
		private Class<? extends Object> clazz;
		

		public List<String> getSelectedIds() {
			return selectedIds;
		}
		public void setSelectedIds(List<String> selectedIds) {
			this.selectedIds = selectedIds;
		}
		
		
		public List<? extends Object> getSelected(){
			return this.getSelected(this.clazz);
		}
		
		@SuppressWarnings("unchecked")
		public <T extends Object> List<T> getSelected(Class<T> clazz){
			String pkToken = TableRequestDto.this.getCore().getPkToken();
			List<String> pkNames = TableRequestDto.this.getCore().getPkNames();
			
			List<T> selectedIdsList = new ArrayList<T>();
			
			for (String string : selectedIds) {
				BeanWrapper beanWrapper = new BeanWrapperImpl(clazz);
				String[] split = string.split(pkToken);
				for (int i = 0; i < split.length; i++) {
					String string2 = split[i];
					beanWrapper.setPropertyValue(pkNames.get(i), string2);
				}
				
				selectedIdsList.add((T)beanWrapper.getWrappedInstance());
			}
			
			return selectedIdsList;
		}
		
		public Boolean getSelectedAll() {
			return selectedAll;
		}
		public void setSelectedAll(Boolean selectedAll) {
			this.selectedAll = selectedAll;
		}
		
		@SuppressWarnings("unchecked")
		public <T extends Object> void setModel(String strClazz){
			try {
				this.clazz = (Class<T>) Class.forName(strClazz);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				/*
				 * FIXME: Gestion error
				 */
			}
		}
	}
	
	public class Jerarquia {
		private String token = "/";		//Separador para los tooltips
		private String tree;			//Nombre elementos expandidos/contraídos
		private String parentId;		//Elemento sobre el que obtener hijos/descendientes
		private boolean child;			//Determina si sólo deben buscarse hijos directos

		public String getToken() {
			return token;
		}
		public void setToken(String token) {
			this.token = token;
		}
		public String getTree() {
			return tree;
		}
		public void setTree(String tree) {
			this.tree = tree;
		}
		public String getParentId() {
			return parentId;
		}
		public void setParentId(String parentId) {
			this.parentId = parentId;
		}
		public boolean isChild() {
			return child;
		}
		public void setChild(boolean child) {
			this.child = child;
		}
	}
	
	//Retrocompatibilidad
	//*******************
		//MANAGER
//		@Deprecated
//		public StringBuilder getPaginationQuery(StringBuilder query){
//			return PaginationManager.getQueryForPagination(this, query, false);
//	    }
//		@Deprecated
//		public StringBuilder getPaginationQueryJerarquia(StringBuilder query){
//			return PaginationManager.getQueryForPagination(this, query, true);
//		}
//		@Deprecated
//		public List<?> getPaginationList(List<?> list){
//			return PaginationManager.getPaginationList(this, list);
//		}
//		@Deprecated
//		public StringBuilder getReorderQuery(StringBuilder query, String... pkCols){
//			return PaginationManager.getReorderQuery(this, query, pkCols);
//	    }
	
		//Pagination
		@Deprecated
		public String getSort() {
			return getSidx();
		}
		@Deprecated
		public void setSort(String sidx) {
			setSidx(sidx);
		}	
		@Deprecated
		public String getAscDsc() {
			return getSord();
		}
		@Deprecated
		public void setAscDsc(String sord) {
			setSord(sord);
		}
}