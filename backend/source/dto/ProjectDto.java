package pt.uc.dei.proj5.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlRootElement;

import pt.uc.dei.proj5.entity.Status;
import pt.uc.dei.proj5.entity.User;

@XmlRootElement
public class ProjectDto implements Serializable {
	private static final long serialVersionUID = 1L;

	private String title_projectDto;
	// não coloquei lastUpdate e create_date
	private String cover_imageDto;
	private String content_projectDto;
	private Status statusProjectDto;
	private String keywords_project;
	private String project_members;
	private String userProjectcreatorDto;
	private String usernameOwner;
	private String lastUpdateDto;
	private int id;

	public ProjectDto() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLastUpdateDto() {
		return lastUpdateDto;
	}

	public void setLastUpdateDto(String lastUpdateDto) {
		this.lastUpdateDto = lastUpdateDto;
	}

	public String getTitle_projectDto() {
		return title_projectDto;
	}

	public void setTitle_projectDto(String title_projectDto) {
		this.title_projectDto = title_projectDto;
	}

	public String getCover_imageDto() {
		return cover_imageDto;
	}

	public void setCover_imageDto(String cover_imageDto) {
		this.cover_imageDto = cover_imageDto;
	}

	public String getContent_projectDto() {
		return content_projectDto;
	}

	public String getUserProjectcreatorDto() {
		return userProjectcreatorDto;
	}

	public void setUserProjectcreatorDto(String userProjectcreatorDto) {
		this.userProjectcreatorDto = userProjectcreatorDto;
	}

	public void setContent_projectDto(String content_projectDto) {
		this.content_projectDto = content_projectDto;
	}

	public Status getStatusProjectDto() {
		return statusProjectDto;
	}

	public void setStatusProjectDto(Status statusProjectDto) {
		this.statusProjectDto = statusProjectDto;
	}

	public String getKeywords_project() {
		return keywords_project;
	}

	public void setKeywords_project(String keywords_project) {
		this.keywords_project = keywords_project;
	}

	public String getProject_members() {
		return project_members;
	}

	public void setProject_members(String project_members) {
		this.project_members = project_members;
	}

	public String getUsernameOwner() {
		return usernameOwner;
	}

	public void setUsernameOwner(String usernameOwner) {
		this.usernameOwner = usernameOwner;
	}

	
}
