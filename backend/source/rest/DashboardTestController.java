package pt.uc.dei.proj5.rest;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import pt.uc.dei.proj5.bean.WebSocketBean;
import pt.uc.dei.proj5.bean.NewsBean;
import pt.uc.dei.proj5.bean.ProjectBean;
import pt.uc.dei.proj5.bean.UserBean;

@Path("/dashboardTest")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class DashboardTestController {

	@Inject
	ProjectBean projectBean;

	@Inject
	NewsBean newsBean;

	@Inject
	UserBean userBean;

	@Inject
	WebSocketBean dashboardBean;

	/**
	 * número de notícias
	 * 
	 * @return
	 */
	@GET
	@Path("/numberOfNews")
	public Response getNumberOfNews() {

		// checkTotalNewsNotOff
		try {
			int number = dashboardBean.checkTotalNewsNotOff();
			return Response.ok(number).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * número de projetos
	 * 
	 * @return
	 */
	@GET
	@Path("/numberOfProjects")
	public Response getNumberOfProjects() {

		try {
			int number = dashboardBean.checkTotalProjectsNotOff();
			return Response.ok(number).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * número de membros
	 * 
	 * @return
	 */
	@GET
	@Path("/numberOfMembers")
	public Response getNumberOfMembers() {

		try {
			int number = dashboardBean.checkTotalMembersInDataBase();
			return Response.ok(number).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * número de keywords diferentes que existem
	 * 
	 * @return
	 */
	@GET
	@Path("/numberDifferentKeywords")
	public Response getNumberDifferentKeywords() {

		System.out.println("getNumberDifferentKeywords");

		try {
			int number = dashboardBean.checkDifferentKeywords();
			return Response.ok(number).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

	/**
	 * Data da última publicação
	 * 
	 * @return
	 */
	@GET
	@Path("/lastPublicationDate")
	public Response getLastPublicationDate() {
		
		System.out.println("getLastPublicationDate");

		try {
			String lastPublicationDate = dashboardBean.dateOfLastPublication();
			return Response.ok(lastPublicationDate).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

//dashboardDataGenerator()
	
	/**
	 * Data da última publicação
	 * 
	 * @return
	 */
	@GET
	@Path("/dataDashboard")
	public Response getDataDashboard() {
		
		System.out.println("getDataDashboard");

		try {
			String dataDashboard = dashboardBean.dashboardDataGenerator();
			return Response.ok(dataDashboard).build();
		} catch (Exception erro) {
			return Response.status(401).build();
		}
	}

}
