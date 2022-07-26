import Translator from "./translator.js";

///////////////////////////////////////////////////////////////////////////
//Cria o objeto das linguagens e da localização dos ficheiros
///////////////////////////////////////////////////////////////////////////
var translator = new Translator({
	persist: false,
	languages: ["EN", "PT"],
	defaultLanguage: "PT",
	detectLanguage: true,
	filesLocation: "/i18n",
});

//Resgatar o idioma guardado no URL da página
let currentLanguage = new URLSearchParams(window.location.search).get("lang");

// Se não tiver nenhum idioma na URL usar o PT
currentLanguage === null
	? (currentLanguage = "PT")
	: (currentLanguage = currentLanguage);
translator.load(currentLanguage);

///////////////////////////////////////////////////////////////////////////
//Comanda a troca entre idiomas, verificando o que foi selecionado na comboBox
//Depois vai ao html e altera o idioma através do valor chave definido
// no ficheiro do respectivo idioma escolhido
///////////////////////////////////////////////////////////////////////////
document.querySelector("select").addEventListener("click", function (evt) {
	console.log("cliquei no select");

	if (evt.target.tagName === "SELECT") {
		console.log("languageSupport " + evt.target.value);
		translator.refresh_elements(evt.target.value);
		dateTranslator(evt.target.value);
	}
});

////////////////////////////////////////////////////////////////////////////////
// É exportado para ser usado em todas as páginas
// Garante que os elementos dinâmicos mantenham-se no idioma escolhido pelo user
/////////////////////////////////////////////////////////////////////////////////
export default function doLanguageRefresh(currentLanguage) {
	console.log("doLanguageRefresh " + currentLanguage);
	translator.refresh_elements(currentLanguage);
	dateTranslator(currentLanguage);
}

////////////////////////////////////////////////////////////////////////////////
// Garante que as datas do site sejam formatadas conforme idioma
/////////////////////////////////////////////////////////////////////////////////
export function dateTranslator(currentLanguage) {
	console.log("translate_dates " + currentLanguage);

	//buscar todos os elementos com este atributo e por em uma lista
	////seleciona todos elementos html que tenham data-i18n-date
	const websiteDateList = document.querySelectorAll("[data-i18n-date]");

	//A cada elemento formata a data, para o idioma escolhido
	for (let index = 0; index < websiteDateList.length; index++) {
		websiteDateList[index].innerHTML = new Intl.DateTimeFormat(
			currentLanguage,
		).format(new Date(websiteDateList[index].getAttribute("data-i18n-date")));
	}
}
