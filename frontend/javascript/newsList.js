import doLanguageRefresh from "./languageSupport.js";
let defaulPath = "http://localhost:8080/backend-projeto5-adrumond/rest/";
let dataUrl = new URLSearchParams();
let notOffNews = [];

let currentLanguage;

/////////////////////////////////////////////////////////////////
//Resgatar dados passados como parametro pelo link por queryString
/////////////////////////////////////////////////////////////////
let param = new URLSearchParams(window.location.search);
let lang = param.get("lang");
let token = param.get("token");
let usernameLoggedUser = param.get("username");

// Se não tiver nenhum idioma na Url da página, definir o idioma como PT
lang === null ? (currentLanguage = "PT") : (currentLanguage = lang);
document.querySelector("select").value = currentLanguage;

window.onload = function loadPage() {
	//console.log("window.onload");
	getTypeUser(usernameLoggedUser);
};

////////////////////////////////////////////////////////////////////////////
//Distribui oa dados de tipo/token do user para os respetivos métodos
//Controla a construção do Menu de acordo com o tipo de user
////////////////////////////////////////////////////////////////////////////
async function returnTypeAndTokenUser(typeLoggedUser) {
	//console.log("estou em returnTypeAndTokenUser  " + typeLoggedUser);
	//console.log(typeof typeLoggedUser); // STRING

	///////////////////////////////////////////////////////////////////////////////////////////////
	// se tipo de user na BD não for um admin, esconde as opções dashboard e gerir membros do menu
	//permite em caso do membro ser promovido ou admin ser despromovido controlar suas ações
	//////////////////////////////////////////////////////////////////////////////////////////////
	if (typeLoggedUser != "ADMINISTRATOR") {
		let adminExclusiveMenu = document.querySelectorAll(".onlyForAdmin");
		//classList me permite colocar nova classe, sem juntar o nome com as que já tem
		// fica nome nome nome e não nomenomenome
		for (let index = 0; index < adminExclusiveMenu.length; index++) {
			//////////////////////////////////////////////////////////////////////////////////////////////////////////////
			// adiciona a classe occult, que no css vai ter o display:none; para impedir a exibição destes itens do menu
			/////////////////////////////////////////////////////////////////////////////////////////////////////////////
			adminExclusiveMenu[index].classList.add("occult");
		}
	} else if (typeLoggedUser == "VISITOR" || typeLoggedUser == null) {
		//A cada troca de página verifica se o user ainda é um membro ou admin
		loadAuthorizatioError();
		dataUrl.append("lang", document.querySelector("select").value);
		setTimeout(function () {
			doLogout(); // se não for mais parte da equipa depois de 2 segundos, força o logout
		}, 2000);
	}
	await getLoggeduser();
	await getNotOffNews(typeLoggedUser);
}

////////////////////////////////////////////////////////////////////////////
//Buscar nome e foto do user logado
////////////////////////////////////////////////////////////////////////////
function getLoggeduser() {
	//let token = user.tokenLoggeduser;
	//console.log("getLoggeduser token " + token);

	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};

	let path = defaulPath + "user/loggedUserData";

	fetch(path, fetchOptions)
		.then((response) => {
			if (response.status === 200) {
				//limpar erro
				return response.json();
			} else if (response.status === 401) {
				//mostrar erro
			} else {
				//mostrar erro
			}
		})
		.then((data) => {
			console.log("user data        " + data);
			doWelcome(data);
		});
}

////////////////////////////////////////////////////////////////////////////
//Criar topo com foto e msg de boas-vindas
////////////////////////////////////////////////////////////////////////////
function doWelcome(user) {
	document.getElementById("user-photo").src = user.photo;
	let topWelcome = document.querySelector("#top-welcome");
	let li = document.createElement("li"); //li
	li.className = "welcome-top-title";
	li.textContent = "Bem-Vindo, ";
	li.setAttribute("data-i18n", "welcome");
	topWelcome.appendChild(li);
	let liName = document.createElement("li"); //li
	liName.className = "nameUser";
	liName.textContent = user.name;
	topWelcome.appendChild(liName);

	let liNews = document.createElement("h3"); //li
	liNews.className = "news-top-title";
	liNews.textContent = "Lista de notícias";
	liNews.setAttribute("data-i18n", "newsList");
	topWelcome.appendChild(liNews);
}

