import React from "react";
import { IntlProvider, FormattedMessage } from "react-intl";
import { backToMainPage } from "../../uriSupport";
import messages from "../../translations/langSupport";
import { useSelector } from "react-redux";
import { useState } from "react";
import store from "../../redux/store/myStore";
import { updateLanguage } from "../../redux/actions/actions";

function Menu() {
	const langState = useSelector((state) => {
		return state.dataIdiom.lang.language;
	});

	// Defino que o valor inicial é o idioma que veio da URI
	let [locale, setLocale] = useState(langState);

	// Cada vez que mudo o idioma, faço o dispatch da ação para atualizar a store com o novo idioma escolhido
	const handleSelect = (e) => {
		store.dispatch(
			updateLanguage({
				language: e.target.value,
			}),
		);

		setLocale(e.target.value);
	};

	//	console.log("estou em Menu e trouxe o idioma  ---------- " + locale);
	console.log(langState);
	return (
		<div>
			<nav className="nav-bar">
				<ul className="nav-bar-list changeStyle">
					<div className="buttons-nav-bar">
						<li className="nav-bar-item">
							<IntlProvider locale={locale} messages={messages[locale]}>
								<button className="button-navbar" onClick={backToMainPage}>
									<FormattedMessage id={"return"} />
								</button>
							</IntlProvider>
						</li>
					</div>
					<li className="nav-bar-item nav-bar-item-languages">
						<div className="div-languages">
							<select
								onChange={handleSelect.this.bind(this)}
								defaultValue={locale}
								className="languages"
							>
								<option key={"EN"}>{"EN"}</option>
								<option key={"PT"}>{"PT"}</option>
							</select>
						</div>
					</li>
				</ul>
			</nav>
		</div>
	);
}
export default Menu;

//linhas 56/57 poderiam ser da forma abaixo também:
/**{["EN", "PT"].map((lang) => (
									<option key={lang}>{lang}</option>
								))} */
