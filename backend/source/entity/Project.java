
package pt.uc.dei.proj5.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
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
@Table(name = "Projects")
public class Project implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private int id;

	@NotBlank
	@Column(name = "title")
	private String title_project;

	@Column(name = "createDate", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp createDate;

	@UpdateTimestamp
	@Column(name = "lastUpdate")
	private Timestamp lastUpdate;

	@NotBlank
	@Column(name = "cover_image")
	private String cover_image;

	@NotBlank
	@Column(name = "content"/*, length = 100000*/)
	private String content_project;

	@NotBlank
	@Column(name = "keywords")
	private String keywords_project;

	@Enumerated(EnumType.STRING)
	@Column(name = "status")
	private Status statusProject;

	@ManyToOne
	// não coloquei o JoinColumn para testar e resultou bem
	private User userJoin_creator;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "Members_Proj_List")
	private Collection<User> membersProjectList;// - membros envolvidos no projeto

	@ManyToMany(mappedBy = "projectsOfNews")
	private Collection<News> newsOfProject;// noticias ligadas ao projeto

	
	public Project() {
	
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle_project() {
		return title_project;
	}

	public void setTitle_project(String title_project) {
		this.title_project = title_project;
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

	public String getContent_project() {
		return content_project;
	}

	public void setContent_project(String content_project) {
		this.content_project = content_project;
	}

	public Status getStatusProject() {
		return statusProject;
	}

	public void setStatusProject(Status statusProject) {
		this.statusProject = statusProject;
	}

	public String getKeywords_project() {
		return keywords_project;
	}

	public void setKeywords_project(String keywords_project) {
		this.keywords_project = keywords_project;
	}

	public User getUserJoin_creator() {
		return userJoin_creator;
	}

	public void setUserJoin_creator(User userJoin_creator) {
		this.userJoin_creator = userJoin_creator;
	}

	public Collection<User> getMembersProjectList() {
		return membersProjectList;
	}

	public void setMembersProjectList(Collection<User> membersProjectList) {
		this.membersProjectList = membersProjectList;
	}

	public Collection<News> getNewsOfProject() {
		return newsOfProject;
	}

	public void setNewsOfProject(Collection<News> newsOfProject) {
		this.newsOfProject = newsOfProject;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", title_project=" + title_project + ", createDate=" + createDate + ", lastUpdate="
				+ lastUpdate + "]";
	}
	
	

}
