package pt.uc.dei.proj5.rest;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.uc.dei.proj5.bean.UserBean;
import pt.uc.dei.proj5.dto.UserDto;
import pt.uc.dei.proj5.entity.User;
import pt.uc.dei.proj5.entity.User.UserType;

@Path("/user")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class UserController {

	@Inject
	UserBean userBean;

//Para criar o java doc - /** enter	
	/**
	 * Método para inserir novos utilizadores
	 * 
	 * @param userDto
	 * @return
	 */
	@POST
	@Path("/insertUser")
	public Response addNewMember(UserDto userDto) {
		UserDto userDtoAnswer = userBean.createNewUserInDatabase(userDto);
		if (userDtoAnswer != null) {
			return Response.ok().build();
		} else {
			return Response.status(403).build();
		}
	}

	/**
	 * Método para fazer login
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@Path("/login")
	@POST
	public Response loginUtilizador(@HeaderParam("username") String username,
			@HeaderParam("password") String password) {

		//System.out.println("loginUtilizador");

		User userFound = userBean.validateLogin(username, password);

		// Visitante não pode fazer login - somente após admin promover a membro
		if (username.isEmpty() || password.isEmpty() || userFound == null
				|| userFound.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(401).build();
		}

		if (userBean.validateLoginAndGenerateToken(userFound)) {
			return Response.ok(userBean.generateUserJsonString(userFound)).build();
		} else {
			return Response.status(403).build();
		}
	}

	@Path("/logout")
	@POST
	public Response logout(@HeaderParam("username") String username) {

		if (username == null || username.isEmpty()) {
			return Response.status(401).build();
		}

		boolean logoutSuccessfully = userBean.doLogout(username);

		if (logoutSuccessfully) {
			return Response.ok().build();
		} else {
			return Response.status(403).build();
		}

	}

	/**
	 * Permite à um ADMIN aprovar o registo de um user visitor E promover este user
	 * a membro da organização
	 * 
	 * @param username
	 * @param password
	 * @return
	 */
	@Path("/approveRegistration/{username}")
	@POST
	public Response approveRegistrationAndPromoteToMember(@HeaderParam("token") String token,
			@PathParam("username") String usernameUserToApprove) {

		System.out.println("entrei em approveRegistrationAndPromoteToMember " + usernameUserToApprove + token);

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		

		if (loggedUser == null || !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			/*
			 * 403 Forbidden - O cliente não tem direitos de acesso ao conteúdo portanto o
			 * servidor está rejeitando dar a resposta. Diferente do código 401, aqui a
			 * identidade do cliente é conhecida.
			 */
			return Response.status(403).build();
		}

		boolean approvedUser = userBean.approveRegistrationAndPromoteToMember(usernameUserToApprove);

		if (approvedUser) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Permite a um ADMIN buscar a lista de todos os users com registo pendente de
	 * aprovação/promoção a membro. Será usado para listar estes users no frontend
	 * para um ADMIN ver/aprovar estes registos.
	 * 
	 * @param token
	 * @return
	 */
	@GET
	@Path("/listUsersVisitors")
	public Response listUsersPendingRegistrationApproval(@HeaderParam("token") String token) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		try {

			List<UserDto> visitorsDto = userBean.getAllVisitorsDto();
			return Response.ok(visitorsDto).build();

		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Permite listar toda a equipa da organização (membros e admins). Será usado na
	 * parte privada do admin referente ao gerenciamento de membros
	 * 
	 * @param token
	 * @return
	 */
	@GET
	@Path("/listTeam")
	public Response listTeam(@HeaderParam("token") String token) {
		
		System.out.println("--------------------------------------------------------------listTeam token " + token);

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);
		
		System.out.println(loggedUser);

		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {
			List<UserDto> adminsAndMembersDto = userBean.getAllAdminsAndMembersDto();
			return Response.ok(adminsAndMembersDto).build();

		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Permite listar toda a equipa da organização (membros e admins). Será usado na
	 * parte PÚBLICA do site para a página onde a equipa da organização deve ser
	 * apresentada.
	 * 
	 * @param token
	 * @return
	 */
	@GET
	@Path("/listAdminsAndMembers")
	public Response listUsersAdminsAndMembers() {

		try {
			List<UserDto> adminsAndMembersDto = userBean.getAllAdminsAndMembersDto();
			return Response.ok(adminsAndMembersDto).build();

		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Permite editar os dados : nome, email, foto e biografia de um user No
	 * frontend será controlado que somente membros/admins podem editar a biografia.
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/editProfile")
	public Response editMemberAndAdminProfile(@HeaderParam("token") String token, UserDto userDto) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || !loggedUser.getUsername().equals(userDto.getUsernameDto())) {
			return Response.status(403).build();
		}

		boolean updatedUser = userBean.editUserProfile(userDto, loggedUser.getTypeUser(),token);

		if (updatedUser) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Permite fazer upgrade de membro para o status de administrador.
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/promoveToAdmin/{username}")
	public Response promoteMemberToAdmin(@HeaderParam("token") String token,
			@PathParam("username") String usernameUserToPromove) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		boolean promotedUser = userBean.promoteToAdmin(usernameUserToPromove);

		if (promotedUser) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Permite downgrade de um membro para o status de visitante
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/downgradeToVisitor/{username}")
	public Response downgradeToVisitor(@HeaderParam("token") String token,
			@PathParam("username") String usernameUserToDowngrade) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		boolean downgradeUser = userBean.downgradeMemberAndAdmin(usernameUserToDowngrade);

		if (downgradeUser) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Permite downgrade de um admin para membro
	 * 
	 * @param token
	 * @return
	 */
	@POST
	@Path("/downgradeAdmin/{username}")
	public Response downgradeAdminToMember(@HeaderParam("token") String token,
			@PathParam("username") String usernameUserToDowngrade) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		boolean downgradeUser = userBean.downgradeAdmin(usernameUserToDowngrade);

		if (downgradeUser) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Vai buscar um determinado user através do username Será usado para quando o
	 * User criador de conteúdo quiser associar/disassociar novos membros O frontend
	 * terá uma tag de busca para o criador do conteúdo colocar o username de quem
	 * deseja add/remover e este método retornará o User, para que o criador
	 * confirme a ação de add/remover o associado
	 * 
	 * @return
	 */
	@GET
	@Path("/userMemberOrAdmin/{username}")
	public Response getUserByUsername(@HeaderParam("token") String token,
			@PathParam("username") String usernameForSearch) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {
			UserDto userDtoFound = userBean.getUserDtoByUsername(usernameForSearch);

			if (userDtoFound != null) {// achou um admin ou um membro com aquele username
				return Response.ok(userDtoFound).build();
			}
			return Response.status(406).build();
			/*
			 * 406 Not Acceptable Essa resposta é enviada quando o servidor da Web após
			 * realizar a negociação de conteúdo orientada pelo servidor, não encontra
			 * nenhum conteúdo seguindo os critérios fornecidos pelo agente do usuário.
			 */

		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca somente user do tipo members
	 * 
	 * @param token
	 * @param usernameForSearch
	 * @return
	 */
	@GET
	@Path("/listMembers")
	public Response getOnlyMembers(@HeaderParam("token") String token) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		try {
			List<UserDto> onlyMembersDto = userBean.getAllMembersDto();
			return Response.ok(onlyMembersDto).build();

		} catch (Exception erro) {
			return Response.status(401).build();
		}

	}

	/**
	 * 
	 * 
	 * @param token
	 * @param usernameForSearch
	 * @return
	 */
	@GET
	@Path("/loggedUserData")
	public Response getLoggedUserData(@HeaderParam("token") String token) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {
			return Response.ok(userBean.getUserPhotoAndName(loggedUser)).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}

	}

	/**
	 * Vai permitir remover da base de dados um user que teve seu pedido de registo
	 * negado por um admin
	 * 
	 * @param username
	 * @param token
	 * @return
	 */
	@Path("/reject/{username}")
	@POST
	public Response rejectVisitor(@PathParam("username") String username, @HeaderParam("token") String token) {

		if (token.isEmpty() || username.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return Response.status(403).build();
		}

		boolean userRejected = userBean.rejectRecord(username);

		if (userRejected) {
			return Response.ok().build();
		} else {
			return Response.status(401).build();
		}
	}

	/**
	 * Busca somente o tipo de um user pelo seu username
	 * Usado no frontend para controle de funcionalidades
	 * 
	 * @param username
	 * @return
	 */
	@GET
	@Path("/typeUser")
	public Response getTypeUSerByUsername(@HeaderParam("username") String username) {
		
		System.out.println("getTokenByUsername com o username " + username);

		if (username == null || username.isEmpty()) {
			return Response.status(401).build();
		}

		String typeUser = userBean.getTypeUserInDatabase(username);

		if (typeUser == null) {
			return Response.status(403).build();
		}

		try {
			return Response.ok(typeUser).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}

	}

	/**
	 * Busca dados de um user 
	 * 
	 * @param token
	 * @param usernameForSearch
	 * @return
	 */
	@GET
	@Path("/loggedUser/{username}")
	public Response getUserDataToEdit(@HeaderParam("token") String token, @PathParam("username") String username) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		// Não encontrou um user para o token OU o user é um visitante ou user logado
		// não é o mesmo do username
		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)
				|| !loggedUser.getUsername().equals(username)) {
			return Response.status(403).build();
		}

		try {
			UserDto userDtoFound = userBean.getUserDtoByUsername(username);

			if (userDtoFound != null) {// achou um admin ou um membro com aquele username
				return Response.ok(userDtoFound).build();
			}
			return Response.status(406).build();
			/*
			 * 406 Not Acceptable Essa resposta é enviada quando o servidor da Web após
			 * realizar a negociação de conteúdo orientada pelo servidor, não encontra
			 * nenhum conteúdo seguindo os critérios fornecidos pelo agente do usuário.
			 */

		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Vai buscar um determinado user através do username Será usado para quando o
	 * User criador de conteúdo quiser associar/disassociar novos membros O frontend
	 * terá uma tag de busca para o criador do conteúdo colocar o username de quem
	 * deseja add/remover e este método retornará o User, para que o criador
	 * confirme a ação de add/remover o associado
	 * 
	 * @return
	 */
	@GET
	@Path("/usernames")
	public Response getUsernamesMembersAndAdmins(@HeaderParam("token") String token) {

		if (token == null || token.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.getUserInDatabaseByToken(token);

		if (loggedUser == null || loggedUser.getTypeUser().equals(UserType.VISITOR)) {
			return Response.status(403).build();
		}

		try {
			List<String> usernames = userBean.getUsernamesAdminsAndMembers();
			;
			if (usernames != null) {// achou um admin ou um membro com aquele username
				return Response.ok(usernames).build();
			}
			return Response.status(406).build();
			/*
			 * 406 Not Acceptable Essa resposta é enviada quando o servidor da Web após
			 * realizar a negociação de conteúdo orientada pelo servidor, não encontra
			 * nenhum conteúdo seguindo os critérios fornecidos pelo agente do usuário.
			 */

		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}
	
	/**
	 * 
	 * 
	 * @param token
	 * @param usernameForSearch
	 * @return
	 */
	@GET
	@Path("/returnLogged")
	public Response returnLoggedUserByUsername(@HeaderParam("username") String username) {

		if (username == null || username.isEmpty()) {
			return Response.status(401).build();
		}

		User loggedUser = userBean.returnUserByUsername(username);

		if (loggedUser == null) {
			return Response.status(403).build();
		}

		try {
			return Response.ok(userBean.getUserDataByusername(username)).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}

	}

}
