import doLanguageRefresh from "./languageSupport.js";
let defaulPath = "http://localhost:8080/backend-projeto5-adrumond/rest/";
let dataUrl = new URLSearchParams();
let notOffProjects = [];
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
	console.log("window.onload");
	getTypeUser(usernameLoggedUser);
};

////////////////////////////////////////////////////////////////////////////
//Distribui oa dados de tipo/token do user para os respetivos métodos
//Controla a construção do Menu de acordo com o tipo de user
////////////////////////////////////////////////////////////////////////////
async function returnTypeAndTokenUser(typeLoggedUser) {
	console.log("estou em returnTypeAndTokenUser  " + typeLoggedUser);
	console.log(typeof typeLoggedUser); // STRING

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
	await getNotOffProjects(typeLoggedUser);
}

////////////////////////////////////////////////////////////////////////////
//Buscar lista de projetos visible/unvisible
////////////////////////////////////////////////////////////////////////////
async function getNotOffProjects(typeLoggedUser) {
	console.log("getNotOffProjects");

	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};

	let path = defaulPath + "project/allProjects";

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
			notOffProjects = data;
			console.log("XXXXXXXXXXXXXXXXXX " + notOffProjects);
			loadNotOffProjects(notOffProjects, typeLoggedUser);
		});
}

