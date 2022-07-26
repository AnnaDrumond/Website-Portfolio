import * as types from "../constants/ActionTypes";

// O reducer é a função abaixo que recebe o estado anterior (initialModel) e
// atualiza para o novo estado de acordo com o que foi enviado dentro da minha ação

// Os reducers ficam dentro da minha store, quando envio dados ao educer estou na store
/**O Store do redux é o que gerencia o state. Ao criar um store, precisamos informar
 *  qual é a função reducer que irá manipular
 * - ou não - os dados (também conhecidos como state). */

//define que o estado inicial é o determinado no initialModel
//recebe a action e vai separar os dados
const dataCards = (
	state = {
		cardsStats: {
			totalMembers: 0,
			pendingRecords: 0,
			lastPublication: 0,
			totalProjects: 0,
			totalNews: 0,
			totalKeywords: 0,
		},
	},
	action,
) => {
	//console.log("entrei no dataCArds em reducerCards");

	// Verificar qual o tipo de action que recebi e com base nesta action consigo
	// aceder os dados guardados em dataReceivedInAction
	if (action.type === types.UPDATE_CARD) {
		//console.log("entrei no if " + action.type);
		//console.log(action);

		//console.log(action.dataReceivedInAction.totalMembers);

		// abaixo preciso ter a mesma estrutura que defini acima no state inicial
		return {
			...state, //os três pontos é para trazer tudo que tem dentro de state
			cardsStats: {
				totalMembers: action.dataReceivedInAction.totalMembers,
				pendingRecords: action.dataReceivedInAction.registrationPendingApproval,
				lastPublication: action.dataReceivedInAction.dateLastPublication,
				totalProjects: action.dataReceivedInAction.totalProjects,
				totalNews: action.dataReceivedInAction.totalNews,
				totalKeywords: action.dataReceivedInAction.totalDifferentKeywords,
			},
		};
	} else {
		return state; //Ou seja, mantem o status do initialModel recebido no state na linha 21
	}
};

export default dataCards;
