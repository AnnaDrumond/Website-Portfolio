import { updateDataCards } from "./redux/actions/actions";
import returnToken from "./uriSupport";
import store from "./redux/store/myStore";
var wsocket;

//esta função será importada pelo App
export const connectToWebsocket = function () {
	console.log("entrei no connectToWebsocket");
	const rootWs =
		"ws://localhost:8080/backend-projeto5-adrumond/generalWebSocket/";

	wsocket = new WebSocket(rootWs + returnToken()); // é criado um novo objeto webSocket com o path e pathParametro(token)

	wsocket.onmessage = (event) => {
		var dataReceived = JSON.parse(event.data); // receber os dados que vieram do backend
		//console.log("dentro do on message");

		//o dispatcher vai enviar os dados recebidos do back end para a store criada na pasta store/index.js
		//Aqui estou disparando uma ação/action(updateDataCards) para enviar os dados para a store.
		// DAqui vou para  a Action updateDataCards
		store.dispatch(updateDataCards(dataReceived)); //jsonStatisticsData - meu array com as datas
	}; //está chamando o método onMessage da linha 22
};

// NOTA: abaixo seria similar ao window.on.load
//document.addEventListener("DOMContentLoaded", connect);
