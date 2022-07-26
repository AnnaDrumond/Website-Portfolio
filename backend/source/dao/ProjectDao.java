package pt.uc.dei.proj5.dao;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;

import pt.uc.dei.proj5.dto.ProjectDto;
import pt.uc.dei.proj5.entity.News;
import pt.uc.dei.proj5.entity.Project;
import pt.uc.dei.proj5.entity.Status;
import pt.uc.dei.proj5.entity.User;

@Stateless
public class ProjectDao extends AbstractDao<Project> {
	private static final long serialVersionUID = 1L;

	// Fontes de pesquisa CriteriaAPI
	// https://docs.oracle.com/javaee/7/api/javax/persistence/criteria/CriteriaBuilder.html
	// https://www.baeldung.com/jpa-criteria-api-in-expressions
	// https://www.baeldung.com/hibernate-criteria-queries
	// https://www.baeldung.com/jpa-queries

	public ProjectDao() {
		super(Project.class);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Converte uma entidade Projeto para Dto
	 * 
	 * @param projectEntity
	 * @return
	 */
	public ProjectDto convertEntityProjectToDto(Project projectEntity) {

		ProjectDto projectDto = new ProjectDto();
		projectDto.setTitle_projectDto(projectEntity.getTitle_project());
		projectDto.setContent_projectDto(projectEntity.getContent_project());
		projectDto.setCover_imageDto(projectEntity.getCover_image());
		projectDto.setKeywords_project(projectEntity.getKeywords_project().toLowerCase());
		projectDto.setStatusProjectDto(projectEntity.getStatusProject());
		// No frontend só precisarei do nome completo do meu User criador do projeto
		// Então não irei enviar dados a mais do que preciso para não gastar
		// desnecessariamente processamento
		projectDto.setUserProjectcreatorDto(projectEntity.getUserJoin_creator().getFirst_name() + " "
				+ projectEntity.getUserJoin_creator().getLast_name());
		projectDto.setLastUpdateDto(new SimpleDateFormat("yyyy-MM-dd").format(projectEntity.getLastUpdate()));
		projectDto.setId(projectEntity.getId());
		projectDto.setUsernameOwner(projectEntity.getUserJoin_creator().getUsername());
		return projectDto;
	}

	/**
	 * Converte um Dto Projeto para Entidade
	 * 
	 * @param ProjectDto
	 * @return
	 */
	public Project convertDtoProjectToEntity(ProjectDto ProjectDto, User user_creator) {

		Project projectEntity = new Project();
		// Não coloquei verificações se os valores que vieram do dto são vazios/nulos
		// Pois tenho a anotação @NotBlank na entidade Project
		projectEntity.setTitle_project(ProjectDto.getTitle_projectDto());
		projectEntity.setContent_project(ProjectDto.getContent_projectDto());
		projectEntity.setCover_image(ProjectDto.getCover_imageDto());
		projectEntity.setStatusProject(ProjectDto.getStatusProjectDto());
		projectEntity.setUserJoin_creator(user_creator);
		projectEntity.setKeywords_project(ProjectDto.getKeywords_project().toLowerCase());

		return projectEntity;
	}

	/**
	 * Usado no método de atualizar a notícia Converte/Atualiza um Dto Projeto para
	 * a entidade especifica trazida da Bd.
	 * 
	 * @param ProjectDto
	 * @param idProjectEntity
	 * @return
	 */
	public Project convertDtoProjectAndUpdateEntity(ProjectDto ProjectDto, int idProjectEntity) {

		System.out.println("entrei em convertDtoProjectAndUpdateEntity - dao");
		// Vou buscar a própria entidade a ser alterada
		Project projectEntity = find(idProjectEntity);
		projectEntity.setTitle_project(ProjectDto.getTitle_projectDto());
		projectEntity.setContent_project(ProjectDto.getContent_projectDto());
		projectEntity.setCover_image(ProjectDto.getCover_imageDto());
		projectEntity.setStatusProject(ProjectDto.getStatusProjectDto());
		projectEntity.setKeywords_project(ProjectDto.getKeywords_project().toLowerCase());
		projectEntity.setId(idProjectEntity);
		return projectEntity;
	}

	/**
	 * Verifica se já existe na BD um Projeto com título igual ao título do projeto
	 * que está sendo inserido no momento
	 * 
	 * @param title_project
	 * @return
	 */
	public boolean checkDatabaseProjectWithTheSameName(String title_project) {

		try {
			System.out.println("entrei no try em checkDatabaseProjectWithTheSameName - Dao");
			
			final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);
			Root<Project> c = criteriaQuery.from(Project.class);
			criteriaQuery.select(c).where(em.getCriteriaBuilder().equal(c.get("title_project"), title_project));

			List<Project> projects = em.createQuery(criteriaQuery).getResultList();
			
			System.out.println(projects.size());
			
			if (projects.size() > 0) {
				return true;
			}
			return false;

		} catch (EJBException e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Busca a lista de membros associados a um determinado projeto. Será usado para
	 * o que consta na ficha do projeto página 2, linha 8 onde diz que na página do
	 * projeto deve constar "que membros da organização participaram nesse projeto"
	 * 
	 * @param username
	 * @return
	 */
	public List<User> searchProjectMembersInDatabase(int idProject) {
		// O que quero é buscar USER
		final CriteriaQuery<User> criteriaQuery = em.getCriteriaBuilder().createQuery(User.class);

		// Aqui entra a tabela principal, qual a tabela de onde quero a resposta
		Root<User> user = criteriaQuery.from(User.class);

		// Juntar a tabela User com Tabela Project - entre () entra o atributo que une a
		// entidade User à entidade Project / Basta ir a classe da Entidade para saber
		// quem por
		Join<User, Project> project = user.join("projectMembersList");

		criteriaQuery.orderBy(em.getCriteriaBuilder().asc(user.get("first_name")));

		criteriaQuery.select(user).where(em.getCriteriaBuilder().equal(project.get("id"), idProject));

		try {
			return em.createQuery(criteriaQuery).getResultList();
		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Busca na base de dados todos os projetos com visibilidade pública.
	 * 
	 * @return
	 */
	public List<Project> getAllVisibleProjectsInDataBase() {
		try {
			final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);
			Root<Project> c = criteriaQuery.from(Project.class);

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(c.get("title_project")));
			
			
			criteriaQuery.select(c).where(em.getCriteriaBuilder().equal(c.get("statusProject"), Status.VISIBLE));
			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca na base de dados todos os projetos com visibilidade PRIVADA/unvisible.
	 * 
	 * @return
	 */
	public List<Project> getAllUnvisibleProjectsInDataBase() {
		try {
			final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);
			Root<Project> c = criteriaQuery.from(Project.class);

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(c.get("title_project")));

			criteriaQuery.select(c).where(em.getCriteriaBuilder().equal(c.get("statusProject"), Status.UNVISIBLE));
			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca na base de dados todos os projetos visible OU unvisible. Não lista os
	 * projetos com status off
	 * 
	 * @return
	 */
	public List<Project> getAllNotOffProjectsInDataBase() {

		System.out.println("entrei em getAllNotOffProjectsInDataBase - dao");
		try {
			final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);
			Root<Project> c = criteriaQuery.from(Project.class);

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(c.get("title_project")));

			criteriaQuery.select(c)
					.where(em.getCriteriaBuilder().or(
							em.getCriteriaBuilder().equal(c.get("statusProject"), Status.UNVISIBLE),
							em.getCriteriaBuilder().equal(c.get("statusProject"), Status.VISIBLE)));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Buscar lista de projetos em que um determinado user está associado/envolvido
	 * 
	 * @param username
	 * @return
	 */
	public List<Project> searchListOfProjectsAssociatedWithUserInDatabase(String username) {

		System.out.println("entrei em buscar lsia de projetos de um user - dao - trouxe username: " + username);
		final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);

		Root<Project> project = criteriaQuery.from(Project.class);
		Join<Project, User> user = project.join("membersProjectList");

		criteriaQuery.orderBy(em.getCriteriaBuilder().asc(project.get("title_project")));
		criteriaQuery.select(project).where(em.getCriteriaBuilder().equal(user.get("username"), username));

		try {
			return em.createQuery(criteriaQuery).getResultList();
		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}

	}

	/**
	 * Busca na base de dados todos os projetos com status somente visible, que
	 * tenham a keyWord recebida como parametro e devolve como resultado uma lista
	 * em ordem ascendente de títulos de projeto.
	 * 
	 * @return
	 */
	/*public List<Project> searchForVisibleProjectsByKeywordInDataBase(String keyword) {

		System.out.println("entrei em searchForNotOffProjectsByKeywordInDataBase - dao - keyword: " + keyword);

		try {
			final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);
			Root<Project> c = criteriaQuery.from(Project.class);

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(c.get("title_project")));

			criteriaQuery.select(c)

					.where(em.getCriteriaBuilder().or(
							em.getCriteriaBuilder().like(c.get("keywords_project"), '%' + keyword.toLowerCase() + '%'),
							em.getCriteriaBuilder().like(c.get("keywords_project"), '%' + keyword.toLowerCase()),
							em.getCriteriaBuilder().like(c.get("keywords_project"), keyword.toLowerCase() + '%')),
							(em.getCriteriaBuilder()
									.and(em.getCriteriaBuilder().equal(c.get("statusProject"), Status.VISIBLE))));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}*/

	/**
	 * Busca na base de dados todos os projetos (qualquer status), de um determinado
	 * user que tenham a keyWord recebida como parametro e devolve como resultado
	 * uma lista em ordem ascendente de títulos de projeto.
	 * Não devo precisar
	 * 
	 * @return
	 */
	/*public List<Project> searchForAllProjectsByKeywordInDataBase(String keyword, String username) {

		System.out.println("entrei em searchForNotOffProjectsByKeywordInDataBase - dao - keyword: " + keyword);

		try {
			final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);
			Root<Project> c = criteriaQuery.from(Project.class);
			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(c.get("title_project")));
			Join<Project, User> user = c.join("userJoin_creator");

			criteriaQuery.select(c)

					.where(em.getCriteriaBuilder().or(
							em.getCriteriaBuilder().like(c.get("keywords_project"), '%' + keyword.toLowerCase() + '%'),
							em.getCriteriaBuilder().like(c.get("keywords_project"), '%' + keyword.toLowerCase()),
							em.getCriteriaBuilder().like(c.get("keywords_project"), keyword.toLowerCase() + '%')),
							(em.getCriteriaBuilder()
									.and(em.getCriteriaBuilder().equal(user.get("username"), username))));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}*/

	/**
	 * Busca a lista de noticias visible associadas a um determinado projeto
	 * 
	 * @return
	 */
	public List<News> searchForVisibleNewsAssociatedWithAProjectInDataBase(int idProject) {

		System.out.println("searchForVisibleProjectsAssociatedWithANewsInDataBase ");
		try {

			final CriteriaQuery<News> criteriaQuery = em.getCriteriaBuilder().createQuery(News.class);
			Root<News> root = criteriaQuery.from(News.class);
			Join<News, Project> project = root.join("projectsOfNews");

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(root.get("title_news")));

			criteriaQuery.select(root)
					.where(em.getCriteriaBuilder().and(
							em.getCriteriaBuilder().equal(root.get("statusNews"), Status.VISIBLE),
							em.getCriteriaBuilder().equal(project.get("id"), idProject)));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Busca a lista de noticias visible ou invisible associadas a um determinado projeto
	 * 
	 * @return
	 */
	public List<News> searchForAllNotOffNewsAssociatedWithAProjecInDataBase(int idProject) {

		System.out.println("searchForAllNotOffNewsAssociatedWithAProjecInDataBase ");
		try {

			final CriteriaQuery<News> criteriaQuery = em.getCriteriaBuilder().createQuery(News.class);
			Root<News> root = criteriaQuery.from(News.class);
			Join<News, Project> project = root.join("projectsOfNews");

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(root.get("title_news")));

			criteriaQuery.select(root)
					.where(em.getCriteriaBuilder().or(
							em.getCriteriaBuilder().equal(root.get("statusNews"), Status.UNVISIBLE),
							em.getCriteriaBuilder().equal(root.get("statusNews"), Status.VISIBLE)),
							(em.getCriteriaBuilder().and(em.getCriteriaBuilder().equal(project.get("id"), idProject))));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Busca a lista de projetos criados por um determinado user, para exibir a
	 * lista completa (todos status) de projetos, ao criador dos mesmos ou a um
	 * admin.
	 * 
	 * @param username
	 * @return
	 */
	public List<Project> searchForProjectsByUserInDatabase(String username) {

		System.out.println("entrei searchForProjectsByUserInDatabase - dao - trouxe username: " + username);
		final CriteriaQuery<Project> criteriaQuery = em.getCriteriaBuilder().createQuery(Project.class);

		Root<Project> project = criteriaQuery.from(Project.class);
		Join<Project, User> user = project.join("userJoin_creator");

		criteriaQuery.orderBy(em.getCriteriaBuilder().asc(project.get("title_project")));

		criteriaQuery.select(project).where(em.getCriteriaBuilder().equal(user.get("username"), username));

		try {
			return em.createQuery(criteriaQuery).getResultList();
		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Verifica se já existe um determinado user associado a um determinado projeto
	 * 
	 * @param username
	 * @return
	 */
	public boolean searchMemberInAProject(int idProject, String usernameToManage) {

		System.out.println("searchMemberInAProject  " + idProject + " " + usernameToManage);

		final CriteriaQuery<User> criteriaQuery = em.getCriteriaBuilder().createQuery(User.class);
		Root<User> user = criteriaQuery.from(User.class);
		Join<User, Project> project = user.join("projectMembersList");

		criteriaQuery.select(user)
				.where(em.getCriteriaBuilder().and(em.getCriteriaBuilder().equal(project.get("id"), idProject),
						em.getCriteriaBuilder().equal(user.get("username"), usernameToManage)));
		try {

			int results = em.createQuery(criteriaQuery).getResultList().size();

			// System.out.println(results);

			return (results > 0) ? true : false;

		} catch (EJBException e) {
			e.printStackTrace();
			return true;
		}

	}

	/**
	 * Lista as noticias associadas a um determinada projeto
	 * 
	 * @param newsId
	 * @return
	 */
	public List<News> searchAssociatedNewsInDataBase(int idNews) {

		System.out.println("searchAssociatedNewsInDataBase - Projectdao");

		try {

			final CriteriaQuery<News> criteriaQuery = em.getCriteriaBuilder().createQuery(News.class);
			Root<News> root = criteriaQuery.from(News.class);
			Join<News, Project> project = root.join("projectsOfNews");
			criteriaQuery.select(root).where(em.getCriteriaBuilder().equal(project.get("id"), idNews));
			return em.createQuery(criteriaQuery).getResultList();

		} catch (Exception e) {
			System.out.println("-----------  exceção --------------");
			return null;
		}
	}
	
	/**
	 * Verifica se a noticia já existe na lista de noticias associados deste
	 * projeto que veio como parametro boolean projectAlreadyAssociatedWithThisNews;
	 * 
	 * @param title_news
	 * @return
	 */
	public boolean checkDataBaseNewsAssociatedWithTheSameId(int id) {

		try {
			System.out.println("entrei no try em checkDatabaseNewsAssociatedWithTheSameId - projectDao");

			final CriteriaQuery<News> criteriaQuery = em.getCriteriaBuilder().createQuery(News.class);
			Root<News> rootNews = criteriaQuery.from(News.class);
			Join<News, Project> project = rootNews.join("projectsOfNews");

			criteriaQuery.select(rootNews).where(em.getCriteriaBuilder().equal(project.get("id"), id));

			int sizeList = em.createQuery(criteriaQuery).getResultList().size();
			
			System.out.println("encontrou noticia com mesmo id " + sizeList);

			if (sizeList > 0) {
				return true;
			}
			return false;

		} catch (EJBException e) {
			e.printStackTrace();
			return false;
		}
	}
	

}
