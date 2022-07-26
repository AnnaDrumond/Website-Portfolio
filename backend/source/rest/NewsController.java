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

@Path("/news")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class NewsController {

	@Inject
	ProjectBean projectBean;

	@Inject
	NewsBean newsBean;

	@Inject
	UserBean userBean;

	/**
	 * Adiciona novas notícias - somente para admins e membros
	 * 
	 * @param token
	 * @param newsDto
	 * @return
	 */
	@POST
	@Path("/insertNews")
	public Response addNews(@HeaderParam("token") String token, NewsDto newsDto) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}
		// Quem adiciona Projeto é um membro ou um admin
		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		boolean newAdded = newsBean.addNews(newsDto, loggedUser);

		if (newAdded) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Permite modificar o status de um projeto para visible, unvisible ou off.
	 * 
	 * @param token
	 * @param idNews
	 * @param newStatus
	 * @return
	 */
	@POST
	@Path("/modifyNewsStatus/{id}/{status}")
	public Response modifyNewsStatus(@HeaderParam("token") String token, @PathParam("id") String idNews,
			@PathParam("status") String newStatus) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		boolean userIsProjectCreator = newsBean.checkIfUserIsNewsCreator(loggedUser, Integer.valueOf(idNews));

		//System.out.println("voltei no controller: " + userIsProjectCreator);
		// Preciso ver se o user logado é o userCreator da noticia ou se é um admin
		if (loggedUser == null || !userIsProjectCreator && !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		boolean modifiedStatus = newsBean.modifyNewsStatusInDataBase(Integer.valueOf(idNews), newStatus.toUpperCase());

		if (modifiedStatus) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Permite editar uma noticia
	 * 
	 * @param token
	 * @param ProjectDto
	 * @return
	 */
	@POST
	@Path("/editNews/{id}")
	public Response editNews(@HeaderParam("token") String token, NewsDto newsDto, @PathParam("id") String idNews) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		boolean userIsNewsCreator = newsBean.checkIfUserIsNewsCreator(loggedUser, Integer.valueOf(idNews));
		// System.out.println("voltei no controller: " + userIsNewsCreator);
		// Preciso ver se o user logado é o userCreator do projeto ou se é um admin
		if (loggedUser == null || !userIsNewsCreator && !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		boolean updatedNews = newsBean.editNewsInDataBase(Integer.valueOf(idNews), newsDto);

		if (updatedNews) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de todas os noticias visible. Para os exibir aos visitantes
	 * 
	 * @return
	 */
	@GET
	@Path("/visibleNews")
	public Response getVisibleNews() {

		// System.out.println("getVisibleNews");

		try {
			List<NewsDto> visibleNewsDto = newsBean.getVisibleNewsDto();
			return Response.ok(visibleNewsDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de todos as noticias visible/unvisible/off. Para os exibir aos
	 * admins e membros após o login
	 * 
	 * @return
	 */
	@GET
	@Path("/allNews")
	public Response getAllNews(@HeaderParam("token") String token) {

		//System.out.println("allNews");

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		//System.out.println(loggedUser.getTypeUser());

		// User do token não é admin e nem membro
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {
			List<NewsDto> newsNotOffDto = newsBean.getAllNewsDto();

			return Response.ok(newsNotOffDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}
	
	/**
	 * Busca a lista de todos as noticias visible/unvisible. Para os exibir aos
	 * admins e membros após o login
	 * 
	 * @return
	 */
	@GET
	@Path("/allNotOffNews")
	public Response getAllNotOffNews(@HeaderParam("token") String token) {

		System.out.println("allNews");

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		System.out.println(loggedUser.getTypeUser());

		// User do token não é admin e nem membro
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {
			List<NewsDto> newsNotOffDto = newsBean.getAllnotOffNewsDto();

			return Response.ok(newsNotOffDto).build();
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
	@Path("/newsUser/{username}")
	public Response getNewsListCreatedByUser(@HeaderParam("token") String token,
			@PathParam("username") String username) {

		if (token == null || token.isEmpty() || username.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		// User do token NÂO é o mesmo do username e também não é admin
		if (loggedUser == null || !loggedUser.getUsername().equals(username)
				&& !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		try {
			List<NewsDto> newsDto = newsBean.searchForNewsByUser(username);
			return Response.ok(newsDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca uma noticia através do seu ID
	 * 
	 * @return
	 */
	@GET
	@Path("/newsById/{id}")
	public Response getNewsById(@HeaderParam("token") String token, @PathParam("id") String idNews) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		boolean userIsNewsCreator = newsBean.checkIfUserIsNewsCreator(loggedUser, Integer.valueOf(idNews));

		// System.out.println("voltei no controller: " + userIsNewsCreator);
		// Preciso ver se o user logado é o userCreator do projeto ou se é um admin
		if (loggedUser == null || !userIsNewsCreator && !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		try {
			NewsDto newsListDto = newsBean.getNewsDtoById(Integer.valueOf(idNews));
			return Response.ok(newsListDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca uma noticia visible através do seu ID
	 * 
	 * @return
	 */
	@GET
	@Path("/newsVisibleById/{id}")
	public Response getNewsVisibleById(@PathParam("id") String idNews) {

		try {
			NewsDto newsListDto = newsBean.getNewsDtoById(Integer.valueOf(idNews));
			return Response.ok(newsListDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Permite associar/desassociar membros a um Projeto
	 * 
	 * @param token
	 * @param usernameToManage
	 * @param idNews
	 * @return
	 */
	@POST
	@Path("/manageMembersNews/{id}/{username}")
	public Response manageNewsMembers(@HeaderParam("token") String token,
			@PathParam("username") String usernameToManage, @PathParam("id") String idNews) {

		System.out.println("manageNewsMembers -------------------------");

		if (token == null || token.isEmpty() || usernameToManage.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		News news = newsBean.findNewsById(Integer.valueOf(idNews));

		System.out.println("voltei ao controller");
		
				// Segundo o projeto, qualquer membro/admin pode associar/desassociar outros
		// membros
		// inclusive a si próprio
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		boolean updatedNews = newsBean.manageNewsMembersService(Integer.valueOf(idNews), usernameToManage);

		if (updatedNews) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de membros associados a um determinado noticia. Será usado para
	 * o que consta na ficha do projeto página 2, linha 8 onde diz que na página do
	 * projeto deve constar "que membros da organização participaram nesse projeto"
	 * 
	 * @param token
	 * @param idNews
	 * @return
	 */
	@GET
	@Path("/newslistMembers/{id}")
	public Response getAssociatedNewsMembers(/* @HeaderParam("token") String token, */ @PathParam("id") String idNews) {

		try {
			List<UserDto> newsMembersList = newsBean.searchNewsMembers(Integer.valueOf(idNews));

			System.out.println(newsMembersList);

			return Response.ok(newsMembersList).build();

		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de noticias em que um determinado membro/admin aparece como
	 * associado/colaborador/co-autor
	 * 
	 * @return
	 */
	@GET
	@Path("/newsListAssociatedWithUser/{username}")
	public Response getNewslistAssociatedWithUser(@HeaderParam("token") String token,
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

		// System.out.println("xxxxx");

		try {
			List<NewsDto> newsDto = newsBean.searchListOfNewsAssociatedWithUser(username);
			return Response.ok(newsDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de noticias com status visible ou unvisible através da keyword
	 * indicada como parametro. Será para pesquisas feitas por membros ou admins.
	 * 
	 * @return
	 */
	@GET
	@Path("/notOffNewsByKeyword/{keyword}")
	public Response getNotOffNewsByKeyword(@HeaderParam("token") String token, @PathParam("keyword") String keyword) {

		// System.out.println("entrei em getNotOffNewsByKeyword");

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		// User logado pode ser admin ou pode ser membro
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {
			List<NewsDto> newsListDto = newsBean.searchForNotOffNewsByKeyword(keyword);
			return Response.ok(newsListDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de noticias com status somente visible através da keyword
	 * indicada como parametro. Será para pesquisas feitas por vistantes sem token e
	 * login.
	 * 
	 * @return
	 */
	@GET
	@Path("/visibleNewsByKeyword/{keyword}")
	public Response getVisibleNewsByKeyword(@PathParam("keyword") String keyword) {

		System.out.println("entrei em getVisibleNewsByKeyword");

		try {
			List<NewsDto> newsListDto = newsBean.searchForVisibleNewsByKeyword(keyword);
			return Response.ok(newsListDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de projetos com status visible associados a uma determinada
	 * noticia. Será usado para o diz que na ficha folha 2 - linhas 10/11 "fazendo
	 * sempre acompanhar uma ligação para o projeto e para os membros em volta do
	 * projeto" Ou seja, na página de noticias serão listados os nomes do proejtos
	 * relacionados a mesma com um link para a página do projeto, e na página do
	 * projeto que constará os membros associados a este projeto.
	 * 
	 * @return
	 */
	@GET
	@Path("/projectsAssociated/{idNews}")
	public Response getVisibleProjectsAssociatedWithANews(@PathParam("idNews") String idNews) {

		System.out.println("entrei em getVisibleProjectsAssociatedWithANews");

		try {

			List<ProjectDto> projectsDto = newsBean
					.searchForVisibleProjectsAssociatedWithANews(Integer.valueOf(idNews));
			return Response.ok(projectsDto).build();

		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca a lista de projetos com status visible/unvisible associados a uma
	 * determinada noticia. Será usado para o diz que na ficha folha 2 - linhas
	 * 10/11 "fazendo sempre acompanhar uma ligação para o projeto e para os membros
	 * em volta do projeto" Ou seja, na página de noticias serão listados os nomes
	 * do proejtos relacionados a mesma com um link para a página do projeto, e na
	 * página do projeto que constará os membros associados a este projeto.
	 * 
	 * @return
	 */
	@GET
	@Path("/allProjectsAssociated/{idNews}")
	public Response getNotOffProjectsAssociatedWithANews(@PathParam("idNews") String idNews,
			@HeaderParam("token") String token) {

		System.out.println("entrei em allProjectsAssociated");

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		// Pode ser um admin ou um membro - só não pode ser um visitante
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {

			List<ProjectDto> projectsDto = newsBean
					.searchForAllNotOffProjectsAssociatedWithANews(Integer.valueOf(idNews));
			return Response.ok(projectsDto).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

}
