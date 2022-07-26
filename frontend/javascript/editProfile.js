import doLanguageRefresh from "./languageSupport.js";
let defaulPath = "http://localhost:8080/backend-projeto5-adrumond/rest/";
let userToEdit;
let pageLanguage;
let dataUrl = new URLSearchParams();

/////////////////////////////////////////////////////////////////
//Resgatar os dados passados como parametro pelo link por queryString
/////////////////////////////////////////////////////////////////
let param = new URLSearchParams(window.location.search);
let lang = param.get("lang");
let token = param.get("token");
let usernameLoggedUser = param.get("username");
lang === null ? (pageLanguage = "PT") : (pageLanguage = lang);
document.querySelector("select").value = pageLanguage; // settar o idioma na comboBox

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
	} else if (typeLoggedUser == "VISITOR") {
		//A cada troca de página verifica se o user ainda é um membro ou admin
		loadAuthorizatioError();
		dataUrl.append("lang", document.querySelector("select").value);
		setTimeout(function () {
			doLogout(); // se não for mais parte da equipa depois de 2 segundos, força o logout
		}, 2000);
	}
	await getUserData();
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

document.querySelector("#edit-user").addEventListener("click", function () {
	updatedUser(); //////////************************************** */
});

/////////////////////////////////////////////////////////////////
//Buscar dados do utilizador logado
/////////////////////////////////////////////////////////////////
async function getUserData() {
	console.log("getUserData");
	console.log(usernameLoggedUser);
	//http://localhost:8080/backend-projeto5-adrumond/rest/user/loggedUser/momo
	const fetchOptions = {
		method: "GET",
		headers: {
			"Content-Type": "application/json",
			token,
		},
	};

	let path = defaulPath + "user/loggedUser/" + usernameLoggedUser;

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
			userToEdit = data;
			loadUserData(userToEdit);
			//chamar método que vai carregar dados do user no ecrã
			//console.log("XXXXXXXXXXXXXXXXXX " + data.first_nameDto);
		});
}

//////////////////////////////////////////////////////////
//Carregar os dados do user nos inputs
//////////////////////////////////////////////////////////
function loadUserData(userToEdit) {
	console.log("loadUserData");
	console.log(userToEdit);
	console.log(userToEdit.first_nameDto);
	//document.getQ("firstName").value = utilizadorLogado.firstName;
	let nome = userToEdit.first_nameDto;
	document.getElementById("firstName-form").value = userToEdit.first_nameDto;
	document.getElementById("lastName-form").value = userToEdit.last_nameDto;
	document.getElementById("username-form-edit").value = userToEdit.usernameDto;
	document.getElementById("password-form-edit").value = userToEdit.passwordDto;
	document.getElementById("email-form").value = userToEdit.emailDto;
	document.getElementById("photo-form").value = userToEdit.photo_userDto;
	document.getElementById("biography-form").value = userToEdit.biographyDto;

	////// colocar username e password somente leitura///////////////////////////////////
	document.getElementById("username-form-edit").readOnly = true;
	document.getElementById("password-form-edit").readOnly = true;
}

//////////////////////////////////////////////////////////
//Editar perfil
//////////////////////////////////////////////////////////
async function updatedUser() {
	console.log("entrei em updatedUser");

	let firstName = document.getElementById("firstName-form").value;
	let firstNameTrim = firstName.trim();

	let lastName = document.getElementById("lastName-form").value;
	let lastNameTrim = lastName.trim();

	let username = document.getElementById("username-form-edit").value;
	let usernameTrim = username.trim();

	let password = document.getElementById("password-form-edit").value;
	let passwordTrim = password.trim();

	let email = document.getElementById("email-form").value;
	let emailTrim = email.trim();

	let photo = document.getElementById("photo-form").value;
	let photoTrim = photo.trim();

	let bio = document.getElementById("biography-form").value;
	let bioTrim = bio.trim();

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
		photoTrim.length > 0 &&
		bio != "" &&
		bioTrim.length > 0
	) {
		//cria o json que será enviado ao backend com os dados do novo user
		let userUpdated = {
			first_nameDto: firstName,
			last_nameDto: lastName,
			usernameDto: username,
			passwordDto: password,
			emailDto: email,
			biographyDto: bio,
			photo_userDto: photo,
			registrationApprovedDto: true,
		};

		//cria os dados que irão ser enviados pelo fetch
		const fetchOpcoes = {
			method: "POST",
			body: JSON.stringify(userUpdated),
			headers: {
				token,
				Accept: "*/*",
				"Content-Type": "application/json",
			},
		};

		let path = defaulPath + "user/editProfile";

		await fetch(path, fetchOpcoes).then((response) => {
			if (response.status == 200) {
				recordSucess();
			} else {
				console.log("deu erro no registo");
				//colocar chamada de método que exibe mensagem de erro no registo
			}
		});
	} else {
		errorEmptyFields();
	}
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

////////////////////////////////////////////////////////////////////////
//Mensagens de erro e sucesso*****
///////////////////////////////////////////////////////////////////////

function recordSucess() {
	let currentcurrentLanguage = document.querySelector("select").value;

	if (currentcurrentLanguage == "PT") {
		document.querySelector(".mensagemSucesso").innerText =
			"Dados do utilizador atualizados.";
	} else {
		document.querySelector(".mensagemSucesso").innerText = "Updated user data.";
	}
	// Depois de 5 segundos tira a mensagem do ecrã
	setTimeout(function () {
		document.querySelector(".mensagemSucesso").innerText = "";
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
			returnTypeAndTokenUser(data);
		});
}
