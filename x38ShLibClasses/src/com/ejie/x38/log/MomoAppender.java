package com.ejie.x38.log;

import com.ejie.w43ta.clients.W43taMomoTraceClient;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.status.ErrorStatus;

public class MomoAppender extends AppenderBase<ILoggingEvent>{

	private Layout<ILoggingEvent> layout;
	
	/**
	 * SERVICIO
	 */
	private String servicio;
	/**
	 * APP
	 */
	private String app;
	/**
	 * SECURITY_TOKEN_ID
	 */
	private String securityTokenId;
	/**
	 * W43TA_ENDPOINT_URI
	 */
	private String w43taEndpointUri;
	/**
	 * W43TA_ENDPOINT_PORT
	 */
	private Integer w43taEndpointPort;
	/**
	 * DEVELOPMENT_MODE
	 */
	private Boolean develomentMode;
	/**
	 * doMomo
	 */
	private Boolean doMomo;
	
	
	
	public void setServicio(String servicio) {
		this.servicio = servicio;
	}


	public void setApp(String app) {
		this.app = app;
	}


	public void setSecurityTokenId(String securityTokenId) {
		this.securityTokenId = securityTokenId;
	}


	public void setW43taEndpointUri(String w43taEndpointUri) {
		this.w43taEndpointUri = w43taEndpointUri;
	}


	public void setW43taEndpointPort(Integer w43taEndpointPort) {
		this.w43taEndpointPort = w43taEndpointPort;
	}


	public void setDevelomentMode(Boolean develomentMode) {
		this.develomentMode = develomentMode;
	}

	public void setDoMomo(Boolean doMomo) {
		this.doMomo = doMomo;
	}
	
	
	public void setLayout(Layout<ILoggingEvent> layout) {
		this.layout = layout;
	}

	private W43taMomoTraceClient getMomoTraceClient(String servicio, String app, String token, String serviceEndPoint, int port, boolean isLocal){
		try{
			return W43taMomoTraceClient.getInstance(servicio, app, securityTokenId, w43taEndpointUri, w43taEndpointPort, develomentMode);
		} catch (Exception e) {
            addStatus(new ErrorStatus("Failed to initialize MOMO", this, e));
            return null;
        }
	}
						
	
	@Override
	protected void append(ILoggingEvent event) {
		
		if(this.doMomo && !this.develomentMode){
			
			W43taMomoTraceClient mtc = this.getMomoTraceClient(
					this.servicio,
					this.app,
					this.securityTokenId,
					this.w43taEndpointUri,
					this.w43taEndpointPort,
					this.develomentMode);
			
			if (mtc!=null){
			
				switch(event.getLevel().levelInt){
				case Level.DEBUG_INT:
					mtc.debug(this.layout.doLayout(event));
					break;
				case Level.INFO_INT:
					mtc.info(this.layout.doLayout(event));
					break;
				case Level.WARN_INT:
					mtc.warning(this.layout.doLayout(event));
					break;
				case Level.ERROR_INT:
					mtc.error(this.layout.doLayout(event));
					break;
				}
			}
		}
	}
	
}
