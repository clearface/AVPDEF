package com.ibm.avp.def.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/* version history
	v1: First numbered version 09/11/2017.
*/

public class Utils 
{
	static final String logClassname = "com.ibm.avp.def.test.Utils";
	static final int myVersion = 1;
	static final String STR_PROPSDIRPATHVAR = "USER_INSTALL_ROOT";
	static final String STR_PROPSFILENAME = "/AVP_DEFEM.props";
	static final String KEY_LOGLEVEL = "logLevel";
  static final int maxLogLevel = 3;
  
	protected static Properties myProperties = new Properties();
	static int currentLogLevel = 0;
	
	static { 
		readProperties();
	}
	
	public static void logString (String inClassMethod, String inLogString, int inLogLevel)
	{
		if (inLogLevel <= currentLogLevel)
			System.out.println (inClassMethod + ": " + inLogString);
	}
	
	public static void logEnter (String inClassMethod) {
		logString (inClassMethod, "enter", 1);
	}
	
	public static void logExit (String inClassMethod) {
		logString (inClassMethod, "exit", 1);
	}
	
	public static int getCurrentLogLevel() { return currentLogLevel; }
  
	public static void setCurrentLogLevel (int inLevel) {
		if (inLevel < 0)
			currentLogLevel = 0;
		else
		if (inLevel > maxLogLevel)
			currentLogLevel = maxLogLevel;
		else
			currentLogLevel = inLevel;
		logString (logClassname + ".setCurrentLogLevel()", "Set new log level = " + Integer.toString(currentLogLevel), 0);
	}

	public static Map<String,String> getCopyOfProperties()
	{
		if (myProperties == null)
			return null;
		
		Map<String,String> returnmap = new HashMap<String,String>();
		if (!myProperties.isEmpty()) {
			String thiskey;
			String thisval;
			Enumeration<Object> menu = myProperties.keys();
			while (menu.hasMoreElements()) {
				thiskey = (String)(menu.nextElement());
				thisval = (String)myProperties.getProperty(thiskey);
				returnmap.put (thiskey,  thisval);
			}
		}
		
		return returnmap;
	}
	
	public static String getProperty (String inKey) {
		if (inKey == null)
			return null;
		else
		if (myProperties.containsKey(inKey))
			return (String)(myProperties.get(inKey));
		else {
			logString(logClassname + ".getProperty()", "unknown key=" + inKey, 0);
			return "";
		}
	}
	
	public static void setProperty (String inKey, String inValue) {
		logEnter (logClassname + ".setProperty(" + inKey + ", " + inValue + ")");
		if (inKey == null)
			logString (logClassname + ".setProperty()", "Key is null", 0);
		else {
			myProperties.setProperty(inKey, inValue);
			notifyReaders();
		}
	}
	
	public static void readProperties() 
	{
		logString (logClassname + ".readProperties()", "version = " + Integer.toString(myVersion), 0);
		
		setCurrentLogLevel(Integer.parseInt(myProperties.getProperty(KEY_LOGLEVEL)));
		if (currentLogLevel > 1) 
				printProperties();
		notifyReaders();
		logExit(logClassname + ".readProperties()");
	}
	
	/* notify implementers of IPropertiesConsumer that props are here */
	protected static void notifyReaders() {
		logEnter(logClassname + ".notifyReaders()");
	}
	
	protected static void printProperties() {
		if (myProperties == null)
			System.out.println (logClassname + ".printProperties(): Properties is null");
		else
		if (myProperties.isEmpty())
			System.out.println (logClassname + ".printProperties(): Properties is empty");
		else {
			Map<String,String> propsmap = new HashMap<String,String>();
			Enumeration<String> penum = (Enumeration<String>)(myProperties.propertyNames());
			String thisname;
			while (penum.hasMoreElements()) {
				thisname = penum.nextElement();
				propsmap.put (thisname, myProperties.getProperty(thisname));
			}
			printStringsMapToSystemout (propsmap, "Properties");
		}
	}
	
	public static void printStringsMapToSystemout (Map<String,String> inMap, String inLabel) {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append (logClassname + " | " + inLabel + ": { ");
		if (inMap == null)
			strbuf.append("map is null");
		else
		if (inMap.isEmpty())
			strbuf.append("map is empty");
		else {
			Iterator<String> mit = inMap.keySet().iterator();
			String thiskey, thisvalue;
			while (mit.hasNext()) {
				thiskey = mit.next();
				thisvalue = inMap.get(thiskey).toString();
				strbuf.append (thiskey + "=" + thisvalue);
				if (mit.hasNext())
					strbuf.append (" | ");
			}
		}
		strbuf.append (" }");
		System.out.println (strbuf.toString());
	}
	
	public static void printStringsListToSystemout (List<String> inList, String inLabel) {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append (logClassname + " | " + inLabel + ": { ");
		if (inList == null)
			strbuf.append("list is null");
		else
		if (inList.isEmpty())
			strbuf.append("list is empty");
		else {
			Iterator<String> lit = inList.iterator();
			while (lit.hasNext()) {
				strbuf.append (lit.next());
				if (lit.hasNext())
					strbuf.append (" | ");
			}
		}
		strbuf.append (" }");
		System.out.println (strbuf.toString());
	}
}
