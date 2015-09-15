package com.ejie.x38.dto;

public class TableRow<T> {

	private String id;
	private Integer rowNum;
	private Integer page;
	private Integer pageRowNum;
	private T obj;
	
	
	public TableRow() {
		super();
	}
	
	public TableRow(T obj) {
		super();
		this.obj = obj;
	}

	public TableRow(String id, Integer rowNum, Integer page) {
		super();
		this.id = id;
		this.rowNum = rowNum;
		this.page = page;
	}
	
	public TableRow(String id, Integer rowNum, Integer page, T obj) {
		super();
		this.id = id;
		this.rowNum = rowNum;
		this.page = page;
		this.obj = obj;
	}


	public String getId() {
		return id;
	}


	public void setId(String id) {
		this.id = id;
	}


	public Integer getRowNum() {
		return this.rowNum;
	}


	public void setRowNum(Integer rowNum) {
		this.rowNum = rowNum;
	}


	public Integer getPage() {
		return this.page;
	}


	public void setPage(Integer page) {
		this.page = page;
	}

	
	public T getObj() {
		return obj;
	}

	
	public void setObj(T obj) {
		this.obj = obj;
	}
	
	public void applyPagination(Pagination pagination){
		this.page = this.rowNum / pagination.getPage().intValue();
		this.pageRowNum = this.rowNum % pagination.getPage().intValue()+1;
	}
}
