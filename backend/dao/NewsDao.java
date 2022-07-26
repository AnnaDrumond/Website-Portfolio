package pt.uc.dei.proj5.dao;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.enterprise.inject.New;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import org.hibernate.query.criteria.internal.expression.function.AggregationFunction;

import pt.uc.dei.proj5.dto.NewsDto;
import pt.uc.dei.proj5.entity.News;
import pt.uc.dei.proj5.entity.Project;
import pt.uc.dei.proj5.entity.Status;
import pt.uc.dei.proj5.entity.User;

@Stateless
public class NewsDao extends AbstractDao<News> {
	private static final long serialVersionUID = 1L;

	// Fontes de pesquisa CriteriaAPI
	// https://docs.oracle.com/javaee/7/api/javax/persistence/criteria/CriteriaBuilder.html
	// https://www.baeldung.com/jpa-criteria-api-in-expressions
	// https://www.baeldung.com/hibernate-criteria-queries
	// https://www.baeldung.com/jpa-queries
	// https://www.tabnine.com/code/java/methods/javax.persistence.criteria.CriteriaBuilder/greatest

	public NewsDao() {
		super(News.class);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Converte News Entidade para Dto
	 * 
	 * @param newsEntity
	 * @return
	 */
	public NewsDto convertEntityNewsToDto(News newsEntity) {

		NewsDto newsDto = new NewsDto();
		newsDto.setTitle_newsDto(newsEntity.getTitle_news());
		newsDto.setCover_imageDto(newsEntity.getCover_image());
		newsDto.setContent_newsDto(newsEntity.getContent_news());
		newsDto.setStatusNewsDto(newsEntity.getStatusNews());
		newsDto.setKeywords_news(newsEntity.getKeywords_news().toLowerCase());
		newsDto.setLastUpdateDto(new SimpleDateFormat("yyyy-MM-dd").format(newsEntity.getLastUpdate()));
		newsDto.setUserNewscreatorDto(newsEntity.getUserJoin_creator().getFirst_name() + " "
				+ newsEntity.getUserJoin_creator().getLast_name());
		newsDto.setUsernameNewsCreator(newsEntity.getUserJoin_creator().getUsername());
		// A lista de membros de uma noticia, tem um endpoint de busca próprio dele
		newsDto.setId(newsEntity.getId());
		return newsDto;

	}

	/**
	 * Converte News News Dto para Entidade
	 * 
	 * @param newsDto
	 * @param user_creator
	 * @return
	 */
	public News convertNewsDtoToEntity(NewsDto newsDto, User user_creator) {

		News newsEntity = new News();
		newsEntity.setTitle_news(newsDto.getTitle_newsDto());
		newsEntity.setCover_image(newsDto.getCover_imageDto());
		newsEntity.setContent_news(newsDto.getContent_newsDto());
		newsEntity.setStatusNews(newsDto.getStatusNewsDto());
		newsEntity.setKeywords_news(newsDto.getKeywords_news().toLowerCase());
		newsEntity.setUserJoin_creator(user_creator);
		newsEntity.setId(newsDto.getId());
		return newsEntity;
	}

	/**
	 * Verifica se já existe na BD uma noticia com título igual ao título da noticia
	 * que está sendo inserido no momento
	 * 
	 * @param title_news
	 * @return
	 */
	public boolean checkDatabaseNewsWithTheSameTitle(String title_news) {

		try {
			// System.out.println("entrei no try em checkDatabaseNewsWithTheSameTitle -
			// Dao");

			final CriteriaQuery<News> criteriaQuery = em.getCriteriaBuilder().createQuery(News.class);
			Root<News> c = criteriaQuery.from(News.class);
			criteriaQuery.select(c).where(em.getCriteriaBuilder().equal(c.get("title_news"), title_news));

			List<News> news = em.createQuery(criteriaQuery).getResultList();

			if (news.size() > 0) {
				return true;
			}
			return false;

		} catch (EJBException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Usado no método de atualizar a notícia Converte/Atualiza um Dto Projeto para
	 * a entidade especifica trazida da Bd.
	 * 
	 * @param newsDto
	 * @param idNewsEntity
	 * @return
	 */
	public News convertDtoNewsAndUpdateEntity(NewsDto newsDto, int idNewsEntity) {

		System.out.println("entrei em convertDtoNewsAndUpdateEntity - dao");
		// Vou buscar a própria entidade a ser alterada
		News newsEntity = find(idNewsEntity);
		newsEntity.setTitle_news(newsDto.getTitle_newsDto());
		newsEntity.setCover_image(newsDto.getCover_imageDto());
		newsEntity.setContent_news(newsDto.getContent_newsDto());
		newsEntity.setStatusNews(newsDto.getStatusNewsDto());
		newsEntity.setKeywords_news(newsDto.getKeywords_news().toLowerCase());
		newsEntity.setId(idNewsEntity);
		return newsEntity;
	}

	/**
	 * Busca na base de dados todos as noticias com visibilidade pública.
	 * 
	 * @return
	 */
	public List<News> getAllVisibleNewsInDataBase() {
		try {
			final CriteriaQuery<News> criteriaQuery = em.getCriteriaBuilder().createQuery(News.class);
			Root<News> c = criteriaQuery.from(News.class);

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(c.get("title_news")));
			criteriaQuery.select(c).where(em.getCriteriaBuilder().equal(c.get("statusNews"), Status.VISIBLE));
			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca na base de dados todas as news visible OU unvisible. Não lista news com
	 * status off
	 * 
	 * @return
	 */
	public List<News> getAllNotOffNewsInDataBase() {

		// System.out.println("entrei em getAllNotOffProjectsInDataBase - dao");
		try {
			final CriteriaQuery<News> criteriaQuery = em.getCriteriaBuilder().createQuery(News.class);
			Root<News> c = criteriaQuery.from(News.class);

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(c.get("title_news")));

			criteriaQuery.select(c)
					.where(em.getCriteriaBuilder().or(
							em.getCriteriaBuilder().equal(c.get("statusNews"), Status.UNVISIBLE),
							em.getCriteriaBuilder().equal(c.get("statusNews"), Status.VISIBLE)));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca a lista de noticias criados por um determinado user, para exibir a
	 * lista completa (todos status) de projetos, ao criador dos mesmos ou a um
	 * admin.
	 * 
	 * @param username
	 * @return
	 */
	public List<News> searchForNewsByUserInDatabase(String username) {

		// System.out.println("entrei searchForNewsByUserInDatabase - dao - trouxe
		// username: " + username);
		final CriteriaQuery<News> criteriaQuery = em.getCriteriaBuilder().createQuery(News.class);

		Root<News> project = criteriaQuery.from(News.class);
		Join<News, User> user = project.join("userJoin_creator");

		criteriaQuery.orderBy(em.getCriteriaBuilder().asc(project.get("title_news")));

		criteriaQuery.select(project).where(em.getCriteriaBuilder().equal(user.get("username"), username));

		try {
			return em.createQuery(criteriaQuery).getResultList();
		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca a lista de membros associados a uma determinada noticia.
	 * 
	 * @param username
	 * @return
	 */
	public List<User> searchNewsMembersInDatabase(int idProject) {
		// O que quero é buscar USER
		final CriteriaQuery<User> criteriaQuery = em.getCriteriaBuilder().createQuery(User.class);

		// Aqui entra a tabela principal, qual a tabela de onde quero a resposta
		Root<User> user = criteriaQuery.from(User.class);

		// Juntar a tabela User com Tabela News - entre () entra o atributo que une a
		// entidade User à entidade Project / Basta ir a classe da Entidade para saber
		// quem por aqui
		Join<User, News> news = user.join("newsMembersList");

		criteriaQuery.orderBy(em.getCriteriaBuilder().asc(user.get("first_name")));

		criteriaQuery.select(user).where(
				em.getCriteriaBuilder()
						.equal(news.get("id"), idProject));

		try {
			return em.createQuery(criteriaQuery).getResultList();
		} catch (EJBException e) {
			e.printStackTrace();
			return null;
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
	public List<Project> searchForVisibleProjectsAssociatedWithANewsInDataBase(int idNews) {

		System.out.println("searchForVisibleProjectsAssociatedWithANewsInDataBase ");
		try {

			final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);
			Root<Project> root = criteriaQuery.from(Project.class);
			Join<Project, News> news = root.join("newsOfProject");

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(root.get("title_project")));

			criteriaQuery.select(root)
					.where(em.getCriteriaBuilder().and(
							em.getCriteriaBuilder().equal(root.get("statusProject"), Status.VISIBLE),
							em.getCriteriaBuilder().equal(news.get("id"), idNews)));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Busca a lista de projetos com status visible OU unvisible associados a uma
	 * determinada noticia. Será usado para o diz que na ficha folha 2 - linhas
	 * 10/11 "fazendo sempre acompanhar uma ligação para o projeto e para os membros
	 * em volta do projeto" Ou seja, na página de noticias serão listados os nomes
	 * do proejtos relacionados a mesma com um link para a página do projeto, e na
	 * página do projeto que constará os membros associados a este projeto.
	 * 
	 * @return
	 */
	public List<Project> searchForAllNotOffProjectsAssociatedWithANewsInDataBase(int idNews) {

		System.out.println("searchForAllNotOffProjectsAssociatedWithANewsInDataBase ");
		try {

			final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);
			Root<Project> root = criteriaQuery.from(Project.class);
			Join<Project, News> news = root.join("newsOfProject");

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(root.get("title_project")));

			criteriaQuery.select(root)
					.where(em.getCriteriaBuilder().or(
							em.getCriteriaBuilder().equal(root.get("statusProject"), Status.UNVISIBLE),
							em.getCriteriaBuilder().equal(root.get("statusProject"), Status.VISIBLE)),
							(em.getCriteriaBuilder().and(em.getCriteriaBuilder().equal(news.get("id"), idNews))));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Buscar lista de noticias em que um determinado user está associado/envolvido
	 * 
	 * @param username
	 * @return
	 */
	public List<News> searchListOfNewsAssociatedWithUserInDatabase(String username) {

		System.out.println("entrei em buscar lsia de noticias de um user - dao - trouxe username: " + username);
		final CriteriaQuery<News> criteriaQuery = em.getCriteriaBuilder().createQuery(News.class);

		Root<News> news = criteriaQuery.from(News.class);
		Join<News, User> user = news.join("membersNewsList");

		criteriaQuery.orderBy(em.getCriteriaBuilder().asc(news.get("title_news")));
		criteriaQuery.select(news).where(em.getCriteriaBuilder().equal(user.get("username"), username));

		try {
			return em.createQuery(criteriaQuery).getResultList();
		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Lista os projetos associados a uma determinada noticia
	 * 
	 * @param newsId
	 * @return
	 */
	public List<Project> searchAssociatedProjectsInDataBase(int idNews) {

		System.out.println("searchAssociatedProjectsInDataBase - Newsdao");

		try {

			final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);
			Root<Project> root = criteriaQuery.from(Project.class);
			Join<Project, News> news = root.join("newsOfProject");
			criteriaQuery.select(root).where(em.getCriteriaBuilder().equal(news.get("id"), idNews));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (Exception e) {
			System.out.println("-----------  exceção --------------");
			return null;
		}
	}

	/**
	 * Verifica se o projeto já existe na lista de projetos associados desta noticia
	 * que veio como parametro boolean projectAlreadyAssociatedWithThisNews;
	 * 
	 * @param title_news
	 * @return
	 */
	public boolean checkDataBaseProjectAssociatedWithTheSameId(int id) {

		try {
			System.out.println("entrei no try em checkDatabaseProjectAssociatedWithTheSameId - newsDao ");

			final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);
			Root<Project> rootProject = criteriaQuery.from(Project.class);
			Join<Project, News> news = rootProject.join("newsOfProject");

			criteriaQuery.select(rootProject).where(em.getCriteriaBuilder().equal(news.get("id"), id));

			int sizeList = em.createQuery(criteriaQuery).getResultList().size();

			System.out.println("encontrou projeto com mesmo id " + sizeList);

			if (sizeList > 0) {
				return true;
			}
			return false;

		} catch (EJBException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Busca na base de dados todas as keywords da tabela news.
	 * 
	 * @return
	 */
	public List<String> searchKeywordsFromNewsTable() {

		System.out.println("searchKeywordsFromNewsTable");
		try {
			final CriteriaQuery<String> criteriaQuery = em.getCriteriaBuilder().createQuery(String.class);

			Root<News> c = criteriaQuery.from(News.class);
			criteriaQuery.select(c.get("keywords_news"));

			return em.createQuery(criteriaQuery).getResultList();
		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Lista os projetos associados a uma determinada noticia
	 * 
	 * @param newsId
	 * @return
	 */
	public boolean searchAssociatedProjectsSameId(int idNews, int idProject) {

		System.out.println("searchAssociatedProjectsInDataBase - Newsdao");

		try {

			final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);
			Root<Project> root = criteriaQuery.from(Project.class);
			Join<Project, News> news = root.join("newsOfProject");
			criteriaQuery.select(root).where(em.getCriteriaBuilder().equal(news.get("id"), idNews));

			List<Project> projects = em.createQuery(criteriaQuery).getResultList();

			boolean alreadyAssociated = false;
			for (Project element : projects) {
				if (element.getId() == idProject) {
					alreadyAssociated = true;
					break;
				}
			}

			return alreadyAssociated;

		} catch (Exception e) {
			System.out.println("-----------  exceção --------------");
			return false;
		}
	}

}
