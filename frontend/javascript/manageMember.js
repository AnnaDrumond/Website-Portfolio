import doLanguageRefresh from "./languageSupport.js";
let defaulPath = "http://localhost:8080/backend-projeto5-adrumond/rest/";
let dataUrl = new URLSearchParams();
let visitors = [];
let membersAndAdmin = [];
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
	getAllVisitors();
	getAllTeam();
	getTypeUser(usernameLoggedUser);
};

function returnTypeAndTokenUser(typeLoggedUser) {
	if (typeLoggedUser == "VISITOR" || typeLoggedUser == null) {
		//A cada troca de página verifica se o user ainda é um membro ou admin
		loadAuthorizatioError();
		dataUrl.append("lang", document.querySelector("select").value);
		setTimeout(function () {
			doLogout(); // se não for mais parte da equipa depois de 2 segundos, força o logout
		}, 2000);
	}
}

////////////////////////////////////////////////////////////////////////////
//Buscar lista de visitantes
////////////////////////////////////////////////////////////////////////////
function getAllVisitors() {
	console.log("getAllVisitors");

	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};
	let path = defaulPath + "user/listUsersVisitors";

	fetch(path, fetchOptions)
		.then((response) => {
			if (response.status === 200) {
				return response.json();
			} else if (response.status === 401) {
				//mostrar erro
			} else {
				//mostrar erro
			}
		})
		.then((data) => {
			visitors = data;
			console.log(visitors);
			loadVisitors(visitors);
		});
}

///////////////////////////////////////////////////////////////////////
//Carregar a lista dos membros da organização no ecrã
///////////////////////////////////////////////////////////////////////
function loadVisitors(visitors) {
	console.log("entrei em loadMembersAndAdmins");

	for (let i = 0; i < visitors.length; i++) {
		let PageMembersAndAdmins = document.querySelector("#section-visitors");

		let usersArticle = document.createElement("article"); //li

		let divContainerUsers = document.createElement("div");
		divContainerUsers.className = "users-box";

		usersArticle.appendChild(divContainerUsers);

		let divTextsUsers = document.createElement("div");
		divTextsUsers.className = "users-box-texts";

		let photo = document.createElement("img"); // imagem
		photo.className = "user-image";
		photo.src = visitors[i].photo_userDto;
		photo.setAttribute("height", "100");

		divContainerUsers.appendChild(photo);
		divContainerUsers.appendChild(divTextsUsers);

		let name = document.createElement("h4"); //titulo
		name.className = "user-name";
		name.innerText = visitors[i].first_nameDto + " " + visitors[i].last_nameDto;
		divTextsUsers.appendChild(name);

		let type = document.createElement("h5"); //data ultima atualização
		type.className = "user-type";
		type.innerText = visitors[i].typeUserDto;
		type.setAttribute("data-i18n", "visitor");
		divTextsUsers.appendChild(type);

		let buttonApprove = document.createElement("button");
		buttonApprove.className = "visitor-button";
		buttonApprove.innerText = "Aprovar";
		buttonApprove.setAttribute("data-i18n", "aprove");
		divTextsUsers.appendChild(buttonApprove);
		buttonApprove.addEventListener("click", function (e) {
			approveRegistration(visitors[i].usernameDto);
		});

		let buttonDelete = document.createElement("button");
		buttonDelete.className = "visitor-button";
		buttonDelete.innerText = "Rejeitar";
		buttonDelete.setAttribute("data-i18n", "reject");
		divTextsUsers.appendChild(buttonDelete);
		buttonDelete.addEventListener("click", function (e) {
			rejectRequest(visitors[i].usernameDto);
		});

		PageMembersAndAdmins.appendChild(usersArticle);
	}
	///////////////////////////////////////////////////////////////////////////////////////////
	//Este método está em currentLanguageSupport, que por sua vez foi exportada para este ficheiro JS
	///////////////////////////////////////////////////////////////////////////////////////////
	doLanguageRefresh(document.querySelector("#languages").value);
}

