package pt.uc.dei.proj5.dao;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import pt.uc.dei.proj5.entity.News;
import pt.uc.dei.proj5.entity.Status;

@TransactionAttribute(TransactionAttributeType.REQUIRED)
public abstract class AbstractDao<T extends Serializable> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	// Fontes de pesquisa CriteriaAPI
		// https://docs.oracle.com/javaee/7/api/javax/persistence/criteria/CriteriaBuilder.html
		// https://www.baeldung.com/jpa-criteria-api-in-expressions
		// https://www.baeldung.com/hibernate-criteria-queries
		// https://www.baeldung.com/jpa-queries
		//https://www.javatpoint.com/java-tuple
		//https://www.devmedia.com.br/hibernate-api-criteria-realizando-consultas/29627

	private final Class<T> clazz;

	@PersistenceContext(unitName = "backend-projeto5-adrumond") // nome do projecto em si. nome da aplicaçao
	protected EntityManager em;

	public AbstractDao(Class<T> clazz) {
		this.clazz = clazz;
	}

	public T find(Object id) {
		return em.find(clazz, id);
	}

	public void persist(final T entity) {
		// System.out.println("entrei no persist e trouxe: " + entity);
		em.persist(entity);
	}

	public void merge(final T entity) {
		em.merge(entity);
	}

	public void delete(final T entity) {
		em.remove(em.contains(entity) ? entity : em.merge(entity));
	}

	public void deleteById(final Object entityId) {
		final T entity = find(entityId);
		delete(entity);
	}

	public List<T> findAll() {
		
		
		final CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(clazz);
		criteriaQuery.select(criteriaQuery.from(clazz));
		return em.createQuery(criteriaQuery).getResultList();
	}

	public void deleteAll() {

		final CriteriaDelete<T> criteriaDelete = em.getCriteriaBuilder().createCriteriaDelete(clazz);
		criteriaDelete.from(clazz);
		em.createQuery(criteriaDelete).executeUpdate();
	}

	/**
	 * Busca na base de dados todas as keywords da tabela enviada como entidade para
	 * a consulta.
	 * 
	 * @return
	 */

	public List<String> searchKeywordsFromEntityTable(String columnToSearch) {

		//System.out.println("searchKeywordsFromProjectTable");
		try {

			final CriteriaQuery<String> criteriaQuery = em.getCriteriaBuilder().createQuery(String.class);

			Root<T> c = criteriaQuery.from(clazz);

			criteriaQuery.select(c.get(columnToSearch));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Vai a Base de dados buscar a entidade com o conteúdo de acordo com a entidade
	 * chamada
	 * 
	 * @param ColumnName
	 * @return
	 */
	public T getContentWithMostRecentDate(String columnToSearch) {

		try {
			//System.out.println("getContentWithMostRecentDate - Abstract Dao");

			final CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(clazz);
			Root<T> mainRoot = criteriaQuery.from(clazz);
			// fazer subconsulta para data
			Subquery<Date> subqueryCreateDate = criteriaQuery.subquery(Date.class);
			Root<T> rootSubqueryCreateDate = subqueryCreateDate.from(clazz);
			// obter a data máxima na subconsulta
			subqueryCreateDate
					.select(em.getCriteriaBuilder().greatest(rootSubqueryCreateDate.<Date>get(columnToSearch)));
			// fazer um predicado da subconsulta
			Predicate predicateCreatedate = em.getCriteriaBuilder().equal(mainRoot.get(columnToSearch),
					subqueryCreateDate);
			// atribuir predicado ao principal
			criteriaQuery.where(predicateCreatedate);
			// e obter os resultados
			return em.createQuery(criteriaQuery).getSingleResult();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca na base de dados todos as entidades com status visible OU unvisible, que
	 * tenham a keyWord recebida como parametro e devolve como resultado uma lista
	 * em ordem ascendente de títulos de entidade.
	 * 
	 * @return
	 */
	public List<T> searchForNotOffEntitiesByKeywordInDataBase(String keyword, String columnNameStatus,
			String columnNameKeywords, String columnTitle) {

		//System.out.println("entrei em searchForNotOffProjectsByKeywordInDataBase - abstractdao - keyword: " + keyword);

		try {
			final CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(clazz);

			Root<T> root = criteriaQuery.from(clazz);

			//criteriaQuery.distinct(true);// se quiser não ter resultados repetidos
			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(root.get(columnTitle)));

			criteriaQuery.select(root)

					.where(em.getCriteriaBuilder().or(

							em.getCriteriaBuilder().like(root.get(columnNameKeywords),
									'%' + keyword.toLowerCase() + '%'),
							em.getCriteriaBuilder().like(root.get(columnNameKeywords), '%' + keyword.toLowerCase()),
							em.getCriteriaBuilder().like(root.get(columnNameKeywords), keyword.toLowerCase() + '%')),

							(em.getCriteriaBuilder().and(em.getCriteriaBuilder().or(
									em.getCriteriaBuilder().equal(root.get(columnNameStatus), Status.UNVISIBLE),
									em.getCriteriaBuilder().equal(root.get(columnNameStatus), Status.VISIBLE)))));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca na base de dados todos as entidades com status somente visible, que
	 * tenham a keyWord recebida como parametro e devolve como resultado uma lista
	 * em ordem ascendente de títulos de entidade.
	 * 
	 * @return
	 */
	public List<T> searchForVisibleEntitiesByKeywordInDataBase(String keyword, String columnNameStatus,
			String columnNameKeywords, String columnTitle) {

		//System.out.println("entrei em searchForVisibleEntitiesByKeywordInDataBase - Abdao - keyword: " + keyword);

		try {
			final CriteriaQuery<T> criteriaQuery = em.getCriteriaBuilder().createQuery(clazz);
			Root<T> c = criteriaQuery.from(clazz);

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(c.get(columnTitle)));

			criteriaQuery.select(c)

					.where(em.getCriteriaBuilder().or(
							em.getCriteriaBuilder().like(c.get(columnNameKeywords), '%' + keyword.toLowerCase() + '%'),
							em.getCriteriaBuilder().like(c.get(columnNameKeywords), '%' + keyword.toLowerCase()),
							em.getCriteriaBuilder().like(c.get(columnNameKeywords), keyword.toLowerCase() + '%')),
							(em.getCriteriaBuilder()
									.and(em.getCriteriaBuilder().equal(c.get(columnNameStatus), Status.VISIBLE))));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/**
	 * Busca na base de dados a quantidade total de projetos/noticias notOff
	 * 
	 * @return
	 */
	public long searchTotalContent(String columnName) {
		try {
			
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Long> cqCount = builder.createQuery(Long.class);
			
			CriteriaQuery<T> cqEntity = builder.createQuery(clazz);
			Root<T> entityRoot = cqCount.from(cqEntity.getResultType());

			cqCount.select(builder.count(entityRoot));

			Predicate predicate = em.getCriteriaBuilder().or(
					em.getCriteriaBuilder().equal(entityRoot.get(columnName), Status.UNVISIBLE),
					em.getCriteriaBuilder().equal(entityRoot.get(columnName), Status.VISIBLE));

			cqCount.where(predicate);

			return em.createQuery(cqCount).getSingleResult();

		} catch (EJBException e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	/// EXEMPLO DE CRITERIA COM NOTEQUAL
	/*//método para devolver uma lista de utilizadores que estão aprovados
		public List<User> XXXXX(String username) {		
			try {
				final CriteriaQuery<User> criteriaQuery = em.getCriteriaBuilder().createQuery(User.class);
				Root<User> u = criteriaQuery.from(User.class);
				criteriaQuery.select(u)
						.where(em.getCriteriaBuilder().and(
								em.getCriteriaBuilder().equal(u.get("approved"), true), 
								em.getCriteriaBuilder().notEqual(u.get("username"), username)));
				return em.createQuery(criteriaQuery).getResultList();		
			} catch(Exception e) {
				return null;
			}
		}*/
}
