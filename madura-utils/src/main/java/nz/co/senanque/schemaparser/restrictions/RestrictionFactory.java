package nz.co.senanque.schemaparser.restrictions;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Element;

public class RestrictionFactory {
	
	public static List<Restriction> getRestrictions(Element e1) {
        if (e1.getName().equals("restriction"))
        {
            String type = e1.getAttributeValue("base");
            List<Restriction> ret = new ArrayList<>();
        	for (Element restriction: (List<Element>)e1.getChildren()) {
        		if (restriction.getName().equals("maxLength")) {
        			ret.add(new MaxLength(restriction));
        			continue;
        		}
        		if (restriction.getName().equals("length")) {
        			ret.add(new Length(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("maxExclusive")) {
        			ret.add(new MaxExclusive(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("minExclusive")) {
        			ret.add(new MinExclusive(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("maxInclusive")) {
        			ret.add(new MaxInclusive(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("minInclusive")) {
        			ret.add(new MinInclusive(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("fractionDigits")) {
        			ret.add(new FractionDigits(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("totalDigits")) {
        			ret.add(new TotalDigits(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("pattern")) {
        			ret.add(new Pattern(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("enumeration")) {
        			ret.add(new Enumerate(restriction));
        			continue;
        		}
        	}
        	return ret;
        }
        return null;
	}
}
