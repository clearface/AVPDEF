package com.ibm.avp.def.test;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.StringReader;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

public class DEFEvent 
{
	protected static String logClassname = "com.ibm.avp.def.test.DEFEvent";
	
	protected JSONObject eventJSON = null;
  
  // fields used in this data structure
	protected static final String FLD_PROCESSID = "processId";
	protected static final String FLD_EVENTSEQ = "eventSequenceId";
	protected static final String FLD_STEPCOUNTER = "stepCounter";
	protected static final String FLD_TIMESTAMP = "timestamp";
	protected static final String FLD_EVENTTYPE = "eventType";

	public DEFEvent() { }
	
	public DEFEvent (String inEventAsString) {
		Utils.logEnter(logClassname + ".constructor(XML)");
    StringReader myreader = new StringReader(inEventAsString);
		eventJSON = JSONObject.inEventAsString;
	}
	
	public String getXML() { return eventXML; }
	
	public String getEventType() { return "not implemented"; }
	public String getCorrelationId() { return "not implemented"; }
	public String getEventId() { return "not implemented"; }
	
	public String getStringProperty (String inKey) {
		if (eventSDO == null)
			return null;
		else
			return eventSDO.getString(inKey);
	}
}
