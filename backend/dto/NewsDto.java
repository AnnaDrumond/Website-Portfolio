package pt.uc.dei.proj5.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import pt.uc.dei.proj5.entity.Status;

@XmlRootElement
public class NewsDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title_newsDto;
	private String cover_imageDto;
	private String content_newsDto;
	private Status statusNewsDto;
	private String keywords_news;
	private String news_members;
	private String userNewscreatorDto;
	private String usernameNewsCreator;
	private String lastUpdateDto;
	private int id;

	public NewsDto() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle_newsDto() {
		return title_newsDto;
	}

	public void setTitle_newsDto(String title_newsDto) {
		this.title_newsDto = title_newsDto;
	}

	public String getCover_imageDto() {
		return cover_imageDto;
	}

	public void setCover_imageDto(String cover_imageDto) {
		this.cover_imageDto = cover_imageDto;
	}

	public String getContent_newsDto() {
		return content_newsDto;
	}

	public void setContent_newsDto(String content_newsDto) {
		this.content_newsDto = content_newsDto;
	}

	public Status getStatusNewsDto() {
		return statusNewsDto;
	}

	public void setStatusNewsDto(Status statusNewsDto) {
		this.statusNewsDto = statusNewsDto;
	}

	public String getKeywords_news() {
		return keywords_news;
	}

	public void setKeywords_news(String keywords_news) {
		this.keywords_news = keywords_news;
	}

	public String getUserNewscreatorDto() {
		return userNewscreatorDto;
	}

	public void setUserNewscreatorDto(String userNewscreatorDto) {
		this.userNewscreatorDto = userNewscreatorDto;
	}

	public String getLastUpdateDto() {
		return lastUpdateDto;
	}

	public void setLastUpdateDto(String lastUpdateDto) {
		this.lastUpdateDto = lastUpdateDto;
	}

	public String getNews_members() {
		return news_members;
	}

	public void setNews_members(String news_members) {
		this.news_members = news_members;
	}

	public String getUsernameNewsCreator() {
		return usernameNewsCreator;
	}

	public void setUsernameNewsCreator(String usernameNewsCreator) {
		this.usernameNewsCreator = usernameNewsCreator;
	}
	
	
	

}
