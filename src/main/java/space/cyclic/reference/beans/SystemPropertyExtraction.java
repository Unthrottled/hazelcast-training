package space.cyclic.reference.beans;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class SystemPropertyExtraction {

    public String getProperty(String validSystemProperty) {
        String toReturn = "";
        if (validSystemProperty != null && !validSystemProperty.isEmpty()){
            toReturn = System.getProperty(validSystemProperty);
            if (toReturn.equalsIgnoreCase("null")) toReturn = "";
        }
        return toReturn;
    }
}
