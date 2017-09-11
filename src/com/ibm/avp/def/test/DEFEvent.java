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
implements Comparable<DEFEvent>, Serializable
{
	protected static String logClassname = "com.ibm.avp.def.test.DEFEvent";
	// Serializable
	private static final long serialVersionUID = -247600350809632303L;
	
	protected JSONObject eventJSON = null;
  
  // fields used in this data structure
	protected static final String FLD_PROCESSID = "processId";
	protected static final String FLD_EVENTSEQ = "eventSequenceId";
	protected static final String FLD_STEPCOUNTER = "stepCounter";
	protected static final String FLD_TIMESTAMP = "timestamp";
	protected static final String FLD_EVENTTYPE = "eventType";
	protected static final String FLD_EVENTID = "eventId";

	public DEFEvent() { }
	
	public DEFEvent (JSONObject inEventAsJson) {
		Utils.logEnter(logClassname + ".constructor(JSON)");
		if (inEventAsJson == null)
			Utils.logEnter(logClassname + ".constructor(JSON is null)");
		else
		if (inEventAsJson.isEmpty())
			Utils.logEnter(logClassname + ".constructor(JSON is empty)");
		else
			eventJSON = inEventAsJson;
	}
	
	public DEFEvent (String inEventAsString) {
		Utils.logEnter(logClassname + ".constructor(XML)");
		if (inEventAsString == null)
			Utils.logEnter(logClassname + ".constructor(XML is null)");
		else
		if (inEventAsString.isEmpty())
			Utils.logEnter(logClassname + ".constructor(XML is empty)");
		else {
			StringReader myreader = new StringReader(inEventAsString);
			try {
				eventJSON = JSONObject.parse(myreader);
			}
			catch (IOException ex) {
				Utils.logException (logClassname + ".constructor()", ex, "IOException on JSONObject.parse()");
			}
		}
	}
	
	public JSONObject getJSON() { return eventJSON; }
	
	public String getEventType() { return "not implemented"; }
	public String getCorrelationId() { return "not implemented"; }
	public String getEventId() { return "not implemented"; }
	
	public int compareTo(DEFEvent otherEvent) {
		if (Utils.getCurrentLogLevel() > 1) {
			Utils.logString(logClassname + ".compareTo()", "This => " + toDebugString(), 2);
			Utils.logString(logClassname + ".compareTo()", "Other => " + otherEvent.toDebugString(), 2);
		}
		
		int returnvalue = 0;
		if (otherEvent == null)
			returnvalue =  1;
		else
			returnvalue =  compareDetails(otherEvent);
		
		Utils.logExit(logClassname + ".compareTo() = " + Integer.toString(returnvalue));
		return returnvalue;
	}
	
	/* this is implementation for Comparable.compare(), other event passed in should be non-null/empty */
	protected int compareDetails (DEFEvent otherEvent)
	{
		Utils.logEnter(logClassname + ".compareDetails()");
		
		// compare that correlation id's are equal, so are from same process
		int comparison = compareProperty (otherEvent, FLD_PROCESSID);
		Utils.logString(logClassname + ".compareDetails()", FLD_PROCESSID + "=" + Integer.toString(comparison), 2);
		if (comparison != 0)
			return comparison;
		
		// compare event sequences
		comparison = compareProperty (otherEvent, FLD_EVENTSEQ);
		Utils.logString(logClassname + ".compareDetails()", "eventSequenceId=" + Integer.toString(comparison), 2);
		if (comparison != 0)
			return comparison;
		
		//compare activity sequences
		comparison = compareProperty (otherEvent, FLD_STEPCOUNTER);
		Utils.logString(logClassname + ".compareDetails()", "stepCounter=" + Integer.toString(comparison), 2);
		if (comparison != 0)
			return comparison;
		
		//compare time stamps
		comparison = compareProperty (otherEvent, FLD_TIMESTAMP);
		Utils.logString(logClassname + ".compareDetails()", "timestamp=" + Integer.toString(comparison), 2);
		if (comparison != 0)
			return comparison;
		
		//compare the event types
		comparison = compareProperty (otherEvent, FLD_EVENTTYPE);
		Utils.logString(logClassname + ".compareDetails()", "type=" + Integer.toString(comparison), 2);
		if (comparison != 0)
			return comparison;
		
		// else return 1
		Utils.logString(logClassname + ".compareDetails()", "unable to differentiate the events; returning 1", 0);
		return 1;
	}
	
	/* utility method returns 0 if the properties are equal;
	 * caller should guarantee non-null and ready() for the other event */
	protected int compareProperty (DEFEvent otherEvent, String inProperty)
	{
		Utils.logEnter(logClassname + ".compareProperty(" + inProperty + ")");
		String otherproperty = otherEvent.getStringProperty(inProperty);
		if ((otherproperty == null) || otherproperty.isEmpty()) {
			Utils.logString(logClassname + ".compareProperty(" + inProperty + ")", 
				("otherproperty " + (otherproperty==null?"null":"empty")) + "; return 1", 2);
			return 1;
		}
		else {
			String myproperty = getStringProperty(inProperty);
			if ((myproperty == null) || myproperty.isEmpty()) {
				Utils.logString(logClassname + ".compareProperty(" + inProperty + ")", 
					("myproperty " + (myproperty==null?"null":"empty")) + "; return -1", 2);
				return -1;
			}
			else {
				int returnval = myproperty.compareToIgnoreCase(otherproperty);
				Utils.logString(logClassname + ".compareProperty(" + inProperty + ")", 
					"compare " + myproperty + " vs " + otherproperty + "; return " + Integer.toString(returnval), 2);
				return returnval;
			}
		}
	}
	
	public String getStringProperty (String inKey) {
		if (eventJSON == null)
			return null;
		else
			return (String)(eventJSON.get(inKey));
	}
	
	protected String toDebugString() {
		StringBuffer strbuf = new StringBuffer();
		strbuf.append (logClassname + " = [");
		if (Utils.getCurrentLogLevel() < 2) {
			strbuf.append ("processId: " + getStringProperty(FLD_PROCESSID) + " | ");
			strbuf.append ("eventSequenceId: " + getStringProperty(FLD_EVENTSEQ) + " | ");
			strbuf.append ("stepCounter: " + getStringProperty(FLD_STEPCOUNTER) + " | ");
			strbuf.append ("timestamp: " + getStringProperty(FLD_TIMESTAMP) + " | ");
			strbuf.append ("eventType: " + getStringProperty(FLD_EVENTTYPE));
		}
		else
			try { strbuf.append("\n" + eventJSON.serialize() + "\n"); }
			catch (IOException ex) {
				strbuf.append ("IOException on JSON.serialize(): " + ex.toString());
			}
		
		strbuf.append("]");
		return strbuf.toString();
	}
}
