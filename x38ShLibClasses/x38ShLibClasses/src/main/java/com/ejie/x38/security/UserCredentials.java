/*
* Copyright 2012 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.security;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.LazyDynaBean;
import org.apache.commons.beanutils.MutableDynaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejie.x38.util.Constants;

/**
 * 
 * @author UDA
 *
 */
public class UserCredentials implements Credentials {

	private static final long serialVersionUID = 1L;
	
	private static final Logger logger = LoggerFactory
	.getLogger(UserCredentials.class);
	
	private String nif = null;
	private String policy = null;
	private String userName = "NULL";
	private String name = "NULL";
	private String surname = "NULL";
	private String fullName = "NULL";
	private String position = "NULL";
	private String uidSession = null;
	private boolean isCertificate = false;
	private String udaValidateSessionId = null;
	private Vector<String> userProfiles = null;
	private boolean destroySessionSecuritySystem = false;
	private DynaBean subjectCert = null;
	private ArrayList<String> userDataProperties = null;
	
	//Constructor functions
	public UserCredentials(){
		super();
	}

	public UserCredentials(Vector<String> userProfiles, String userName, HashMap<String,String> userData, String nif,
			String uidSession, String position, String udaValidateSessionId, String policy, boolean isCertificate, boolean destroySessionSecuritySystem) {
		
		super();
		
		this.nif = nif;
		this.userName = userName;
		this.uidSession = uidSession;
		this.position = position;
		this.udaValidateSessionId = udaValidateSessionId;
		this.userProfiles = userProfiles;
		this.isCertificate = isCertificate;
		this.policy = policy;
		this.destroySessionSecuritySystem = destroySessionSecuritySystem;
		
		dinamicSubjectCertLoad(userData);
	}
	
	public UserCredentials(Vector<String> userProfiles, String userName, String name, String surname, String fullName, String nif,
			String uidSession, String position, String udaValidateSessionId, String policy, boolean isCertificate, boolean destroySessionSecuritySystem) {
		super();
		this.nif = nif;
		this.userName = userName;
		this.name = name;
		this.surname = surname;
		this.fullName = fullName;
		this.uidSession = uidSession;
		this.position = position;
		this.udaValidateSessionId = udaValidateSessionId;
		this.userProfiles = userProfiles;
		this.isCertificate = isCertificate;
		this.policy = policy;
		this.destroySessionSecuritySystem = destroySessionSecuritySystem;
	}
	
	public UserCredentials(Vector<String> userProfiles, String userName, String nif,
			String uidSession, String position, String udaValidateSessionId, String policy, boolean isCertificate) {
		super();
		this.nif = nif;
		this.userName = userName;
		this.uidSession = uidSession;
		this.position = position;
		this.udaValidateSessionId = udaValidateSessionId;
		this.userProfiles = userProfiles;
		this.isCertificate = isCertificate;
		this.policy = policy;
	}

	//Getters & Setters
	public String getNif() {
		return this.nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}
	
