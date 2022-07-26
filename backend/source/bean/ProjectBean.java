package pt.uc.dei.proj5.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.ejb.EJBException;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.servlet.jsp.tagext.TryCatchFinally;

import pt.uc.dei.proj5.dao.NewsDao;
import pt.uc.dei.proj5.dao.ProjectDao;
import pt.uc.dei.proj5.dao.UserDao;
import pt.uc.dei.proj5.dto.NewsDto;
import pt.uc.dei.proj5.dto.ProjectDto;
import pt.uc.dei.proj5.dto.UserDto;
import pt.uc.dei.proj5.entity.News;
import pt.uc.dei.proj5.entity.Project;
import pt.uc.dei.proj5.entity.Status;
import pt.uc.dei.proj5.entity.User;
import pt.uc.dei.proj5.entity.User.UserType;

@RequestScoped
public class ProjectBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	UserDao userDao;

	@Inject
	NewsDao newsDao;

	@Inject
	ProjectDao projectDao;

	public boolean addNewProject(ProjectDto projectDto, User user_creator) {

		try {
			Project projectEntity = new Project();
			projectEntity = projectDao.convertDtoProjectToEntity(projectDto, user_creator);
			boolean foundProjectWithTheSameName = projectDao
					.checkDatabaseProjectWithTheSameName(projectEntity.getTitle_project());

			if (!foundProjectWithTheSameName) {
				projectEntity.setUserJoin_creator(user_creator);
				List<User> members_user = generateListOfProjectAssociatedMembers(projectDto);

				projectEntity.setMembersProjectList(members_user);

				projectDao.persist(projectEntity);
				return true;
			}
			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Vai permitir que a lista de membros envolvidos no projeto que será trazida do
	 * frontend venha no formato "string;string;string". O método irá separar os
	 * usernames dos membros envolvidos no projeto, e irá gerar/criar uma lista de
	 * users com base nesta lista de usernames.
	 * 
	 * @param projectDto
	 * @return
	 */
	public List<User> generateListOfProjectAssociatedMembers(ProjectDto projectDto) {

		List<String> members_username = new ArrayList<>(Arrays.asList(projectDto.getProject_members().split(";")));
		List<User> members_user = new ArrayList<User>();

		System.out.println("generateListOfProjectMembersUsers");

		if (members_username != null && members_username.get(0) != "") {

			for (String string_username : members_username) {

				User userFound = userDao.find(string_username);

				if (!userFound.getTypeUser().equals(UserType.VISITOR) && userFound != null) {// visitantes não podem ser
																								// associados a um //
																								// projeto
					members_user.add(userFound);
				}
			}

		}

		return members_user;

	}

	/**
	 * Verifica se o User é o criador do Projeto
	 * 
	 * @param loggedUser
	 * @param IdProject
	 * @return
	 */
	public boolean checkIfUserIsProjectCreator(User loggedUser, int IdProject) {
		Project project = projectDao.find(IdProject);
		if (loggedUser.getUsername().equals(project.getUserJoin_creator().getUsername())) {
			return true;
		}
		return false;
	}

	/**
	 * Chama a consulta à base de dados e transforma a lista de User encontrados em
	 * uma lista de Dto
	 * 
	 * @param IdProject
	 * @return
	 */
	public List<UserDto> searchProjectMembers(int IdProject) {

		List<User> membersEntity = projectDao.searchProjectMembersInDatabase(IdProject);
		List<UserDto> projectMembersDto = new ArrayList<UserDto>();

		for (User user : membersEntity) {
			UserDto userDto = userDao.convertEntityUserToDto(user);
			projectMembersDto.add(userDto);
		}
		return projectMembersDto;
	}

	/**
	 * Permite editar um projeto na base de dados.
	 * 
	 * @param idProject
	 * @param projectDto
	 * @param userCreator
	 * @return
	 */
	public boolean editProjectInDataBase(int idProject, ProjectDto projectDto) {

		System.out.println("entrei em editProjectInDataBase - bean ------------");

		Project originalProject = projectDao.find(idProject);
		Project updatedProjectDb = projectDao.convertDtoProjectAndUpdateEntity(projectDto, idProject);

		List<User> members_user = generateListOfProjectAssociatedMembers(projectDto);

		updatedProjectDb.setMembersProjectList(members_user);

		try {

			updatedProjectDb.setUserJoin_creator(originalProject.getUserJoin_creator());
			projectDao.merge(updatedProjectDb);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * Modifica o status de um Projeto.
	 * 
	 * @param IdProject
	 * @param newStatus
	 * @return
	 */
	public boolean modifyProjectStatusInDataBase(int IdProject, String newStatus) {

		Project projectFound = projectDao.find(IdProject);

		try {
			switch (newStatus) {
			case "VISIBLE":
				projectFound.setStatusProject(Status.VISIBLE);
				break;
			case "UNVISIBLE":
				projectFound.setStatusProject(Status.UNVISIBLE);
				break;
			case "OFF":
				projectFound.setStatusProject(Status.OFF);
				break;
			}
			projectDao.merge(projectFound);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Lista todos todos os projetos com visibilidade pública.
	 * 
	 * @return
	 */
	public List<ProjectDto> getVisibleProjectsDto() {

		List<Project> entityProjects = projectDao.getAllVisibleProjectsInDataBase();
		List<ProjectDto> dtoProjects = new ArrayList<ProjectDto>();

		for (Project project : entityProjects) {
			ProjectDto projectDto = projectDao.convertEntityProjectToDto(project);
			dtoProjects.add(projectDto);
		}
		return dtoProjects;
	}

	/**
	 * Lista todos todos os projetos com visibilidade privada/unvisible.
	 * 
	 * @return
	 */
	public List<ProjectDto> getUnvisibleProjectsDto() {

		List<Project> entityProjects = projectDao.getAllUnvisibleProjectsInDataBase();
		List<ProjectDto> dtoProjects = new ArrayList<ProjectDto>();

		for (Project project : entityProjects) {
			ProjectDto projectDto = projectDao.convertEntityProjectToDto(project);
			dtoProjects.add(projectDto);
		}
		return dtoProjects;
	}

	/**
	 * Lista todos todos os projetos visible/unvisible.
	 * 
	 * @return
	 */
	public List<ProjectDto> getAllProjectsDto() {

		System.out.println("entrei em getNotOffProjectsDto - bean");

		List<Project> entityProjects = projectDao.findAll();
		System.out.println("voltei a bean com a lista: " + entityProjects);
		List<ProjectDto> dtoProjects = new ArrayList<ProjectDto>();

		for (Project project : entityProjects) {
			ProjectDto projectDto = projectDao.convertEntityProjectToDto(project);
			dtoProjects.add(projectDto);
		}
		return dtoProjects;
	}

	/**
	 * Lista todos todos os projetos visible/unvisible.
	 * 
	 * @return
	 */
	public List<ProjectDto> getAllNotOffProjectsDto() {

		System.out.println("entrei em getNotOffProjectsDto - bean");

		List<Project> entityProjects = projectDao.getAllNotOffProjectsInDataBase();
		System.out.println("voltei a bean com a lista: " + entityProjects);
		List<ProjectDto> dtoProjects = new ArrayList<ProjectDto>();

		for (Project project : entityProjects) {
			ProjectDto projectDto = projectDao.convertEntityProjectToDto(project);
			dtoProjects.add(projectDto);
		}
		return dtoProjects;
	}

	/**
	 * Busca um projeto na base de dados através do seu id.
	 * 
	 * @param IdProject
	 * @return
	 */
	public ProjectDto getProjectDtoById(int IdProject) {
		Project projectFound = projectDao.find(IdProject);
		ProjectDto projectDto = projectDao.convertEntityProjectToDto(projectFound);
		return projectDto;
	}

	/**
	 * Busca a lista de projetos em que um determinado user está associado
	 * 
	 * @param username
	 * @return
	 */
	public List<ProjectDto> searchListOfProjectsAssociatedWithUser(String username) {

		List<Project> entityProjects = projectDao.searchListOfProjectsAssociatedWithUserInDatabase(username);
		List<ProjectDto> dtoProjects = new ArrayList<ProjectDto>();

		for (Project project : entityProjects) {
			ProjectDto projectDto = projectDao.convertEntityProjectToDto(project);
			dtoProjects.add(projectDto);
		}
		return dtoProjects;
	}

	/**
	 * Busca projetos não visiveis/invisiveis e não apagados através da keyword
	 * indicada .
	 * 
	 * @param keyword
	 * @return
	 */
	public List<ProjectDto> searchForNotOffProjectsByKeyword(String keyword) {

		// System.out.println("searchForNotOffProjectsByKeyword - bean - keyword: " +
		// keyword);
		String columnNameStatus = "statusProject";
		String columnNameKeywords = "keywords_project";
		String columnTitle = "title_project";
		List<Project> entityProjects = projectDao.searchForNotOffEntitiesByKeywordInDataBase(keyword, columnNameStatus,
				columnNameKeywords, columnTitle);
		List<ProjectDto> dtoProjects = new ArrayList<ProjectDto>();

		for (Project project : entityProjects) {
			ProjectDto projectDto = projectDao.convertEntityProjectToDto(project);
			dtoProjects.add(projectDto);
		}
		return dtoProjects;

	}

	/**
	 * Busca projetos públicos/visíveis através da keyword indicada .
	 * 
	 * @param keyword
	 * @return
	 */
	public List<ProjectDto> searchForVisibleProjectsByKeyword(String keyword) {

		// System.out.println("searchForVisibleProjectsByKeyword - bean - keyword: " +
		// keyword);

		String columnNameStatus = "statusProject";
		String columnNameKeywords = "keywords_project";
		String columnTitle = "title_project";
		List<Project> entityProjects = projectDao.searchForVisibleEntitiesByKeywordInDataBase(keyword, columnNameStatus,
				columnNameKeywords, columnTitle);

		List<ProjectDto> dtoProjects = new ArrayList<ProjectDto>();

		for (Project project : entityProjects) {
			ProjectDto projectDto = projectDao.convertEntityProjectToDto(project);
			dtoProjects.add(projectDto);
		}
		return dtoProjects;
	}

	/**
	 * Busca todos os projetos de um determinado user através da keyword indicada .
	 * 
	 * @param keyword
	 * @return
	 */
	/*
	 * public List<ProjectDto> searchForAllProjectsByKeyword(String keyword, String
	 * username) {
	 * 
	 * // System.out.println("searchForVisibleProjectsByKeyword - bean - keyword: "
	 * + // keyword);
	 * 
	 * List<Project> entityProjects =
	 * projectDao.searchForAllProjectsByKeywordInDataBase(keyword, username);
	 * List<ProjectDto> dtoProjects = new ArrayList<ProjectDto>();
	 * 
	 * for (Project project : entityProjects) { ProjectDto projectDto =
	 * projectDao.convertEntityProjectToDto(project); dtoProjects.add(projectDto); }
	 * return dtoProjects;
	 * 
	 * }
	 */

	/**
	 * Busca a lista de projetos criados por um determinado user. para exibir a
	 * lista completa (todos status) de projetos, ao criador dos mesmos ou a um
	 * admin.
	 * 
	 * @param username
	 * @return
	 */
	public List<ProjectDto> searchForProjectsByUser(String username) {

		System.out.println("searchForProjectsByUser - bean -  keyword: " + username);

		List<Project> entityProjects = projectDao.searchForProjectsByUserInDatabase(username);
		List<ProjectDto> dtoProjects = new ArrayList<ProjectDto>();

		for (Project project : entityProjects) {
			ProjectDto projectDto = projectDao.convertEntityProjectToDto(project);
			dtoProjects.add(projectDto);
		}
		return dtoProjects;

	}

	/**
	 * Associa/desassocia membro/admin a um projeto
	 * 
	 * @param id
	 * @return
	 */
	public boolean manageProjectMembersService(int idProject, String usernameToManage) {

		System.out.println("manageProjectMembersService - bean -  id: " + idProject + usernameToManage);

		// Pesquisa na BD se este Projeto já tem este user (dono do usernameToManage)
		// associado.
		List<User> membersOfProject = projectDao.searchProjectMembersInDatabase(idProject);
		User userToManage = userDao.find(usernameToManage);

		boolean isUserFound = false;
		int indexAux = 0;

		for (int i = 0; i < membersOfProject.size(); i++) {
			if (membersOfProject.get(i).getUsername().equals(usernameToManage)) {
				isUserFound = true;
				indexAux = i;
			}
		}

		Project project = projectDao.find(idProject); // projeto a ter os membros atualizados

		if (!isUserFound) {
			System.out.println("não encontrei o user");// se não encontrou este este user na lista, adiciona ele
			membersOfProject.add(userToManage);

		} else if (isUserFound) {// se encontrou o user, retira ele
			System.out.println("entrei no else para remover o membro ");
			membersOfProject.remove(indexAux);
		}

		// System.out.println(membersOfProject);

		project.setMembersProjectList(membersOfProject);

		try {
			projectDao.merge(project);
			return true;

		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * 
	 * @param idProject
	 * @return
	 */
	public Project findProjectById(int idProject) {
		return projectDao.find(idProject);
	}

	/**
	 * Busca todas oas noticias visiveis associadas a um determinado projeto
	 * 
	 * @param keyword
	 * @return
	 */
	public List<NewsDto> searchForVisibleNewsAssociatedWithAProject(int idProject) {

		System.out.println("searchForVisibleNewsAssociatedWithAProject - bean - idProject: " + idProject);
		List<News> newsListEntity = projectDao.searchForVisibleNewsAssociatedWithAProjectInDataBase(idProject);
		List<NewsDto> newsListDto = new ArrayList<NewsDto>();

		for (News news : newsListEntity) {
			NewsDto newsDto = newsDao.convertEntityNewsToDto(news);
			newsListDto.add(newsDto);
		}
		return newsListDto;

	}

	/**
	 * Busca todas oas noticias visiveis associadas a um determinado projeto
	 * 
	 * @param keyword
	 * @return
	 */
	public List<NewsDto> searchForAllNotOffNewsAssociatedWithAproject(int idProject) {

		System.out.println("searchForVisibleNewsAssociatedWithAProject - bean - idProject: " + idProject);
		List<News> newsListEntity = projectDao.searchForAllNotOffNewsAssociatedWithAProjecInDataBase(idProject);

		System.out.println("voltei da BD" + newsListEntity);

		List<NewsDto> newsListDto = new ArrayList<NewsDto>();

		for (News news : newsListEntity) {
			NewsDto newsDto = newsDao.convertEntityNewsToDto(news);
			newsListDto.add(newsDto);
		}
		return newsListDto;

	}

	/**
	 * Atualiza as listas de conteúdo associado de noticias e projeto associando um
	 * ao outro
	 * 
	 * @param project
	 * @param news
	 * @return
	 */
	public boolean associateProjectAndNewsService(Project project, News news) {

		System.out.println("associateProjectAndNewsService");

		System.out.println("projeto: " + project.getTitle_project() + " noticia: " + news.getTitle_news());

		// Verificação se a noticia já existe na lista de noticias associadas deste
		// projeto que veio como parametro
		boolean alreadyAssociated = newsDao.searchAssociatedProjectsSameId(news.getId(), project.getId());

		try {

			if (project != null && news != null && !alreadyAssociated) {
				// Buscar lista de projetos associados a noticia com o id informado
				List<Project> projectsAssociatedWithTheNews = newsDao.searchAssociatedProjectsInDataBase(news.getId());

				// Buscar lista de noticias associadas ao projeto com o id informado
				List<News> newsAssociatedWithTheProject = projectDao.searchAssociatedNewsInDataBase(project.getId());

				// Exemplo para não esquecer: Quero associar projeto X com noticia Y
				// Na lista de projetos da noticia Y deve estar lá o projeto X
				// E na lista de noticias do projeto X deve lá constar Y
				projectsAssociatedWithTheNews.add(project);
				newsAssociatedWithTheProject.add(news);

				// Por fim cada noticia/projeto deve ter sua lista de conteúdo associado
				// atualizada
				// Agora constando o novo projeto/notícia na lista respetiva
				news.setProjectsOfNews(projectsAssociatedWithTheNews);
				project.setNewsOfProject(newsAssociatedWithTheProject);

				newsDao.merge(news);
				projectDao.merge(project);
				return true;
			}
			return false;

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Atualiza as listas de conteúdo associado de noticias e projeto associando um
	 * ao outro
	 * 
	 * @param project
	 * @param news
	 * @return
	 */
	public boolean disassociateProjectAndNewsService(Project project, News news) {
		System.out.println("disassociateProjectAndNewsService");

		try {

			if (project != null && news != null) {
				// Buscar lista de projetos associados a noticia com o id informado
				List<Project> projects = newsDao.searchAssociatedProjectsInDataBase(news.getId());

				// Buscar lista de noticias associadas ao projeto com o id informado
				List<News> newsList = projectDao.searchAssociatedNewsInDataBase(project.getId());

				for (int i = 0; i < projects.size(); i++) {

					if (projects.get(i).getId() == project.getId()) {
						System.out.println("idddddd" + project.getId());
						projects.remove(i);
					}
				}

				for (int i = 0; i < newsList.size(); i++) {
					if (newsList.get(i).getId() == news.getId()) {
						System.out.println("id" + news.getId());
						newsList.remove(i);
					}
				}
				// Por fim cada noticia/projeto deve ter sua lista de conteúdo associado
				// atualizada
				// Agora constando o novo projeto/notícia na lista respetiva
				news.setProjectsOfNews(projects);
				project.setNewsOfProject(newsList);

				newsDao.merge(news);
				projectDao.merge(project);
				return true;
			}
			System.out.println("falso 1");
			return false;

		} catch (Exception e) {
			System.out.println("falso 2");
			return false;
		}
	}

}
