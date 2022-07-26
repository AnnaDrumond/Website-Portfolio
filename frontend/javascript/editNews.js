import doLanguageRefresh from "./languageSupport.js";
let defaulPath = "http://localhost:8080/backend-projeto5-adrumond/rest/";
let pageLanguage;
let contentAssociated = [];
let allContent = [];
let contentFinal = [];
let auxilarKeyWord = ".";
let auxilarMember = ".";
let newsFound;
let arrayKeys;
let arrayFinalMembers = [];
let associatedMembers = [];
let notAssociatedMembers = [];
let notAssociatedUsernames = [];
let dataUrl = new URLSearchParams();

/////////////////////////////////////////////////////////////////
//Resgatar o id passado como parametro pelo link por queryString
/////////////////////////////////////////////////////////////////
let param = new URLSearchParams(window.location.search);
let idNews = param.get("idNews");
let lang = param.get("lang");
let token = param.get("token");
let usernameLoggedUser = param.get("username");
lang === null ? (pageLanguage = "PT") : (pageLanguage = lang);
document.querySelector("select").value = pageLanguage; // settar o idioma na comboBox

window.onload = function loadPage() {
	getTypeUser(usernameLoggedUser);
	getAssociateContent(idNews);
	getNewsById();
	getAssociatedNewsMembers();
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

/////////////////////////////////////////////////////////////////
//Buscar lista de conteúdo (projetos) associado
/////////////////////////////////////////////////////////////////
async function getAssociateContent(idNews) {
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};

	let path = defaulPath + "news/allProjectsAssociated/" + idNews;

	await fetch(path, fetchOptions)
		.then((response) => {
			if (response.status === 200) {
				return response.json();
			} else {
				//mostrar erro
			}
		})
		.then((data) => {
			contentAssociated = data;
			//console.log(
			//	"trouxe os projetos associados a esta noticia " + contentAssociated,
			//);
			getAllContent(contentAssociated);
			loadDisassociateContent(contentAssociated);
		});
}

/////////////////////////////////////////////////////////////////
//Buscar lista projetos gerais
/////////////////////////////////////////////////////////////////
async function getAllContent(contentAssociated) {
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};

	let path = defaulPath + "project/allProjectsNotOff";

	await fetch(path, fetchOptions)
		.then((response) => {
			if (response.status === 200) {
				return response.json();
			} else {
				//mostrar erro
			}
		})
		.then((data) => {
			// aqui tenhos todos os projetos do site
			allContent = data;
			//console.log("trouxe os projetos gerais do site" + allContent);
			loadContentNotAssociate(contentAssociated, allContent);
		});
}

/////////////////////////////////////////////////////////////////
//Carregar os projetos ainda não associados
/////////////////////////////////////////////////////////////////
function loadContentNotAssociate(contentAssociated, allContent) {
	contentFinal = allContent;
	//percorrer as listas comparando
	for (let i = 0; i < contentFinal.length; i++) {
		for (let j = 0; j < contentAssociated.length; j++) {
			if (contentFinal[i].id == contentAssociated[j].id) {
				contentFinal.splice(i, 1);
			}
		}
	}
	//	console.log(" lista de projetos ainda não associados a esta noticia");
	//console.log(contentFinal);
	loadNotAssociateContent(contentFinal);
}

/////////////////////////////////////////////////////////////////
// carregar conteúdo ainda não associado
/////////////////////////////////////////////////////////////////
function loadNotAssociateContent(contentFinal) {
	//console.log("loadDisassociateContent");
	let section = document.querySelector("#associate-content");
	//console.log("XXXXXXXXXXXXXXXXXX " + usernames);
	if (contentFinal != null || contentFinal != "") {
		contentFinal.forEach((element) => {
			//console.log(element.title_projectDto);
			//Impedir que o user possa se associar a um conteúdo que ele próprio está a criar
			let content = document.createElement("button");
			content.className = "button-content";
			content.innerText = element.title_projectDto;
			content.addEventListener("click", function (e) {
				console.log("cliquei no botao desassociar conteúdo");
				doAssociateContent(element.id);
			});
			section.appendChild(content);
		});
	}
}