////////////////////////////////////////////////////////////////////////////
//Buscar lista de noticias visible/unvisible
////////////////////////////////////////////////////////////////////////////
async function getNotOffNews(typeLoggedUser) {
	//console.log("getNotOffNews");

	//let token = user.tokenLoggeduser;
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};

	let path = defaulPath + "news/allNews";

	await fetch(path, fetchOptions)
		.then((response) => {
			if (response.status === 200) {
				//limpar erro
				return response.json();
			} else if (response.status === 401) {
				//mostrar erro
			} else {
				//mostrar erro
			}
		})
		.then((data) => {
			notOffNews = data;
			loadNotOffNews(notOffNews, typeLoggedUser);
		});
}

//////////////////////////////////////////////////////////
//Carregar a lista de noticias visible/invisible no ecrã
//////////////////////////////////////////////////////////
function loadNotOffNews(notOffNews, typeLoggedUser) {
	console.log("entrei em loadNotOffNews com tipo de user " + typeLoggedUser);

	for (let i = 0; i < notOffNews.length; i++) {
		//statusNewsDto

		if (notOffNews[i].statusNewsDto == "OFF") {
			if (
				typeLoggedUser != "ADMINISTRATOR" &&
				notOffNews[i].usernameNewsCreator != usernameLoggedUser
			) {
			} else {
				console.log(notOffNews[i].usernameNewsCreator);

				let pageNews = document.querySelector("#section-newsNotOff");
				let newsArticle = document.createElement("article"); //li

				let divPrincipal = document.createElement("div");
				divPrincipal.className = "news-box";
				//colocar a div dentro do article
				newsArticle.appendChild(divPrincipal);

				let divTexts = document.createElement("div");
				divTexts.className = "news-box-texts";

				let image = document.createElement("img"); // imagem
				image.className = "news-image";
				image.src = notOffNews[i].cover_imageDto;
				image.setAttribute("height", "120");
				image.setAttribute("width", "180");

				//colocar a imagem e a segunda div dentro da div principal
				divPrincipal.appendChild(image);
				divPrincipal.appendChild(divTexts);

				let title = document.createElement("h5"); //titulo
				title.className = "news-title-notOff";
				title.innerText = notOffNews[i].title_newsDto;
				//colocar o titulo dentro da segunda div
				divTexts.appendChild(title);

				let creator = document.createElement("h6"); //titulo
				creator.className = "news-creator";
				creator.innerText = notOffNews[i].userNewscreatorDto;
				divTexts.appendChild(creator);

				/*let status = document.createElement("h6"); //titulo
				status.className = "news-creator";
				status.innerText = notOffNews[i].statusNewsDto;
				divTexts.appendChild(status);*/

				let lastUpdate = document.createElement("h6"); //data ultima atualização
				lastUpdate.className = "news-lestUpdate";
				lastUpdate.innerText = notOffNews[i].lastUpdateDto;
				lastUpdate.setAttribute("data-i18n-date", notOffNews[i].lastUpdateDto);

				divTexts.appendChild(lastUpdate);

				let buttonLearnMore = document.createElement("button");
				buttonLearnMore.className = "news-buttonLearnMore";
				buttonLearnMore.innerText = "Ver Noticia";
				buttonLearnMore.setAttribute("data-i18n", "seeNews");

				buttonLearnMore.addEventListener("click", function (e) {
					//criar query string para enviar dados como parametro pelo link
					//enviar o id como parametro pelo link
					dataUrl.append("idNews", notOffNews[i].id);
					dataUrl.append("token", token);
					dataUrl.append("username", usernameLoggedUser);
					dataUrl.append("lang", document.querySelector("select").value); // Aqui levo comigo o id e o idioma
					window.location.href = "newsPage.html?" + dataUrl.toString();
				});

				if (
					typeLoggedUser == "ADMINISTRATOR" ||
					usernameLoggedUser == notOffNews[i].usernameNewsCreator
				) {
					let buttonEditNews = document.createElement("button");
					buttonEditNews.className = "news-buttonLearnMore";
					buttonEditNews.innerText = "Editar Noticia";
					buttonEditNews.setAttribute("data-i18n", "editNews");
					buttonEditNews.addEventListener("click", function (e) {
						//criar query string para enviar dados como parametro pelo link
						//enviar o id como parametro pelo link
						dataUrl.append("idNews", notOffNews[i].id);
						dataUrl.append("token", token);
						dataUrl.append("username", usernameLoggedUser);
						dataUrl.append("lang", document.querySelector("select").value);
						window.location.href = "editNews.html?" + dataUrl.toString();
					});
					divTexts.appendChild(buttonEditNews);
				}

				//colocar o botão dentro da segunda div
				divTexts.appendChild(buttonLearnMore);
				//colocar o article que tem tudo dentro, para dentro da section que está no ficheiro html
				pageNews.appendChild(newsArticle);
			}
			///////////////////////////////////////////////
		} else if (notOffNews[i].statusNewsDto != "OFF") {
			console.log(notOffNews[i].usernameNewsCreator);

			let pageNews = document.querySelector("#section-newsNotOff");
			let newsArticle = document.createElement("article"); //li

			let divPrincipal = document.createElement("div");
			divPrincipal.className = "news-box";
			//colocar a div dentro do article
			newsArticle.appendChild(divPrincipal);

			let divTexts = document.createElement("div");
			divTexts.className = "news-box-texts";

			let image = document.createElement("img"); // imagem
			image.className = "news-image";
			image.src = notOffNews[i].cover_imageDto;
			image.setAttribute("height", "120");
			image.setAttribute("width", "180");

			//colocar a imagem e a segunda div dentro da div principal
			divPrincipal.appendChild(image);
			divPrincipal.appendChild(divTexts);

			let title = document.createElement("h5"); //titulo
			title.className = "news-title-notOff";
			title.innerText = notOffNews[i].title_newsDto;
			//colocar o titulo dentro da segunda div
			divTexts.appendChild(title);

			let creator = document.createElement("h6"); //titulo
			creator.className = "news-creator";
			creator.innerText = notOffNews[i].userNewscreatorDto;
			divTexts.appendChild(creator);

			/*let status = document.createElement("h6"); //titulo
			status.className = "news-creator";
			status.innerText = notOffNews[i].statusNewsDto;
			divTexts.appendChild(status);*/

			let lastUpdate = document.createElement("h6"); //data ultima atualização
			lastUpdate.className = "news-lestUpdate";
			lastUpdate.innerText = notOffNews[i].lastUpdateDto;
			lastUpdate.setAttribute("data-i18n-date", notOffNews[i].lastUpdateDto);

			divTexts.appendChild(lastUpdate);

			let buttonLearnMore = document.createElement("button");
			buttonLearnMore.className = "news-buttonLearnMore";
			buttonLearnMore.innerText = "Ver Noticia";
			buttonLearnMore.setAttribute("data-i18n", "seeNews");

			buttonLearnMore.addEventListener("click", function (e) {
				//criar query string para enviar dados como parametro pelo link
				//enviar o id como parametro pelo link
				dataUrl.append("idNews", notOffNews[i].id);
				dataUrl.append("token", token);
				dataUrl.append("username", usernameLoggedUser);
				dataUrl.append("lang", document.querySelector("select").value); // Aqui levo comigo o id e o idioma
				window.location.href = "newsPage.html?" + dataUrl.toString();
			});

			if (
				typeLoggedUser == "ADMINISTRATOR" ||
				usernameLoggedUser == notOffNews[i].usernameNewsCreator
			) {
				let buttonEditNews = document.createElement("button");
				buttonEditNews.className = "news-buttonLearnMore";
				buttonEditNews.innerText = "Editar Noticia";
				buttonEditNews.setAttribute("data-i18n", "editNews");
				buttonEditNews.addEventListener("click", function (e) {
					//criar query string para enviar dados como parametro pelo link
					//enviar o id como parametro pelo link
					dataUrl.append("idNews", notOffNews[i].id);
					dataUrl.append("token", token);
					dataUrl.append("username", usernameLoggedUser);
					dataUrl.append("lang", document.querySelector("select").value);
					window.location.href = "editNews.html?" + dataUrl.toString();
				});
				divTexts.appendChild(buttonEditNews);
			}

			//colocar o botão dentro da segunda div
			divTexts.appendChild(buttonLearnMore);
			//colocar o article que tem tudo dentro, para dentro da section que está no ficheiro html
			pageNews.appendChild(newsArticle);
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	//Este método está em currentLanguageSupport, que por sua vez foi exportada para este ficheiro JS
	///////////////////////////////////////////////////////////////////////////////////////////
	doLanguageRefresh(document.querySelector("#languages").value);
}

//////////////////////////////////////////////////////////
//Logout
//////////////////////////////////////////////////////////
function doLogout() {
	const headers = new Headers();
	headers.append("username", usernameLoggedUser);

	const fetchOpcoes = {
		method: "POST",
		"Content-Type": "application/json",
		headers: headers,
	};

	let path = defaulPath + "user/logout";

	fetch(path, fetchOpcoes).then((response) => {
		console.log("Response Status: " + response.status);

		if (response.status == 200) {
			dataUrl.append("lang", document.querySelector("select").value);
			window.location.href = "index.html?" + dataUrl.toString();
		} else {
			//tratar erro
		}
	});
}

//////////////////////////////////////////////////////////////////////////////////
// Ações dos botões do do Menu.
// Irá construir a URL com o idioma atual para o mesmo ser resgatado em
// languageSupport.js na linha 18 e outros dados conforme necessidade.
//////////////////////////////////////////////////////////////////////////////////

//EDITAR PERFIL////////////////////////////////////////////////////////////////
document
	.querySelector("#nav-bar-profile")
	.addEventListener("click", function () {
		dataUrl.append("lang", document.querySelector("select").value);
		dataUrl.append("token", token);
		dataUrl.append("username", usernameLoggedUser);
		window.location.href = "EditProfile.html?" + dataUrl.toString();
	});

//CRIAR PROJETO////////////////////////////////////////////////////////////////
document
	.querySelector("#nav-bar-project")
	.addEventListener("click", function () {
		dataUrl.append("lang", document.querySelector("select").value);
		dataUrl.append("token", token);
		dataUrl.append("username", usernameLoggedUser);
		window.location.href = "createProject.html?" + dataUrl.toString();
	});

//CRIAR NOTICIAS////////////////////////////////////////////////////////////////
document
	.querySelector("#nav-bar-create-news")
	.addEventListener("click", function () {
		dataUrl.append("lang", document.querySelector("select").value);
		dataUrl.append("token", token);
		dataUrl.append("username", usernameLoggedUser);
		window.location.href = "createNews.html?" + dataUrl.toString();
	});

//PROJETOS/////////////////////////////////////////////////////////////////
document
	.querySelector("#nav-bar-projectsList")
	.addEventListener("click", function () {
		dataUrl.append("lang", document.querySelector("select").value);
		dataUrl.append("token", token);
		dataUrl.append("username", usernameLoggedUser);
		window.location.href = "projectsList.html?" + dataUrl.toString();
	});

//LOGOUT/////////////////////////////////////////////////////////////////////////
document
	.querySelector("#button-nav-bar-logout")
	.addEventListener("click", function () {
		doLogout();
	});

//GERIR MEMBROS/////////////////////////////////////////////////////////////////
document
	.querySelector("#button-nav-manage-member")
	.addEventListener("click", function () {
		dataUrl.append("lang", document.querySelector("select").value);
		dataUrl.append("token", token);
		dataUrl.append("username", usernameLoggedUser);
		window.location.href = "manageMember.html?" + dataUrl.toString();
	});

// DASHBOARD///////////////////////////////////////////////////////////
document.querySelector("#dashboard-nav").addEventListener("click", function () {
	dataUrl.append("lang", document.querySelector("select").value);
	dataUrl.append("token", token);
	dataUrl.append("username", usernameLoggedUser);
	window.location.href = "dashboard.html?" + dataUrl.toString();
});

////////////////////////////////////////////////////////////////////////////
//Buscar token e tipo de user logado na base de dados
////////////////////////////////////////////////////////////////////////////
async function getTypeUser(username) {
	//console.log("getTypeUser");
	const headers = new Headers();
	headers.append("username", username);

	const fetchOptions = {
		method: "GET",
		"Content-Type": "application/json",
		headers: headers,
	};

	let path = defaulPath + "user/typeUser";
	await fetch(path, fetchOptions)
		.then((response) => {
			if (response.status === 200) {
				return response.text();
			} else {
				loadAuthorizatioError();
				setTimeout(function () {
					doLogout(); // se não consegue obter o tipo de user, força o logout
				}, 2000);
			}
		})
		.then((data) => {
			//console.log("estou no fetch " + data);
			returnTypeAndTokenUser(data);
		});
}

////////////////////////////////////////////////////////////////////////
//Mensagens de erro
///////////////////////////////////////////////////////////////////////
function loadAuthorizatioError() {
	let currentcurrentLanguage = document.querySelector("select").value;

	if (currentcurrentLanguage == "PT") {
		document.querySelector(".mensagemErro").innerText =
			"Não possui autorização para esta página.";
	} else {
		document.querySelector(".mensagemErro").innerText =
			"You do not have authorization for this page";
	}
	// Depois de 5 segundos tira a mensagem do ecrã
	setTimeout(function () {
		document.querySelector(".mensagemErro").innerText = "";
	}, 3000);
}

////////////////////////////////////////////////////////////

////////////////////////////////////////////////////////////////////////////
//Buscar noticias que possuem determinada keyword
////////////////////////////////////////////////////////////////////////////
let notOffNewsByKeyword = [];
let buttonSearch = document.querySelector("#button-search");
buttonSearch.addEventListener("click", getTypeUserAuxiliary);

async function getNewsByKeyword(isAdmin) {
	console.log("cliquei no search e entrei em getNewsByKeyword   " + isAdmin);

	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};

	let keywordToFound = document.getElementById("input-search").value;
	console.log(keywordToFound);
	let path = defaulPath + "news/notOffNewsByKeyword/" + keywordToFound;

	await fetch(path, fetchOptions)
		.then((response) => {
			console.log(response.status);
			if (response.status === 200) {
				return response.json();
			} else {
				//mostrar erro
			}
		})
		.then((data) => {
			notOffNewsByKeyword = data;
			console.log("XXXXXXXXXXXXXXXXXX " + notOffNewsByKeyword);
			updateNews(notOffNewsByKeyword, isAdmin);
		});
}

/////////////////////////////////////////////////
//Recarregar somente news com uma certa keyword
/////////////////////////////////////////////////
function updateNews(notOffNewsByKeyword, isAdmin) {
	let newsList = document.querySelector("#section-newsNotOff");
	for (let i = 0; newsList.children.length > 0; i++) {
		newsList.removeChild(newsList.children[0]);
	}
	loadNotOffNews(notOffNewsByKeyword, isAdmin);
}

async function getTypeUserAuxiliary() {
	let username = usernameLoggedUser;
	console.log("getTypeUser");
	const headers = new Headers();
	headers.append("username", username);

	const fetchOptions = {
		method: "GET",
		"Content-Type": "application/json",
		headers: headers,
	};

	let path = defaulPath + "user/typeUser";
	await fetch(path, fetchOptions)
		.then((response) => {
			if (response.status === 200) {
				return response.text();
			} else {
				loadAuthorizatioError();
				setTimeout(function () {
					doLogout(); // se não consegue obter o tipo de user, força o logout
				}, 2000);
			}
		})
		.then((data) => {
			console.log("estou no fetcXXXXXXXXXXXXXXXXXXXXXXX " + data);
			getNewsByKeyword(data);
		});
}
