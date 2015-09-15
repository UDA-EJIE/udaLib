package com.ejie.x38.rss;

/**
 * Representación de un canal de un feed RSS.
 * 
 * @author UDA
 *
 */
public class RssChannel {

	
	/**
	 * El nombre del canal. Es el cnombre por el que se va a referenciar el servicio.
	 */
	private String title;
	/**
	 * La URL del sitio web correspondiente al canal..
	 */
	private String link;
	/**
	 * Descripción del canal
	 */
	private String description;
	
	public RssChannel() {
		super();
	}

	public RssChannel(String title, String link, String description) {
		super();
		this.title = title;
		this.link = link;
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}