/////////////////////////////////////////////////////////////////
//Disassociar conteúdo
/////////////////////////////////////////////////////////////////
async function doAssociateContent(idProject) {
	const fetchOptions = {
		method: "POST",
		headers: { "Content-Type": "application/json", token },
	};

	let path =
		defaulPath + "project/associateProject/" + idProject + "/toNews/" + idNews;

	await fetch(path, fetchOptions).then((response) => {
		console.log("Response Status: " + response.status);
		if (response.status == 200) {
			console.log("conteúdo desassociado");
			document.location.reload(true);
		} else {
		}
	});
}

/////////////////////////////////////////////////////////////////
// carregar conteúdo associado
/////////////////////////////////////////////////////////////////
function loadDisassociateContent(contentAssociated) {
	//console.log("loadDisassociateContent               contentAssociated");
	//console.log(contentAssociated)
	let section = document.querySelector("#dissaciate-content");
	//console.log("XXXXXXXXXXXXXXXXXX " + usernames);
	if (contentAssociated != null || contentAssociated != "") {
		document.querySelector(".mensagemNoAssociatedMembers").innerText = "";
		contentAssociated.forEach((element) => {
			//console.log(element.title_projectDto);
			//Impedir que o user possa se associar a um conteúdo que ele próprio está a criar
			let content = document.createElement("button");
			content.className = "button-content";
			content.innerText = element.title_projectDto;
			content.addEventListener("click", function (e) {
				console.log("cliquei no botao desassociar conteúdo");
				doDisassociateContent(element.id);
			});
			section.appendChild(content);
		});
	}

	if (contentAssociated.length == 0) {
		let p = document.querySelector(".mensagemNoAssociatedMembers");
		p.setAttribute("data-i18n", "alert1");
	}
	///////////////////////////////////////////////////////////////////////////////////////////
	//Este método está em currentLanguageSupport, que por sua vez foi exportada para este ficheiro JS
	///////////////////////////////////////////////////////////////////////////////////////////
	doLanguageRefresh(document.querySelector("#languages").value);
}

/////////////////////////////////////////////////////////////////
//Disassociar conteúdo
/////////////////////////////////////////////////////////////////
async function doDisassociateContent(idProject) {
	const fetchOptions = {
		method: "POST",
		headers: { "Content-Type": "application/json", token },
	};

	////associateProject/{idProject}/toNews/{idNews}
	let path =
		defaulPath +
		"project/disassociateProject/" +
		idProject +
		"/toNews/" +
		idNews;

	await fetch(path, fetchOptions).then((response) => {
		console.log("Response Status: " + response.status);
		if (response.status == 200) {
			//console.log("conteúdo desassociado");
			document.location.reload(true);
			window.scrollTo(0, document.body.scrollHeight);
		} else {
		}
	});
}

