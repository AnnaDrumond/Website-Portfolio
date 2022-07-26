package pt.uc.dei.proj5.dao;

import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Stateless;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import pt.uc.dei.proj5.dto.UserDto;
import pt.uc.dei.proj5.entity.Status;
import pt.uc.dei.proj5.entity.User;
import pt.uc.dei.proj5.entity.User.UserType;

@Stateless
public class UserDao extends AbstractDao<User> {
	private static final long serialVersionUID = 1L;

	// Fontes de pesquisa CriteriaAPI
	// https://docs.oracle.com/javaee/7/api/javax/persistence/criteria/CriteriaBuilder.html
	// https://www.baeldung.com/jpa-criteria-api-in-expressions
	// https://www.baeldung.com/hibernate-criteria-queries
	// https://www.baeldung.com/jpa-queries
	// https://www.javatpoint.com/java-tuple

	public UserDao() {
		super(User.class);
		// TODO Auto-generated constructor stub
	}

	public UserDto convertEntityUserToDto(User userEntity) {
		// System.out.println("Entrei em convertEntityUserToDto");
		UserDto userDto = new UserDto();
		userDto.setFirst_nameDto(userEntity.getFirst_name());
		userDto.setLast_nameDto(userEntity.getLast_name());
		userDto.setUsernameDto(userEntity.getUsername());
		userDto.setPasswordDto(userEntity.getPassword());
		userDto.setEmailDto(userEntity.getEmail());
		userDto.setBiographyDto(userEntity.getBiography());
		userDto.setPhoto_userDto(userEntity.getPhoto_user());
		userDto.setTypeUserDto(userEntity.getTypeUser());
		userDto.setRegistrationApprovedDto(userEntity.isRegistrationApproved());
		return userDto;
	}

	public User convertUserDtoToEntity(UserDto userDto) {
		// System.out.println("Entrei em convertUserDtoToEntity - Services");

		User userEntity = new User();
		userEntity.setFirst_name(userDto.getFirst_nameDto());
		userEntity.setLast_name(userDto.getLast_nameDto());
		userEntity.setUsername(userDto.getUsernameDto().toLowerCase());
		userEntity.setPassword(userDto.getPasswordDto());
		userEntity.setEmail(userDto.getEmailDto());
		userEntity.setBiography(userDto.getBiographyDto());
		userEntity.setPhoto_user(userDto.getPhoto_userDto());
		userEntity.setRegistrationApproved(userDto.isRegistrationApprovedDto());
		userEntity.setTypeUser(userDto.getTypeUserDto());
		return userEntity;
	}

	public User convertNewUserDtoToEntity(UserDto userDto) {
		// System.out.println("Entrei em convertNewUserDtoToEntity - Services");

		User userEntity = new User();
		userEntity.setFirst_name(userDto.getFirst_nameDto());
		userEntity.setLast_name(userDto.getLast_nameDto());
		userEntity.setUsername(userDto.getUsernameDto().toLowerCase());
		userEntity.setPassword(userDto.getPasswordDto());
		userEntity.setEmail(userDto.getEmailDto());
		userEntity.setPhoto_user(userDto.getPhoto_userDto());
		userEntity.setRegistrationApproved(false);
		userEntity.setTypeUser(UserType.VISITOR);
		return userEntity;
	}

	public Object authenticateUserLogin(String username, String password) {

		// System.out.println("Entrei em authenticateUserLogin");

		try {

			// System.out.println("Entrei no TRY");

			Object userFound = em.createNamedQuery("User.encontrarUserLogin").setParameter("username", username)
					.setParameter("password", password).getSingleResult();

			return userFound;
		} catch (Exception erro) {
			return null;
		}

	}

