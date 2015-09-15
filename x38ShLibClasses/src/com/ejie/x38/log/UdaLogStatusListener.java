/*
* Copyright 2011 E.J.I.E., S.A.
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
package com.ejie.x38.log;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.spi.ContextAwareBase;
import ch.qos.logback.core.spi.LifeCycle;
import ch.qos.logback.core.status.ErrorStatus;
import ch.qos.logback.core.status.Status;
import ch.qos.logback.core.status.StatusListener;
import ch.qos.logback.core.status.StatusManager;
import ch.qos.logback.core.util.StatusPrinter;

/**
 *
 * Lisen responsible for writing the state of logback in the output of logs
 * 
 * @author UDA
 *  
 */
public class UdaLogStatusListener extends ContextAwareBase implements StatusListener, LifeCycle {

	final static Logger logger = LoggerFactory.getLogger(UdaLogStatusListener.class);
	static final long DEFAULT_RESTROSPECTIVE = 300L;
	boolean isStarted = false;
	long retrospective = 300L;

	private void print(Status status) {
		StringBuilder sb = null;
		if (ErrorStatus.ERROR == status.getEffectiveLevel()){
			sb = new StringBuilder(status.toString());
			StatusPrinter.buildStr(sb, "", status);
			System.out.println(sb);
			sb = null;
//			logger.error(sb.toString(), status.getThrowable());
		}
//		else if (ErrorStatus.WARN == status.getEffectiveLevel()){
//			sb = new StringBuilder(status.toString());
//			logger.warn(sb.toString());
//		} else {
//			sb = new StringBuilder(status.toString());
//			logger.info(sb.toString());
//		}
	}

	public void addStatusEvent(Status status) {
		if (!this.isStarted)
			return;
		print(status);
	}

	private void retrospectivePrint()
	{
		long now = System.currentTimeMillis();
		StatusManager sm = this.context.getStatusManager();
		List<Status> statusList = sm.getCopyOfStatusList();
		for (Status status : statusList) {
			long timestamp = status.getDate().longValue();
			if (now - timestamp < this.retrospective)
				print(status);
		}
	}

	public void start()
	{
		this.isStarted = true;
		if (this.retrospective > 0L)
			retrospectivePrint();
	}

	public void setRetrospective(long retrospective)
	{
		this.retrospective = retrospective;
	}

	public long getRetrospective() {
		return this.retrospective;
	}

	public void stop() {
		this.isStarted = false;
	}

	public boolean isStarted() {
		return this.isStarted;
	}
}