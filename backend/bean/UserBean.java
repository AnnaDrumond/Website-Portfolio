package pt.uc.dei.proj5.bean;

import javax.enterprise.context.RequestScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;

import org.json.JSONObject;

import pt.uc.dei.proj5.dao.NewsDao;
import pt.uc.dei.proj5.dao.ProjectDao;
import pt.uc.dei.proj5.dao.UserDao;
import pt.uc.dei.proj5.dto.UserDto;
import pt.uc.dei.proj5.entity.User;
import pt.uc.dei.proj5.entity.User.UserType;

@RequestScoped
public class UserBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	UserDao userDao;

	@Inject
	NewsDao newsDao;

	@Inject
	ProjectDao projectDao;

	/**
	 * Cria um novo user na base de dados
	 * 
	 * @param userDto
	 * @return
	 */
	public UserDto createNewUserInDatabase(UserDto userDto) {

		try {
			User userEntity = userDao.convertNewUserDtoToEntity(userDto);
			userDao.persist(userEntity);
			return userDto;
		} catch (Exception erro) {
			return null;
		}
	}

	/**
	 * Vai a BD validar o user que pretende se logar ao site
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	public User validateLogin(String username, String password) {

		User userEntity = (User) userDao.authenticateUserLogin(username, password);
		return userEntity;
	}

	/**
	 * 
	 * @param userEntity
	 * @return
	 */
	public boolean validateLoginAndGenerateToken(User userEntity) {

		try {
			userEntity.setToken(generateToken());
			userDao.merge(userEntity);
			return true;
		} catch (Exception erro) {
			return false;
		}
	}

	/**
	 * responsável por gerar token ao user que foi validado no login
	 * 
	 * @return
	 */
	public String generateToken() {
		UUID tokenUuid = UUID.randomUUID();
		String token = tokenUuid.toString();
		return token;
	}

	/**
	 * Cria um Json para enviar ao front end os dados que serão usados para mostrar
	 * alguns dados do user após o login
	 * 
	 * @param userFound
	 * @return
	 */
	public String generateUserJsonString(User userFound) {/////
		JSONObject userJsonObject = new JSONObject();
		// userJsonObject.put("nome", userFound.getFirst_name());
		userJsonObject.put("type", userFound.getTypeUser());
		userJsonObject.put("token", userFound.getToken());
		userJsonObject.put("username", userFound.getUsername());
		return userJsonObject.toString();
	}

	/**
	 * Permite buscar na BD o user pelo token
	 * 
	 * @param token
	 * @return
	 */
	public User getUserInDatabaseByToken(String token) {
		try {
			User userEntity = (User) userDao.findUserByToken(token);
			System.out.println("voltei de getUserInDatabaseByToken e trouxe o user: " +
			userEntity.getFirst_name());
			return userEntity;
		} catch (Exception erro) {
			erro.printStackTrace();
			return null;
		}
	}

	/**
	 * Permite buscar na BD o token de um user através de seu username
	 * 
	 * @param token
	 * @return
	 */
	public String getTypeUserInDatabase(String username) {
		try {
			UserType type =  userDao.getTypeUserInDataBase(username);
			// System.out.println("voltei de getUserInDatabaseByToken e trouxe o user: " +
			// userEntity.getFirst_name());
			return type.toString();
		} catch (Exception erro) {
			erro.printStackTrace();
			return null;
		}
	}

	/**
	 * Permite buscar na BD o user pelo token
	 * 
	 * @param token
	 * @return
	 */
	public String getUserPhotoAndName(User userEntity) {
		try {
			JSONObject userJsonObject = new JSONObject();
			userJsonObject.put("photo", userEntity.getPhoto_user());
			userJsonObject.put("name", userEntity.getFirst_name() + " " + userEntity.getLast_name());
			return userJsonObject.toString();
		} catch (Exception erro) {
			erro.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Permite buscar na BD o token e tipo de um user
	 * 
	 * @param token
	 * @return
	 */
	public String getUserDataByusername(String username) {
		try {
			User userEntity = userDao.find(username);
			JSONObject userJsonObject = new JSONObject();
			userJsonObject.put("tokenLoggeduser", userEntity.getToken());
			userJsonObject.put("typeLoggedUser", userEntity.getTypeUser());
			return userJsonObject.toString();
		} catch (Exception erro) {
			erro.printStackTrace();
			return null;
		}
	}

	/**
	 * Aprova o registo de um visitante e o promove a membro da organização
	 * 
	 * @param userToApprove
	 * @return
	 */
	public boolean approveRegistrationAndPromoteToMember(String username) {
		//System.out.println("entrei em approveRegistrationAndPromoteToMember em UserBean com o username " + username);
		try {
			User userToApprove = userDao.find(username);
			userToApprove.setRegistrationApproved(true);
			userToApprove.setTypeUser(UserType.MEMBER);
			userDao.merge(userToApprove);
			return true;
		} catch (Exception e) {
			//System.out.println("Entrei no catch");
			//e.printStackTrace();
			return false;
		}
	}

	/**
	 * Lista todos os membros e admins/equipa de organização
	 * 
	 * @return
	 */
	public List<UserDto> getAllAdminsAndMembersDto() {
		
		System.out.println("getAllAdminsAndMembersDto");

		List<User> adminsAndMembers = userDao.getAllAdminsAndMembers();
		List<UserDto> adminsAndMembersDto = new ArrayList<UserDto>();

		for (User user : adminsAndMembers) {
			UserDto userDto = userDao.convertEntityUserToDto(user);
			adminsAndMembersDto.add(userDto);
		}
		return adminsAndMembersDto;
	}

	// getAllMembersDto
	/**
	 * Lista todos os utilizadores do tipo member
	 * 
	 * @return
	 */
	public List<UserDto> getAllMembersDto() {

		List<User> adminsAndMembers = userDao.getAllMembersInDataBase();
		List<UserDto> adminsAndMembersDto = new ArrayList<UserDto>();

		for (User user : adminsAndMembers) {
			UserDto userDto = userDao.convertEntityUserToDto(user);
			adminsAndMembersDto.add(userDto);
		}
		return adminsAndMembersDto;
	}

	/**
	 * Lista todos os users com pedido de registo pendente de aprovação, ou seja,
	 * visitantes
	 * 
	 * @return
	 */
	public List<UserDto> getAllVisitorsDto() {

		List<User> visitors = userDao.getAllUsersWithRegistrationPendingApproval();
		List<UserDto> visitorsDto = new ArrayList<UserDto>();

		for (User user : visitors) {
			UserDto userDto = userDao.convertEntityUserToDto(user);
			visitorsDto.add(userDto);
		}
		return visitorsDto;
	}

	/**
	 * Vai permitir atualizar os dados de um admin ou membro
	 * 
	 * @return
	 */
	public boolean editUserProfile(UserDto userDto, UserType typeUser, String token) {

		try {
			User userEntity = userDao.convertUserDtoToEntity(userDto);
			userEntity.setTypeUser(typeUser);
			userEntity.setToken(token);
			userDao.merge(userEntity);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Permite promover um Membro à Admin. Impede que visitantes sejam promovidos a
	 * Admin.
	 * 
	 * @param userToApprove
	 * @return
	 */
	public boolean promoteToAdmin(String usernameUserToPromove) {
		// System.out.println("entrei em promoteToAdmin em UserBean");
		try {

			User userToPromove = userDao.find(usernameUserToPromove);

			if (userToPromove.getTypeUser().equals(UserType.MEMBER)) {

				userToPromove.setRegistrationApproved(true);
				userToPromove.setTypeUser(UserType.ADMINISTRATOR);
				userDao.merge(userToPromove);
				return true;
			}

			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Permite downgrade de membros/admins para status visitor
	 * 
	 * @param userToApprove
	 * @return
	 */
	public boolean downgradeMemberAndAdmin(String usernameUserToDowngrade) {
		System.out.println("entrei em downgradeMember em UserBean");
		try {

			User userToDrowgrade = userDao.find(usernameUserToDowngrade);

			if (!userToDrowgrade.getTypeUser().equals(UserType.VISITOR)) {
				
				//buscar lista de projetos associados a este user e excluir este user
				
				
				
				
				userToDrowgrade.setTypeUser(UserType.VISITOR);
				userToDrowgrade.setRegistrationApproved(false);
				userToDrowgrade.setBiography(null);
				userToDrowgrade.setToken(null);
				userDao.merge(userToDrowgrade);
				return true;
			}

			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Permite downgrade de um administrador para o status de visitante
	 * 
	 * @param userToApprove
	 * @return
	 */
	public boolean downgradeAdmin(String usernameUserToDowngrade) {
		System.out.println("entrei em downgradeAdmin em UserBean");
		try {

			User userToDrowgrade = userDao.find(usernameUserToDowngrade);

			if (userToDrowgrade.getTypeUser().equals(UserType.ADMINISTRATOR)) {
				userToDrowgrade.setTypeUser(UserType.MEMBER);
				userDao.merge(userToDrowgrade);
				return true;
			}

			return false;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * 
	 * @param usernameForSearch
	 * @return
	 */
	public UserDto getUserDtoByUsername(String usernameForSearch) {

		User userFound = userDao.find(usernameForSearch);
		if (!userFound.getTypeUser().equals(UserType.VISITOR)) {
			UserDto userFoundDto = userDao.convertEntityUserToDto(userFound);
			return userFoundDto;
		}
		return null;
	}

	/**
	 * Logout
	 * 
	 * @param tokenParametro
	 * @return
	 */
	public boolean doLogout(String username) {

		User userEntity = userDao.find(username);

		try {
			userEntity.setToken(null);
			userDao.merge(userEntity);
			return true;

		} catch (Exception erro) {
			return false;
		}
	}

	/**
	 * Permite remover o user da base de dados
	 * 
	 * @param user
	 */
	public boolean rejectRecord(String username) {
		User userToReject = userDao.find(username);

		if (userToReject.getTypeUser().equals(UserType.VISITOR)) {
			userToReject.setTypeUser(UserType.REJECTED);
			userDao.merge(userToReject);
			return true;
		}
		return false;
	}

	public List<String> getUsernamesAdminsAndMembers() {
		return userDao.getAllUsernamesAdminsAndMembers();

	}
	
	public User returnUserByUsername(String username) {
		return userDao.find(username);
	}
	
	

}
