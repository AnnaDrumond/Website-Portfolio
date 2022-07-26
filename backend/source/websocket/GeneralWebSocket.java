package pt.uc.dei.proj5.websocket;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.inject.Inject;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import pt.uc.dei.proj5.bean.WebSocketBean;

@ServerEndpoint(value = "/generalWebSocket/{token}")
public class GeneralWebSocket {

	@Inject
	WebSocketBean websocketBean;
	// private String loggedUser;

	private static final Logger logger = Logger.getLogger(WebSocketBean.class.getName());

	/**
	 * Chamado quando a sessão for aberta
	 * 
	 * @param session
	 * @param username
	 */
	@OnOpen
	public void openConnection(Session session, @PathParam("token") String token) {

		logger.log(Level.INFO, "********** Connection opened *************.");

		try {
			
			//validar o token & se quem está logado é um admin
			boolean isValidAdminToken = websocketBean.validateTokenForAcess(token);

			if (isValidAdminToken) {
				session.getUserProperties().put("token", token);
				// loggedUser = username;
				System.out.println("isValidAdminToken - IF");
				WebSocketBean.queues.add(session);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Chamado quando a sessão é fechada/removida
	 * 
	 * @param session
	 */
	@OnClose
	public void closedConnection(Session session) {
		logger.log(Level.INFO, " ----- Connection closed. ------");
		// loggedUser = "";
		WebSocketBean.queues.remove(session);
	}

	/**
	 * Chamado em caso de erro na sessão
	 * 
	 * @param session
	 * @param t
	 */
	@OnError
	public void error(Session session, Throwable t) {
		logger.log(Level.INFO, t.toString());
		logger.log(Level.INFO, "Connection error.");
		// loggedUser = "";
		WebSocketBean.queues.remove(session);
	}

	/**
	 * Envia os dados do backend para o frontend
	 * 
	 * @param session
	 * @param dashboardInformationList
	 */
	public static void send(Session session, String dashboardInformationList) {

		try {
			session.getBasicRemote().sendText(dashboardInformationList);
			logger.log(Level.INFO, "Sent: {0}", dashboardInformationList);

		} catch (IOException e) {
			logger.log(Level.INFO, e.toString());
		}
	}

	/**
	 * Recebe mensagem do frontend e traz para o back Quando o endpoint WebSocket
	 * recebe uma mensagem/resposta, o método anotado com @OnMessage será chamado.
	 * // O parâmetro “msg” é o id da notificação. // Nota: preciso sempre ter nos
	 * parametros uma String, Boolean ou pong, senão não faz deploy
	 * 
	 * @param session
	 * @param msg
	 * @param username
	 */
	/*@OnMessage
	public void textMessage(Session session, String usernameUserToApprove) {

		String token = session.getUserProperties().get("token").toString();
		
		boolean isValidAdminToken = websocketBean.validateTokenForAcess(token);

		if (isValidAdminToken) {// valida token e conf se é um admin
			try {
				// Aqui não posso chamar métodos que estejam no Bean que seja @Request ou @Session scope
				websocketBean.approveRegistrationAndPromoteToMember(usernameUserToApprove);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}*/

}
