package pt.uc.dei.proj5.bean;

import java.util.ArrayList;
import java.util.List;

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
import pt.uc.dei.proj5.dto.NewsDto;
import pt.uc.dei.proj5.entity.News;
import pt.uc.dei.proj5.entity.User;
import pt.uc.dei.proj5.entity.User.UserType;

//Para habilitar as dependencias do Mockito
// @RunWith(MockitoJUnitRunner.class)
public class NewsBeanTest {
	/*
	 * Outra forma de ativar as dependencias seria colocar
	 * "MockitoAnnotations.initMocks(this)" no método que tem a anotação "@Before"
	 */

	@InjectMocks
	public NewsBean newsBean;

	// @Mock
	// public NewsBean newsBean;

	@Mock
	private UserDao userDao;
	@Mock
	private NewsDao newsDao;
	@Mock
	private ProjectDao projectDao;

	@Before
	public void setUp() {
		newsBean = new NewsBean();// classe escolhida para o mock
		MockitoAnnotations.initMocks(this);
	}

	// **************** Funcionalidade addNews ***********************

	/**
	 * Testando com news com titulos diferentes e tudo ok
	 * 
	 */
	@Test
	public void addNewsTest() {
		// given - cenário
		String username = "legolas";
		String title = "rivendalle";

		User userCreator = new User();
		userCreator.setUsername(username);//////////////////

		NewsDto newsDto = new NewsDto();
		newsDto.setTitle_newsDto(title);
		newsDto.setContent_newsDto("elfos");
		newsDto.setUsernameNewsCreator(username);//////////////////
		newsDto.setNews_members("frodo");

		// News que será usada como sendo a news convertida de DTO -> entity
		News newsToTest = new News();
		newsToTest.setTitle_news(title);
		newsToTest.setContent_news("elfos");
		newsToTest.setUserJoin_creator(userCreator);////////////////////

		// User a ser procurado quando o método addNews chama o método
		// generateListOfNewsAssociatedMembers
		User userA = new User();
		userA.setUsername("frodo");
		userA.setTypeUser(UserType.MEMBER);

		// when - situação
		// verificar criação do user entity
		Mockito.when(newsDao.convertNewsDtoToEntity(newsDto, userCreator)).thenReturn(newsToTest);

		Boolean expected = false;
		Mockito.when(newsDao.checkDatabaseNewsWithTheSameTitle(newsToTest.getTitle_news())).thenReturn(expected);

		// fui para o método generateListOfNewsAssociatedMembers
		Mockito.when(userDao.find("frodo")).thenReturn(userA);

		Boolean isTrue = newsBean.addNews(newsDto, userCreator);

		// then - resposta do método

		// seria esperado que o username que veio do dto seja o mesmo username do
		// userCreattor colocado na entidade news
		Assert.assertEquals(newsToTest.getUserJoin_creator().getUsername(), userCreator.getUsername());
		// Será esperado que não foi encontrado news com mesmo titulo da news criada no
		// momento
		Assert.assertFalse(newsDao.checkDatabaseNewsWithTheSameTitle(newsToTest.getTitle_news()));
		// Será esperado que o método addNews retorne true
		Assert.assertTrue(isTrue);
		// Verificar se será invocado o persist ao menos uma vez
		Mockito.verify(newsDao, Mockito.times(1)).persist(newsToTest);
	}

	/**
	 * Testando caso o newsDto venha sem nenhuma informação/vazio Se o dto veio
	 * vazio do frontend, não deve passar pelo persist/add a news
	 */
	@Test
	public void addNewsWithEmptyNewsDtoTest() {
		// given - cenário
		String username = "galadriel";

		User userCreator = new User();
		userCreator.setUsername(username);//////////////////

		NewsDto newsDto = new NewsDto();

		// News que será usada como sendo a news convertida de DTO -> entity
		// Tem só o userCreator porque este não vem do Dto, o userCreator vai como
		// parametro para o método de conversão
		News newsToTest = new News();
		newsToTest.setUserJoin_creator(userCreator);////////////////////

		// User a ser encontrado quando o método addNews chama o método
		// generateListOfNewsAssociatedMembers, aqui SE newsDto veio sem nada, também
		// não veio em newsDto um username para a busca, ou seja, a busca na BD retornou
		// null
		User userA = null;

		// when - situação/ato/ação
		// verificar criação do news entity devolvendo ums news sem nada dentro
		Mockito.when(newsDao.convertNewsDtoToEntity(newsDto, userCreator)).thenReturn(newsToTest);

		Boolean expected = false;
		Mockito.when(newsDao.checkDatabaseNewsWithTheSameTitle(newsToTest.getTitle_news())).thenReturn(expected);

		// fui para o método generateListOfNewsAssociatedMembers e levei um newsDto
		// vazio ou seja, não tenho usernames na lista, por isso não tenho quem buscar
		// na BD a busca será com valor null e terá null como resposta.
		Mockito.when(userDao.find(null)).thenReturn(userA);

		Boolean isMethodRunned = newsBean.addNews(newsDto, userCreator);// chamar o método para correr

		//////// then - verificações

		// Será esperado que não foi encontrado news com mesmo titulo da news criada no
		// momento
		Assert.assertFalse(newsDao.checkDatabaseNewsWithTheSameTitle(newsToTest.getTitle_news()));
		Assert.assertFalse(isMethodRunned);// Será esperado que o método addNews retorne falso
		Mockito.verify(newsDao, Mockito.times(0)).persist(newsToTest);// não deve invocar o persist
	}

