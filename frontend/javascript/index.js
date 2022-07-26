import doLanguageRefresh from "./languageSupport.js";
import translate_dates from "./languageSupport.js";
let visibleNews = [];
let currentLanguage;
let visibleNewsByKeyword = [];
let membersAndAdminsList = [];
let defaulPath = "http://localhost:8080/backend-projeto5-adrumond/rest/";
let buttonSearch = document.querySelector("#button-search");
let dataUrl = new URLSearchParams();
const desiredElement = document.getElementById("nav-bar-list-id"); // elemento alvo
const pixelsAmount = "100"; // Quantidade de pixels a contar do TOP até definir a cor

let param = new URLSearchParams(window.location.search);
let lang = param.get("lang");
lang === null ? (currentLanguage = "PT") : (currentLanguage = lang);
document.querySelector("select").value = currentLanguage;
console.log(currentLanguage + "-------------------------");

//////////////////////////////////////////////////////////
// Ação do botão PROJETO do Menu
//////////////////////////////////////////////////////////
document
	.querySelector("#nav-bar-project")
	.addEventListener("click", function () {
		dataUrl.append("lang", document.querySelector("select").value);
		window.location.href = "publicProjects.html?" + dataUrl.toString();
	});

//////////////////////////////////////////////////////////
// Ação dos botões de registar e login
//////////////////////////////////////////////////////////
document
	.querySelector("#register-user")
	.addEventListener("click", registerNewuser);

document.querySelector("#login-user").addEventListener("click", validateLogin);

//////////////////////////////////////////////////////////
window.onload = function loadPage() {
	getAllVisibleNews();
	getAllMembersAndAdmins();
};

/////////////////////////////////////////////////
//Carregar a lista de noticias visible no ecrã
/////////////////////////////////////////////////
function loadVisibleNews(visibleNews) {
	console.log("entrei em loadVisibleNews" + visibleNews);

	for (let i = 0; i < visibleNews.length; i++) {
		let pageNews = document.querySelector("#section-news");
		let newsArticle = document.createElement("article"); //li

		let divPrincipal = document.createElement("div");
		divPrincipal.className = "news-box";
		//colocar a div dentro do article
		newsArticle.appendChild(divPrincipal);

		let divTexts = document.createElement("div");
		divTexts.className = "news-box-texts";

		let image = document.createElement("img"); // imagem
		image.className = "news-image";
		image.src = visibleNews[i].cover_imageDto;
		image.setAttribute("height", "180");
		image.setAttribute("width", "180");

		//colocar a imagem e a segunda div dentro da div principal
		divPrincipal.appendChild(image);
		divPrincipal.appendChild(divTexts);

		let title = document.createElement("h5"); //titulo
		title.className = "news-title";
		title.innerText = visibleNews[i].title_newsDto;
		//colocar o titulo dentro da segunda div
		divTexts.appendChild(title);

		let lastUpdate = document.createElement("h6"); //data ultima atualização
		lastUpdate.className = "news-lestUpdate";
		lastUpdate.innerText = visibleNews[i].lastUpdateDto;
		lastUpdate.setAttribute("data-i18n-date", visibleNews[i].lastUpdateDto);
		divTexts.appendChild(lastUpdate);

		let buttonLearnMore = document.createElement("button");
		buttonLearnMore.className = "news-buttonLearnMore";
		buttonLearnMore.innerText = "Ver Noticia";
		buttonLearnMore.setAttribute("data-i18n", "seeNews");

		buttonLearnMore.addEventListener("click", function (e) {
			//criar query string para enviar dados como parametro pelo link
			//enviar o id como parametro pelo link
			dataUrl.append("idNews", visibleNews[i].id);
			dataUrl.append("lang", document.querySelector("select").value); // Aqui levo comigo o id e o idioma
			window.location.href = "newsPage.html?" + dataUrl.toString();
		});

		//colocar o botão dentro da segunda div
		divTexts.appendChild(buttonLearnMore);
		//colocar o article que tem tudo dentro, para dentro da section que está no ficheiro html
		pageNews.appendChild(newsArticle);
	}
	doLanguageRefresh(document.querySelector("#languages").value);
}

