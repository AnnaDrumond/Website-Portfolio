import doLanguageRefresh from "./languageSupport.js";
let defaulPath = "http://localhost:8080/backend-projeto5-adrumond/rest/";
let newsFound;
let projectsAssociated = [];
let associatedMembers = [];
let keywordsList = [];
let usernames = [];
let pageLanguage;
let typeVisitor;
let dataUrl = new URLSearchParams();

/////////////////////////////////////////////////////////////////
//Resgatar os dados passados como parametro pelo link por queryString
/////////////////////////////////////////////////////////////////
let param = new URLSearchParams(window.location.search);
let idNews = param.get("idNews");
let lang = param.get("lang");
let token = param.get("token");
let usernameLoggedUser = param.get("username");
lang === null ? (pageLanguage = "PT") : (pageLanguage = lang);
document.querySelector("select").value = pageLanguage; // settar o idioma na comboBox

window.onload = function loadPage() {
	//console.log("window.onload");
	if (usernameLoggedUser == null) {
		returnTypeAndTokenUser(null);
	} else {
		getTypeUser(usernameLoggedUser);
	}
};

////////////////////////////////////////////////////////////////////////////
//Distribui oa dados de tipo/token do user para os respetivos métodos
//Controla a construção do Menu de acordo com o tipo de user
////////////////////////////////////////////////////////////////////////////
async function returnTypeAndTokenUser(typeLoggedUser) {
	//console.log("estou em returnTypeAndTokenUser  " + typeLoggedUser);
	//console.log(typeof typeLoggedUser); // STRING
	// se tipo de user for null colovar visitor
	typeLoggedUser === null
		? (typeVisitor = "VISITOR")
		: (typeVisitor = typeLoggedUser);
	///////////////////////////////////////////////////////////////////////////////////////////////
	// se tipo de user na BD não for um admin, esconde as opções dashboard e gerir membros do menu
	//permite em caso do membro ser promovido ou admin ser despromovido controlar suas ações
	//////////////////////////////////////////////////////////////////////////////////////////////
	if (typeVisitor == "VISITOR" || typeVisitor == null) {
		let backMenu = document.querySelector("#back-menu");
		//nav-bar-project
		//classList me permite colocar nova classe, sem juntar o nome com as que já tem
		// fica nome nome nome e não nomenomenome
		backMenu.classList.add("occult");
	} else if (typeVisitor != "VISITOR") {
		// Se não é visitante exibe o botão VOLTAR
		let homeMenu = document.querySelector("#home-menu");
		homeMenu.classList.add("occult");
		getUsernames();
	}
	await getNewsById(typeVisitor);
	await getAssociatedProjects(typeVisitor);
	await getAssociatedNewsMembers(typeVisitor);
}

//////////////////////////////////////////////////////////////////////////////////
// Ação dos botões do Menu.
// Irá construir a URL com o idioma atual para o mesmo ser resgatado em
// languageSupport.js na linha 18.
//////////////////////////////////////////////////////////////////////////////////
document
	.querySelector("#button-nav-bar-home")
	.addEventListener("click", function () {
		dataUrl.append("lang", document.querySelector("select").value);
		window.location.href = "index.html?" + dataUrl.toString();
	});

document
	.querySelector("#button-nav-bar-back")
	.addEventListener("click", function () {
		let token = param.get("token");
		dataUrl.append("token", token);
		dataUrl.append("username", usernameLoggedUser);
		dataUrl.append("lang", document.querySelector("select").value);
		window.location.href = "newsList.html?" + dataUrl.toString();
	});

/////////////////////////////////////////////////////////////////
//Buscar a noticia do id indicado na base de dados
/////////////////////////////////////////////////////////////////
async function getNewsById() {
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let path = defaulPath + "news/newsVisibleById/" + idNews;

	await fetch(path, fetchOptions)
		.then((response) => response.json())
		.then((response) => {
			console.log("getNewsById");
			console.log(response);
			newsFound = response;
			newsDetails(newsFound);
			loadKeywords(newsFound.keywords_news);
		})

		.catch((erro) => {
			if (newsFound !== null || newsFound !== "") {
				//retornarErroGeral();
				console.log("deu exceção");
			}
			console.log("deu exceção");
		});
}

/////////////////////////////////////////////////////////////////
//Buscar os projetos associados a esta noticia
/////////////////////////////////////////////////////////////////
async function getAssociatedProjects() {
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let path = defaulPath + "news/projectsAssociated/" + idNews;

	await fetch(path, fetchOptions)
		.then((response) => response.json())
		.then((response) => {
			console.log("getAssociatedProjects");
			console.log(response);
			projectsAssociated = response;
			loadAssociatedProjects(projectsAssociated);
		})

		.catch((erro) => {
			if (projectsAssociated !== null || projectsAssociated !== "") {
				//retornarErroGeral();
				console.log("deu exceção");
			}
			console.log("deu exceção");
		});
}