/////////////////////////////////////////////////////////////////
//Buscar a noticia do id indicado na base de dados
/////////////////////////////////////////////////////////////////
async function getNewsById() {
	//console.log("getNewsById");
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};

	let path = defaulPath + "news/newsById/" + idNews;

	await fetch(path, fetchOptions)
		.then((response) => response.json())
		.then((response) => {
			//("getNewsById");
			newsFound = response;
			loadContentData(newsFound);
			let keys = newsFound.keywords_news;
			arrayKeys = keys.split(";");
			//console.log("depois do split-------------------------------");
			//console.log(arrayKeys);
			manageKeywords(arrayKeys);
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
// carregar/construir/descontruir keywords
/////////////////////////////////////////////////////////////////
function manageKeywords(arrayKeys) {
	//console.log("loadDisassociateContent");
	//console.log(arrayKeys);
	let section = document.querySelector("#keyword-create");
	//console.log("XXXXXXXXXXXXXXXXXX " + usernames);
	if (arrayKeys != null || arrayKeys != "") {
		for (let index = 0; index < arrayKeys.length; index++) {
			//console.log("forEach das keywords");
			//Impedir que o user possa se associar a um conteúdo que ele próprio está a criar
			let content = document.createElement("button");
			content.className = "button-content";
			content.innerText = arrayKeys[index];
			content.addEventListener("click", function (e) {
				console.log("cliquei no botao excluir key " + arrayKeys[index]);
				manageKeywordsAuxiliar(index, arrayKeys);
			});
			section.appendChild(content);
		}
	}

	let input = document.createElement("input");
	input.className = "input-item";
	input.id = "keyword-form";
	input.setAttribute("data-i18n", "insertKey");
	section.appendChild(input);

	let button = document.createElement("button");
	button.className = "button-key-add";
	button.id = "add-keyword";
	button.setAttribute("data-i18n", "add");
	section.appendChild(button);
	button.addEventListener("click", function (e) {
		let newKeyword = document
			.getElementById("keyword-form")
			.value.toLowerCase();
		let index = null;
		//console.log("cliquei no botao add key " + newKeyword);
		manageKeywordsAuxiliar(index, arrayKeys, newKeyword);
	});

	///////////////////////////////////////////////////////////////////////////////////////////
	//Este método está em currentLanguageSupport, que por sua vez foi exportada para este ficheiro JS
	///////////////////////////////////////////////////////////////////////////////////////////
	doLanguageRefresh(document.querySelector("#languages").value);
}

/////////////////////////////////////////////////////////////////
// deletar keywords do array
/////////////////////////////////////////////////////////////////
function manageKeywordsAuxiliar(index, arrayKeys, newKeyword) {
	let isSameKey = false;

	if (index != null && newKeyword == null) {
		// veio com o index para excluir key do array
		arrayKeys.splice(index, 1);
		//veio com a nova key para adicionar ao array
	} else if (index == null && newKeyword != null) {
		////// verificar se já tem esta keyword///////////////////////
		for (let i = 0; i < arrayKeys.length; i++) {
			//console.log("entrei no for");
			if (newKeyword == arrayKeys[i]) {
				isSameKey = true;
			}
		}

		if (isSameKey === false && newKeyword != "") {
			arrayKeys.push(newKeyword); // se ainda não tem esta key pode adicionar
		} else {
			console.log("entreo no else do erro de mesma keyword");
			errorSameKey(); //senão informa erro ao user
		}
		document.querySelector("#keyword-form").value = "";
	}
	let section = document.querySelector("#keyword-create");
	for (let i = 0; section.children.length > 0; i++) {
		section.removeChild(section.children[0]);
	}
	manageKeywords(arrayKeys);
}

/////////////////////////////////////////////////////////////////
// Buscar membros associados a esta noticia
/////////////////////////////////////////////////////////////////
async function getAssociatedNewsMembers() {
	//console.log(
	//	"get membros associados *******************************************",
	//);
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let path = defaulPath + "news/newslistMembers/" + idNews;
	manageMembersAssociated;
	await fetch(path, fetchOptions)
		.then((response) => response.json())
		.then((response) => {
			associatedMembers = response;
			getAllTeam(associatedMembers);
			//só carregar na página se lista MAIOR que 0
		})
		.catch((erro) => {
			console.log("deu exceção fora do if");
		});
}

////////////////////////////////////////////////////////////////////////////
//Buscar Equipa da organização
////////////////////////////////////////////////////////////////////////////
function getAllTeam(associatedMembers) {
	//console.log("getAllTeam");

	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json", token },
	};
	let path = defaulPath + "user/listTeam";

	fetch(path, fetchOptions)
		.then((response) => {
			if (response.status === 200) {
				return response.json();
			}
		})
		.then((data) => {
			console.log(
				"XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX",
			);
			console.log(data);
			notAssociatedMembers = data;

			generatorArraysUsernames(notAssociatedMembers, associatedMembers);
		});
}

//notAssociatedUsernames
/////////////////////////////////////////////////////////////////
// Gerar arrays somente com usernames
/////////////////////////////////////////////////////////////////
function generatorArraysUsernames(notAssociatedMembers, associatedMembers) {
	//localizar os users que ainda não são associados a esta noticia
	if (associatedMembers.length > 0) {
		for (let i = 0; i < notAssociatedMembers.length; i++) {
			for (let j = 0; j < associatedMembers.length; j++) {
				if (
					notAssociatedMembers[i].usernameDto ==
					associatedMembers[j].usernameDto
				) {
					notAssociatedMembers.splice(i, 1); //retiro quem já é associado
				}
			}
		} //notAssociatedMembers
	}
	//Gerar um array com os usernames dos users JÁ associados a noticia
	for (let index = 0; index < associatedMembers.length; index++) {
		//////colocar os membros já associados no meu array auxiliar arrayFinalMembers:
		arrayFinalMembers[index] = associatedMembers[index].usernameDto;
		//////////////////////////////////////////////////////////////////////
	}
	//Gerar um array com os usernames dos users ainda NÂO associados a noticia
	//notAssociatedUsernames
	for (let index = 0; index < notAssociatedMembers.length; index++) {
		//////colocar os membros já associados no meu array auxiliar arrayFinalMembers:
		notAssociatedUsernames[index] = notAssociatedMembers[index].usernameDto;
		//////////////////////////////////////////////////////////////////////
	}
	//tirar o user logado neste momento, se não for um admin
	for (let index = 0; index < notAssociatedUsernames.length; index++) {
		if (typeLoggedUser == "ADMINISTRATOR") {
		} else {
			if (notAssociatedUsernames[index] === usernameLoggedUser) {
				notAssociatedUsernames.splice(index, 1); //retiro quem já é associado
			}
		}
	}

	manageMembersAssociated(arrayFinalMembers, notAssociatedUsernames);
}

/////////////////////////////////////////////////////////////////
// carregar membros **************************************************
/////////////////////////////////////////////////////////////////
function manageMembersAssociated(arrayFinalMembers, notAssociatedUsernames) {
	//console.log("manageMembersAssociated");
	let section = document.querySelector("#members-associated");
	let command;

	//Percorre o array dos users que irão para o backend para selecionar o username a excluir
	for (let index = 0; index < arrayFinalMembers.length; index++) {
		let deleteMember = document.createElement("button");
		deleteMember.className = "button-content";
		deleteMember.innerText = arrayFinalMembers[index];
		deleteMember.addEventListener("click", function (e) {
			////////////////
			command = "delete";
			//levo o array, o user que quero adicionar/excluir e o index do user no array
			manageMembersAssociatedAuxiliar(
				arrayFinalMembers,
				arrayFinalMembers[index], //levo username a excluir
				index, //posição do username a excluir
				notAssociatedUsernames, //array de não associados
				command,
			);
		});
		section.appendChild(deleteMember);
	}

	let div = document.createElement("div");
	div.className = "container-div";

	let label = document.createElement("label");
	label.className = "label-content";
	label.innerText = "Adicionar novos membros a este conteúdo";
	label.setAttribute("data-i18n", "deleteM");
	div.appendChild(label);
	section.appendChild(div);

	for (let index = 0; index < notAssociatedUsernames.length; index++) {
		let addMember = document.createElement("button");
		addMember.className = "button-content";
		addMember.innerText = notAssociatedUsernames[index];
		addMember.addEventListener("click", function (e) {
			command = "add";
			//levo o array e o user que quero adicionar/excluir
			manageMembersAssociatedAuxiliar(
				arrayFinalMembers,
				notAssociatedUsernames[index], //username a incluir
				index,
				notAssociatedUsernames,
				command,
			);
		});
		section.appendChild(addMember);
	}

	///////////////////////////////////////////////////////////////////////////////////////////
	//Este método está em currentLanguageSupport, que por sua vez foi exportada para este ficheiro JS
	///////////////////////////////////////////////////////////////////////////////////////////
	doLanguageRefresh(document.querySelector("#languages").value);
}

/////////////////////////////////////////////////////////////////
// gerir membros a incluir/excluir
/////////////////////////////////////////////////////////////////
function manageMembersAssociatedAuxiliar(
	arrayFinalMembers,
	username,
	index,
	notAssociatedUsernames,
	command,
) {
	//arrayFinalMembers.push(associatedMembers[index].usernameDto);
	//auxilarMember
	let isSameMember = false;

	//EXCLUIR*****************************************
	if (command == "delete") {
		// veio com o index para excluir key do array
		arrayFinalMembers.splice(index, 1); //tiro do array final que vai ao backend
		notAssociatedUsernames.push(username); //coloco no array de não associados
		//veio com a nova key para adicionar ao array
	}

	if (command == "add") {
		////// verificar se já tem esta keyword///////////////////////
		for (let i = 0; i < arrayFinalMembers.length; i++) {
			//console.log("entrei no for");
			if (username == arrayFinalMembers[i]) {
				isSameMember = true;
			}
		}

		//ADICIONAR*************************************
		// se ainda não tem este membro pode adicionar
		if (isSameMember === false) {
			notAssociatedUsernames.splice(index, 1); //tiro do array de não associados
			arrayFinalMembers.push(username); //coloco no array final que vai ao backend
		} else {
			errorSameMember(); //senão informa erro ao user
		}
	}

	let section = document.querySelector("#members-associated");
	for (let i = 0; section.children.length > 0; i++) {
		section.removeChild(section.children[0]);
	}

	//console.log("estou depois do excluir/add membrOOOOOOOOOOOOOOOOO");
	manageMembersAssociated(arrayFinalMembers, notAssociatedUsernames);
}

//////////////////////////////////////////////////////////
//Carregar os dados do conteúdo nos inputs
//////////////////////////////////////////////////////////
function loadContentData(newsFound) {
	//console.log("loadContentData");
	//console.log(newsFound);
	document.getElementById("title-form").value = newsFound.title_newsDto;
	document.getElementById("photo-content-form").value =
		newsFound.cover_imageDto;
	document.getElementById("text-area-form").value = newsFound.content_newsDto;
	let comboBox = document.querySelector("#comboBoxStatus");
	let option1 = document.createElement("option");
	let originalStatus = newsFound.statusNewsDto;
	option1.innerText = newsFound.statusNewsDto;

	comboBox.appendChild(option1);

	let option2 = document.createElement("option");
	let option3 = document.createElement("option");

	switch (originalStatus) {
		case "OFF":
			option1.setAttribute("data-i18n", "off");
			option2.innerText = "VISIBLE";
			option2.setAttribute("data-i18n", "visible");
			option3.innerText = "UNVISIBLE";
			option3.setAttribute("data-i18n", "unvisible");
			break;
		case "VISIBLE":
			option1.setAttribute("data-i18n", "visible");
			option2.innerText = "OFF";
			option2.setAttribute("data-i18n", "off");
			option3.innerText = "UNVISIBLE";
			option3.setAttribute("data-i18n", "unvisible");
			break;
		case "UNVISIBLE":
			option1.setAttribute("data-i18n", "unvisible");
			option2.innerText = "OFF";
			option2.setAttribute("data-i18n", "off");
			option3.innerText = "VISIBLE";
			option3.setAttribute("data-i18n", "visible");
			break;
		default:
			break;
	}

	comboBox.appendChild(option2);
	comboBox.appendChild(option3);
}

/////////////////////////////////////////////////////////////////
// Ação do botão de guardar alterações ao conteúdo no final
/////////////////////////////////////////////////////////////////
document.querySelector("#edit-content").addEventListener("click", function () {
	console.log("cliquei no botão guardar");
	let isStatusOk = true;
	// let isMembersOk = true;
	let isKeywordsOk = true;

	//Montar os membros e keywords para o formato que o backend está esperando
	for (let i = 0; i < arrayKeys.length; i++) {
		auxilarKeyWord += arrayKeys[i] + ";";
	}
	let keywordString = auxilarKeyWord.substring(1, auxilarKeyWord.length - 1);
	if (keywordString.includes(".")) {
		isKeywordsOk = false;
	}
	console.log(keywordString);

	for (let i = 0; i < arrayFinalMembers.length; i++) {
		auxilarMember += arrayFinalMembers[i] + ";";
	}

	let memberString = auxilarMember.substring(1, auxilarMember.length - 1);
	if (memberString.includes(".")) {
		// isMembersOk = false;
		memberString = "";
	}
	console.log(memberString);

	let title = document.getElementById("title-form").value;
	let titleTrim = title.trim();

	let photo = document.getElementById("photo-content-form").value;
	let photoTrim = photo.trim();

	let description = document.getElementById("text-area-form").value;
	let descriptionTrim = description.trim();

	let textComboBox = document.querySelector("#comboBoxStatus").value;
	let formattedStatus = textComboBox;
	//Conferir se o user selecionou um status corretamente
	if (textComboBox == "Escolha a opção" || textComboBox == "Choose an option") {
		isStatusOk = false;
		errorEmptyFields();
	} else {
		//Controlar para que o valor seja respetivo ao Enum do backend
		if (textComboBox == "VISIVEL") {
			formattedStatus = "VISIBLE";
		} else if (textComboBox == "INVISIVEL") {
			formattedStatus = "UNVISIBLE";
		} else if (textComboBox == "APAGADO") {
			formattedStatus = "OFF";
		}
	}

	if (
		title != "" &&
		titleTrim.length > 0 &&
		photo != "" &&
		photoTrim.length > 0 &&
		description != "" &&
		descriptionTrim.length > 0 &&
		isStatusOk === true &&
		isKeywordsOk === true
	) {
		let contentUpdated = {
			title_newsDto: title,
			cover_imageDto: photo,
			content_newsDto: description,
			statusNewsDto: formattedStatus,
			keywords_news: keywordString,
			news_members: memberString,
		};

		//cria os dados que irão ser enviados pelo fetch
		const fetchOpcoes = {
			method: "POST",
			body: JSON.stringify(contentUpdated),
			headers: {
				token,
				Accept: "*/*",
				"Content-Type": "application/json",
			},
		};

		let path = defaulPath + "news/editNews/" + idNews;

		fetch(path, fetchOpcoes).then((response) => {
			if (response.status == 200) {
				recordSucess();
			} else {
				console.log("deu erro no registo");
				//colocar chamada de método que exibe mensagem de erro no registo
			}
		});
		// Aqui faz o fetch
	} else {
		errorEmptyFields();
	}
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
		}
	});
}

