package com.ibm.avp.def.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

import commonj.sdo.DataObject;

@Path(value="/events")
public class RestApi_Events
{
	protected static final String myClassname = "com.ibm.avp.def.test.RestApi_Events";
	
	// properties key
	public static final String KEY_EVENTSDEFAULTTYPE = "REST.Events.DefaultType";
	
	// JSON keys
	public static final String KEY_CORRELATIONID = RestApi_EventsManager.KEY_CORRELATIONID;
	public static final String KEY_PROCESSID = "processId";
	public static final String KEY_NUMPROCESSES = "numberOfProcesses";
	public static final String KEY_PROCESSIDS = "processIds";
	public static final String KEY_NUMEVENTSPERPROCID = "numberOfEvents_perProcessId";
	public static final String KEY_NUMEVENTS = RestApi_EventsManager.KEY_NUMEVENTS;
	public static final String KEY_EVENTSDATA = RestApi_EventsManager.KEY_EVENTSDATA;
	public static final String KEY_EVENTID = RestApi_EventsManager.KEY_EVENTID;
	public static final String KEY_EVENTTIME = RestApi_EventsManager.KEY_EVENTTIME;
	public static final String KEY_EVENTTYPE = RestApi_EventsManager.KEY_EVENTTYPE;
	
	public RestEventsImpl() { 
		Utils.logEnter (myClassname + " : constructor");
	}
	
	@GET
  @Produces(value="text/plain")
  public String getTopLevelData() {
		Utils.logEnter (myClassname + ".getTopLevelData()");
		
		RestApi_EventsManager eventsmgr = RestApi_EventsManager.getEventsManager();
		Map<String,SortedSet<DEFEvent>> allevents = eventsmgr.getAllDefEventsOfTypeAsSDO (DEFEventFactory.TYPE_SLIM);
		
		JSONObject jsonmap = new JSONObject();
		if (allevents == null)
			jsonmap.put(KEY_NUMPROCESSES, "null");
		else {
			JSONArray procevents = new JSONArray();
			JSONArray procidslist = new JSONArray();
			JSONObject pidevent = null;
			Set<String> procids = allevents.keySet();
			int numevents = 0;
			String thiskey;
			SortedSet<DEFEvent> thisset;
			Iterator<String> mit = procids.iterator();
			while (mit.hasNext()) {
				thiskey = mit.next();
				thisset = allevents.get(thiskey);
				procidslist.add(thiskey);
				pidevent = new JSONObject();
				pidevent.put(KEY_PROCESSID, thiskey);
				pidevent.put(KEY_NUMEVENTS, thisset.size());
				procevents.add(pidevent);
				numevents += thisset.size();
			}
			
			jsonmap.put(KEY_NUMPROCESSES, Integer.toString(procids.size()));
			jsonmap.put(KEY_NUMEVENTS, Integer.toString(numevents));
			jsonmap.put(KEY_PROCESSIDS, procidslist);
			jsonmap.put(KEY_NUMEVENTSPERPROCID, procevents);
		}

		String returnstring = "no results";
		try {
			returnstring = jsonmap.serialize();
		}
		catch (IOException ex) {
			returnstring = "IOException on JSON.serialize(): " + ex.getMessage();
			Utils.logString(myClassname + ".getTopLevelData()", returnstring, 0);
		}
		
		Utils.logExit (myClassname + ".getTopLevelData()");
		
		return returnstring;
  }

  @GET
  @Produces(value="text/plain")
  @Path(value="{id}")
  public String getEventsDataForId (@PathParam("id") String inCorrelationId) {
    	Utils.logEnter (myClassname + ".getEventsDataForId(" + inCorrelationId + ")");
    	String dflttype = Utils.getProperty(KEY_EVENTSDEFAULTTYPE);
    	return getEventsDataOfTypeForId (dflttype, inCorrelationId);
  }
    
  protected String getEventsDataOfTypeForId (String inDataType, String inCorrelationId) {
    Utils.logEnter (myClassname + ".getEventsDataOfTypeForId(" + inDataType + ", " + inCorrelationId + ")");
		
		RestApi_EventsManager eventsmgr = RestApi_EventsManager.getEventsManager();
		JSONArray eventslist = eventsmgr.getDefEventsOfTypeForCorrelationIdAsJSON (inDataType, inCorrelationId);

		JSONObject returnmap = new JSONObject();
		returnmap.put(KEY_CORRELATIONID, inCorrelationId);
		returnmap.put(KEY_NUMEVENTS, (eventslist == null ? "null" : Integer.toString(eventslist.size())));
		if (eventslist == null)
			returnmap.put(KEY_EVENTSDATA, "null");
		else 
			returnmap.put(KEY_EVENTSDATA, eventslist);

		String returnstring = "no results";
		try {
			returnstring = returnmap.serialize();
		}
		catch (IOException ex) {
			returnstring = "IOException on JSON.serialize(): " + ex.getMessage();
			Utils.logString(myClassname + ".getEventsDataForId()", returnstring, 0);
		}
		
		Utils.logExit (myClassname + ".getEventsDataOfTypeForId()");
		
		return returnstring;
  }
}
