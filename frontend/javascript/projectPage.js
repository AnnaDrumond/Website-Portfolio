import doLanguageRefresh from "./languageSupport.js";
let defaulPath = "http://localhost:8080/backend-projeto5-adrumond/rest/";
let projectFound;
let newsAssociated = [];
let associatedMembers = [];
let keywordsList = [];
let usernames = [];
let pageLanguage;
let typeVisitor;
let dataUrl = new URLSearchParams();
/////////////////////////////////////////////////////////////////
//Resgatar o id passado como parametro pelo link por queryString
/////////////////////////////////////////////////////////////////
let param = new URLSearchParams(window.location.search);
let idProject = param.get("idProject");
let lang = param.get("lang");
let token = param.get("token");
let usernameLoggedUser = param.get("username");
lang === null ? (pageLanguage = "PT") : (pageLanguage = lang);
document.querySelector("select").value = pageLanguage; // settar o idioma na comboBox

window.onload = function loadPage() {
	console.log("window.onload");
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
	console.log("estou em returnTypeAndTokenUser  " + typeLoggedUser);
	console.log(typeof typeLoggedUser); // STRING
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
	getProjectById(typeVisitor);
	getAssociatedNews(typeVisitor);
	getAssociatedProjectMembers(typeVisitor);
}

/////////////////////////////////////////////////////////////////
//Buscar o projeto do id indicado na base de dados
/////////////////////////////////////////////////////////////////
async function getProjectById() {
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let path = defaulPath + "project/projectVisibleById/" + idProject;

	await fetch(path, fetchOptions)
		.then((response) => response.json())
		.then((response) => {
			console.log("getProjectById");
			console.log(response);
			projectFound = response;
			projectDetails(projectFound);
			loadKeywords(projectFound.keywords_project);
		})

		.catch((erro) => {
			/*if (projectFound !== null || projectFound !== "") {
				//retornarErroGeral();
				console.log("deu exceção -----");
			}*/
			console.log("deu exceção - getProjectById");
		});
}

/////////////////////////////////////////////////////////////////
//Buscar as notícias associadas a este projeto
/////////////////////////////////////////////////////////////////
async function getAssociatedNews() {
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let path = defaulPath + "project/newsAssociated/" + idProject;

	await fetch(path, fetchOptions)
		.then((response) => response.json())
		.then((response) => {
			console.log("getAssociatedNews");
			console.log(response);
			newsAssociated = response;
			loadAssociatedNews(newsAssociated, typeLoggedUser);
		})

		.catch((erro) => {
			/*if (newsAssociated !== null || newsAssociated !== "") {
				//retornarErroGeral();
				console.log("deu exceção ***");
			}*/
			console.log("deu exceção - getAssociatedNews");
		});
}

