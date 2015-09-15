package com.ejie.x38.util;

import java.util.Hashtable;

import com.ejie.x38.log.LogConstants;

public class TableManager {

	public static Hashtable<String, String> initTable (){
		Hashtable<String, String> table = new Hashtable<String, String>(11);
		for(String param:LogConstants.parameters){
			table.put(param, "");
		}
		return table;
	}
}