package com.ejie.x38.dao.sql;

import com.ejie.x38.dao.sql.codecs.Codec;
import com.ejie.x38.dao.sql.codecs.OracleCodec;

public class OracleEncoder {
	
	private static Codec codec;
	
	private static OracleEncoder instance;
	
	public static OracleEncoder getInstance(){
		if (OracleEncoder.instance == null){
			OracleEncoder.instance = new OracleEncoder();
			OracleEncoder.codec = new OracleCodec();
		}
		
		return OracleEncoder.instance;
	}
	
	
	private OracleEncoder(){
		
	}
	
	public String encode(String input){
		return SqlEncoder.encodeForSQL(OracleEncoder.codec, input);
	}
	
}
