import React from "react";
import messages from "../../translations/langSupport";
import { IntlProvider, FormattedMessage, FormattedDate, FormattedDateParts, FormattedTime } from "react-intl";

function CardDate(props) {
	return (
		<div className="card">
			<IntlProvider locale={props.language} messages={messages[props.language]}>
				<div>
					<FormattedMessage id={props.text} />
                    &#128198;
                    <br />
                    <FormattedDate value={(new Date(props.valueCard))}/>
                    &emsp;
                    <FormattedTime value={(new Date(props.valueCard))}/>	
				</div>
			</IntlProvider>
		</div>
	);
}
export default CardDate;