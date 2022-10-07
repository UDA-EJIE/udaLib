package com.ejie.x38.dao.sql.codecs;

/**
 * Implementation of the Codec interface for Oracle strings. This function will only protect you from SQLi in the case of user data
 * bring placed within an Oracle quoted string such as:
 * 
 * select * from table where user_name='  USERDATA    ';
 * 
 * @see <a href="http://oraqa.com/2006/03/20/how-to-escape-single-quotes-in-strings/">how-to-escape-single-quotes-in-strings</a>
 * 
 * @author Jeff Williams (jeff.williams .at. aspectsecurity.com) <a href="http://www.aspectsecurity.com">Aspect Security</a>
 * @author Jim Manico (jim@manico.net) <a href="http://www.manico.net">Manico.net</a>
 * @since June 1, 2007
 * @see org.owasp.esapi.Encoder
 */
public class OracleCodec extends Codec {


	/**
	 * {@inheritDoc}
	 * 
	 * Encodes ' to ''
     *
	 * Encodes ' to ''
     *
     * @param immune
     */
	public String encodeCharacter( char[] immune, Character c ) {
		if ( c.charValue() == '\'' )
        	return "\'\'";
        return ""+c;
	}
	


	/**
	 * {@inheritDoc}
	 *
	 * Returns the decoded version of the character starting at index, or
	 * null if no decoding is possible.
	 *
	 * Formats all are legal
	 *   '' decodes to '
	 */
	public Character decodeCharacter( PushbackString input ) {
		input.mark();
		Character first = input.next();
		if ( first == null ) {
			input.reset();
			return null;
		}

		// if this is not an encoded character, return null
		if ( first.charValue() != '\'' ) {
			input.reset();
			return null;
		}

		Character second = input.next();
		if ( second == null ) {
			input.reset();
			return null;
		}
		
		// if this is not an encoded character, return null
		if ( second.charValue() != '\'' ) {
			input.reset();
			return null;
		}
		return( Character.valueOf( '\'' ) );
	}

}