	/**
	 * Testando adicionar noticia que tenha o mesmo título de uma notícia que já
	 * existe na BD
	 */
	@Test
	public void addNewsWithSameTitleTest() {
		// given - cenário
		String username = "gandalf";
		String title = "condado";

		User userCreator = new User();
		userCreator.setUsername(username);//////////////////

		NewsDto newsDto = new NewsDto();
		newsDto.setTitle_newsDto(title);
		newsDto.setContent_newsDto("elfos");
		newsDto.setUsernameNewsCreator(username);//////////////////
		newsDto.setNews_members("frodo");

		// News que será usada como sendo a news convertida de DTO -> entity
		News newsToTest = new News();
		newsToTest.setTitle_news(title);
		newsToTest.setContent_news("elfos");
		newsToTest.setUserJoin_creator(userCreator);

		// User a ser procurado quando o método addNews chama o método
		// generateListOfNewsAssociatedMembers
		User userA = new User();
		userA.setUsername("frodo");
		userA.setTypeUser(UserType.MEMBER);

		// when - situação/ato/ação
		// verificar criação do user entity
		Mockito.when(newsDao.convertNewsDtoToEntity(newsDto, userCreator)).thenReturn(newsToTest);

		Boolean expected = true;// neste caso teria encontrado news com o mesmo titulo
		Mockito.when(newsDao.checkDatabaseNewsWithTheSameTitle(newsToTest.getTitle_news())).thenReturn(expected);

		Mockito.when(userDao.find("frodo")).thenReturn(userA);// fui para o método generateListOfNewsAssociatedMembers

		Boolean isMethodRunned = newsBean.addNews(newsDto, userCreator);// chamar o método a correr

		//////// then - verificações

		// Será esperado que foi encontrado news com mesmo titulo da news criada
		Assert.assertTrue(newsDao.checkDatabaseNewsWithTheSameTitle(newsToTest.getTitle_news()));
		Assert.assertFalse(isMethodRunned);// Será esperado que o método addNews retorne false
		Mockito.verify(newsDao, Mockito.times(0)).persist(newsToTest);// Não deve conseguir ir até o persist
	}

	/**
	 * Testar se a noticia é criada sem membros associados
	 */
	@Test
	public void addNewsWithEmptyMemberList() {

		// given - cenário
		String username = "saruman";
		String title = "gondor";

		User userCreator = new User();
		userCreator.setUsername(username);//////////////////

		NewsDto newsDto = new NewsDto();
		newsDto.setTitle_newsDto(title);
		newsDto.setUsernameNewsCreator(username);//////////////////
		newsDto.setNews_members("");

		// News que será usada como sendo a news convertida de DTO -> entity
		News newsToTest = new News();
		newsToTest.setTitle_news(title);
		newsToTest.setUserJoin_creator(userCreator);////////////////////

		// when - situação
		// verificar criação do user entity
		Mockito.when(newsDao.convertNewsDtoToEntity(newsDto, userCreator)).thenReturn(newsToTest);

		Boolean expected = false;
		Mockito.when(newsDao.checkDatabaseNewsWithTheSameTitle(newsToTest.getTitle_news())).thenReturn(expected);

		Boolean isMethodRunned = newsBean.addNews(newsDto, userCreator);

		Assert.assertTrue(isMethodRunned);// Será esperado que o método addNews retorne true
		// Verificar se será invocado o persist ao menos uma vez
		Mockito.verify(newsDao, Mockito.times(1)).persist(newsToTest);// then - resposta do método
	}

	/**
	 * O método generateListOfNewsAssociatedMembers é invocado dentro de addNews
	 * Teste para verificar se o método adiciona a alista de membros um user com
	 * status de visitante
	 */
	@Test
	public void generateListOfNewsAssociatedMembersAsVisitorTest() {

		NewsDto newsDto = new NewsDto();
		newsDto.setNews_members("smeagol");

		// User a ser devolvido pelo find
		User userA = new User();
		userA.setUsername("smeagol");
		userA.setTypeUser(UserType.VISITOR);

		List<User> members_user = new ArrayList<User>();// lista de resposta do método
		
		// lista para comparar se a resposta do método foi uma lista vazia
		List<User> comparator = new ArrayList<User>();

		// when - situação
		Mockito.when(userDao.find("smeagol")).thenReturn(userA);
		members_user = newsBean.generateListOfNewsAssociatedMembers(newsDto);

		// then - resposta do método
		Assert.assertEquals(members_user, comparator);
	}

}