////////////////////////////////////////////////////////////////////////
//Mensagens de erro e sucesso*****
///////////////////////////////////////////////////////////////////////

function errorEmptyFields() {
	//window.scrollTo(0, 0); //levar de volta ao topo da página

	console.log(errorEmptyFields);
	let p = document.querySelector(".mensagemCamposVazios");
	p.setAttribute("data-i18n", "alert2");
	// Depois de 5 segundos tira a mensagem do ecrã
	setTimeout(function () {
		document.querySelector(".mensagemCamposVazios").innerText = "";
	}, 3000);
}

function errorSameKey() {
	window.scrollTo(0, 0); //levar de volta ao topo da página
	let p = document.querySelector(".mensagemSameKey");
	p.setAttribute("data-i18n", "alert5");
	// Depois de 5 segundos tira a mensagem do ecrã
	setTimeout(function () {
		document.querySelector(".mensagemSameKey").innerText = "";
	}, 3000);
}

function errorSameMember() {
	window.scrollTo(0, 0); //levar de volta ao topo da página
	let p = document.querySelector(".mensagemSameMember");
	p.setAttribute("data-i18n", "alert4");
	// Depois de 5 segundos tira a mensagem do ecrã
	setTimeout(function () {
		document.querySelector(".mensagemSameMember").innerText = "";
	}, 3000);
}

function recordSucess() {
	let currentcurrentLanguage = document.querySelector("select").value;
	window.scrollTo(0, 0); //levar de volta ao topo da página
	if (currentcurrentLanguage == "PT") {
		document.querySelector(".mensagemSucessoEdit").innerText =
			"Conteúdo editado com sucesso.";
	} else {
		document.querySelector(".mensagemSucessoEdit").innerText =
			"Content successfully edited.";
	}
	// Depois de 5 segundos tira a mensagem do ecrã
	setTimeout(function () {
		document.querySelector(".mensagemSucessoEdit").innerText = "";
	}, 3000);

	setTimeout(function () {
		document.location.reload(true);
	}, 3000);
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
