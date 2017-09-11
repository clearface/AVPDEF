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
	protected static Map<String,SortedSet<DEFEvent>> eventsCollections = new HashMap<String,SortedSet<DEFEvent>>();

	public static synchronized RestApi_EventsManager getEventsManager() {
    if (emInstance == null)
      emInstance = new RestApi_EventsManager();
    return emInstance;
  }
  
	public RestApi_EventsManager() { }

	public void addEvent (DEFEvent inEvent) {
		Utils.logEnter(myClassname + ".addEvent()");
		String correlationId = (inEvent == null ? null : inEvent.getCorrelationId());
		addEventWithCorrelationId (inEvent, correlationId);
	}
	
	public void addEventWithCorrelationId (DEFEvent inEvent, String inCorrelationId) {
		Utils.logEnter(myClassname + ".addEventWithCorrelationId(" + inCorrelationId + ")");
		if (inEvent == null)
			Utils.logString(myClassname + ".addEventWithCorrelationId()", "Event is null", 0);
		else {
			String eventtype = inEvent.getEventType();
			if (eventtype == null)
				Utils.logString(myClassname + ".addEventWithCorrelationId()", "EventType is null", 0);
			else
			if (eventtype.isEmpty() || eventtype.equalsIgnoreCase(DEFEventFactory.TYPE_UNDEFINED))
				Utils.logString(myClassname + ".addEventWithCorrelationId()", 
					"EventType undefined <" + eventtype + ">", 0);
			else
				addEventOfTypeWithCorrelationId (inEvent, eventtype, inCorrelationId);
		}
		Utils.logExit(myClassname + ".addEventWithCorrelationId(" + inCorrelationId + ")");
	}
	
	public void addEventOfTypeFromXML (String inEventType, String inEventXML) {
		Utils.logEnter(myClassname + ".addEventOfTypeFromXML()");
		DEFEvent defevent = DEFEventFactory.createEventOfTypeFromXML (inEventType, inEventXML);
		String correlationid = defevent.getCorrelationId();
		addEventWithCorrelationId (defevent, correlationid);
	}
	
	public void addEventOfTypeFromXMLwithCorrelationId (String inEventType, String inEventXML, String inCorrelationId) {
		Utils.logEnter(myClassname + ".addEventOfTypeFromXMLwithCorrelationId(" + inCorrelationId + ")");
		DEFEvent defevent = DEFEventFactory.createEventOfTypeFromXML (inEventType, inEventXML);
		addEventWithCorrelationId (defevent, inCorrelationId);
	}

	/** type = DEFEventFactory.TYPE_*; return map keyed on correlationid */
	public Map<String,SortedSet<DEFEvent>> getAllDefEventsOfType (String inEventType) 
	{
		Utils.logEnter(myClassname + ".getAllDefEventsOfType(" + inEventType + ")");
		Map<String,SortedSet<DEFEvent>> typemap = eventsCollections.get(inEventType);
		if (typemap == null)
			Utils.logString(myClassname + ".getAllDefEventsOfType()", 
				"No events of type <" + inEventType + ">", 1);
		return typemap;
	}

	/** return a json list with of eventmap objects with keys = eventid, eventxml */
	public JSONArray getDefEventsOfTypeForCorrelationIdAsJSON (String inEventType, String inCorrelationId) 
	{
		Utils.logEnter(myClassname + ".getDefEventsOfTypeForCorrelationIdAsJSON(" + 
			inCorrelationId + ", " + inEventType + ")");
		
		// if returning type INFO then we compose that here, it isn't in a collection
		boolean isinfotype = inEventType.equalsIgnoreCase(DEFEventFactory.TYPE_INFO);
		String eventtype = (isinfotype ? DEFEventFactory.TYPE_SLIM : inEventType);

		JSONArray returnevents = new JSONArray();
		SortedSet<DEFEvent> eventslist = null;
		Map<String,SortedSet<DEFEvent>> typemap = eventsCollections.get(eventtype);
		if (typemap == null)
			Utils.logString(myClassname + ".getDefEventsOfTypeForCorrelationIdAsSDO()", 
				"No events of type <" + eventtype + ">", 1);
		else {
			eventslist = typemap.get(inCorrelationId);
			if ((eventslist == null) || eventslist.isEmpty())
				Utils.logString(myClassname + ".getDefEventsOfTypeForCorrelationIdAsSDO()", 
					"No events of type <" + eventtype + "> for correlationId=" + inCorrelationId, 1);
			else {
				DEFEvent thisevent = null;
				JSONObject thisobj = null;
				Iterator<DEFEvent> ssit = eventslist.iterator();
				while (ssit.hasNext()) {
					thisevent = ssit.next();
					thisobj = new JSONObject();
					thisobj.put (KEY_EVENTID, thisevent.getEventId());
					if (isinfotype) {
						thisobj.put (KEY_EVENTTIME, thisevent.getStringProperty(DEFEvent_PFGSlim.FLD_TIMESTAMP));
						thisobj.put (KEY_EVENTTYPE, thisevent.getStringProperty(DEFEvent_PFGSlim.FLD_EVENTTYPE));
					}
					else
						thisobj.put (KEY_EVENTDATA, thisevent.getXML());
					returnevents.add(thisobj);
				}
			}
		}
		
		Utils.logExit(myClassname + ".getDefEventsOfTypeForCorrelationIdAsJSON(" + inCorrelationId + ")");
		return returnevents;
	}
	
	public int getCurrentLogLevel() {
		return Utils.getCurrentLogLevel();
	}
  
	/* share with global runtime */
	public void setCurrentLogLevel(int inLevel) {
		Utils.setCurrentLogLevel (inLevel);
	}
	
	/* Every addEvent() variation eventually comes here, synchronizes access to eventsCollections;
	 * this is the only place that eventsCollections is inserted to;
	 * also shares the event with EventsPersister after its inserted */
	protected void addEventOfTypeWithCorrelationId (DEFEvent inEvent, String inType, String inCorrelationId)
	{
		Utils.logEnter(myClassname + ".addEventOfTypeWithCorrelationId(" + inCorrelationId + ")");
		
		// add this event to a set of events with same correlationId
		SortedSet<DEFEvent> eventslist = null;
		
		// synchronize access to the events collection and create/insert of contents
		synchronized (eventsCollections) {
			Utils.logEnter(myClassname + ".addEventOfTypeWithCorrelationId(" + inCorrelationId + ") {synchronized block}");
			
			// get the collection of events for this event type; create it if necessary
			Map<String,SortedSet<DEFEvent>> typemap = eventsCollections.get(inType);
			if (typemap == null) {
				typemap = new HashMap<String,SortedSet<DEFEvent>>();
				eventsCollections.put (inType, typemap);
			}
			else
				eventslist = typemap.get(inCorrelationId);
			
			// create the set for this correlationId if necessary
			if (eventslist == null) {
				eventslist = new TreeSet<DEFEvent>();
				typemap.put(inCorrelationId, eventslist);
			}
			Utils.logExit(myClassname + ".addEventOfTypeWithCorrelationId(" + inCorrelationId + ") {synchronized block}");
		}
			
		// add this event to the set for the correlationId
		Utils.logString(myClassname + ".addEventOfTypeWithCorrelationId(" + inCorrelationId + ")", "add DEFEvent to set", 2);
		eventslist.add(inEvent);
		
		// share with persister
		Utils.logString(myClassname + ".addEventOfTypeWithCorrelationId(" + inCorrelationId + ")", "share with EventsPersister", 2);
		EventsPersister.processEvent(inEvent);
		
		Utils.logExit(myClassname + ".addEventOfTypeWithCorrelationId(" + inCorrelationId + ")");
	}
}
