package com.ejie.x38.rss;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Item;

public class RssFeedView extends AbstractRssFeedView {

	// Titulo del feed
	private String title;
	// Descripción del feed
	private String descripcion;
	// Link del feed
	private String link;
	
	
	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed,
		HttpServletRequest request) {
 
		feed.setTitle(this.title);
		feed.setDescription(this.descripcion);
		feed.setLink(this.link);
		 
		super.buildFeedMetadata(model, feed, request);
	}
 
 
	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model,
		HttpServletRequest request, HttpServletResponse response)
		throws Exception {
 
		@SuppressWarnings("unchecked")
		List<RssContent> listContent = (List<RssContent>) model.get("feedContent");
		List<Item> items = new ArrayList<Item>(listContent.size());
 
		for(RssContent rssContent : listContent ){
 
			Item item = new Item();
 
			Content content = new Content();
			content.setValue(rssContent.getDescription());
			item.setContent(content);
 
			item.setTitle(rssContent.getTitle());
			item.setLink(rssContent.getLink());
			item.setPubDate(rssContent.getPubDate());
 
			items.add(item);
		}
 
		return items;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public void setLink(String link) {
		this.link = link;
	}
}