/////////////////////////////////////////////////////////////////
//Gerar html detalhes da noticia
/////////////////////////////////////////////////////////////////
function newsDetails(newsFound) {
	if (newsFound !== null || newsFound !== "") {
		let pageNewsDetails = document.querySelector("#section-news-details");

		let divPrincipal = document.createElement("div");
		divPrincipal.className = "details-box";

		let image = document.createElement("img");
		image.className = "image-details";
		image.setAttribute("height", "300");
		image.setAttribute("width", "300");
		image.src = newsFound.cover_imageDto;

		divPrincipal.appendChild(image);

		let title = document.createElement("h2");
		title.className = "title-details";
		title.innerText = newsFound.title_newsDto;
		divPrincipal.appendChild(title);

		let detailsNews = document.createElement("h4");
		detailsNews.className = "details";
		detailsNews.innerText = "Detalhes da Notícia";
		detailsNews.setAttribute("data-i18n", "detailsNews");
		
		divPrincipal.appendChild(detailsNews);

		if (newsFound.statusNewsDto == "OFF") {
			let status = document.createElement("h4");
			status.className = "off";
			status.innerText = "APAGADO";
			status.setAttribute("data-i18n", "off");
			divPrincipal.appendChild(status);
		}

		let content = document.createElement("p");
		content.className = "content-details";
		content.innerText = newsFound.content_newsDto;
		divPrincipal.appendChild(content);
		pageNewsDetails.appendChild(divPrincipal);

		let ppLastUpdate = document.querySelector("#lastUpdateP");
		ppLastUpdate.innerText = newsFound.lastUpdateDto;
		ppLastUpdate.setAttribute("data-i18n-date", newsFound.lastUpdateDto);

		let pCreatorName = document.querySelector("#creatorName");
		pCreatorName.innerText = newsFound.userNewscreatorDto;
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	//Este método está em pageLanguageSupport, que por sua vez foi exportada para este ficheiro JS
	///////////////////////////////////////////////////////////////////////////////////////////
	doLanguageRefresh(document.querySelector("#languages").value);
}

/////////////////////////////////////////////////////////////////
//Buscar membros envolvidos na noticia
/////////////////////////////////////////////////////////////////
async function getAssociatedNewsMembers(typeLoggedUser) {
	//console.log("get membros associados *******************************************")
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let path = defaulPath + "news/newslistMembers/" + idNews;

	await fetch(path, fetchOptions)
		.then((response) => response.json())
		.then((response) => {
			console.log(response);
			associatedMembers = response;
			loadNewsTeam(associatedMembers, typeLoggedUser);
		})

		.catch((erro) => {
			/*if (associatedMembers !== null || associatedMembers !== "") {
				//retornarErroGeral();
				console.log("deu exceção no if");
			}*/
			console.log("deu exceção fora do if");
		});
}

/////////////////////////////////////////////////////////////////
//Gerar html equipa envolvida
/////////////////////////////////////////////////////////////////
function loadNewsTeam(associatedMembers, typeLoggedUser) {
	//console.log("loda news team")
	let teamNews = document.querySelector(".cardY");

	if (associatedMembers !== null || associatedMembers !== "") {
		associatedMembers.forEach((element) => {
			let member = document.createElement("p");
			member.className = "news-team";
			member.innerText = element.first_nameDto + " " + element.last_nameDto;
			teamNews.appendChild(member);

			/*if (typeLoggedUser == "VISITOR") {
			} else {
				let div = document.createElement("div");
				div.className = "div-members";

				let buttonDelete = document.createElement("button");
				buttonDelete.className = "member-buttonDelete";
				buttonDelete.innerText = "Desassociar";
				buttonDelete.setAttribute("data-i18n", "deleteMember");
				buttonDelete.addEventListener("click", function (e) {
					doManageMember(element.usernameDto);
				});
				div.appendChild(buttonDelete);
				member.appendChild(div);
			}*/
		});
	}
}

/////////////////////////////////////////////////////////////////
//Carregar projetos associados a notícia
/////////////////////////////////////////////////////////////////
function loadAssociatedProjects(projectsAssociated) {
	console.log("entrei em loadNews" + projectsAssociated);

	if (projectsAssociated !== null || projectsAssociated !== "") {
		projectsAssociated.forEach((element) => {
			let pageProjects = document.querySelector("#section-projects-news");

			//let divIcon = document.createElement("div");
			//pageProjects.appendChild(divIcon);

			//let divTitle = document.createElement("div");
			//divTitle.className = "titleAssociated-project-box";

			let title = document.createElement("p"); //titulo
			title.className = "project-associated-title";
			title.innerText = element.title_projectDto;

			title.addEventListener("click", function (e) {
				let dataUrl = new URLSearchParams();
				dataUrl.append("idProject", element.id);
				dataUrl.append("token", token);
				dataUrl.append("username", usernameLoggedUser);
				dataUrl.append("lang", document.querySelector("select").value);
				window.location.href = "projectPage.html?" + dataUrl.toString();
			});

			pageProjects.appendChild(title);
		});
	}
}

/////////////////////////////////////////////////////////////////
//Listar as keywords da noticia
/////////////////////////////////////////////////////////////////
function loadKeywords(keywordNews) {
	keywordsList = keywordNews.split(";");
	let sectionKeywords = document.querySelector("#section-keywords-news");
	//console.log("loadKeywords");

	keywordsList.forEach((element) => {
		console.log(element);
		let icon = document.createElement("i"); //icone
		icon.className = "fa-solid fa-key";
		let keywordSpan = document.createElement("span");
		keywordSpan.className = "keyword-span";
		keywordSpan.innerText = element;
		keywordSpan.appendChild(icon);
		sectionKeywords.appendChild(keywordSpan);
	});
}

//////////////////////////////////////////////////////////
// associar/desassociar membros
//////////////////////////////////////////////////////////
function doManageMember(username) {
	const headers = new Headers();
	headers.append("token", token);

	console.log("doManageMember " + idNews + username);

	const fetchOpcoes = {
		method: "POST",
		"Content-Type": "application/json",
		headers: headers,
	};
	//falta o username a ser add/excluido
	let path = defaulPath + "news/manageMembersNews/" + idNews + "/" + username;

	fetch(path, fetchOpcoes).then((response) => {
		console.log("Response Status: " + response.status);
		if (response.status == 200) {
			document.location.reload(true);
		} else {
			//colocar para exibir mensagem de que deu erro ao adicionar excluir
			console.log("deu exceção no manage member");
			//tratar erro
		}
	});
}

/////////////////////////////////////////////////////////////////
// carregar usernames
/////////////////////////////////////////////////////////////////
function loadUsernames(usernames) {
	let section = document.querySelector("#section-add-member-news");
	let comboBox = document.createElement("select");
	comboBox.className = "comboBoxUsername";
	//console.log("XXXXXXXXXXXXXXXXXX " + usernames);
	if (usernames !== null || usernames !== "") {
		usernames.forEach((element) => {
			//Impedir que o user possa se associar a um conteúdo que ele próprio está a criar
			if (element != usernameLoggedUser) {
				let username = document.createElement("option");
				username.className = "username-team";
				username.textContent = element;
				comboBox.appendChild(username);
			}
		});
	}
	section.appendChild(comboBox);
	let buttonAddMember = document.createElement("button");

	buttonAddMember.className = "button-addMember";
	buttonAddMember.innerText = "GERIR MEMBROS";
	buttonAddMember.setAttribute("data-i18n", "manageMembers");
	buttonAddMember.addEventListener("click", function (e) {
		let textInput = document
			.querySelector(".comboBoxUsername")
			.value.toLowerCase();
		doManageMember(textInput);
	});
	section.appendChild(buttonAddMember);
}

/////////////////////////////////////////////////////////////////
//Buscar lista de usernames
/////////////////////////////////////////////////////////////////
async function getUsernames() {
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};

	let path = defaulPath + "user/usernames";

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
			usernames = data;
			console.log("XXXXXXXXXXXXXXXXXX " + usernames);

			loadUsernames(usernames);
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
//Buscar token e tipo de user logado na base de dados
////////////////////////////////////////////////////////////////////////////
async function verifyTypeUser(usernameLoggedUser) {
	console.log("getTypeUser");
	const headers = new Headers();
	headers.append("username", usernameLoggedUser);

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
			let typeUser = data;
			if (typeUser == null) {
				dataUrl.append("lang", document.querySelector("select").value);
				window.location.href = "publicProjects.html?" + dataUrl.toString();
			} else if (typeUser != "VISITOR") {
				let token = param.get("token");
				dataUrl.append("token", token);
				dataUrl.append("username", usernameLoggedUser);
				dataUrl.append("lang", document.querySelector("select").value);
				window.location.href = "projectsList.html?" + dataUrl.toString();
			}
		});
}
