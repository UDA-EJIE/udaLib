package com.ejie.x38.rss;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.servlet.view.feed.AbstractRssFeedView;

import com.ejie.x38.rss.exception.RssInitializationException;
import com.sun.syndication.feed.rss.Channel;
import com.sun.syndication.feed.rss.Content;
import com.sun.syndication.feed.rss.Item;

/**
 * Clase encargada de resolver la view que va a devolver el feed RSS.
 * 
 * @author UDA
 *
 */
public class RssFeedView extends AbstractRssFeedView {

	/**
	 * Titulo del feed.
	 */
	private String title;
	/**
	 * Descripción del feed.
	 */
	private String descripcion;
	/**
	 * Enlace al feed.
	 */
	private String link;
	/**
	 * Key utilizada para internacionalizar el título del feed.
	 */
	private String i18nTitle;
	/**
	 * Key utilizada para internacionalizar la descripción del feed.
	 */
	private String i18nDescripcion;

	@Autowired(required = false)
	private ReloadableResourceBundleMessageSource messageSource;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void buildFeedMetadata(Map<String, Object> model, Channel feed, HttpServletRequest request) {

		if (this.i18nTitle != null && this.i18nDescripcion != null && this.messageSource == null) {
			throw new RssInitializationException("No se puede resolver el valor de la propiedad i18nTitle al no existir un bean messageSource definido");
		}

		if (this.i18nDescripcion != null && this.messageSource == null) {
			throw new RssInitializationException("No se puede resolver el valor de la propiedad i18nDescripcion al no existir un bean messageSource definido");
		}

		// Titulo del feed
		if (this.i18nTitle != null) {
			feed.setTitle(this.messageSource.getMessage(this.i18nTitle, null, LocaleContextHolder.getLocale()));
		} else {
			feed.setTitle(this.title);
		}

		// Descripción del feed
		if (this.i18nDescripcion != null) {
			feed.setDescription(this.messageSource.getMessage(this.i18nDescripcion, null, LocaleContextHolder.getLocale()));
		} else {
			feed.setDescription(this.descripcion);
		}

		// Link del feed
		feed.setLink(this.link);

		super.buildFeedMetadata(model, feed, request);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected List<Item> buildFeedItems(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {

		@SuppressWarnings("unchecked")
		List<RssContent> listContent = (List<RssContent>) model.get("feedContent");
		List<Item> items = new ArrayList<Item>(listContent.size());

		for (RssContent rssContent : listContent) {

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

	/**
	 * Setter del parámetro title
	 * 
	 * @param title
	 *            Título del feed
	 * 
	 */
	public void setTitle(String title) {
		this.title = title;
	}

	/**
	 * Setter del parámetro descripcion
	 * 
	 * @param descripcion
	 *            Descripción del feed
	 * 
	 */
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	/**
	 * Setter del parámetro link
	 * 
	 * @param link
	 *            Link del feed
	 * 
	 */
	public void setLink(String link) {
		this.link = link;
	}

	/**
	 * Setter del parámetro i18nTitle
	 * 
	 * @param i18nTitle
	 *            Key utilizada para internacionalizar el título del feed
	 * 
	 */
	public void setI18nTitle(String i18nTitle) {
		this.i18nTitle = i18nTitle;
	}

	/**
	 * Setter del parámetro i18nDescripcion
	 * 
	 * @param i18nDescripcion
	 *            Key utilizada para internacionalizar la descripción del feed
	 * 
	 */
	public void setI18nDescripcion(String i18nDescripcion) {
		this.i18nDescripcion = i18nDescripcion;
	}

}
