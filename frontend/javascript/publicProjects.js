import doLanguageRefresh from "./languageSupport.js";
let visibleProjects = [];
let visibleProjectsByKeyword = [];
let buttonSearch = document.querySelector("#button-search");
let defaulPath = "http://localhost:8080/backend-projeto5-adrumond/rest/";
let dataUrl = new URLSearchParams();
let currentLanguage;

/////////////////////////////////////////////////////////////////
//Resgatar dados passados como parametro pelo link por queryString
/////////////////////////////////////////////////////////////////
let param = new URLSearchParams(window.location.search);
const lang = param.get("lang");
// Se não tiver nenhum idioma na Url da página, definir o idioma como PT
lang === null ? (currentLanguage = "PT") : (currentLanguage = lang);
document.querySelector("select").value = currentLanguage;

window.onload = function loadPageProjectsVisible() {
	console.log("window.onload");
	getAllVisibleProjects();
};

//////////////////////////////////////////////////////////////////////////////////
// Ação do botão INICIO do Menu. É chamado quando o INICIO é clicado no Menu.
// Irá construir a URL com o idioma atual para o mesmo ser resgatado em
// languageSupport.js na linha 18.
//////////////////////////////////////////////////////////////////////////////////
document
	.querySelector("#button-nav-bar-home")
	.addEventListener("click", function () {
		dataUrl.append("lang", document.querySelector("select").value);
		window.location.href = "index.html?" + dataUrl.toString();
	});

/////////////////////////////////////////////////
//Buscar lista de projetos com status visible
/////////////////////////////////////////////////
async function getAllVisibleProjects() {
	console.log("getAllVisibleProjects");
	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let path = defaulPath + "project/visibleProjects";

	await fetch(path, fetchOptions)
		.then((response) => response.json())
		.then((response) => {
			console.log(response);
			visibleProjects = response;
			loadProjects(visibleProjects);
		})

		.catch((erro) => {
			/*if (response !== null || response !== "") {
				//retornarErroGeral();
				console.log("deu exceção");
			}*/
			console.log("deu exceção");
		});
}

/////////////////////////////////////////////////
//Carregar a lista de projetos visible no ecrã
/////////////////////////////////////////////////
function loadProjects(visibleProjects) {
	console.log("entrei em loadNews" + visibleProjects);

	for (let i = 0; i < visibleProjects.length; i++) {
		let pageProjects = document.querySelector("#section-projects");
		let projectArticle = document.createElement("article"); //li

		let divPrincipal = document.createElement("div");
		divPrincipal.className = "project-box";
		//colocar a div dentro do article
		projectArticle.appendChild(divPrincipal);

		let divTexts = document.createElement("div");
		divTexts.className = "project-box-texts";

		let image = document.createElement("img"); // imagem
		image.className = "project-image";
		image.src = visibleProjects[i].cover_imageDto;
		image.setAttribute("height", "100");
		image.setAttribute("width", "100");

		//colocar a imagem e a segunda div dentro da div principal
		divPrincipal.appendChild(image);
		divPrincipal.appendChild(divTexts);

		let title = document.createElement("h5"); //titulo
		title.className = "project-title";
		title.innerText = visibleProjects[i].title_projectDto;
		//colocar o titulo dentro da segunda div
		divTexts.appendChild(title);

		let creator = document.createElement("h6"); //titulo
		creator.className = "project-creator";
		creator.innerText = visibleProjects[i].userProjectcreatorDto;
		//colocar o titulo dentro da segunda div
		divTexts.appendChild(creator);

		let lastUpdate = document.createElement("h6"); //data ultima atualização
		lastUpdate.className = "project-lastUpdate";
		lastUpdate.innerText = visibleProjects[i].lastUpdateDto;
		lastUpdate.setAttribute("data-i18n-date", visibleProjects[i].lastUpdateDto);
		
		divTexts.appendChild(lastUpdate);

		let buttonLearnMore = document.createElement("button");
		buttonLearnMore.className = "project-buttonLearnMore";
		buttonLearnMore.textContent = "Ver Projeto";
		buttonLearnMore.setAttribute("data-i18n", "seeProject");

		buttonLearnMore.addEventListener("click", function (e) {
			let dataUrl = new URLSearchParams();
			dataUrl.append("idProject", visibleProjects[i].id);
			dataUrl.append("lang", document.querySelector("select").value); // Aqui levo comigo o id e o idioma
			window.location.href = "projectPage.html?" + dataUrl.toString();
			//chamar método que vai trocar para a página da notícia enviando o id como parametro
		});

		//colocar o botão dentro da segunda div
		divTexts.appendChild(buttonLearnMore);
		//colocar o article que tem tudo dentro, para dentro da section que está no ficheiro html
		pageProjects.appendChild(projectArticle);
	}
	///////////////////////////////////////////////////////////////////////////////////////////
	//Este método está em currentLanguageSupport, que por sua vez foi exportada para este ficheiro JS
	///////////////////////////////////////////////////////////////////////////////////////////
	doLanguageRefresh(document.querySelector("#languages").value);
}

buttonSearch.addEventListener("click", getProjectsByKeyword);

////////////////////////////////////////////////////////////////////////////
//Buscar noticias que possuem determinada keyword
////////////////////////////////////////////////////////////////////////////
async function getProjectsByKeyword() {
	console.log("cliquei no search e entrei em getNewsByKeyword");

	const fetchOptions = {
		method: "GET",
		headers: { "Content-Type": "application/json" },
	};

	let keywordToFound = document.getElementById("input-search").value;

	let path =
		defaulPath + "project/getVisibleProjectsByKeyword/" + keywordToFound;

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
			visibleProjectsByKeyword = data;
			console.log("XXXXXXXXXXXXXXXXXX " + visibleProjectsByKeyword);
			updateNews(visibleProjectsByKeyword);
		});
}

/////////////////////////////////////////////////
//Recarregar somente projetos com uma certa keyword
/////////////////////////////////////////////////
function updateNews(visibleProjectsByKeyword) {
	let newsList = document.querySelector("#section-projects");
	for (let i = 0; newsList.children.length > 0; i++) {
		newsList.removeChild(newsList.children[0]);
	}
	loadProjects(visibleProjectsByKeyword);
}
