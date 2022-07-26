import { updateLanguage } from "./redux/actions/actions";
import store from "./redux/store/myStore";

// Primeiro extrair os dados que vieram na URI da página:

let dataUrl = new URLSearchParams(window.location.search);
//let currentLanguage = dataUrl.get("lang");
//let tokenUrl = dataUrl.get("token");
//let usernameLoggedUser = dataUrl.get("username");

//lang === null ? (pageLanguage = "PT") : (pageLanguage = lang);

let currentLanguage = "EN";
let tokenUrl = "2e2fee87-2fa6-48ad-b75f-3b2837e7e909";
let usernameLoggedUser = "momo"; //

// Será usado pelo webSocket pois o backend recebe o token como PathParametro
export default function returnToken() {
	return "4983c693-2cab-4d10-a8fa-6a732820cb3e";
}

////////////////////////////////////////////////////////////////////
//Fazer um dispatcher para os enviar ao reducer
// para enviar, crio um objeto Json com o idioma - linha 32
////////////////////////////////////////////////////////////////////

//o dispatcher vai enviar os dados recebidos do back end para a store criada na pasta store/index.js
//Aqui estou disparando uma ação/action(updateDataCards) para enviar os dados para a store.
// DAqui vou para  a Action updateDataCards
store.dispatch(
	updateLanguage({
		language: currentLanguage,
	}),
);

////////////////////////////////////////////////////////////////////
// reconstruir a URL para me levar de voltar a homePage do backOffice
////////////////////////////////////////////////////////////////////
export function backToMainPage() {
	dataUrl.append("token", tokenUrl);
	dataUrl.append("username", usernameLoggedUser);
	dataUrl.append("lang", document.querySelector("select").value);
	window.location.href = "newsList.html?" + dataUrl.toString();
}
