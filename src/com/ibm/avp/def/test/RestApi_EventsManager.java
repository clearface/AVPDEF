package com.ibm.avp.def.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import com.ibm.json.java.JSONArray;
import com.ibm.json.java.JSONObject;

/**
 * Manages events for collection and persistence.
 * Manages a runtime collection of events, grouped by correlation id.
 */
public class RestApi_EventsManager 
{
  protected static RestApi_EventsManager emInstance = null;
  protected static final String myClassname = "com.ibm.avp.def.test.RestApi_EventsManager";
  
	// JSON map keys
	public static final String KEY_CORRELATIONID = "correlationId";
	public static final String KEY_NUMEVENTS = "numberOfEvents";
	public static final String KEY_EVENTIDS = "eventIds";
	public static final String KEY_EVENTSDATA = "eventsData";
	public static final String KEY_EVENTID = "eventId";
	public static final String KEY_EVENTDATA = "eventData";
	public static final String KEY_EVENTTIME = "eventTime";
	public static final String KEY_EVENTTYPE = "eventType";
	
	/* maintain a map of events objects collections, keyed by event platform type;
	 * each type has its own map of collections of DEFEvent instances, keyed on a correlation id, 
	 * sorted best effort in order.
	 * A platform-specific subclass will maintain its map/collection of platform events. */
	protected static Map<String,SortedSet<DEFEvent>> myDefEvents = new HashMap<String,SortedSet<DEFEvent>>();

	public static synchronized RestApi_EventsManager getEventsManager() {
		if (emInstance == null) {
			emInstance = new RestApi_EventsManager();
			MockEvents.populateEventsManager(emInstance);
		}
		return emInstance;
	}
  
	public RestApi_EventsManager() { 
		
	}

	public void addEvent (DEFEvent inEvent) {
		Utils.logEnter(myClassname + ".addEvent()");
		String correlationId = (inEvent == null ? null : inEvent.getCorrelationId());
		addEventWithCorrelationId (inEvent, correlationId);
	}
	
	public void addEventWithCorrelationId (DEFEvent inEvent, String inCorrelationId) {
		Utils.logEnter(myClassname + ".addEventWithCorrelationId(" + inCorrelationId + ")");
		if (inEvent == null)
			Utils.logString(myClassname + ".addEventWithCorrelationId()", "Event is null", 0);
		else
			addEvent (inEvent, inCorrelationId);
		
		Utils.logExit(myClassname + ".addEventWithCorrelationId(" + inCorrelationId + ")");
	}

	/** return map keyed on correlationid */
	public Map<String,SortedSet<DEFEvent>> getAllDefEvents() 
	{
		return myDefEvents;
	}

	/** return map keyed on correlationid */
	public SortedSet<DEFEvent> getDefEventsForCorrelationId (String inId) 
	{
		return myDefEvents.get(inId);
	}
	
	public int getCurrentLogLevel() {
		return Utils.getCurrentLogLevel();
	}
  
	/* share with global runtime */
	public void setCurrentLogLevel(int inLevel) {
		Utils.setCurrentLogLevel (inLevel);
	}
	
	protected void addEvent (DEFEvent inEvent, String inCorrelationId)
	{
		Utils.logEnter(myClassname + ".addEvent (" + inCorrelationId + ")");
		
		// add this event to a set of events with same correlationId
		SortedSet<DEFEvent> eventslist = null;
		
		// synchronize access to the events collection and create/insert of contents
		synchronized (myDefEvents) {
			Utils.logEnter(myClassname + ".addEvent (" + inCorrelationId + ") {synchronized block}");
			
			eventslist = myDefEvents.get(inCorrelationId);
			
			// create the set for this correlationId if necessary
			if (eventslist == null) {
				eventslist = new TreeSet<DEFEvent>();
				myDefEvents.put(inCorrelationId, eventslist);
			}
			Utils.logExit(myClassname + ".addEvent(" + inCorrelationId + ") {synchronized block}");
		}
			
		// add this event to the set for the correlationId
		Utils.logString(myClassname + ".addEvent (" + inCorrelationId + ")", "add DEFEvent to set", 2);
		eventslist.add(inEvent);
		
		Utils.logExit(myClassname + ".addEvent (" + inCorrelationId + ")");
	}
}
