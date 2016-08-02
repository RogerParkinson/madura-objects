package nz.co.senanque.schemaparser.restrictions;

import java.util.List;

import org.jdom.Element;

public class RestrictionFactory {
	
	public static Restrictions getRestrictions(Element e1) {
        if (e1.getName().equals("restriction"))
        {
            String type = e1.getAttributeValue("base");
    		Restrictions ret = new Restrictions(type);
        	for (Element restriction: (List<Element>)e1.getChildren()) {
        		if (restriction.getName().equals("maxLength")) {
        			ret.addRestriction(new MaxLength(restriction));
        			continue;
        		}
        		if (restriction.getName().equals("length")) {
        			ret.addRestriction(new Length(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("maxExclusive")) {
        			ret.addRestriction(new MaxExclusive(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("minExclusive")) {
        			ret.addRestriction(new MinExclusive(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("maxInclusive")) {
        			ret.addRestriction(new MaxInclusive(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("minInclusive")) {
        			ret.addRestriction(new MinInclusive(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("fractionDigits")) {
        			ret.addRestriction(new FractionDigits(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("totalDigits")) {
        			ret.addRestriction(new TotalDigits(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("pattern")) {
        			ret.addRestriction(new Pattern(restriction));
        			continue;
        		}
           		if (restriction.getName().equals("enumerate")) {
        			ret.addEnumerate(new Enumerate(restriction));
        			continue;
        		}
    			
        	}
        	return ret;
        }
        return null;

	}

}