////////////////////////////////////////////////////////////////////////////
//Buscar Equipa da organização
////////////////////////////////////////////////////////////////////////////
function getAllTeam() {
	console.log("getAllTeam");

	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};
	let path = defaulPath + "user/listTeam";

	fetch(path, fetchOptions)
		.then((response) => {
			if (response.status === 200) {
				return response.json();
			} else if (response.status === 401) {
				//mostrar erro
			} else {
				//mostrar erro
			}
		})
		.then((data) => {
			membersAndAdmin = data;
			console.log(membersAndAdmin);
			loadTeam(membersAndAdmin);
		});
}

///////////////////////////////////////////////////////////////////////
//Carregar a lista dos membros da organização no ecrã
///////////////////////////////////////////////////////////////////////
function loadTeam(membersAndAdmin) {
	console.log("entrei em loadMembersAndAdmins");

	for (let i = 0; i < membersAndAdmin.length; i++) {
		let PageMembersAndAdmins = document.querySelector("#section-members");

		let usersArticle = document.createElement("article"); //li

		let divContainerUsers = document.createElement("div");
		divContainerUsers.className = "users-box";

		usersArticle.appendChild(divContainerUsers);

		let divTextsUsers = document.createElement("div");
		divTextsUsers.className = "users-box-texts";

		let photo = document.createElement("img"); // imagem
		photo.className = "team-image";
		photo.src = membersAndAdmin[i].photo_userDto;
		photo.setAttribute("height", "100");

		divContainerUsers.appendChild(photo);
		divContainerUsers.appendChild(divTextsUsers);

		let name = document.createElement("h4"); //titulo
		name.className = "user-name";
		name.innerText =
			membersAndAdmin[i].first_nameDto + " " + membersAndAdmin[i].last_nameDto;
		divTextsUsers.appendChild(name);

		let type = document.createElement("h5"); //data ultima atualização
		type.className = "user-type";
		type.innerText = membersAndAdmin[i].typeUserDto;
		let typeAux = membersAndAdmin[i].typeUserDto;

		divTextsUsers.appendChild(type);

		console.log(membersAndAdmin[i].usernameDto);
		console.log(usernameLoggedUser);

		if (membersAndAdmin[i].usernameDto !== usernameLoggedUser) {
			let button = document.createElement("button");
			button.className = "visitor-button";
			button.innerText = "Tornar visitante";
			button.setAttribute("data-i18n", "toVisitor");
			divTextsUsers.appendChild(button);
			button.addEventListener("click", function (e) {
				downgradeToVisitor(membersAndAdmin[i].usernameDto);
			});

			if (typeAux == "ADMINISTRATOR") {
				//////////////usernameDto///////////////////////////////////////////////////////////////////////
				type.setAttribute("data-i18n", "admin");
				let button = document.createElement("button");
				button.className = "visitor-button";
				button.innerText = "Tornar Membro";
				button.setAttribute("data-i18n", "toMember");
				divTextsUsers.appendChild(button);
				button.addEventListener("click", function (e) {
					downgradeToMember(membersAndAdmin[i].usernameDto);
				});
			} else {
				type.setAttribute("data-i18n", "member");
				let button = document.createElement("button");
				button.className = "visitor-button";
				button.innerText = "Tornar Admin";
				button.setAttribute("data-i18n", "toAdmin");
				divTextsUsers.appendChild(button);
				button.addEventListener("click", function (e) {
					upgradeToAdmin(membersAndAdmin[i].usernameDto);
				});
			}
		} else {
			//visitor-button
			type.setAttribute("data-i18n", "admin");
			let button = document.createElement("button");
			button.className = "admin-button";
			button.innerText = "Admin logado";
			button.setAttribute("data-i18n", "loggedAdmin");
			divTextsUsers.appendChild(button);
		}

		PageMembersAndAdmins.appendChild(usersArticle);
	}
	///////////////////////////////////////////////////////////////////////////////////////////
	//Este método está em currentLanguageSupport, que por sua vez foi exportada para este ficheiro JS
	///////////////////////////////////////////////////////////////////////////////////////////
	doLanguageRefresh(document.querySelector("#languages").value);
}

