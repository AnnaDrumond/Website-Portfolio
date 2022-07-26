import messages from "../../translations/langSupport";
import React from "react";
import {
	IntlProvider,
	FormattedMessage,
	FormattedDate,
	FormattedTime,
} from "react-intl";

function Card(props) {
	
	if (props.typeCard === "number") {
		return (
			<div className="card">
				<IntlProvider
					locale={props.language}
					messages={messages[props.language]}
				>
					<div>
						<FormattedMessage id={props.text} />
						&#128203;
						<p className="value">{props.valueCard}</p>
					</div>
				</IntlProvider>
			</div>
		);
	} else {
		return (
			<div className="card">
				<IntlProvider
					locale={props.language}
					messages={messages[props.language]}
				>
					<div>
						<FormattedMessage id={props.text} />
						&#128198;
						<br />
						<FormattedDate value={new Date(props.valueCard)} />
						&emsp;
						<FormattedTime value={new Date(props.valueCard)} />
					</div>
				</IntlProvider>
			</div>
		);
	}
}
export default Card;

// pesquisar render condicional
