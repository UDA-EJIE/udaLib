package com.ejie.x38.dao.sql;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.aop.AopInvocationException;

/**
 * The Class NlsLangControl.
 */
@Aspect
public class NlsLangControl {

	/** The sqls. */
	private List<String> sqls;
	
	/** The problems. */
	boolean problems = Boolean.TRUE;
	
	/** The nls config. */
	HashMap<String,String> nlsConfig = new HashMap<String, String>(); 
	
	/**
	 * Instantiates a new nls lang control.
	 */
	NlsLangControl(){}
	
    /**
     * Prepare nls lang.
     *
     * @param connection the connection
     * @throws SQLException the SQL exception
     */
    @AfterReturning(value = "execution(java.sql.Connection javax.sql.DataSource+.getConnection(..))", returning = "connection")
    private void prepareNlsLang (Connection connection) throws SQLException {
    	
    	if(this.problems) {
    		
	    	Statement statement = null;
	    	try{
	    		
	    		statement = connection.createStatement();
	    		
	    		if(nlsConfig.size() == 0) {
	    			ResultSet sessionPrametersRS = statement.executeQuery("select * from nls_session_parameters");
	    			
	    			while (sessionPrametersRS.next()) {
	    				nlsConfig.put(sessionPrametersRS.getString("PARAMETER"), sessionPrametersRS.getString("VALUE"));
	    			}
	    			
	    			if("SPANISH".equalsIgnoreCase(nlsConfig.get("NLS_LANGUAGE"))){
	    				problems = Boolean.FALSE;
	    			} else {
	    				problems = Boolean.TRUE;
	    			}
	    		}
	    		
	    		//Validamos el update
	    		ResultSet validateRS = statement.executeQuery("select * from nls_session_parameters");
	    		
	    		while (validateRS.next()) {
    				
	    			if(validateRS.getString("PARAMETER").equalsIgnoreCase("NLS_LANGUAGE")) {
	    				if(!("SPANISH".equalsIgnoreCase(validateRS.getString("VALUE")))){
	    					for (String sql : sqls) {
	    						statement.executeUpdate(sql);
	    					}
	    					
	    				}
	    				
	    				//Una vez valorado el idioma salimos de la valoracion de datos
	    				break;
	    			}
    			}
	    		
	    		
	    	}catch(SQLException ex){
	    		throw new AopInvocationException("Se ha producido un error en la ejecución del aspecto", ex);
	    	}finally{
	    		if (statement != null) statement.close();
	    	}
    	}
    }

	/**
	 * Sets the sqls.
	 *
	 * @param sqls the new sqls
	 */
	public void setSqls(List<String> sqls) {
		this.sqls = sqls;
	}
	
	/**
	 * Gets the nls config.
	 *
	 * @return the nls config
	 */
	public Map<String,String> getNlsConfig() {
		return this.nlsConfig; 
	}
}