	public Object findUserByToken(String token) {
		// System.out.println("findUserByToken");
		try {
			Object userFound = em.createNamedQuery("User.encontrarPeloToken").setParameter("token", token)
					.getSingleResult();

			return userFound;

		} catch (Exception erro) {
			System.out.println("deu exceção - findUserByToken");
			erro.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca na base de dados todos os users com registo aprovado, ou seja, todos os
	 * membros e todos os admins que formam a equipa da organização.
	 * 
	 * @return
	 */
	public List<User> getAllAdminsAndMembers() {

		System.out.println("getAllAdminsAndMembersDto - DAOOOOO");
		try {

			final CriteriaQuery<User> criteriaQuery = em.getCriteriaBuilder().createQuery(User.class);
			Root<User> c = criteriaQuery.from(User.class);
			criteriaQuery.select(c).where(em.getCriteriaBuilder().equal(c.get("registrationApproved"), true));
			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Lista todos os utilizadores do tipo MEMBER Devolve a lista em ordem
	 * ascendente de nome.
	 * 
	 * @return
	 */
	public List<User> getAllMembersInDataBase() {

		try {

			final CriteriaQuery<User> criteriaQuery = em.getCriteriaBuilder().createQuery(User.class);

			Root<User> c = criteriaQuery.from(User.class);

			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(c.get("first_name")));

			criteriaQuery.select(c).where(em.getCriteriaBuilder().equal(c.get("typeUser"), UserType.MEMBER));
			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca o token de um user através de seu username
	 * 
	 * @return
	 */
	public UserType getTypeUserInDataBase(String username) {

		System.out.println("getTypeUserInDataBase");

		try {

			final CriteriaQuery<UserType> criteriaQuery = em.getCriteriaBuilder().createQuery(UserType.class);

			Root<User> user = criteriaQuery.from(User.class);

			criteriaQuery.select(user.get("typeUser"))
					.where(em.getCriteriaBuilder().equal(user.get("username"), username));

			return em.createQuery(criteriaQuery).getSingleResult();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca na base de dados todos os users com registo NÃO aprovado, ou seja,
	 * todos os vistantes com pedido de registo pendente.
	 * 
	 * @return
	 */
	public List<User> getAllUsersWithRegistrationPendingApproval() {

		//System.out.println("getAllUsersWithRegistrationPendingApproval");

		try {

			final CriteriaQuery<User> criteriaQuery = em.getCriteriaBuilder().createQuery(User.class);

			Root<User> c = criteriaQuery.from(User.class);

			// NOTA: se eu quiser somente a lista de username, com este critério, trocar
			// pelas linhas abaixo
			// final CriteriaQuery<String> criteriaQuery =
			// em.getCriteriaBuilder().createQuery(String.class);
			// Root<User> c = criteriaQuery.from(User.class);
			// criteriaQuery.select(c.get("username")).where(em.getCriteriaBuilder().equal(c.get("registrationApproved"),
			// false));
			// return em.createQuery(criteriaQuery).getResultList();

			criteriaQuery.select(c).where(em.getCriteriaBuilder().equal(c.get("typeUser"), UserType.VISITOR));

			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca na base de dados todos os usernames de admins e membros Usado para
	 * exibir a lista nas comboBox do frontEnd
	 * 
	 * @return
	 */
	public List<String> getAllUsernamesAdminsAndMembers() {

		//System.out.println("getAllUsersWithRegistrationPendingApproval");

		try {

			
			// NOTA: se eu quiser somente a lista de username, com este critério, trocar
			// pelas linhas abaixo
			final CriteriaQuery<String> criteriaQuery = em.getCriteriaBuilder().createQuery(String.class);
			Root<User> c = criteriaQuery.from(User.class);
			
			// Ordenar em ordem alfabética
			criteriaQuery.orderBy(em.getCriteriaBuilder().asc(c.get("username")));
			
			criteriaQuery.select(c.get("username"))
					.where(em.getCriteriaBuilder().or(
							em.getCriteriaBuilder().equal(c.get("typeUser"), UserType.MEMBER),
							em.getCriteriaBuilder().equal(c.get("typeUser"), UserType.ADMINISTRATOR)));
			
			return em.createQuery(criteriaQuery).getResultList();

		} catch (EJBException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Busca na base de dados o total de membros do site
	 * 
	 * @return
	 */
	public long searchTotalMembers() {
		try {
			CriteriaBuilder builder = em.getCriteriaBuilder();
			CriteriaQuery<Long> cqCount = builder.createQuery(Long.class);
			CriteriaQuery<User> cqEntity = builder.createQuery(User.class);
			Root<User> entityRoot = cqCount.from(cqEntity.getResultType());

			cqCount.select(builder.count(entityRoot));

			Predicate predicate = em.getCriteriaBuilder().equal(entityRoot.get("typeUser"), UserType.MEMBER);

			cqCount.where(predicate);

			return em.createQuery(cqCount).getSingleResult();

		} catch (EJBException e) {
			e.printStackTrace();
			return 0;
		}
	}

}
