package com.ejie.x38.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Pagination
  implements Serializable
{
  private static final long serialVersionUID = 1L;
  private Long rows;
  private Long page;
  private String sidx;
  private String sord;

  public Pagination()
  {
  }

  public Pagination(Long rows, Long page, String sidx, String sord)
  {
    this.rows = rows;
    this.page = page;
    this.sidx = sidx;
    this.sord = sord;
  }

  public Long getRows() {
    return this.rows;
  }
  public void setRows(Long rows) {
    this.rows = rows;
  }
  public Long getPage() {
    return this.page;
  }
  public void setPage(Long page) {
    this.page = page;
  }
  public String getSidx() {
    return this.sidx;
  }
  public void setSidx(String sidx) {
    if (!"".equals(sidx))
      this.sidx = sidx;
  }

  public String getSord() {
    return this.sord;
  }
  public void setSord(String sord) {
    this.sord = sord;
  }

  public String toString() {
    StringBuilder result = new StringBuilder();
    result.append(getClass().getName()).append(" Object {");
    result.append(" [ rows: ").append(this.rows).append(" ]");
    result.append(" [ page: ").append(this.page).append(" ]");
    result.append(" [ sidx: ").append(this.sidx).append(" ]");
    result.append(" [ sord: ").append(this.sord).append(" ]");
    result.append("}");
    return result.toString();
  }

  public StringBuilder getPaginationQuery(StringBuilder query)
  {
    StringBuilder paginationQuery = new StringBuilder();
    if (getSidx() != null) {
      paginationQuery.append(" ORDER BY ");
      paginationQuery.append(getSidx());
      paginationQuery.append(" ");
      paginationQuery.append(getSord());
      query.append(paginationQuery);
    }

    paginationQuery = new StringBuilder();
    Long rows = getRows();
    Long page = getPage();
    if ((page != null) && (rows != null))
      paginationQuery.append("SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > " + rows.longValue() * (page.longValue() - 1L) + " and rnum < " + (rows.longValue() * page.longValue() + 1L));
    else if (rows != null)
      paginationQuery.append("SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > 0 and rnum < " + (rows.longValue() + 1L));
    else {
      return query;
    }
    return paginationQuery;
  }

  public List<?> getPaginationList(List<?> list) {
    List<Object> returnList = new ArrayList<Object>();

    Long rows = getRows();
    Long page = getPage();
    if ((page != null) && (rows != null)) {
      for (int i = (int)(rows.longValue() * (page.longValue() - 1L)); i < rows.longValue() * page.longValue(); i++)
        returnList.add(list.get(i));
    }
    else if (rows != null) {
      for (int i = 0; i < rows.longValue(); i++)
        returnList.add(list.get(i));
    }
    else {
      return list;
    }
    return returnList;
  }

  public String getSort()
  {
    return getSidx();
  }
  public void setSort(String sidx) {
    setSidx(sidx);
  }
  public String getAscDsc() {
    return getSord();
  }
  public void setAscDsc(String sord) {
    setSord(sord);
  }
}