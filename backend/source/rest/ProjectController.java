package pt.uc.dei.proj5.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.uc.dei.proj5.bean.NewsBean;
import pt.uc.dei.proj5.bean.ProjectBean;
import pt.uc.dei.proj5.bean.UserBean;
import pt.uc.dei.proj5.dto.NewsDto;
import pt.uc.dei.proj5.dto.ProjectDto;
import pt.uc.dei.proj5.dto.UserDto;
import pt.uc.dei.proj5.entity.News;
import pt.uc.dei.proj5.entity.Project;
import pt.uc.dei.proj5.entity.Status;
import pt.uc.dei.proj5.entity.User;
import pt.uc.dei.proj5.entity.User.UserType;

@Path("/project")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class ProjectController {

	@Inject
	ProjectBean projectBean;

	@Inject
	UserBean userBean;

	@Inject
	NewsBean newsBean;

	/**
	 * Adiciona novos projetos - somente para admins e membros
	 * 
	 * @param token
	 * @param ProjectDto
	 * @return
	 */
	@POST
	@Path("/insertProject")
	public Response addNewProject(@HeaderParam("token") String token, ProjectDto ProjectDto) {
		
		System.out.println("addNewProject");

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}
		// Quem adiciona Projeto é um membro ou um admin
		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		boolean projectAdded = projectBean.addNewProject(ProjectDto, loggedUser);

		if (projectAdded) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de membros associados a um determinado projeto. Será usado para
	 * o que consta na ficha do projeto página 2, linha 8 onde diz que na página do
	 * projeto deve constar "que membros da organização participaram nesse projeto"
	 * 
	 * @param token
	 * @param idProject
	 * @return
	 */
	@GET
	@Path("/getProjectMembers/{id}")
	public Response getAssociatedProjectMembers(@PathParam("id") String idProject) {
		
		System.out.println("getAssociatedProjectMembers");

		try {
			List<UserDto> membersProject = projectBean.searchProjectMembers(Integer.valueOf(idProject));

			System.out.println(membersProject);

			return Response.ok(membersProject).build();

		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Permite editar um projeto
	 * 
	 * @param token
	 * @param ProjectDto
	 * @return
	 */
	@POST
	@Path("/editProject/{id}")
	public Response editProject(@HeaderParam("token") String token, ProjectDto ProjectDto,
			@PathParam("id") String idProject) {
		
		System.out.println("editProject*******************");
		System.out.println("trouxe o dto " + ProjectDto);

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		boolean userIsProjectCreator = projectBean.checkIfUserIsProjectCreator(loggedUser, Integer.valueOf(idProject));
		
		System.out.println("voltei no controller: " + userIsProjectCreator);
		// Preciso ver se o user logado é o userCreator do projeto ou se é um admin, ou
		// seja
		// se não for o user do projeto
		if (loggedUser == null || !userIsProjectCreator && !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		boolean updatedProject = projectBean.editProjectInDataBase(Integer.valueOf(idProject), ProjectDto);

		if (updatedProject) {
			return Response.ok().build();
		} else {
			System.out.println("entrei no else do controller");
			return Response.status(401).build();
		}

	}

	/**
	 * Permite modificar o status de um projeto para visible, unvisible ou off.
	 * 
	 * @param token
	 * @param idProject
	 * @param newStatus
	 * @return
	 */
	@POST
	@Path("/modifyProjectStatus/{id}/{status}")
	public Response modifyProjectStatus(@HeaderParam("token") String token, @PathParam("id") String idProject,
			@PathParam("status") String newStatus) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		boolean userIsProjectCreator = projectBean.checkIfUserIsProjectCreator(loggedUser, Integer.valueOf(idProject));

		System.out.println("voltei no controller: " + userIsProjectCreator);
		// Preciso ver se o user logado é o userCreator do projeto ou se é um admin
		if (loggedUser == null || !userIsProjectCreator && !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		boolean modifiedStatus = projectBean.modifyProjectStatusInDataBase(Integer.valueOf(idProject),
				newStatus.toUpperCase());

		if (modifiedStatus) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de todos os projetos visible. Para os exibir aos visitantes
	 * 
	 * @return
	 */
	@GET
	@Path("/visibleProjects")
	public Response getVisibleProjects() {

		System.out.println("getVisibleProjects");

		try {
			List<ProjectDto> visibleProjectsDto = projectBean.getVisibleProjectsDto();
			return Response.ok(visibleProjectsDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de todos os projetos unvisible. Nota: acho que não precisarei
	 * deste.
	 * 
	 * @return
	 */
	@GET
	@Path("/getUnvisibleProjects")
	public Response getUnvisibleProjects() {

		try {
			List<ProjectDto> unvisibleProjectsDto = projectBean.getUnvisibleProjectsDto();
			return Response.ok(unvisibleProjectsDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de todos os projetos visible/unvisible. Para os exibir aos
	 * admins e membros após o login
	 * 
	 * @return
	 */
	@GET
	@Path("/allProjects")
	public Response getAllProjects(@HeaderParam("token") String token) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		// User do token NÂO é o mesmo do username ee também não é admin
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {
			List<ProjectDto> projectsNotOffDto = projectBean.getAllProjectsDto();

			return Response.ok(projectsNotOffDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}
	
	/**
	 * Busca a lista de todos os projetos visible/unvisible. Para os exibir aos
	 * admins e membros após o login
	 * 
	 * @return
	 */
	@GET
	@Path("/allProjectsNotOff")
	public Response getAllProjectsNotOff(@HeaderParam("token") String token) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		// User do token NÂO é o mesmo do username ee também não é admin
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {
			List<ProjectDto> projectsNotOffDto = projectBean.getAllNotOffProjectsDto();

			return Response.ok(projectsNotOffDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de projetos criados por um determinado user. para exibir a
	 * lista completa (todos status) de projetos, ao criador dos mesmos ou a um
	 * admin.
	 * 
	 * @param token
	 * @param username
	 * @return
	 */
	@GET
	@Path("/projectsUser/{username}")
	public Response getProjectsCreatedByUser(@HeaderParam("token") String token,
			@PathParam("username") String username) {

		if (token == null || token.isEmpty() || username.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		// User do token NÂO é o mesmo do username ee também não é admin
		if (loggedUser == null || !loggedUser.getUsername().equals(username)
				&& !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		try {
			List<ProjectDto> projectDto = projectBean.searchForProjectsByUser(username);
			return Response.ok(projectDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca um projeto através do seu ID
	 * 
	 * @return
	 */
	@GET
	@Path("/projectById/{id}")
	public Response getProjectById(@HeaderParam("token") String token, @PathParam("id") String idProject) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		boolean userIsProjectCreator = projectBean.checkIfUserIsProjectCreator(loggedUser, Integer.valueOf(idProject));

		// System.out.println("voltei no controller: " + userIsProjectCreator);
		// Preciso ver se o user logado é o userCreator do projeto ou se é um admin
		if (loggedUser == null || !userIsProjectCreator && !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		try {
			ProjectDto projectDto = projectBean.getProjectDtoById(Integer.valueOf(idProject));
			return Response.ok(projectDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca um projeto visible através do seu ID
	 * 
	 * @return
	 */
	@GET
	@Path("/projectVisibleById/{id}")
	public Response getProjectVisibleById(@PathParam("id") String idProject) {

		try {
			ProjectDto projectDto = projectBean.getProjectDtoById(Integer.valueOf(idProject));
			return Response.ok(projectDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de Projetos em que um determinado membro/admin aparece como
	 * associado/colaborador/co-autor
	 * 
	 * @return
	 */
	@GET
	@Path("/getProjectsAssociatedWithUser/{username}")
	public Response getProjectsAssociatedWithUser(@HeaderParam("token") String token,
			@PathParam("username") String username) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		// Qualquer admin/membro pode consultar a lista dos projetos em que está
		// envolvido
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {
			List<ProjectDto> projectDto = projectBean.searchListOfProjectsAssociatedWithUser(username);
			return Response.ok(projectDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de projetos com status visible ou unvisible através da keyword
	 * indicada como parametro. Será para pesquisas feitas por membros ou admins.
	 * 
	 * @return
	 */
	@GET
	@Path("/getNotOffProjectsByKeyword/{keyword}")
	public Response getNotOffProjectsByKeyword(@HeaderParam("token") String token,
			@PathParam("keyword") String keyword) {

		System.out.println("entrei em getNotOffProjectsByKeyword");

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		// User logado pode ser admin ou pode ser membro
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {
			List<ProjectDto> projectDto = projectBean.searchForNotOffProjectsByKeyword(keyword);
			return Response.ok(projectDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de projetos com status somente visible através da keyword
	 * indicada como parametro. Será para pesquisas feitas por vistantes sem token e
	 * login.
	 * 
	 * @return
	 */
	@GET
	@Path("/getVisibleProjectsByKeyword/{keyword}")
	public Response getVisibleProjectsByKeyword(@PathParam("keyword") String keyword) {

		System.out.println("entrei em getVisibleProjectsByKeyword");

		try {
			List<ProjectDto> projectDto = projectBean.searchForVisibleProjectsByKeyword(keyword);
			return Response.ok(projectDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	
	/**
	 * Permite associar/desassociar membros a um Projeto
	 * 
	 * @param token
	 * @param usernameToManage
	 * @param idProject
	 * @return
	 */
	@POST
	@Path("/manageMembersProject/{id}/{username}")
	public Response manageProjectMembers(@HeaderParam("token") String token,
			@PathParam("username") String usernameToManage, @PathParam("id") String idProject) {

		System.out.println("manageProjectMembers");

		if (token == null || token.isEmpty() || usernameToManage.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		Project project = projectBean.findProjectById(Integer.valueOf(idProject));

		// Segundo o projeto, qualquer membro/admin pode associar/desassociar outros
		// membros
		// inclusive a si próprio
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		boolean updatedProject = projectBean.manageProjectMembersService(Integer.valueOf(idProject), usernameToManage);

		if (updatedProject) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Associa Projeto e noticias
	 * 
	 * @param token
	 * @param idProject
	 * @param idNews
	 * @return
	 */
	@POST
	@Path("/associateProject/{idProject}/toNews/{idNews}")
	public Response associateProjectAndNews(@HeaderParam("token") String token,
			@PathParam("idProject") String idProject, @PathParam("idNews") String idNews) {

		System.out.println("associateProjectAndNews");

		// falta impedir que um determinado projeto seja associado + do que uma vez a
		// uma determinada noticia e vice-versa

		if (token == null || token.isEmpty() || idProject.isEmpty() || idNews.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		News news = newsBean.findNewsById(Integer.valueOf(idNews));
		Project project = projectBean.findProjectById(Integer.valueOf(idProject));
		boolean userIsNewsCreator = newsBean.checkIfUserIsNewsCreator(loggedUser, Integer.valueOf(idNews));

		// Os conteúdos com status apagados não poderiam ser associados uns aos outros
		if (news.getStatusNews().equals(Status.OFF) || project.getStatusProject().equals(Status.OFF)) {
			return Response.status(406).build();
		}
		/*
		 * 406 Not Acceptable Essa resposta é enviada quando o servidor da Web após
		 * realizar a negociação de conteúdo orientada pelo servidor, não encontra
		 * nenhum conteúdo seguindo os critérios fornecidos pelo agente do usuário.
		 */

		// Quem pode associar é o criador da noticia ou um admin
		if (loggedUser == null || !userIsNewsCreator && !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		boolean successfulAssociation = projectBean.associateProjectAndNewsService(project, news);

		if (successfulAssociation) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	
	/**
	 * Desassocia Projeto e noticias
	 * 
	 * @param token
	 * @param idProject
	 * @param idNews
	 * @return
	 */
	@POST
	@Path("/disassociateProject/{idProject}/toNews/{idNews}")
	public Response disassociateProjectAndNews(@HeaderParam("token") String token,
			@PathParam("idProject") String idProject, @PathParam("idNews") String idNews) {

		System.out.println("disassociateProject");

		// falta impedir que um determinado projeto seja associado + do que uma vez a
		// uma determinada noticia e vice-versa

		if (token == null || token.isEmpty() || idProject.isEmpty() || idNews.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		News news = newsBean.findNewsById(Integer.valueOf(idNews));
		Project project = projectBean.findProjectById(Integer.valueOf(idProject));
		boolean userIsNewsCreator = newsBean.checkIfUserIsNewsCreator(loggedUser, Integer.valueOf(idNews));

		// Os conteúdos com status apagados não poderiam ser associados uns aos outros
		if (news.getStatusNews().equals(Status.OFF) || project.getStatusProject().equals(Status.OFF)) {
			return Response.status(406).build();
		}
		/*
		 * 406 Not Acceptable Essa resposta é enviada quando o servidor da Web após
		 * realizar a negociação de conteúdo orientada pelo servidor, não encontra
		 * nenhum conteúdo seguindo os critérios fornecidos pelo agente do usuário.
		 */

		// Quem pode associar é o criador da noticia ou um admin
		if (loggedUser == null || !userIsNewsCreator && !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		boolean successfulAssociation = projectBean.disassociateProjectAndNewsService(project, news);

		if (successfulAssociation) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}
	/**
	 * Busca a lista de noticias com status visible associados a um determinado
	 * projeto
	 * 
	 * @return
	 */
	@GET
	@Path("/newsAssociated/{idProject}")
	public Response getVisibleNewsAssociatedWithAProject(@PathParam("idProject") String idProject) {

		System.out.println("entrei em getVisibleProjectsAssociatedWithANews");

		try {

			List<NewsDto> newsListDto = projectBean
					.searchForVisibleNewsAssociatedWithAProject(Integer.valueOf(idProject));
			return Response.ok(newsListDto).build();

		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de noticias com status visible/unvisible associadas a um
	 * determinado projeto
	 * 
	 * @return
	 */
	@GET
	@Path("/allNewsAssociated/{idProject}")
	public Response getNotOffNewssAssociatedWithAProject(@PathParam("idProject") String idProject,
			@HeaderParam("token") String token) {

		System.out.println("entrei em allNewssAssociated");

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		// Pode ser um admin ou um membro - só não pode ser um visitante
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {

			List<NewsDto> newsListDto = projectBean
					.searchForAllNotOffNewsAssociatedWithAproject(Integer.valueOf(idProject));
			
			return Response.ok(newsListDto).build();
			
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

}