//////////////////////////////////////////////////////////
// Aprovar pedido de registo
//////////////////////////////////////////////////////////
function approveRegistration(username) {
	const fetchOpcoes = {
		method: "POST",
		headers: { "Content-Type": "application/json", token },
	};
	//falta o username a ser add/excluido
	let path = defaulPath + "user/approveRegistration/" + username;

	console.log(token);

	fetch(path, fetchOpcoes).then((response) => {
		console.log("Response Status: " + response.status);
		if (response.status == 200) {
			document.location.reload(true);
		} else {
			//colocar para exibir mensagem de que deu erro ao adicionar excluir
			console.log("deu exceção no approveRegistration");
			//tratar erro
		}
	});
}

//////////////////////////////////////////////////////////
// Rejeitar pedido de registo
//////////////////////////////////////////////////////////
function rejectRequest(username) {
	const fetchOpcoes = {
		method: "POST",
		headers: { "Content-Type": "application/json", token },
	};
	let path = defaulPath + "user/reject/" + username;

	console.log(token);

	fetch(path, fetchOpcoes).then((response) => {
		console.log("Response Status: " + response.status);
		if (response.status == 200) {
			document.location.reload(true);
		} else {
			//colocar para exibir mensagem de que deu erro ao adicionar excluir
			console.log("deu exceção no approveRegistration");
			//tratar erro
		}
	});
}

//////////////////////////////////////////////////////////
// Downgrade para visitante
//////////////////////////////////////////////////////////
function downgradeToVisitor(username) {
	const fetchOpcoes = {
		method: "POST",
		headers: { "Content-Type": "application/json", token },
	};
	//falta o username a ser add/excluido
	let path = defaulPath + "user/downgradeToVisitor/" + username;

	fetch(path, fetchOpcoes).then((response) => {
		console.log("Response Status: " + response.status);
		if (response.status == 200) {
			document.location.reload(true);
		} else {
			//colocar para exibir mensagem de que deu erro ao adicionar excluir
			console.log("deu exceção no downgradeToVisitor");
			//tratar erro
		}
	});
}

//////////////////////////////////////////////////////////
// Downgrade de admin para membro
//////////////////////////////////////////////////////////
function downgradeToMember(username) {
	const fetchOpcoes = {
		method: "POST",
		headers: { "Content-Type": "application/json", token },
	};
	//falta o username a ser add/excluido
	let path = defaulPath + "user/downgradeAdmin/" + username;

	fetch(path, fetchOpcoes).then((response) => {
		console.log("Response Status: " + response.status);
		if (response.status == 200) {
			document.location.reload(true);
		} else {
			//colocar para exibir mensagem de que deu erro ao adicionar excluir
			console.log("deu exceção no downgradeToVisitor");
			//tratar erro
		}
	});
}

//////////////////////////////////////////////////////////
// Upgrade de memebro para admin
//////////////////////////////////////////////////////////
function upgradeToAdmin(username) {
	const fetchOpcoes = {
		method: "POST",
		headers: { "Content-Type": "application/json", token },
	};
	//falta o username a ser add/excluido
	let path = defaulPath + "user/promoveToAdmin/" + username;

	fetch(path, fetchOpcoes).then((response) => {
		console.log("Response Status: " + response.status);
		if (response.status == 200) {
			document.location.reload(true);
		} else {
			//colocar para exibir mensagem de que deu erro ao adicionar excluir
			console.log("deu exceção no downgradeToVisitor");
			//tratar erro
		}
	});
}

//////////////////////////////////////////////////////////////////////////////////
// Ações dos botões do do Menu.
// Irá construir a URL com o idioma atual para o mesmo ser resgatado em
// languageSupport.js na linha 18 e outros dados conforme necessidade.
//////////////////////////////////////////////////////////////////////////////////
//VOLTAR///////////////////////////////////////////////////
document
	.querySelector("#button-nav-bar-back")
	.addEventListener("click", function () {
		console.log("CLIQUEI NO BOTÃO VOLTAR");
		dataUrl.append("token", token);
		dataUrl.append("username", usernameLoggedUser);
		dataUrl.append("lang", document.querySelector("select").value);
		window.location.href = "newsList.html?" + dataUrl.toString();
	});

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
