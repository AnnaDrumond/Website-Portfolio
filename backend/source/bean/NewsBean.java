package pt.uc.dei.proj5.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

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
public class NewsBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	UserDao userDao;

	@Inject
	NewsDao newsDao;

	@Inject
	ProjectDao projectDao;

	public boolean addNews(NewsDto newsDto, User user_creator) {

		try {

			News newsEntity = newsDao.convertNewsDtoToEntity(newsDto, user_creator);

			boolean foundNewsWithTheSameTitle = newsDao.checkDatabaseNewsWithTheSameTitle(newsEntity.getTitle_news());

			if (!foundNewsWithTheSameTitle) {

				newsEntity.setUserJoin_creator(user_creator);
				List<User> members_user = generateListOfNewsAssociatedMembers(newsDto);
				newsEntity.setMembersNewsList(members_user);
				newsDao.persist(newsEntity);

				return true;
			}

			return false;

		} catch (Exception e) {

			return false;
		}
	}

	/**
	 * Vai permitir que a lista de membros envolvidos na noticia que será trazida do
	 * frontend venha no formato "string;string;string". O método irá separar os
	 * usernames dos membros envolvidos na noticia, e irá gerar/criar uma lista de
	 * users com base nesta lista de usernames.
	 * 
	 * @param projectDto
	 * @return
	 */
	public List<User> generateListOfNewsAssociatedMembers(NewsDto newsDto) {

		List<String> members_username = new ArrayList<>(Arrays.asList(newsDto.getNews_members().split(";")));
		List<User> members_user = new ArrayList<User>();

		if (members_username != null && members_username.get(0) != "") {

			for (String string_username : members_username) {
				User userFound = userDao.find(string_username);

				// visitantes não podem ser associados a nada
				if (!userFound.getTypeUser().equals(UserType.VISITOR) && userFound != null) {
					members_user.add(userFound);
				}
			}
		}
		return members_user;
	}

	/**
	 * Verifica se o User é o criador da noticia
	 * 
	 * @param loggedUser
	 * @param IdProject
	 * @return
	 */
	public boolean checkIfUserIsNewsCreator(User loggedUser, int idNews) {
		News news = newsDao.find(idNews);
		if (loggedUser.getUsername().equals(news.getUserJoin_creator().getUsername())) {
			return true;
		}
		return false;
	}

	/**
	 * Modifica o status de uma noticia.
	 * 
	 * @param IdProject
	 * @param newStatus
	 * @return
	 */
	public boolean modifyNewsStatusInDataBase(int idNews, String newStatus) {

		News newsFound = newsDao.find(idNews);

		try {
			switch (newStatus) {
			case "VISIBLE":
				newsFound.setStatusNews(Status.VISIBLE);
				break;
			case "UNVISIBLE":
				newsFound.setStatusNews(Status.UNVISIBLE);
				break;
			case "OFF":
				newsFound.setStatusNews(Status.OFF);
				break;
			}
			newsDao.merge(newsFound);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Permite editar um projeto na base de dados.
	 * 
	 * @param idProject
	 * @param projectDto
	 * @param userCreator
	 * @return
	 */
	public boolean editNewsInDataBase(int idNews, NewsDto newstDto) {

		System.out.println("entrei em editProjectInDataBase - bean");

		News originalNews = newsDao.find(idNews);
		News updatedNewsDb = newsDao.convertDtoNewsAndUpdateEntity(newstDto, idNews);

		List<User> members_user = generateListOfNewsAssociatedMembers(newstDto);

		updatedNewsDb.setMembersNewsList(members_user);

		try {
			updatedNewsDb.setUserJoin_creator(originalNews.getUserJoin_creator());
			newsDao.merge(updatedNewsDb);
			return true;
		} catch (Exception e) {
			return false;
		}

	}

	/**
	 * Lista todos todos as news com visibilidade pública.
	 * 
	 * @return
	 */
	public List<NewsDto> getVisibleNewsDto() {

		List<News> entitynews = newsDao.getAllVisibleNewsInDataBase();
		List<NewsDto> dtonews = new ArrayList<NewsDto>();
		for (News news : entitynews) {
			NewsDto newsDto = newsDao.convertEntityNewsToDto(news);
			dtonews.add(newsDto);
		}
		return dtonews;
	}

	/**
	 * Lista todos todos as noticias visible/unvisible/off.
	 * 
	 * @return
	 */
	public List<NewsDto> getAllNewsDto() {

		// System.out.println("entrei em getNotOffNewssDto - bean");

		List<News> entitynews = newsDao.findAll();
		List<NewsDto> dtonews = new ArrayList<NewsDto>();
		for (News news : entitynews) {
			NewsDto newsDto = newsDao.convertEntityNewsToDto(news);
			dtonews.add(newsDto);
		}
		return dtonews;
	}

	/**
	 * Lista todos todos as noticias visible/unvisible/off.
	 * 
	 * @return
	 */
	public List<NewsDto> getAllnotOffNewsDto() {

		// System.out.println("entrei em getNotOffNewssDto - bean");

		List<News> entitynews = newsDao.getAllNotOffNewsInDataBase();
		List<NewsDto> dtonews = new ArrayList<NewsDto>();
		for (News news : entitynews) {
			NewsDto newsDto = newsDao.convertEntityNewsToDto(news);
			dtonews.add(newsDto);
		}
		return dtonews;
	}

	/**
	 * Busca a lista de noticias criados por um determinado user. para exibir a
	 * lista completa (todos status) de noticias, ao criador das mesmas ou a um
	 * admin.
	 * 
	 * @param username
	 * @return
	 */
	public List<NewsDto> searchForNewsByUser(String username) {

		System.out.println("searchForProjectsByUser - bean -  keyword: " + username);

		List<News> entitynews = newsDao.searchForNewsByUserInDatabase(username);
		List<NewsDto> listNewsDto = new ArrayList<NewsDto>();

		for (News news : entitynews) {
			NewsDto newsDto = newsDao.convertEntityNewsToDto(news);
			listNewsDto.add(newsDto);
		}
		return listNewsDto;

	}

	/**
	 * Busca uma noticia na base de dados através do seu id.
	 * 
	 * @param Idnews
	 * @return
	 */
	public NewsDto getNewsDtoById(int Idnews) {
		News newsFound = newsDao.find(Idnews);
		NewsDto projectDto = newsDao.convertEntityNewsToDto(newsFound);
		return projectDto;
	}

	/**
	 * 
	 * @param idProject
	 * @return
	 */
	public News findNewsById(int Idnews) {
		return newsDao.find(Idnews);
	}

	/**
	 * Associa/desassocia membro/admin a uma noticia
	 * 
	 * @param id
	 * @return
	 */
	public boolean manageNewsMembersService(int idNews, String usernameToManage) {

		System.out.println("manageNewsMembersService - bean -  id: " + idNews + usernameToManage);

		// Pesquisa na BD se este Projeto já tem este user (dono do usernameToManage)
		// associado.
		List<User> membersOfNews = newsDao.searchNewsMembersInDatabase(idNews);
		User userToManage = userDao.find(usernameToManage);

		boolean isUserFound = false;
		int indexAux = 0;

		for (int i = 0; i < membersOfNews.size(); i++) {
			if (membersOfNews.get(i).getUsername().equals(usernameToManage)) {
				isUserFound = true;
				indexAux = i;
			}
		}

		System.out.println(isUserFound);

		News news = newsDao.find(idNews); // projeto a ter os membros atualizados

		if (!isUserFound) {
			System.out.println("não encontrei o user");// se não encontrou este este user na lista, adiciona ele
			membersOfNews.add(userToManage);

		} else if (isUserFound) {// se encontrou o user, retira ele
			System.out.println("entrei no else para remover o membro ");
			membersOfNews.remove(indexAux);
		}

		// System.out.println(membersOfProject);

		news.setMembersNewsList(membersOfNews);

		try {
			newsDao.merge(news);
			return true;

		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Chama a consulta à base de dados e transforma a lista de User encontrados em
	 * uma lista de Dto
	 * 
	 * @param IdNews
	 * @return
	 */
	public List<UserDto> searchNewsMembers(int IdNews) {

		List<User> membersEntity = newsDao.searchNewsMembersInDatabase(IdNews);
		List<UserDto> newsMembersDto = new ArrayList<UserDto>();

		for (User user : membersEntity) {
			UserDto userDto = userDao.convertEntityUserToDto(user);
			newsMembersDto.add(userDto);
		}
		return newsMembersDto;
	}

	/**
	 * Busca a lista de projetos em que um determinado user está associado
	 * 
	 * @param username
	 * @return
	 */
	public List<NewsDto> searchListOfNewsAssociatedWithUser(String username) {

		System.out.println("searchListOfNewsAssociatedWithUser");

		List<News> entitynewsList = newsDao.searchListOfNewsAssociatedWithUserInDatabase(username);
		List<NewsDto> dtoNewsList = new ArrayList<NewsDto>();

		System.out.println("voltei da bd");

		for (News news : entitynewsList) {
			NewsDto newsDto = newsDao.convertEntityNewsToDto(news);
			dtoNewsList.add(newsDto);
		}
		return dtoNewsList;
	}

	/**
	 * Busca noticias não visiveis/invisiveis e não apagados através da keyword
	 * indicada .
	 * 
	 * @param keyword
	 * @return
	 */
	public List<NewsDto> searchForNotOffNewsByKeyword(String keyword) {

		// System.out.println("searchForNotOffNewsByKeyword - bean - keyword: " +
		// keyword);

		String columnNameStatus = "statusNews";
		String columnNameKeywords = "keywords_news";
		String columnTitle = "title_news";
		List<News> newsListEntity = newsDao.searchForNotOffEntitiesByKeywordInDataBase(keyword, columnNameStatus,
				columnNameKeywords, columnTitle);

		List<NewsDto> newsListDto = new ArrayList<NewsDto>();

		for (News news : newsListEntity) {
			NewsDto newsDto = newsDao.convertEntityNewsToDto(news);
			newsListDto.add(newsDto);
		}
		return newsListDto;
	}

	/**
	 * Busca projetos públicos/visíveis através da keyword indicada .
	 * 
	 * @param keyword
	 * @return
	 */
	public List<NewsDto> searchForVisibleNewsByKeyword(String keyword) {

		// System.out.println("searchForVisibleProjectsByKeyword - bean - keyword: " +
		// keyword);

		String columnNameStatus = "statusNews";
		String columnNameKeywords = "keywords_news";
		String columnTitle = "title_news";
		List<News> newsListEntity = newsDao.searchForVisibleEntitiesByKeywordInDataBase(keyword, columnNameStatus,
				columnNameKeywords, columnTitle);
		List<NewsDto> newsListDto = new ArrayList<NewsDto>();

		for (News news : newsListEntity) {
			NewsDto newsDto = newsDao.convertEntityNewsToDto(news);
			newsListDto.add(newsDto);
		}
		return newsListDto;
	}

	/**
	 * Busca todas os projetos visiveis associados a uma determinada noticia Será
	 * usado para o diz que na ficha folha 2 - linhas 10/11 "fazendo sempre
	 * acompanhar uma ligação para o projeto e para os membros em volta do projeto"
	 * Ou seja, na página de noticias serão listados os nomes do proejtos
	 * relacionados a mesma com um link para a página do projeto, e na página do
	 * projeto que constará os membros associados a este projeto. Por isso neste
	 * método não estou enviando os memebros associados aos projetos.
	 * 
	 * @param keyword
	 * @return
	 */
	public List<ProjectDto> searchForVisibleProjectsAssociatedWithANews(int idNews) {

		System.out.println("searchForVisibleProjectsAssociatedWithANews - bean - idNews: " + idNews);

		List<Project> projectsEntity = newsDao.searchForVisibleProjectsAssociatedWithANewsInDataBase(idNews);
		List<ProjectDto> projectsDto = new ArrayList<ProjectDto>();

		System.out.println("voltei ao bean com a lista - " + projectsEntity);

		for (Project project : projectsEntity) {
			ProjectDto projectDto = projectDao.convertEntityProjectToDto(project);
			projectsDto.add(projectDto);
		}
		return projectsDto;
	}

	/**
	 * Busca todas os projetos visiveis ou invisiveis associados a uma determinada
	 * noticia Será usado para o diz que na ficha folha 2 - linhas 10/11 "fazendo
	 * sempre acompanhar uma ligação para o projeto e para os membros em volta do
	 * projeto" Ou seja, na página de noticias serão listados os nomes do proejtos
	 * relacionados a mesma com um link para a página do projeto, e na página do
	 * projeto que constará os membros associados a este projeto. Por isso neste
	 * método não estou enviando os memebros associados aos projetos.
	 * 
	 * @param keyword
	 * @return
	 */
	public List<ProjectDto> searchForAllNotOffProjectsAssociatedWithANews(int idNews) {

		System.out.println("searchForAllNotOffProjectsAssociatedWithANews - bean - idNews: " + idNews);

		List<Project> projectsEntity = newsDao.searchForAllNotOffProjectsAssociatedWithANewsInDataBase(idNews);
		List<ProjectDto> projectsDto = new ArrayList<ProjectDto>();

		System.out.println("voltei ao bean com a lista - " + projectsEntity);

		for (Project project : projectsEntity) {
			ProjectDto projectDto = projectDao.convertEntityProjectToDto(project);
			projectsDto.add(projectDto);
		}
		return projectsDto;
	}

	/**
	 * Atualiza as listas de conteúdo associado de noticias e projeto Associando um
	 * ao outro
	 * 
	 * @param project
	 * @param news
	 * @return
	 */
	public boolean associateNewsAndProjectService(Project project, News news) {////////////// ************************************

		System.out.println("associateNewsAndProjectService");
		// System.out.println("projeto: " + project.getTitle_project() + " noticia: " +
		// news.getTitle_news());

		// Verificação se o projeto já existe na lista de projetos associados desta
		// noticia que veio como parametro
		// boolean projectAlreadyAssociatedWithThisNews = newsDao
		// .checkDataBaseProjectAssociatedWithTheSameId(project.getId());

		// Verificação se a noticia já existe na lista de noticias associadas deste
		// projeto que veio como parametro
		// boolean newsAlreadyAssociatedWithThisProject = projectDao
		// .checkDataBaseNewsAssociatedWithTheSameId(news.getId());

		try {

			if (project != null && news != null /*
												 * && !projectAlreadyAssociatedWithThisNews &&
												 * !newsAlreadyAssociatedWithThisProject
												 */) {
				// Buscar lista de projetos associados a noticia com o id informado
				List<Project> projectsAssociatedWithTheNews = newsDao.searchAssociatedProjectsInDataBase(news.getId());

				System.out.println(projectsAssociatedWithTheNews.size());

				// Buscar lista de noticias associadas ao projeto com o id informado
				List<News> newsAssociatedWithTheProject = projectDao.searchAssociatedNewsInDataBase(project.getId());

				System.out.println(newsAssociatedWithTheProject.size());

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

}