/////////////////////////////////////////////////////////////////
//Gerar html detalhes do projeto
/////////////////////////////////////////////////////////////////
function projectDetails(projectFound) {
	if (projectFound !== null || projectFound !== "") {
		let pageProjectDetails = document.querySelector("#section-project-details");

		let divPrincipal = document.createElement("div");
		divPrincipal.className = "details-box";

		let image = document.createElement("img");
		image.className = "image-details";
		image.setAttribute("height", "300");
		image.setAttribute("width", "300");
		image.src = projectFound.cover_imageDto;

		divPrincipal.appendChild(image);

		let title = document.createElement("h2");
		title.className = "title-details";
		title.innerText = projectFound.title_projectDto;
		divPrincipal.appendChild(title);

		let details = document.createElement("h4");
		details.className = "details";
		details.innerText = "Detalhes do Projeto";
		details.setAttribute("data-i18n", "details");
		divPrincipal.appendChild(details);

		if (projectFound.statusProjectDto == "OFF") {
			let status = document.createElement("h4");
			status.className = "off";
			status.innerText = "APAGADO";
			status.setAttribute("data-i18n", "off");
			divPrincipal.appendChild(status);
		}

		let content = document.createElement("p");
		content.className = "content-details";
		content.innerText = projectFound.content_projectDto;
		divPrincipal.appendChild(content);
		pageProjectDetails.appendChild(divPrincipal);

		let lastUpdate = document.querySelector("#lastUpdateP2");
		lastUpdate.innerText = projectFound.lastUpdateDto;
		lastUpdate.setAttribute("data-i18n-date", projectFound.lastUpdateDto);

		let creatorName = document.querySelector("#creatorNameP2");
		creatorName.innerText = projectFound.userProjectcreatorDto;
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	//Este método está em pageLanguageSupport, que por sua vez foi exportada para este ficheiro JS
	///////////////////////////////////////////////////////////////////////////////////////////
	doLanguageRefresh(document.querySelector("#languages").value);
}

/////////////////////////////////////////////////////////////////
//Buscar membros envolvidos no projeto
/////////////////////////////////////////////////////////////////
async function getAssociatedProjectMembers(typeLoggedUser) {
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let path = defaulPath + "project/getProjectMembers/" + idProject;

	await fetch(path, fetchOptions)
		.then((response) => response.json())
		.then((response) => {
			console.log("getAssociatedNewsMembers");
			console.log(response);
			associatedMembers = response;
			loadProjectTeam(associatedMembers, typeLoggedUser);
		})

		.catch((erro) => {
			if (associatedMembers !== null || associatedMembers !== "") {
				//retornarErroGeral();
				console.log("deu exceção");
			}
			console.log("deu exceção ao buscar os membros");
		});
}

/////////////////////////////////////////////////////////////////
//Gerar html equipa envolvida
/////////////////////////////////////////////////////////////////
function loadProjectTeam(associatedMembers, typeLoggedUser) {
	let teamNews = document.querySelector(".cardY");

	if (associatedMembers !== null || associatedMembers !== "") {
		associatedMembers.forEach((element) => {
			console.log("no for each dos membros + " + element.first_nameDto);
			console.log(typeof element);
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
	///////////////////////////////////////////////////////////////////////////////////////////
	//Este método está em currentLanguageSupport, que por sua vez foi exportada para este ficheiro JS
	///////////////////////////////////////////////////////////////////////////////////////////
	doLanguageRefresh(document.querySelector("#languages").value);
}

/////////////////////////////////////////////////////////////////
//Carregar noticias associadas a este projeto
/////////////////////////////////////////////////////////////////
function loadAssociatedNews(newsAssociated, typeLoggedUser) {
	console.log("entrei em loadAssociatedNews" + newsAssociated);

	if (newsAssociated !== null || newsAssociated !== "") {
		newsAssociated.forEach((element) => {
			let pageProjects = document.querySelector("#section-news-project");
			//let divTitle = document.createElement("div");
			//divTitle.className = "titleAssociated-project-box";

			let title = document.createElement("p"); //titulo
			title.className = "project-associated-title";
			title.innerText = element.title_newsDto;

			title.addEventListener("click", function (e) {
				dataUrl.append("idNews", element.id);
				dataUrl.append("token", token);
				dataUrl.append("username", usernameLoggedUser);
				dataUrl.append("lang", document.querySelector("select").value);
				window.location.href = "newsPage.html?" + dataUrl.toString();
			});
			pageProjects.appendChild(title);
			//pageProjects.appendChild(divTitle);

			console.log("loadAssociatedNews " + usernameLoggedUser);
			console.log(element.usernameNewsCreator);

			/*if (typeUser == null) {
			} else if (
				usernameLoggedUser == element.usernameNewsCreator ||
				typeUser == "ADMINISTRATOR"
			) {
				let buttonDelete = document.createElement("button");
				buttonDelete.className = "content-buttonDelete";
				buttonDelete.innerText = "Desassociar conteúdo";
				//buttonDelete.setAttribute("data-i18n", "deleteContent");
				buttonDelete.addEventListener("click", function (e) {
					dissaciatedContent(element.id);
				});
				pageProjects.appendChild(buttonDelete);
			}*/
		});
	}
}

/////////////////////////////////////////////////////////////////
// carregar usernames
/////////////////////////////////////////////////////////////////
function loadUsernames(usernames) {
	let section = document.querySelector("#section-add-member");
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
	buttonAddMember.innerText = "Gerir membros";
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
//Listar as keywords da noticia
/////////////////////////////////////////////////////////////////
function loadKeywords(keywords_project) {
	// console.log("loadKeywords---------------------------------------------");
	keywordsList = keywords_project.split(";");
	let sectionKeywords = document.querySelector("#section-keywords-project");

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

	const fetchOpcoes = {
		method: "POST",
		"Content-Type": "application/json",
		headers: headers,
	};
	//falta o username a ser add/excluido
	let path =
		defaulPath + "project/manageMembersProject/" + idProject + "/" + username;

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

//////////////////////////////////////////////////////////
// Desassociar conteúedo
//////////////////////////////////////////////////////////
function dissaciatedContent(idNoticia) {
	const headers = new Headers();
	headers.append("token", token);

	const fetchOpcoes = {
		method: "POST",
		"Content-Type": "application/json",
		headers: headers,
	};
	//falta o username a ser add/excluido
	let path =
		defaulPath +
		"project/disassociateProject/" +
		idProject +
		"/toNews/" +
		idNoticia;

	console.log(path);

	fetch(path, fetchOpcoes).then((response) => {
		console.log("Response Status: " + response.status);
		if (response.status == 200) {
			document.location.reload(true);
		} else {
			//colocar para exibir mensagem de que deu erro ao adicionar excluir
			console.log("deu exceção no dissaciatedContent");
			//tratar erro
		}
	});
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

//////////////////////////////////////////////////////////////////////////////////
// Ação do botão INICIO e PROJETOS do Menu. É chamado quando o INICIO é clicado no Menu.
// Irá construir a URL com o idioma atual para o mesmo ser resgatado em
// languageSupport.js na linha 18.
//////////////////////////////////////////////////////////////////////////////////
document
	.querySelector("#button-nav-bar-home")
	.addEventListener("click", function () {
		dataUrl.append("lang", document.querySelector("select").value);
		window.location.href = "index.html?" + dataUrl.toString();
	});

//O mesmo botão do Menu leva sítios diversos, conforme o tipo de user
document
	.querySelector("#nav-bar-project")
	.addEventListener("click", function (e) {
		if (usernameLoggedUser != null) {
			verifyTypeUser(usernameLoggedUser);
		} else {
			dataUrl.append("lang", document.querySelector("select").value);
			window.location.href = "publicProjects.html?" + dataUrl.toString();
		}
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