	public String getPolicy() {
		return this.policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getFullName() {
		return fullName;
	}

	public void setfullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPosition() {
		return position;
	}
	
	public void setPosition(String position) {
		this.position = position;
	}
	
	public String getUidSession() {
		return uidSession;
	}

	public void setUidSession(String uidSession) {
		this.uidSession = uidSession;
	}
	
	public boolean getIsCertificate() {
		return this.isCertificate;
	}

	public void setIsCertificate(boolean isCertificate) {
		this.isCertificate = isCertificate;
	}
	
	public String getUdaValidateSessionId() {
		return udaValidateSessionId;
	}

	public void setUdaValidateSessionId(String udaXLNetsSessionId) {
		this.udaValidateSessionId = udaXLNetsSessionId;
	}
	
	public Vector<String> getUserProfiles() {
		return userProfiles;
	}

	public void setUserProfiles(Vector<String> userProfiles) {
		this.userProfiles = userProfiles;
	}
	
	public boolean getDestroySessionSecuritySystem() {
		return this.destroySessionSecuritySystem;
	}

	public void setDestroySessionSecuritySystem(boolean destroySessionSecuritySystem) {
		this.destroySessionSecuritySystem = destroySessionSecuritySystem;
	}
	
	public ArrayList<String> getUserDataProperties(){
		return this.userDataProperties;
	}
		
	//Functions of Data gestion
	public String toString() {

		StringBuffer strBuffer = new StringBuffer();

		strBuffer.append("UserCredentials [");
		strBuffer.append("userName=").append(userName).append(";");
		
		if(nif != null){
			strBuffer.append("nif=").append("Object NOT NULL (info protected)").append(";");
		} else {
			strBuffer.append("nif=").append("NULL").append(";");
		}
		
		strBuffer.append("name=").append(name).append(";");
		strBuffer.append("surName=").append(surname).append(";");
		strBuffer.append("fullName=").append(fullName).append(";");
		
		if(uidSession != null){
			strBuffer.append("uidSession=").append("Object NOT NULL (info protected)").append(";");
		} else {
			strBuffer.append("uidSession=").append("NULL").append(";");
		}
		
		strBuffer.append("position=").append(position).append(";");
		
		if(udaValidateSessionId != null){
			strBuffer.append("udaXLNetsSessionId=").append("Object NOT NULL (info protected)").append(";");
		} else {
			strBuffer.append("udaXLNetsSessionId=").append("NULL").append(";");
		}
		
		if (userProfiles != null){
			if (userProfiles.size() > 0){
				strBuffer.append("userProfiles=").append("Object NOT NULL. Its size is ").append(userProfiles.size()).append(" (info protected)").append(";");
			} else {
				strBuffer.append("userProfiles=").append("The user doesn't have permissions").append(";");
			}
		} else {
			strBuffer.append("userProfiles=").append("NULL").append(";");
		}
		
		if(isCertificate){
			strBuffer.append("isCertificate=").append("true").append(";");
		} else {
			strBuffer.append("isCertificate=").append("false").append(";");
		}
		
		if(policy != null){
			strBuffer.append("policy=").append("Object NOT NULL (info protected)").append(";");
		} else {
			strBuffer.append("policy=").append("NULL").append(";");
		}
		
		if(destroySessionSecuritySystem){
			strBuffer.append("destroySessionSecuritySystem=").append("true").append(";");
		} else {
			strBuffer.append("destroySessionSecuritySystem=").append("false").append(";");
		}
		
		if (this.subjectCert != null){
			strBuffer.append("subjectCertData=");
			String property;
			strBuffer.append("{");
			for(int i=0; i < this.userDataProperties.size(); i++){
				property = this.userDataProperties.get(i);
				strBuffer.append(property);
				strBuffer.append(":");
				strBuffer.append(getSubjectCert(property));
				if(i < this.userDataProperties.size()-1){
					strBuffer.append(", ");
				}
			}
			strBuffer.append("}");
		}
		
		strBuffer.append("]");

		return strBuffer.toString();
	}
	
	public void loadCredentialsData(PerimetralSecurityWrapper perimetralSecurityWrapper, HttpServletRequest request){
		this.uidSession = perimetralSecurityWrapper.getUserConnectedUidSession(request);
		this.userName = perimetralSecurityWrapper.getUserConnectedUserName(request);
		this.position = perimetralSecurityWrapper.getUserPosition(request);
		this.userProfiles = perimetralSecurityWrapper.getUserInstances(request);
		this.udaValidateSessionId = perimetralSecurityWrapper.getUdaValidateSessionId(request);
		this.policy = perimetralSecurityWrapper.getPolicy(request);
		this.isCertificate = perimetralSecurityWrapper.getIsCertificate(request);
		this.nif = perimetralSecurityWrapper.getNif(request);
		this.destroySessionSecuritySystem = perimetralSecurityWrapper.getDestroySessionSecuritySystem();
		
		dinamicSubjectCertLoad(perimetralSecurityWrapper.getUserDataInfo(request, this.isCertificate));
		
		afterCredentialsCreation(perimetralSecurityWrapper, request);
		
		logger.info( "The incoming user's Credentials are loading. The data of its credentials is: [uidSession = "+uidSession+" ] [userName = "+userName+" ] [position = "+position+"]");
	}
	
	private void dinamicSubjectCertLoad(HashMap<String, String> userData){
		
		this.name = userData.get("name");
		this.surname = userData.get("surname");
		this.fullName = userData.get("fullName");
		
		userData.remove("name");
		userData.remove("surname");
		userData.remove("fullName");
		
		try{
			
			if(isCertificate){
				// extract and save the subjectCert's info  
				Iterator<Map.Entry<String, String>> userDataIterator = userData.entrySet().iterator();
				Map.Entry<String, String> userDataEntry;
				this.userDataProperties = new ArrayList<String>();
				this.subjectCert = new LazyDynaBean();
				MutableDynaClass subjectCertClass = (MutableDynaClass)this.subjectCert.getDynaClass();
				
				// create the subjectCert's properties
				while (userDataIterator.hasNext()) {
					userDataEntry = userDataIterator.next();
					this.userDataProperties.add(userDataEntry.getKey());
					subjectCertClass.add(userDataEntry.getKey(), String.class);
				}
			
			    // set its properties
			    userDataIterator = userData.entrySet().iterator();
			    
			    while (userDataIterator.hasNext()) {
					userDataEntry = userDataIterator.next();
					this.subjectCert.set(userDataEntry.getKey(), userDataEntry.getValue());
				}
			}
		} catch (Exception exc) {
			logger.error("It was Produced a error in the subjectCert's info load. All gets of subjectCert will be empties (\"\")", exc);
			this.subjectCert = null;
		}
		
	    userData = null;
	}
	
	//Adaptation method for applications
	protected void afterCredentialsCreation(PerimetralSecurityWrapper perimetralSecurityWrapper, HttpServletRequest request){}
	
	//Functions to manage the SubjectCert's data  
	public boolean containsSubjectCert(String id){
		if(!id.equals("")){
			for(int i=0; i < this.userDataProperties.size(); i++){
				if (this.userDataProperties.get(i).equals(id)){
					return true;
				}
			}
		}
		return false;
	}
	
	private void deleteUserDataProperties(String id){
		if(!id.equals("")){
			for(int i=0; i < this.userDataProperties.size(); i++){
				if (this.userDataProperties.get(i).equals(id)){
					this.userDataProperties.remove(i);
				}
			}
		}
	}
	
	public String getSubjectCert(String data){
		if (this.subjectCert != null){
			if(containsSubjectCert(data)){
				return (String)this.subjectCert.get(data);
			} else {
				return Constants.dataNotApplicable;
			}
		} else {
			return "";
		}
	}
	
	public void setSubjectCert(String property, String value){
		if (this.subjectCert != null){
			if(!containsSubjectCert(property)){
				MutableDynaClass subjectCertClass = (MutableDynaClass)this.subjectCert.getDynaClass();
				subjectCertClass.add(property, String.class);
				this.userDataProperties.add(property);
			}
			this.subjectCert.set(property, value);
		} 
	}
	
	public void deleteSubjectCert(String property){
		if (this.subjectCert != null){
			if(containsSubjectCert(property)){
				deleteUserDataProperties(property);
				MutableDynaClass subjectCertClass = (MutableDynaClass)this.subjectCert.getDynaClass();
				subjectCertClass.remove(property);
			} 
		}
	}
}