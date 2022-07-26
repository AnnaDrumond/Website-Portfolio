import doLanguageRefresh from "./languageSupport.js";
let defaulPath = "http://localhost:8080/backend-projeto5-adrumond/rest/";
let pageLanguage;
let usernames = [];
let auxilarKeyWord = ".";
let auxilarMember = ".";
let isSameKey = false;
let isSameMember = false;
let dataUrl = new URLSearchParams();
/////////////////////////////////////////////////////////////////
//Resgatar o id passado como parametro pelo link por queryString
/////////////////////////////////////////////////////////////////
let param = new URLSearchParams(window.location.search);
let lang = param.get("lang");
let token = param.get("token");
let usernameLoggedUser = param.get("username");
lang === null ? (pageLanguage = "PT") : (pageLanguage = lang);
document.querySelector("select").value = pageLanguage; // settar o idioma na comboBox

window.onload = function loadPage() {
	getUsernames();
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

//Botão add keyword/////////////////////////////////////////////////////////////////////////
document.querySelector("#add-keyword").addEventListener("click", function () {
	let text = document.getElementById("keyword-form").value.toLowerCase() + ";";
	let section = document.querySelector("#keyword-create");
	let keyList = document.querySelectorAll(".keyword-create");

	////// verificar se já tem esta keyword///////////////////////
	if (keyList.length > 0) {
		for (let i = 0; i < keyList.length; i++) {
			if (text === keyList[i].innerText) {
				isSameKey = true;
			}
		}
	}
	if (isSameKey === false && text != "") {
		let keywordSpan = document.createElement("span");
		keywordSpan.className = "keyword-create";
		keywordSpan.innerText = text;
		section.appendChild(keywordSpan);
		//// ver como tirar o último ; antes de enviar ao back end
	} else {
		errorSameKey();
	}
	document.querySelector("#keyword-form").value = "";
});

//Botão add Membro/////////////////////////////////////////////////////////////////////////
document
	.querySelector("#create-add-member")
	.addEventListener("click", function () {
		let text =
			document.querySelector("#comboBoxUsername").value.toLowerCase() + ";";
		console.log(text);
		let section = document.querySelector("#member-create");
		let memberList = document.querySelectorAll(".member-create");
		////// verificar se já tem este membro///////////////////////
		if (memberList.length > 0) {
			for (let i = 0; i < memberList.length; i++) {
				if (text === memberList[i].innerText) {
					isSameMember = true;
				}
			}
		}
		if (isSameMember === false && text != "") {
			let member = document.createElement("span");
			member.className = "member-create";
			member.innerText = text;
			section.appendChild(member);
		} else {
			errorSameMember();
		}
		document.querySelector("#member-form").value = "";
	});

//Botão voltar///////////////////////////////////////////////////
document
	.querySelector("#button-nav-bar-back")
	.addEventListener("click", function () {
		let token = param.get("token");
		dataUrl.append("token", token);
		dataUrl.append("username", usernameLoggedUser);
		dataUrl.append("lang", document.querySelector("select").value);
		window.location.href = "newsList.html?" + dataUrl.toString();
	});

//Botão add conteúdo///////////////////////////////////////////////////
document
	.querySelector("#create-content")
	.addEventListener("click", function () {
		createProject();
	});

////////////////////////////////////////////////////////////////////////
//Criar Notícia
///////////////////////////////////////////////////////////////////////
function createProject() {
	console.log("createProject");
	let title = document.getElementById("title-form").value;
	let photo = document.getElementById("photo-content-form").value;
	let description = document.getElementById("text-area-form").value;

	let keyList = document.querySelectorAll(".keyword-create");
	console.log("keyList " + keyList);

	keyList.forEach((element) => {
		console.log(element);
		auxilarKeyWord += element.innerText;
	});
	let keywordString = auxilarKeyWord.substring(1, auxilarKeyWord.length - 1);
	console.log(keywordString);

	let memberList = document.querySelectorAll(".member-create");
	memberList.forEach((element) => {
		console.log(element);
		auxilarMember += element.innerText;
	});
	let memberString = auxilarMember.substring(1, auxilarMember.length - 1);
	console.log(memberString);
	if (memberString.includes(".")) {
		memberString = "";
	}

	let textComboBox = document.querySelector("#comboBoxStatus").value;
	let status = textComboBox;
	if (textComboBox == "Escolha a opção" || textComboBox == "Choose an option") {
		errorEmptyFields();
	}

	if (textComboBox == "VISIVEL") {
		status = "VISIBLE";
	} else if (textComboBox == "INVISIVEL") {
		status = "UNVISIBLE";
	} else if (textComboBox == "APAGADO") {
		status = "OFF";
	}

	if (title != "" && photo != "" && description != "" && keywordString != "") {

		//array
		
		let ids = {
			size: title,
			1: photo,
			2: description,
			statusProjectDto: status,
			keywords_project: keywordString,
			project_members: memberString,
		};

		const fetchOpcoes = {
			method: "POST",
			body: JSON.stringify(contentToCreate),
			headers: {
				token,
				Accept: "*/*",
				"Content-Type": "application/json",
			},
		};

		let path = defaulPath + "project/insertProject";

		fetch(path, fetchOpcoes).then((response) => {
			if (response.status == 200) {
				recordSucess();
				console.log("conteudo criado");
				dataUrl.append("token", token);
				dataUrl.append("username", usernameLoggedUser);
				dataUrl.append("lang", document.querySelector("select").value);
				setTimeout(function () {
					window.location.href = "newsList.html?" + dataUrl.toString();
				}, 4000);
			} else {
				console.log("deu erro no registo");
				//colocar chamada de método que exibe mensagem de erro no registo
			}
		});

		// se ok faz o post
	} else {
		errorEmptyFields();
	}
}

//LOGOUT/////////////////////////////////////////////////////////////////////////
document
	.querySelector("#button-nav-bar-logout")
	.addEventListener("click", function () {
		doLogout();
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

////////////////////////////////////////////////////////////////////////
//Mensagens de erro e sucesso*****
///////////////////////////////////////////////////////////////////////

function errorEmptyFields() {
	let currentcurrentLanguage = document.querySelector("select").value;

	if (currentcurrentLanguage == "PT") {
		document.querySelector(".mensagemCamposVazios").innerText =
			"Não podem existir,campos vazios. Favor preencher todos os campos.";
	} else {
		document.querySelector(".mensagemCamposVazios").innerText =
			"Cannot exist, empty fields. Please fill in all fields.";
	}
	// Depois de 5 segundos tira a mensagem do ecrã
	setTimeout(function () {
		document.querySelector(".mensagemCamposVazios").innerText = "";
	}, 3000);
}

function errorSameKey() {
	let currentcurrentLanguage = document.querySelector("select").value;

	if (currentcurrentLanguage == "PT") {
		document.querySelector(".mensagemSameKey").innerText =
			"Keyword já adicionada.";
	} else {
		document.querySelector(".mensagemSameKey").innerText =
			"Keyword already added.";
	}
	// Depois de 5 segundos tira a mensagem do ecrã
	setTimeout(function () {
		document.querySelector(".mensagemSameKey").innerText = "";
	}, 3000);
}

function errorSameMember() {
	let currentcurrentLanguage = document.querySelector("select").value;

	if (currentcurrentLanguage == "PT") {
		document.querySelector(".mensagemSameMember").innerText =
			"Membro já adicionado.";
	} else {
		document.querySelector(".mensagemSameMember").innerText =
			"Member already added.";
	}
	// Depois de 5 segundos tira a mensagem do ecrã
	setTimeout(function () {
		document.querySelector(".mensagemSameMember").innerText = "";
	}, 3000);
}

function recordSucess() {
	let currentcurrentLanguage = document.querySelector("select").value;

	if (currentcurrentLanguage == "PT") {
		document.querySelector(".mensagemSucessoCreate").innerText =
			"Conteúdo criado com sucesso.";
	} else {
		document.querySelector(".mensagemSucessoCreate").innerText =
			"Content created successfully.";
	}
	// Depois de 5 segundos tira a mensagem do ecrã
	setTimeout(function () {
		document.querySelector(".mensagemSucessoCreate").innerText = "";
	}, 3000);
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

/////////////////////////////////////////////////////////////////
// carregar usernames
/////////////////////////////////////////////////////////////////
function loadUsernames(usernames) {
	let comboBox = document.querySelector("#comboBoxUsername");
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
