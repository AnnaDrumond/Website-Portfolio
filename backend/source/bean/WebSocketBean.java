package pt.uc.dei.proj5.bean;

import java.io.Serializable;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerConfig;
import javax.ejb.TimerService;
import javax.inject.Inject;
import javax.websocket.Session;

import org.json.JSONObject;

import pt.uc.dei.proj5.dao.NewsDao;
import pt.uc.dei.proj5.dao.ProjectDao;
import pt.uc.dei.proj5.dao.UserDao;
import pt.uc.dei.proj5.entity.News;
import pt.uc.dei.proj5.entity.Project;
import pt.uc.dei.proj5.entity.User;
import pt.uc.dei.proj5.entity.User.UserType;
import pt.uc.dei.proj5.websocket.GeneralWebSocket;

@Startup
@Singleton
public class WebSocketBean implements Serializable {
	private static final long serialVersionUID = 1L;

	@Inject
	UserDao userDao;

	@Inject
	NewsDao newsDao;

	@Inject
	ProjectDao projectDao;

	@Resource
	TimerService tservice;

	private static final Logger logger = Logger.getLogger("WebSocketBean");
	public static Queue<Session> queues = new ConcurrentLinkedQueue<>();

	public WebSocketBean() {
	}

	/**
	 * Primeiro método a ser chamado quando programa começa a correr
	 */
	@PostConstruct
	public void init() {
		/* Initialize the EJB and create a timer*/
		logger.log(Level.INFO, "Initializing EJB. Tenho quantos timers: " + tservice.getTimers().size());
		for (Timer timerAux : tservice.getTimers()) {
			timerAux.cancel();
		}
		tservice.createIntervalTimer(1000, 5000, new TimerConfig());
	}

	/**
	 * Chamado quando termina o tempo definido no método acima linha 73
	 * 
	 * @param timerCancel
	 */
	@Timeout
	public void timeout(Timer timerCancel) {
		//System.out.println("timeout");
		for (Session session : queues) {
			// a cada 5 segundos vai enviar os dados atualizados ao frontend
			GeneralWebSocket.send(session, dashboardDataGenerator());
		}
	}

	/**
	 * Valida o token e valida se o dono do token é um admin
	 * 
	 * @param token
	 * @return
	 */
	public boolean validateTokenForAcess(String token) {
		User loggedUser = (User) userDao.findUserByToken(token);

		if (token == null || token.isEmpty() || !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return false;
		}
		return true;
	}

	/**
	 * Valida se o user logado é um admin
	 * 
	 * @param token
	 * @return
	 */
	public boolean isLoggedUserAdmin(String username) {
		User loggedUser = userDao.find(username);
		if (username == null || username.isEmpty() || !loggedUser.getTypeUser().equals(UserType.ADMINISTRATOR)) {
			return false;
		}
		return true;
	}

	/**
	 * Chama os métodos do dashboard. Cria a string Json que será enviada pelo
	 * WebSocket com todas as informações necessárias ao Dashboard
	 * 
	 * @return
	 */
	public String dashboardDataGenerator() {

		System.out.println("dashboardDataGenerator");
		JSONObject dashboardInformationList = new JSONObject();
		// número de notícias
		dashboardInformationList.put("totalNews", checkTotalNewsNotOff());
		// número de projetos
		dashboardInformationList.put("totalProjects", checkTotalProjectsNotOff());
		// número de membros
		dashboardInformationList.put("totalMembers", checkTotalMembersInDataBase());
		// número de keywords diferentes que existem
		dashboardInformationList.put("totalDifferentKeywords", checkDifferentKeywords());
		// data da última publicação
		dashboardInformationList.put("dateLastPublication", dateOfLastPublication());
		// número de pedidos de registo pendentes de aprovação/promoção a membro
		dashboardInformationList.put("registrationPendingApproval", checkTotalRegistrationPendingApproval());

		//List<User> usersNotApproval = userDao.getAllUsersWithRegistrationPendingApproval();
		//List<JSONObject> usersJson = new ArrayList<>();

		/*for (User user : usersNotApproval) {
			JSONObject JsonObject7 = new JSONObject();
			JsonObject7.put("userType", user.getTypeUser());
			JsonObject7.put("username", user.getUsername());
			JsonObject7.put("photo", user.getPhoto_user());
			usersJson.add(JsonObject7);
		}

		dashboardInformationList.put("usersJson", usersJson);*/

		return dashboardInformationList.toString();
	}

