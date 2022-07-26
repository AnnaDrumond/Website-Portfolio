

package pt.uc.dei.proj5.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(name = "News")
public class News implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@NotBlank
	@Column(name = "title")
	private String title_news;

	@Column(name = "createDate", nullable = false, updatable = true, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp createDate;

	@UpdateTimestamp
	@Column(name = "lastUpdate", nullable = false, updatable = true, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp lastUpdate;

	@NotBlank
	@Column(name = "cover_image")
	private String cover_image;

	@NotBlank
	@Column(name = "keywords")
	private String keywords_news;

	@NotBlank
	@Column(name = "content"/*, length = 100000*/)
	private String content_news;

	@Enumerated(EnumType.STRING) // PRECISO TER ESTES @column com este columnDefinition ???
	@Column(name = "status")
	private Status statusNews;

	@ManyToOne
	// não coloquei o JoinColumn para testar e resultou bem
	private User userJoin_creator;

	@ManyToMany
	@JoinColumn(name = "Members_News_List")
	private Collection<User> membersNewsList;// - membros envolvidos na noticia

	@ManyToMany
	private Collection<Project> projectsOfNews;// projetos ligados a uma notícia

	public News() {
		super();
	}

	public News(int id, @NotBlank String title_news, Timestamp createDate, Timestamp lastUpdate,
			@NotBlank String cover_image, @NotBlank String keywords_news, @NotBlank String content_news,
			Status statusNews, User userJoin_creator, Collection<User> membersNewsList,
			Collection<Project> projectsOfNews) {
		super();
		this.id = id;
		this.title_news = title_news;
		this.createDate = createDate;
		this.lastUpdate = lastUpdate;
		this.cover_image = cover_image;
		this.keywords_news = keywords_news;
		this.content_news = content_news;
		this.statusNews = statusNews;
		this.userJoin_creator = userJoin_creator;
		this.membersNewsList = membersNewsList;
		this.projectsOfNews = projectsOfNews;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle_news() {
		return title_news;
	}

	public void setTitle_news(String title_news) {
		this.title_news = title_news;
	}

	public Timestamp getCreateDate() {
		return createDate;
	}

	public void setCreateDate(Timestamp createDate) {
		this.createDate = createDate;
	}

	public Timestamp getLastUpdate() {
		return lastUpdate;
	}

	public void setLastUpdate(Timestamp lastUpdate) {
		this.lastUpdate = lastUpdate;
	}

	public String getCover_image() {
		return cover_image;
	}

	public void setCover_image(String cover_image) {
		this.cover_image = cover_image;
	}

	public String getContent_news() {
		return content_news;
	}

	public void setContent_news(String content_news) {
		this.content_news = content_news;
	}

	public Status getStatusNews() {
		return statusNews;
	}

	public void setStatusNews(Status statusNews) {
		this.statusNews = statusNews;
	}

	public String getKeywords_news() {
		return keywords_news;
	}

	public void setKeywords_news(String keywords_news) {
		this.keywords_news = keywords_news;
	}

	public User getUserJoin_creator() {
		return userJoin_creator;
	}

	public void setUserJoin_creator(User userJoin_creator) {
		this.userJoin_creator = userJoin_creator;
	}

	public Collection<User> getMembersNewsList() {
		return membersNewsList;
	}

	public void setMembersNewsList(Collection<User> membersNewsList) {
		this.membersNewsList = membersNewsList;
	}

	public Collection<Project> getProjectsOfNews() {
		return projectsOfNews;
	}

	public void setProjectsOfNews(Collection<Project> projectsOfNews) {
		this.projectsOfNews = projectsOfNews;
	}

	@Override
	public String toString() {
		return "News [id=" + id + ", title_news=" + title_news + ", createDate=" + createDate + ", lastUpdate="
				+ lastUpdate + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(content_news, cover_image, createDate, id, keywords_news, lastUpdate, membersNewsList,
				projectsOfNews, statusNews, title_news, userJoin_creator);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		News other = (News) obj;
		return Objects.equals(content_news, other.content_news) && Objects.equals(cover_image, other.cover_image)
				&& Objects.equals(createDate, other.createDate) && id == other.id
				&& Objects.equals(keywords_news, other.keywords_news) && Objects.equals(lastUpdate, other.lastUpdate)
				&& Objects.equals(membersNewsList, other.membersNewsList)
				&& Objects.equals(projectsOfNews, other.projectsOfNews) && statusNews == other.statusNews
				&& Objects.equals(title_news, other.title_news)
				&& Objects.equals(userJoin_creator, other.userJoin_creator);
	}
	
	
	
	
	

}
