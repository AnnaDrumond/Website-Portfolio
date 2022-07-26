import * as types from "../constants/ActionTypes";

const dataIdiom = (
	state = {
		lang: "PT",
	},
	action,
) => {
	console.log("entrei no dataIdiom em reducerLanguage");

	// Verificar qual o tipo de action que recebi e com base nesta action consigo
	// aceder os dados guardados em languageReceived
	if (action.type === types.LOAD_LANGUAGE) {
		console.log("entrei no if em reducerLanguage " + action.type);
		console.log(action.UrlDataJson);

		// abaixo preciso ter a mesma estrutura que defini acima no state inicial
		return {
			...state, //os três pontos é para trazer tudo que tem dentro de state

			lang: action.UrlDataJson,
		};
	} else {
		return state; //Ou seja, mantem o status do initialModel recebido no state na linha 21
	}
};

export default dataIdiom;
