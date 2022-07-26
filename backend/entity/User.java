package pt.uc.dei.proj5.entity;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;

import org.hibernate.annotations.Type;

@Entity
@Table(name = "Users")
@NamedQueries({ @NamedQuery(name = "User.encontrarTodos", query = "SELECT u FROM User u ORDER BY u.username ASC"),
		@NamedQuery(name = "User.encontrarUserLogin", query = "SELECT u FROM User u WHERE u.username = :username AND  u.password = :password"),
		@NamedQuery(name = "User.encontrarPeloToken", query = "SELECT u FROM User u WHERE u.token = :token"),
		@NamedQuery(name = "User.encontrarPeloUsername", query = "SELECT u FROM User u WHERE u.username = :username") })
public class User implements Serializable {
	private static final long serialVersionUID = 1L;
	/*
	 * A annotation @NotBlank faz a mesma verificação que NotNull e NotEmpty (se o
	 * objeto é diferente de null) , porém diferente da verificação feita por
	 * NotEmpty, aqui é utilizado o método trim() na verificação da String, apagando
	 * assim os espaços em branco na verificação do tamanho do valor. Insertable diz
	 * se é possível inserir novos registros na tabela. Updateable diz se é possível
	 * atualizar os registros na tabela.
	 */
	@NotBlank
	@Column(name = "firstName", updatable = true)
	private String first_name;

	@NotBlank
	@Column(name = "lastName", updatable = true)
	private String last_name;

	@Id
	@Column(name = "username", updatable = false)
	private String username;

	@NotBlank
	@Column(name = "password", updatable = false)
	private String password;

	@Column(name = "token", updatable = true) // só tem token se houver login
	private String token;

	@NotBlank
	@Column(name = "email", unique = true, updatable = true)
	private String email;

	@Column(name = "registerDate", nullable = false, updatable = false, insertable = false, columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
	private Timestamp registerDate;

	@Column(name = "biography", updatable = true)
	private String biography;

	@Column(name = "photo", updatable = true)
	private String photo_user;

	@Column(name = "aproved", updatable = true)
	private boolean registrationApproved;

	public enum UserType {
		VISITOR, MEMBER, ADMINISTRATOR, REJECTED;
	}

	@Enumerated(EnumType.STRING) // PRECISO TER ESTES @column com este columnDefinition ???
	@Column(name = "typeUser", updatable = true)
	private UserType typeUser;

	// fetch = FetchType.EAGER
	// FetchType.EAGER - só pode ser colocado em uma lista por classe, mas pode ser
	// colocado nos JoinColumn
	/*
	 * Exemplo: @OneToOne(fetch = FetchType.EAGER)
	 * 
	 * @JoinColumn(name = "Users_username") private User userJoin;
	 */
	@OneToMany(mappedBy = "userJoin_creator", cascade = CascadeType.REMOVE)
	private Collection<Project> projectsUser;// projetos criados pelo user

	@OneToMany(mappedBy = "userJoin_creator", cascade = CascadeType.REMOVE)
	private Collection<News> newsUser; // noticias criadas pelo user

	@ManyToMany(fetch = FetchType.EAGER, mappedBy = "membersProjectList")
	private Collection<Project> projectMembersList; // projetos em que o user é um membro envolvido

	@ManyToMany(mappedBy = "membersNewsList")
	private Collection<News> newsMembersList; // noticias em que o user é um membro envolvido

	public User() {
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@Type(type = "password")
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Timestamp getRegisterDate() {
		return registerDate;
	}

	public void setRegisterDate(Timestamp registerDate) {
		this.registerDate = registerDate;
	}

	public String getBiography() {
		return biography;
	}

	public void setBiography(String biography) {
		this.biography = biography;
	}

	public String getPhoto_user() {
		return photo_user;
	}

	public void setPhoto_user(String photo_user) {
		this.photo_user = photo_user;
	}

	public boolean isRegistrationApproved() {
		return registrationApproved;
	}

	public void setRegistrationApproved(boolean registrationApproved) {
		this.registrationApproved = registrationApproved;
	}

	public UserType getTypeUser() {
		return typeUser;
	}

	public void setTypeUser(UserType typeUser) {
		this.typeUser = typeUser;
	}

	public Collection<Project> getProjectsUser() {
		return projectsUser;
	}

	public void setProjectsUser(Collection<Project> projectsUser) {
		this.projectsUser = projectsUser;
	}

	public Collection<News> getNewsUser() {
		return newsUser;
	}

	public void setNewsUser(Collection<News> newsUser) {
		this.newsUser = newsUser;
	}

	public Collection<Project> getProjectMembersList() {
		return projectMembersList;
	}

	public void setProjectMembersList(Collection<Project> projectMembersList) {
		this.projectMembersList = projectMembersList;
	}

	public Collection<News> getNewsMembersList() {
		return newsMembersList;
	}

	public void setNewsMembersList(Collection<News> newsMembersList) {
		this.newsMembersList = newsMembersList;
	}

	@Override
	public String toString() {
		return "User [first_name=" + first_name + ", last_name=" + last_name + ", username=" + username + ", password="
				+ password + ", token=" + token + ", email=" + email + ", registerDate=" + registerDate + ", biography="
				+ biography + ", photo_user=" + photo_user + ", registrationApproved=" + registrationApproved
				+ ", typeUser=" + typeUser + "]";
	}

}