//////////////////////////////////////////////////////////
//Carregar a lista de noticias visible/invisible no ecrã
//////////////////////////////////////////////////////////
function loadNotOffProjects(notOffProjects, typeLoggedUser) {
	console.log("entrei em loadNotOffNews");

	for (let i = 0; i < notOffProjects.length; i++) {
		if (notOffProjects[i].statusProjectDto == "OFF") {
			if (
				typeLoggedUser != "ADMINISTRATOR" &&
				notOffProjects[i].usernameOwner != usernameLoggedUser
			) {
			} else {
				console.log(notOffProjects[i].usernameOwner);

				let page = document.querySelector("#section-projectsNotOff");
				let article = document.createElement("article"); //li

				let divPrincipal = document.createElement("div");
				divPrincipal.className = "news-box";
				//colocar a div dentro do article
				article.appendChild(divPrincipal);

				let divTexts = document.createElement("div");
				divTexts.className = "news-box-texts";

				let image = document.createElement("img"); // imagem
				image.className = "news-image";
				image.src = notOffProjects[i].cover_imageDto;
				image.setAttribute("height", "120");
				image.setAttribute("width", "180");

				//colocar a imagem e a segunda div dentro da div principal
				divPrincipal.appendChild(image);
				divPrincipal.appendChild(divTexts);

				let title = document.createElement("h5"); //titulo
				title.className = "news-title-notOff";
				title.innerText = notOffProjects[i].title_projectDto;
				//colocar o titulo dentro da segunda div
				divTexts.appendChild(title);

				let creator = document.createElement("h6"); //titulo
				creator.className = "news-creator";
				creator.innerText = notOffProjects[i].userProjectcreatorDto;
				divTexts.appendChild(creator);

				let lastUpdate = document.createElement("h6"); //data ultima atualização
				lastUpdate.className = "news-lestUpdate";
				lastUpdate.innerText = notOffProjects[i].lastUpdateDto;
				lastUpdate.setAttribute(
					"data-i18n-date",
					notOffProjects[i].lastUpdateDto,
				);

				divTexts.appendChild(lastUpdate);
				//divTexts.appendChild(date);

				let buttonLearnMore = document.createElement("button");
				buttonLearnMore.className = "news-buttonLearnMore";
				buttonLearnMore.innerText = "Ver Projeto";
				buttonLearnMore.setAttribute("data-i18n", "seeProject");

				buttonLearnMore.addEventListener("click", function (e) {
					//criar query string para enviar dados como parametro pelo link
					//enviar o id como parametro pelo link
					console.log("buttonLearnMore.addEventListener");
					console.log(usernameLoggedUser);
					dataUrl.append("idProject", notOffProjects[i].id);
					dataUrl.append("lang", document.querySelector("select").value); // Aqui levo comigo o id e o idioma
					dataUrl.append("token", token);
					dataUrl.append("username", usernameLoggedUser);
					window.location.href = "projectPage.html?" + dataUrl.toString();
				});

				if (
					typeLoggedUser == "ADMINISTRATOR" ||
					usernameLoggedUser == notOffProjects[i].usernameOwner
				) {
					let buttonEdit = document.createElement("button");
					buttonEdit.className = "news-buttonLearnMore";
					buttonEdit.innerText = "Editar Projeto";
					buttonEdit.setAttribute("data-i18n", "editProject");
					buttonEdit.addEventListener("click", function (e) {
						//criar query string para enviar dados como parametro pelo link
						//enviar o id como parametro pelo link
						dataUrl.append("idProject", notOffProjects[i].id);
						dataUrl.append("lang", document.querySelector("select").value);
						dataUrl.append("token", token);
						dataUrl.append("username", usernameLoggedUser);
						window.location.href = "editProject.html?" + dataUrl.toString();
					});
					divTexts.appendChild(buttonEdit);
				}

				//colocar o botão dentro da segunda div
				divTexts.appendChild(buttonLearnMore);
				//colocar o article que tem tudo dentro, para dentro da section que está no ficheiro html
				page.appendChild(article);
			}
		} else if (notOffProjects[i].statusProjectDto != "OFF") {
			console.log(notOffProjects[i].usernameOwner);

			let page = document.querySelector("#section-projectsNotOff");
			let article = document.createElement("article"); //li

			let divPrincipal = document.createElement("div");
			divPrincipal.className = "news-box";
			//colocar a div dentro do article
			article.appendChild(divPrincipal);

			let divTexts = document.createElement("div");
			divTexts.className = "news-box-texts";

			let image = document.createElement("img"); // imagem
			image.className = "news-image";
			image.src = notOffProjects[i].cover_imageDto;
			image.setAttribute("height", "120");
			image.setAttribute("width", "180");

			//colocar a imagem e a segunda div dentro da div principal
			divPrincipal.appendChild(image);
			divPrincipal.appendChild(divTexts);

			let title = document.createElement("h5"); //titulo
			title.className = "news-title-notOff";
			title.innerText = notOffProjects[i].title_projectDto;
			//colocar o titulo dentro da segunda div
			divTexts.appendChild(title);

			let creator = document.createElement("h6"); //titulo
			creator.className = "news-creator";
			creator.innerText = notOffProjects[i].userProjectcreatorDto;
			divTexts.appendChild(creator);

			let lastUpdate = document.createElement("h6"); //data ultima atualização
			lastUpdate.className = "news-lestUpdate";
			lastUpdate.innerText = notOffProjects[i].lastUpdateDto;
			lastUpdate.setAttribute(
				"data-i18n-date",
				notOffProjects[i].lastUpdateDto,
			);
			//lastUpdate.setAttribute("data-i18n", "lastUpdate");
			//froçar reload da página https://developer.mozilla.org/pt-BR/docs/Web/API/Location/reload
			//colocar a data da última publicação dentro da segunda div*/

			divTexts.appendChild(lastUpdate);
			//divTexts.appendChild(date);

			let buttonLearnMore = document.createElement("button");
			buttonLearnMore.className = "news-buttonLearnMore";
			buttonLearnMore.innerText = "Ver Projeto";
			buttonLearnMore.setAttribute("data-i18n", "seeProject");

			buttonLearnMore.addEventListener("click", function (e) {
				//criar query string para enviar dados como parametro pelo link
				//enviar o id como parametro pelo link
				console.log("buttonLearnMore.addEventListener");
				console.log(usernameLoggedUser);
				dataUrl.append("idProject", notOffProjects[i].id);
				dataUrl.append("lang", document.querySelector("select").value); // Aqui levo comigo o id e o idioma
				dataUrl.append("token", token);
				dataUrl.append("username", usernameLoggedUser);
				window.location.href = "projectPage.html?" + dataUrl.toString();
			});

			if (
				typeLoggedUser == "ADMINISTRATOR" ||
				usernameLoggedUser == notOffProjects[i].usernameOwner
			) {
				let buttonEdit = document.createElement("button");
				buttonEdit.className = "news-buttonLearnMore";
				buttonEdit.innerText = "Editar Projeto";
				buttonEdit.setAttribute("data-i18n", "editProject");
				buttonEdit.addEventListener("click", function (e) {
					//criar query string para enviar dados como parametro pelo link
					//enviar o id como parametro pelo link
					dataUrl.append("idProject", notOffProjects[i].id);
					dataUrl.append("lang", document.querySelector("select").value);
					dataUrl.append("token", token);
					dataUrl.append("username", usernameLoggedUser);
					window.location.href = "editProject.html?" + dataUrl.toString();
				});
				divTexts.appendChild(buttonEdit);
			}

			//colocar o botão dentro da segunda div
			divTexts.appendChild(buttonLearnMore);
			//colocar o article que tem tudo dentro, para dentro da section que está no ficheiro html
			page.appendChild(article);
		}

		///////////////////////////////////////////////////////////////////////////////////////////
		//Este método está em currentLanguageSupport, que por sua vez foi exportada para este ficheiro JS
		///////////////////////////////////////////////////////////////////////////////////////////
		doLanguageRefresh(document.querySelector("#languages").value);
	}
}

