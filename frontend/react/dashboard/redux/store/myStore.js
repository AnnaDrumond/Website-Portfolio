//O redux permite controlar os estados dos componentes em qualquer página do site
//indepente da página onde estou. Sem o redux, seria necessário passar o estado do
// componente através de props, de componente pra componente por todo o site/páginas.
//è uma biblioteca do JS separada do react. Faz controle dos estado globais.

// Se o estado tem mais de um "dono"/vai ser usado em mais de que um componente?
// O estado é manipulado por mais de um componente?
//As ações do user, em outros componentes causam efeito nos estados?
//Caso sim, têm-se de usar redux para controle destes estados.
//Por isso no proj 4, era importante usar na comboBox dos usernames.

import { createStore } from "redux";
import rootReducer from "../reducers/myCombineReducers";

export default createStore(rootReducer);

/*Redux é um controlador de estados geral para sua aplicação.

Compartilhar estados entre vários componentes diferentes se torna uma coisa muito fácil quando o utilizamos. 
O Redux é basicamente divido em 3 partes: store, reducers e actions.

A store

"store" é o nome dado pelo Facebook para o conjunto de estados da sua aplicação. 
Vamos pensar na store como um grande centro de informações, que possui disponibilidade 
para receber e entregar exatamente o que o seu componente requisita (seja uma função, 
    ou uma informação propriamente dita). A store é um objeto JavaScript que possui todos
 os estados dos seus componentes.


Os reducers

Cada dado da store deve ter o seu próprio reducer, por exemplo: o dado "user" teria o seu reducer, 
chamado  só para User. Um reducer é encarregado de lidar com todas as ações, como algum componente
 pedindo para alterar algum dado da store.


Actions

Actions são responsáveis por requisitar algo para um reducer. 
Elas devem ser sempre funções puras, dizendo de uma forma leiga, ou seja elas devem 
APENAS enviar os dados ao reducer, nada além disso. Disparar uma ação apenas levando 
os dados que você deseja enviar e o reducer vai receber e usar os dados.*/