package org.internetresources.util;

import java.util.ArrayList;
import java.util.List;

public class PropertiesHelper {
    /**
     * get a string property's value
     * 
     * @param propKey
     *            property key
     * @param defaultValue
     *            default value if the property is not found
     * @return value
     */
    public static String getSystemStringProperty(String propKey,
            String defaultValue) {
        String strProp = System.getProperty(propKey);
        if (strProp == null) {
            strProp = defaultValue;
        }
        return strProp;
    }

    /**
     * internal recursive method to get string properties (array)
     * 
     * @param curResult
     *            current result
     * @param paramName
     *            property key prefix
     * @param i
     *            current indice
     * @return array of property's values
     */
    private static List<String> getSystemStringProperties(
            List<String> curResult, String paramName, int i) {
        String paramIValue = getSystemStringProperty(
                paramName + "." + String.valueOf(i), null);
        if (paramIValue == null) {
            return curResult;
        }
        curResult.add(paramIValue);
        return getSystemStringProperties(curResult, paramName, i + 1);
    }

    /**
     * get the values from a property key prefix
     * 
     * @param paramName
     *            property key prefix
     * @return string array of values
     */
    public static String[] getSystemStringProperties(String paramName) {
        List<String> stringProperties = getSystemStringProperties(
                new ArrayList<String>(), paramName, 0);
        return stringProperties.toArray(new String[stringProperties.size()]);
    }

    public static Boolean getSystemBooleanProperty(String propKey,
            Boolean defaultValue) {
        String strProp = System.getProperty(propKey);
        if (strProp == null) {
            return defaultValue;
        }
        return Boolean.valueOf(strProp);
    }
}
