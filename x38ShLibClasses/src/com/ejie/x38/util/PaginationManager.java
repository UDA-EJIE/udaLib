package com.ejie.x38.util;

import com.ejie.x38.dto.Pagination;

public class PaginationManager {
	public static String getQueryLimits(Pagination pagination , String query)
    {
		String queryAux="";
		if (pagination.getPage()!=null && pagination.getRows()!=null){
			Long paginationRows = pagination.getRows();	
			queryAux= "SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > " + (paginationRows*(pagination.getPage()-1)) +" and rnum < " +(paginationRows*(pagination.getPage())+1);
			return queryAux;
		}else{
			Long paginationRows = pagination.getRows();	
			queryAux= "SELECT * FROM (SELECT rownum rnum, a.*  FROM (" + query + ")a) where rnum > 1 and rnum < " +(paginationRows+1);
			return queryAux;
		}
		
        
    }
}