	/**
	 * número de notícias
	 * 
	 * @return
	 */
	public int checkTotalNewsNotOff() {
		String columnName = "statusNews";
		int total = (int) newsDao.searchTotalContent(columnName);
		return total;
	}

	/**
	 * número de projetos
	 * 
	 * @return
	 */
	public int checkTotalProjectsNotOff() {
		 String columnName = "statusProject";
		int total = (int) projectDao.searchTotalContent(columnName);
		return total;
	}

	/**
	 * número de membros
	 * 
	 * @return
	 */
	public int checkTotalMembersInDataBase() {
		int total = (int) userDao.searchTotalMembers();
		return total;
	}

	/**
	 * número de pedidos de registo pendentes de aprovação/promoção a membro
	 * 
	 * @return
	 */
	public int checkTotalRegistrationPendingApproval() {
		List<User> users = userDao.getAllUsersWithRegistrationPendingApproval();
		return users.size();
	}

	/**
	 * número de keywords diferentes que existem
	 * 
	 * @return
	 */
	public int checkDifferentKeywords() {
		// System.out.println("checkDifferentKeywords");
		String columnToSearch = "keywords_news";
		List<String> allkeywordsNotSplit = newsDao.searchKeywordsFromEntityTable(columnToSearch);

		columnToSearch = "keywords_project";
		allkeywordsNotSplit.addAll(projectDao.searchKeywordsFromEntityTable(columnToSearch));

		HashSet<String> allkeywordsNotRepeated = new HashSet<String>();

		for (String string : allkeywordsNotSplit) {

			List<String> auxiliaryList = new ArrayList<>(Arrays.asList(string.split(";")));
			allkeywordsNotRepeated.addAll(auxiliaryList);

		}
		//System.out.println(allkeywordsNotRepeated);
		return allkeywordsNotRepeated.size();
	}

	/**
	 * data da última publicação
	 * 
	 * @return
	 */
	public String dateOfLastPublication() {
		//System.out.println("dateOfLastPublication");

		// Buscar a notícia e o Projeto com data de criação mais recente
		String columnToSearch = "createDate";
		Timestamp publicationDate1 = newsDao.getContentWithMostRecentDate(columnToSearch).getCreateDate();
		Timestamp publicationDate2 = projectDao.getContentWithMostRecentDate(columnToSearch).getCreateDate();

		// Buscar a notícia e o Projeto com data de atualização/edição mais recente
		columnToSearch = "lastUpdate";
		Timestamp publicationDate3 = newsDao.getContentWithMostRecentDate(columnToSearch).getLastUpdate();
		Timestamp publicationDate4 = projectDao.getContentWithMostRecentDate(columnToSearch).getLastUpdate();

		//System.out.println(publicationDate1 + " " + publicationDate2 + " " + publicationDate3 + " " + publicationDate4);

		List<Timestamp> timeList = new ArrayList<Timestamp>();
		timeList.addAll(Arrays.asList(publicationDate1, publicationDate2, publicationDate3, publicationDate4));
		Timestamp lastPublicationDate = Collections.max(timeList);
		String formatedDate = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(lastPublicationDate);
		//System.out.println("mostRecentPublicationDate " + lastPublicationDate);
		return formatedDate;
	}
	
	/**
	 * Aprova o registo de um visitante e o promove a membro da organização
	 * 
	 * @param userToApprove
	 * @return
	 */
	public void approveRegistrationAndPromoteToMember(String username) {
		System.out.println("entrei em approveRegistrationAndPromoteToMember em UserBean");
		try {
			User userToApprove = userDao.find(username);
			userToApprove.setRegistrationApproved(true);
			userToApprove.setTypeUser(UserType.MEMBER);
			userDao.merge(userToApprove);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