////////////////////////////////////////////////////////////////////////
//Carregar a lista dos membros da organização no ecrã
///////////////////////////////////////////////////////////////////////
function loadMembersAndAdmins(membersAndAdminsList) {
	console.log("entrei em loadMembersAndAdmins");

	for (let i = 0; i < membersAndAdminsList.length; i++) {
		let PageMembersAndAdmins = document.querySelector("#section-members");

		let usersArticle = document.createElement("article"); //li

		let divContainerUsers = document.createElement("div");
		divContainerUsers.className = "users-box";

		usersArticle.appendChild(divContainerUsers);

		let divTextsUsers = document.createElement("div");
		divTextsUsers.className = "users-box-texts";

		let photo = document.createElement("img"); // imagem
		photo.className = "user-image";
		photo.src = membersAndAdminsList[i].photo_userDto;
		photo.setAttribute("height", "160");

		divContainerUsers.appendChild(photo);
		divContainerUsers.appendChild(divTextsUsers);

		let name = document.createElement("h4"); //titulo
		name.className = "user-name";
		name.innerText =
			membersAndAdminsList[i].first_nameDto +
			" " +
			membersAndAdminsList[i].last_nameDto;
		divTextsUsers.appendChild(name);

		let biography = document.createElement("p"); //data ultima atualização
		biography.className = "user-biography";
		biography.innerText = membersAndAdminsList[i].biographyDto;
		divTextsUsers.appendChild(biography);

		PageMembersAndAdmins.appendChild(usersArticle);
	}
	///////////////////////////////////////////////////////////////////////////////////////////
	//Este método está em currentLanguageSupport, que por sua vez foi exportada para este ficheiro JS
	///////////////////////////////////////////////////////////////////////////////////////////
	doLanguageRefresh(document.querySelector("#languages").value);
}

////////////////////////////////////////////////////////////////////////
//Login
///////////////////////////////////////////////////////////////////////
async function validateLogin(e) {
	console.log("validateLogin");
	let username = document.querySelector("#username-login").value;
	let password = document.querySelector("#password-login").value;

	const headersObj = new Headers();
	headersObj.append("username", username);
	headersObj.append("password", password);

	const fetchOptions = {
		method: "POST",
		"Content-Type": "application/json",
		headers: headersObj,
	};

	let path = defaulPath + "user/login";

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
			let user = data;
			// Aqui levo junto com a url o token,username e idioma atual
			dataUrl.append("lang", document.querySelector("select").value);
			dataUrl.append("token", user.token);
			dataUrl.append("username", user.username);
			document.querySelector("#username-login").value = "";
			window.location.href = "newsList.html?" + dataUrl.toString();
		});
}

buttonSearch.addEventListener("click", getNewsByKeyword);

////////////////////////////////////////////////////////////////////////////
//Buscar noticias que possuem determinada keyword
////////////////////////////////////////////////////////////////////////////
async function getNewsByKeyword() {
	console.log("cliquei no search e entrei em getNewsByKeyword");

	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let keywordToFound = document.getElementById("input-search").value;

	let path = defaulPath + "news/visibleNewsByKeyword/" + keywordToFound;

	await fetch(path, fetchOptions)
		.then((response) => {
			if (response.status === 200) {
				return response.json();
			} else {
				//mostrar erro
			}
		})
		.then((data) => {
			visibleNewsByKeyword = data;
			console.log("XXXXXXXXXXXXXXXXXX " + visibleNewsByKeyword);
			updateNews(visibleNewsByKeyword);
		});
}

/////////////////////////////////////////////////
//Recarregar somente news com uma certa keyword
/////////////////////////////////////////////////
function updateNews(visibleNewsByKeyword) {
	let newsList = document.querySelector("#section-news");
	for (let i = 0; newsList.children.length > 0; i++) {
		newsList.removeChild(newsList.children[0]);
	}
	loadVisibleNews(visibleNewsByKeyword);
}

/////////////////////////////////////////////////
//Buscar lista de notícias com status visible
/////////////////////////////////////////////////
async function getAllVisibleNews() {
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let path = defaulPath + "news/visibleNews";

	await fetch(path, fetchOptions)
		.then((response) => response.json())
		.then((response) => {
			console.log(response);
			visibleNews = response;
			loadVisibleNews(visibleNews);
		})

		.catch((erro) => {
			/*if (response !== null || response !== "") {
				//retornarErroGeral();
				console.log("deu exceção");
			}*/
			console.log("deu exceção");
		});
}

