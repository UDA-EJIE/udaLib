package com.ejie.x38.dao.sql;

import com.ejie.x38.dao.sql.codecs.Codec;




/**
 * Reference implementation of the Encoder interface. This implementation takes
 * a whitelist approach to encoding, meaning that everything not specifically identified in a
 * list of "immune" characters is encoded.
 * 
 * @author Jeff Williams (jeff.williams .at. aspectsecurity.com) <a
 *         href="http://www.aspectsecurity.com">Aspect Security</a>
 * @since June 1, 2007
 * @see org.owasp.esapi.Encoder
 */
public class SqlEncoder {

	private final static char[] IMMUNE_SQL = { ' ' };
	
	public static String encodeForSQL(Codec codec, String input) {
	    if( input == null ) {
	    	return null;
	    }
	    return codec.encode(IMMUNE_SQL, input);
	}

}