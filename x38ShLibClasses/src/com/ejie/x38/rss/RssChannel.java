package com.ejie.x38.rss;

public class RssChannel {

	/*
	 * The name of the channel. It's how people refer to your service. If you
	 * have an HTML website that contains the same information as your RSS file,
	 * the title of your channel should be the same as the title of your
	 * website.
	 */
	private String title;
	// The URL to the HTML website corresponding to the channel.
	private String link;
	// Phrase or sentence describing the channel.
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