////////////////////////////////////////////////////////////////////////
//Buscar lista de users membros e admins para a  página onde a equipa
//da organização  deve ser apresentada
///////////////////////////////////////////////////////////////////////
async function getAllMembersAndAdmins() {
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let path = defaulPath + "user/listAdminsAndMembers";

	await fetch(path, fetchOptions)
		.then((response) => response.json())
		.then((response) => {
			membersAndAdminsList = response;
			console.log(membersAndAdminsList);
			loadMembersAndAdmins(membersAndAdminsList);
		})

		.catch((erro) => {
			/*if (response !== null || response !== "") {
				//retornarErroGeral();
				console.log("deu exceção");
			}*/
			console.log("deu exceção");
		});
}

////////////////////////////////////////////////////////////////////////
//Registar novo user
///////////////////////////////////////////////////////////////////////

async function registerNewuser(e) {
	console.log("entrei em registerNewuser");

	let firstName = document.getElementById("firstName-form").value;
	let firstNameTrim = firstName.trim();

	let lastName = document.getElementById("lastName-form").value;
	let lastNameTrim = lastName.trim();

	let username = document.getElementById("username-form").value;
	let usernameTrim = username.trim();

	let password = document.getElementById("password-form").value;
	let passwordTrim = password.trim();

	let email = document.getElementById("email-form").value;
	let emailTrim = email.trim();

	let photo = document.getElementById("photo-form").value;
	let photoTrim = photo.trim();

	if (
		firstName != "" &&
		firstNameTrim.length > 0 &&
		lastName != "" &&
		lastNameTrim.length > 0 &&
		username != "" &&
		usernameTrim.length > 0 &&
		password != "" &&
		passwordTrim.length > 0 &&
		email != "" &&
		emailTrim.length > 0 &&
		photo != "" &&
		photoTrim.length > 0
	) {
		//cria o json que será enviado ao backend com os dados do novo user
		let userToRegister = {
			first_nameDto: firstName,
			last_nameDto: lastName,
			usernameDto: username,
			passwordDto: password,
			emailDto: email,
			photo_userDto: photo,
		};

		//cria os dados que irão ser enviados pelo fetch
		const fetchOpcoes = {
			method: "POST",
			body: JSON.stringify(userToRegister),
			headers: {
				username,
				password,
				Accept: "*/*",
				"Content-Type": "application/json",
			},
		};

		let path = defaulPath + "user/insertUser";

		await fetch(path, fetchOpcoes).then((response) => {
			if (response.status == 200) {
				recordSucess();
				console.log("user registado");
				document.querySelector("#firstName-form").value = "";
				document.querySelector("#lastName-form").value = "";
				document.querySelector("#username-form").value = "";
				document.querySelector("#password-form").value = "";
				document.querySelector("#email-form").value = "";
				document.querySelector("#photo-form").value = "";
			} else {
				console.log("deu erro no registo");
				//colocar chamada de método que exibe mensagem de erro no registo
			}
		});
	} else {
		errorEmptyFields();
	}
}

////////////////////////////////////////////////////////////////////////
//Mensagens de erro e sucesso*****
///////////////////////////////////////////////////////////////////////

function returnRecordError() {
	return (document.querySelector(".mensagemErroRegisto").innerText = "Error");
}

function recordSucess() {
	let currentcurrentLanguage = document.querySelector("select").value;

	if (currentcurrentLanguage == "PT") {
		document.querySelector(".mensagemSucessoRegisto").innerText =
			"Utilizador registado, aguardar aprovação.";
	} else {
		document.querySelector(".mensagemSucessoRegisto").innerText =
			"Registered user, await approval.";
	}
	// Depois de 5 segundos tira a mensagem do ecrã
	setTimeout(function () {
		document.querySelector(".mensagemSucessoRegisto").innerText = "";
	}, 3000);
}

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

////////////////////////////////////
//Menu
///////////////////////////////////
window.addEventListener("scroll", function () {
	if (window.scrollY > pixelsAmount) {
		desiredElement.classList.add("changeStyle"); // adiciona classe "changeColor"
	} else {
		desiredElement.classList.remove("changeStyle"); // remove classe "changeColor"
	}
});
