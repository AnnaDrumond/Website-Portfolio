import * as types from "../constants/ActionTypes";

//Aqui estou definindo a ação updateDataCards

// Linha 11 - estou recebendo so dados que foram enviados quando fiz o dispatcher em webSocket.js linha28
//Linha 12 -  primeiro estou definindo qual o  ActionType desta action, neste caso UPDATE_CARD
// o "types" escrito abaixo é palavra reservada do js
// Linha 12/13  - recebo estes dados e reencaminho ao reducer ( reducerCards.js) os dados e o type que eu defini

// Daqui vou ao reducer em reducerCards
export const updateDataCards = (dataReceivedInAction) => ({
	type: types.UPDATE_CARD,
	dataReceivedInAction,
}); 


export const updateLanguage = (UrlDataJson) => ({
	type: types.LOAD_LANGUAGE,
	UrlDataJson,
}); 

