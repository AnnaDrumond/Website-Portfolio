package pt.uc.dei.proj5.bean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import pt.uc.dei.proj5.dao.NewsDao;
import pt.uc.dei.proj5.dao.ProjectDao;
import pt.uc.dei.proj5.dao.UserDao;
import pt.uc.dei.proj5.entity.User;

public class UserBeanTest {

	@InjectMocks
	public UserBean userBean;
	@Mock
	private UserDao userDao;
	@Mock
	private NewsDao newsDao;
	@Mock
	private ProjectDao projectDao;

	@Before
	public void setUp() {
		userBean = new UserBean();
		MockitoAnnotations.initMocks(this);
	}
	
	
	/// ************ Funcionalidade aprovar registo de um visitante ***************************

	/**
	 * Verifica os casos em que  existe na BD um User respetivo ao username
	 * recebido como parametro pelo método, e verifica se o método invoca
	 * o método merge
	 */
	@Test
	public void approveRegistrationAndPromoteToMemberTest() {
		
		// Cenário
		String username = "legolas";
		
		User userToTest = new User();
		userToTest.setUsername("legolas");
		
		// Ação
		
		//Quando buscar por "legaolas" deve devolver um user com este mesmo username
		Mockito.when(userDao.find(username)).thenReturn(userToTest);
		
		//Verificar se no fim o método correu bem
		Boolean answer = userBean.approveRegistrationAndPromoteToMember(username);

		//verificação
		Assert.assertTrue(answer);
		Mockito.verify(userDao, Mockito.times(1)).merge(userToTest);// o persiste deve ter sido invocado
	}

	/**
	 * Verifica os casos em que não existe na BD um User respetivo ao username
	 * recebido como parametro pelo método
	 */
	@Test
	public void notApproveRegistrationAndPromoteToMemberTest() {
		
		// Cenário
		String username = "aragorn";
		User userToTest = null;
		
		// Ação
		Mockito.when(userDao.find(username)).thenReturn(userToTest);// não encontrou o user na BD
		
		//Verificar se no fim o método correu MAL, pois não achou um user na BD para ser aprovado
		Boolean answer = userBean.approveRegistrationAndPromoteToMember(username);
		
		//verificação
		Assert.assertFalse(answer);
		Mockito.verify(userDao, Mockito.times(0)).merge(userToTest);// o persist não deve ter sido invocado
	}
	
	
	
	
	
	
}