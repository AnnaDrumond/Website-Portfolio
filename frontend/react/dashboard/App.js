import React from "react";
import Card from "./components/card/Card";
import "./app.css";
import { useSelector } from "react-redux";
import Menu from "./components/menu/Menu";

const App = () => {
	//O useSelector será executado sempre que houver um dispatch de uma action
	//Por isso o 'state => state', que seria o mesmo que  state => { return state }
	//Ou seja, aqui recebo o return feito por dataCards em reducerCards seja com valores, seja com os valores do WS
	const stateData = useSelector((state) => {
		return state;
	});

	//console.log("Estou no App depois do useSelector ");

	//pegar dados trazidos do return e separar para enviar aos meus cards
	let totalMembers = stateData.dataCards.cardsStats.totalMembers;
	let pendingRecords = stateData.dataCards.cardsStats.pendingRecords;
	let lastPublication = stateData.dataCards.cardsStats.lastPublication;
	let totalProjects = stateData.dataCards.cardsStats.totalProjects;
	let totalNews = stateData.dataCards.cardsStats.totalNews;
	let totalKeywords = stateData.dataCards.cardsStats.totalKeywords;

	//Recuperar o state do idioma/idioma atual armazenado na store
	let locale = stateData.dataIdiom.lang.language;
	//console.log("estou em app ");
	//console.log(stateData);

	return (
		<div>
			<>
				<Menu />
			</>

			<div className="dashboardContainer">
				<h1 id="tituloDasboard">Painel de Controlo</h1>

				<div className="generalCards">
					<Card
						language={locale}
						text={"nMembers"}
						valueCard={totalMembers}
						typeCard={"number"}
					/>
					<Card
						language={locale}
						text={"records"}
						valueCard={pendingRecords}
						typeCard={"number"}
					/>
					<Card
						language={locale}
						text={"lastPub"}
						valueCard={lastPublication}
						typeCard={"date"}
					/>
				</div>

				<div className="generalCards">
					<Card
						language={locale}
						text={"nProjs"}
						valueCard={totalProjects}
						typeCard={"number"}
					/>
					<Card
						language={locale}
						text={"nNews"}
						valueCard={totalNews}
						typeCard={"number"}
					/>
					<Card
						language={locale}
						text={"totKeywords"}
						valueCard={totalKeywords}
					/>
				</div>
			</div>
		</div>
	);
};



export default App;

/* Sobre useEfect - método da API hooks
A API hooks diminui a verbosidade, permitiu criar componentes através de funções e também utilizar estados
Hooks são uma nova adição no React 16.8.
 Eles permitem que você use o estado e outros recursos do React sem escrever uma classe.

https://reactjs.org/docs/hooks-reference.html
https://reactjs.org/docs/hooks-reference.html#useeffect

Breves resumos:
//1.
//useState guarda o estado do componente. 
//2.
//o useEffect corre sempre que esta funçao corre. para prevenir que isto aconteça passamos uma lista vazia, no segundo argumento da funçao useEffect
//dentro desta lista podiamos passar as dependencias, ie, coisas que quisessemos que ele corresse qdo se altera o setState
*/
