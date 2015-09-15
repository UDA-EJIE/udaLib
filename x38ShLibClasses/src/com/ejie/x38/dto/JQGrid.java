package com.ejie.x38.dto;

public class JQGrid extends Pagination implements java.io.Serializable {

	private static final long serialVersionUID = 1L;
	private String _search;
	private String nd;
	
	/**
	 * @return the _search
	 */
	public String get_search() {
		return _search;
	}

	/**
	 * @param _search the _search to set
	 */
	public void set_search(String _search) {
		this._search = _search;
	}

	/**
	 * @return the nd
	 */
	public String getNd() {
		return nd;
	}

	/**
	 * @param nd the nd to set
	 */
	public void setNd(String nd) {
		this.nd = nd;
	}

	public JQGrid(){}
	
	public JQGrid(String _search, String nd, String rows, Long page, String ascDsc, String sort) {
		super(new Long(rows), page, ascDsc, sort);
		this._search = _search;
		this.nd = nd;		
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		String newLine = System.getProperty("line.separator");
		result.append(this.getClass().getName() + " Object {" + newLine);
		result.append(" _search: " + this._search + newLine);
		result.append(" nd: " + this.nd + newLine);
		result.append("}");
		return result.toString();
	}
}