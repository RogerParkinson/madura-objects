package nz.co.senanque.pizzaorder.factories;

import nz.co.senanque.rules.decisiontable.Column;
import nz.co.senanque.rules.decisiontable.Row;
import nz.co.senanque.rules.factories.DecisionTableFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component("SizesToppingsDecisionTable")
public class SizesToppingsDecisionTable implements DecisionTableFactory {

	private final Logger logger = LoggerFactory.getLogger(SizesToppingsDecisionTable.class);

	public SizesToppingsDecisionTable() {
		logger.debug("");
	}

	public Row[] getRows(String ruleName) {
		Row[] ret = new Row[] {
				new Row(new Column[] { new Column("Seafood"), new Column("Small") }),
				new Row(new Column[] { new Column("Italian"), new Column("Medium") }),
				new Row(new Column[] { new Column("Spanish"), new Column("Medium") }),
				new Row(new Column[] { new Column("Hawaiian"), new Column("Large") }),
				new Row(new Column[] { new Column("Greek"), new Column("Large") }),
				new Row(new Column[] { new Column("Turkish"), new Column("Small") }),
				new Row(new Column[] { new Column("Turkish"), new Column("Medium") }),
				new Row(new Column[] { new Column("Turkish"), new Column("TrulyVast") }) 
		};						
						
		return ret;
	}

}
