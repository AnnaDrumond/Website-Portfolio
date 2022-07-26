import React from "react";
import ReactDOM from "react-dom";
import App from "./App";
import { connectToWebsocket } from "./webSocket";
import { Provider } from "react-redux";
import store from "./redux/store/myStore";

//Primeira coisa que corre no programa da parte do react

// Primeiro chamo o método que abre a conexão com o WebSocket para trazer os dados de que preciso
//sem esta linha minha conexão não abre e meus dados ficam sempre com o estado inicial (0)
connectToWebsocket();

//Em segundo, preciso fazer referencia da minha store para que todos os componentes dentro da app
//possam aceder/atualizar dados que estarão dentro desta minha store
//Em terceiro vou chamar o App que por sua vez chama os componentes CARD e MENU
ReactDOM.render(
	<React.StrictMode>
		<Provider store={store}>
			<App />
		</Provider>
	</React.StrictMode>,
	document.getElementById("root"),
);