//////////////////////////////////////////////////////////////////////////////////
// Ação dos botões do Menu.
// Irá construir a URL com o idioma atual para o mesmo ser resgatado em
// languageSupport.js na linha 18.
//////////////////////////////////////////////////////////////////////////////////
document
	.querySelector("#button-nav-bar-logout")
	.addEventListener("click", function () {
		doLogout();
		dataUrl.append("lang", document.querySelector("select").value);
		window.location.href = "index.html?" + dataUrl.toString();
	});

document
	.querySelector("#button-nav-bar-back")
	.addEventListener("click", function () {
		dataUrl.append("token", token);
		dataUrl.append("username", usernameLoggedUser);
		dataUrl.append("lang", document.querySelector("select").value);
		window.location.href = "newsList.html?" + dataUrl.toString();
	});

// DASHBOARD///////////////////////////////////////////////////////////

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

////////////////////////////////////////////////////////////////////////////
//Buscar token e tipo de user logado na base de dados
////////////////////////////////////////////////////////////////////////////
async function getTypeUser(username) {
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
			console.log("estou no fetch " + data);
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

////////////////////////////////////////////////////////////////////////////
//Buscar projetos que possuem determinada keyword
////////////////////////////////////////////////////////////////////////////

let buttonSearch = document.querySelector("#button-search");
let notOffProjectsByKeyword = [];
buttonSearch.addEventListener("click", getTypeUserAuxiliary);

async function getProjectsByKeyword(isAdmin) {
	console.log("cliquei no search e entrei em getNewsByKeyword   " + isAdmin);

	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};

	let keywordToFound = document.getElementById("input-search").value;
	console.log(keywordToFound);
	let path =
		defaulPath + "project/getNotOffProjectsByKeyword/" + keywordToFound;

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
			notOffProjectsByKeyword = data;
			console.log("XXXXXXXXXXXXXXXXXX " + notOffProjectsByKeyword);
			updateNews(notOffProjectsByKeyword, isAdmin);
		});
}

/////////////////////////////////////////////////
//Recarregar somente news com uma certa keyword
/////////////////////////////////////////////////
function updateNews(notOffProjectsByKeyword, isAdmin) {
	let newsList = document.querySelector("#section-projectsNotOff");
	for (let i = 0; newsList.children.length > 0; i++) {
		newsList.removeChild(newsList.children[0]);
	}
	loadNotOffProjects(notOffProjectsByKeyword, isAdmin);
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
			getProjectsByKeyword(data);
		});
}
