package com.ejie.x38.log;

import com.ejie.w43ta.clients.W43taMomoTraceClient;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Layout;
import ch.qos.logback.core.status.ErrorStatus;

public class MomoAppender extends AppenderBase<ILoggingEvent>{

	private Layout<ILoggingEvent> layout;
	
	

	
	private W43taMomoTraceClient mtc; 
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
	
	
	@Override
    public void start() {
        try {
        	if(this.doMomo && !this.develomentMode){
        		this.mtc = W43taMomoTraceClient.getInstance(
					  this.servicio
					, this.app
					, this.securityTokenId
					, this.w43taEndpointUri
					, this.w43taEndpointPort
					, this.develomentMode);
        	}	
            
        } catch (Exception e) {
            addStatus(new ErrorStatus("Failed to initialize MOMO", this, e));
            return;
        }
        super.start();
    }
	
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

	@Override
	protected void append(ILoggingEvent event) {
		
		if(this.doMomo && !this.develomentMode){
		
			switch(event.getLevel().levelInt){
			case Level.DEBUG_INT:
				this.mtc.debug(this.layout.doLayout(event));
				break;
			case Level.INFO_INT:
				this.mtc.info(this.layout.doLayout(event));
				break;
			case Level.WARN_INT:
				this.mtc.warning(this.layout.doLayout(event));
				break;
			case Level.ERROR_INT:
				this.mtc.error(this.layout.doLayout(event));
				break;
			}
		}
	}
	
}
