package com.ejie.x38.rss;

import java.util.Date;

/**
 * Representación de un elemento de un reed RSS.
 * 
 * @author UDA
 *
 */
public class RssContent {

	// The title of the item.
	private String title;
	// The URL of the item.
	private String link;
	// The item synopsis.
	private String description;
	
	/*
	 * OPTIONALS
	 */
	// Email address of the author of the item.
	private String author;
	// Includes the item in one or more categories.
	private String category;
	// URL of a page for comments relating to the item.
	private String comments;
	// Describes a media object that is attached to the item.
	private String enclosure;
	// A string that uniquely identifies the item.
	private String guid;
	// Indicates when the item was published.
	private Date pubDate;
	// The RSS channel that the item came from.
	private String source;
	
	
	public RssContent() {
		super();
	}
	
	public RssContent(String title, String link, String description,
			String author, String category, String comments, String enclosure,
			String guid, Date pubDate, String source) {
		super();
		this.title = title;
		this.link = link;
		this.description = description;
		this.author = author;
		this.category = category;
		this.comments = comments;
		this.enclosure = enclosure;
		this.guid = guid;
		this.pubDate = pubDate;
		this.source = source;
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
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getEnclosure() {
		return enclosure;
	}
	public void setEnclosure(String enclosure) {
		this.enclosure = enclosure;
	}
	public String getGuid() {
		return guid;
	}
	public void setGuid(String guid) {
		this.guid = guid;
	}
	public Date getPubDate() {
		return pubDate;
	}
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	
	
	
}
