package com.ejie.x38.reports;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class JerarquiaMetadata implements java.io.Serializable {

	private static final long serialVersionUID = 6128508737613161683L;
	
	//Mostrar elementos que cumplen filtro
	boolean showFiltered;
	String filterToken;
	String filterHeaderName; 

	//Tabulación de elementos
	boolean showTabbed;
	String tabToken;
	String tabColumnName;

	//Icono expandido/contraido
	boolean showIcon;
	String iconExpanded;
	String iconUnexpanded;
	String iconNoChild;
	String iconColumnName;
	String iconBeanAtribute;
	List<String> iconCollapsedList;
	
	public JerarquiaMetadata() {
		this.showFiltered = true;
		this.filterToken = "*";
		this.filterHeaderName = "";
		
		this.showTabbed = false;
		this.tabToken = "   ";

		this.showIcon = false;
		this.iconExpanded = "[-]";
		this.iconUnexpanded = "[+]";
		this.iconNoChild = "[ ]";
		this.iconCollapsedList = new ArrayList<String>();
	}
	
	/**
	 * @return the showFiltered
	 */
	public boolean isShowFiltered() {
		return showFiltered;
	}
	/**
	 * @param showFiltered the showFiltered to set
	 */
	public void setShowFiltered(boolean showFiltered) {
		this.showFiltered = showFiltered;
	}
	/**
	 * @return the filterToken
	 */
	public String getFilterToken() {
		return filterToken;
	}
	/**
	 * @param filterToken the filterToken to set
	 */
	public void setFilterToken(String filterToken) {
		this.filterToken = filterToken;
	}
	/**
	 * @return the filterHeaderName
	 */
	public String getFilterHeaderName() {
		return filterHeaderName;
	}
	/**
	 * @param filterHeaderName the filterHeaderName to set
	 */
	public void setFilterHeaderName(String filterHeaderName) {
		this.filterHeaderName = filterHeaderName;
	}
	/**
	 * @return the showTabbed
	 */
	public boolean isShowTabbed() {
		return showTabbed;
	}
	/**
	 * @param showTabbed the showTabbed to set
	 */
	public void setShowTabbed(boolean showTabbed) {
		this.showTabbed = showTabbed;
	}
	/**
	 * @return the tabToken
	 */
	public String getTabToken() {
		return tabToken;
	}
	/**
	 * @param tabToken the tabToken to set
	 */
	public void setTabToken(String tabToken) {
		this.tabToken = tabToken;
	}
	/**
	 * @return the tabColumnName
	 */
	public String getTabColumnName() {
		return tabColumnName;
	}
	/**
	 * @param tabColumnName the tabColumnName to set
	 */
	public void setTabColumnName(String tabColumnName) {
		this.tabColumnName = tabColumnName;
	}
	/**
	 * @return the showIcon
	 */
	public boolean isShowIcon() {
		return showIcon;
	}
	/**
	 * @param showIcon the showIcon to set
	 */
	public void setShowIcon(boolean showIcon) {
		this.showIcon = showIcon;
	}
	/**
	 * @return the iconExpanded
	 */
	public String getIconExpanded() {
		return iconExpanded;
	}
	/**
	 * @param iconExpanded the iconExpanded to set
	 */
	public void setIconExpanded(String iconExpanded) {
		this.iconExpanded = iconExpanded;
	}
	/**
	 * @return the iconUnexpanded
	 */
	public String getIconUnexpanded() {
		return iconUnexpanded;
	}
	/**
	 * @param iconUnexpanded the iconUnexpanded to set
	 */
	public void setIconUnexpanded(String iconUnexpanded) {
		this.iconUnexpanded = iconUnexpanded;
	}
	/**
	 * @return the iconNoChild
	 */
	public String getIconNoChild() {
		return iconNoChild;
	}
	/**
	 * @param iconNoChild the iconNoChild to set
	 */
	public void setIconNoChild(String iconNoChild) {
		this.iconNoChild = iconNoChild;
	}
	/**
	 * @return the iconColumnName
	 */
	public String getIconColumnName() {
		return iconColumnName;
	}
	/**
	 * @param iconColumnName the iconColumnName to set
	 */
	public void setIconColumnName(String iconColumnName) {
		this.iconColumnName = iconColumnName;
	}
	/**
	 * @return the iconBeanAtribute
	 */
	public String getIconBeanAtribute() {
		return iconBeanAtribute;
	}
	/**
	 * @param iconBeanAtribute the iconBeanAtribute to set
	 */
	public void setIconBeanAtribute(String iconBeanAtribute) {
		this.iconBeanAtribute = iconBeanAtribute;
	}
	/**
	 * @return the iconCollapsedList
	 */
	public List<String> getIconCollapsedList() {
		return iconCollapsedList;
	}

	/**
	 * @param iconCollapsedList the iconCollapsedList to set
	 */
	public void setIconCollapsedList(String iconCollapsedString) {
		if (iconCollapsedString != null){
			this.iconCollapsedList = Arrays.asList(iconCollapsedString.split(","));
		}
	}

	@Override
	public String toString() {
		return "JerarquiaMetadata [showFiltered=" + showFiltered
				+ ", filterToken=" + filterToken + ", filterHeaderName="
				+ filterHeaderName + ", showTabbed=" + showTabbed
				+ ", tabToken=" + tabToken + ", tabColumnName=" + tabColumnName
				+ ", showIcon=" + showIcon + ", iconExpanded=" + iconExpanded
				+ ", iconUnexpanded=" + iconUnexpanded + ", iconNoChild="
				+ iconNoChild + ", iconColumnName=" + iconColumnName
				+ ", iconBeanAtribute=" + iconBeanAtribute
				+ ", iconCollapsedList=" + iconCollapsedList + "]";
	}
	
	public String getTab (int level){
		StringBuilder tab = new StringBuilder();
		for (int i = 1; i < level; i++) {
			tab.append(this.getTabToken());
		}
		return tab.toString();
	}
}