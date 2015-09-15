package com.ejie.x38.dto;

import org.codehaus.jackson.annotate.JsonUnwrapped;


/**
 * Object mapper propio de UDA en el que se realiza la configuración necesaria.
 * 
 * @author UDA
 *
 */
@Deprecated
public class Jerarquia<T> implements java.io.Serializable {
	
	private static final long serialVersionUID = 2127819481595995328L;
	
	@JsonUnwrapped
	private T model;
	private int level;
	private boolean isLeaf;
	private String parentNodes;
	private boolean filter;
	
	//Grid-Maint
	private boolean hasChildren;
	private String treeNodes;
	
	public Jerarquia() {
		super();
	}
	

	/**
	 * @return the model
	 */
	public T getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(T model) {
		this.model = model;
	}
	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}
	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}
	/**
	 * @return the hasChildren
	 */
	public boolean isHasChildren() {
		return hasChildren;
	}
	/**
	 * @param hasChildren the hasChildren to set
	 */
	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}
	/**
	 * @return the isLeaf
	 */
	public boolean getIsLeaf() {
		return isLeaf;
	}
	/**
	 * @param isLeaf the isLeaf to set
	 */
	public void setIsLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}
	/**
	 * @return the parentNodes
	 */
	public String getParentNodes() {
		return parentNodes;
	}
	/**
	 * @param parentNodes the parentNodes to set
	 */
	public void setParentNodes(String parentNodes) {
		this.parentNodes = parentNodes;
	}
	/**
	 * @return the filter
	 */
	public boolean isFilter() {
		return filter;
	}
	/**
	 * @param filter the filter to set
	 */
	public void setFilter(boolean filter) {
		this.filter = filter;
	}
	/**
	 * @return the treeNodes
	 */
	public String getTreeNodes() {
		return treeNodes;
	}
	/**
	 * @param treeNodes the treeNodes to set
	 */
	public void setTreeNodes(String treeNodes) {
		this.treeNodes = treeNodes;
	}

	@Override
	public String toString() {
		return "Jerarquia [model=" + model + ", level=" + level
				+ ", hasChildren=" + hasChildren + ", isLeaf=" + isLeaf
				+ ", parentNodes=" + parentNodes + ", filter=" + filter
				+ ", treeNodes=" + treeNodes + "]";
	}
	
	

}