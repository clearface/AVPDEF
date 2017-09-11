package com.ibm.avp.def.test;

import com.ibm.json.java.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

public class MockEvents 
{
	static public final String KEY_NUMPROCS = "numberOfMockProcesses";
	static public final String KEY_MINEVENTS = "loNumberOfMockEvents";
	static public final String KEY_MAXEVENTS = "hiNumberOfMockEvents";
	static public final int DFLT_MINEVENTS = 2;
	static public final int DFLT_MAXEVENTS = 6;
	static public final int MIN_EVENTS = 2;
	static public final int MAX_EVENTS = 16;
	static public final String myClassname = "com.ibm.avp.def.test.MockEvents";
	
	static public final String hexAlphabet = "0123456789abcdef";
	static public final String CHAR_DASH = "-";
	
	// event types
	static public final String ETYPE_PROCSTART = "PROCESS_STARTED";
	static public final String ETYPE_PROCCOMPLETE = "PROCESS_COMPLETED";
	static public final String ETYPE_ACTREADY = "ACTIVITY_READY";
	static public final String ETYPE_ACTCOMPLETE = "ACTIVITY_COMPLETED";
	static public final String ETYPE_EVENTTHROW = "EVENT_THROWN";
	static public final String ETYPE_EVENTCATCH = "EVENT_CAUGHT";
	static public final String ETYPE_GWACTIVATE = "GATEWAY_ACTIVATED";
	static public final String ETYPE_GWCOMPLETE = "GATEWAY_COMPLETED";
	static protected String[] randomTypes = new String[] {
		ETYPE_ACTREADY, ETYPE_ACTCOMPLETE, ETYPE_EVENTTHROW, 
		ETYPE_EVENTCATCH, ETYPE_GWACTIVATE, ETYPE_GWCOMPLETE
	};
	
	static public final SimpleDateFormat tsFormatter = 
		      new SimpleDateFormat ("yyyy-MM-ddTHH:mm:ss-SSS zzz");
	
	static  public void populateEventsManager (RestApi_EventsManager inManager)
	{
		int numprocesses = Utils.getIntProperty (KEY_NUMPROCS);
		if (numprocesses <= 0) {
			Utils.logString(myClassname + ".populateEventsManager()", 
				KEY_NUMPROCS + "=" + Integer.toString(numprocesses), 0);
			return;
		}
		
		Random generator = new Random(System.currentTimeMillis() + 10510);
		//start 4 hours ago
		long eventtime = System.currentTimeMillis() - 14400000;
		long eventmaxmillis = 300000;
		
		int loevents = Utils.getIntProperty (KEY_MINEVENTS);
		if ((loevents < MIN_EVENTS) || (loevents > MAX_EVENTS))
			loevents = DFLT_MINEVENTS;
		int hievents = Utils.getIntProperty (KEY_MAXEVENTS);
		if (hievents < loevents)
			hievents = (DFLT_MAXEVENTS >= loevents ? DFLT_MAXEVENTS : loevents);
		else
		if (hievents > MAX_EVENTS)
			hievents = DFLT_MAXEVENTS;
		
		JSONObject thisevent = null; 
		for (int procscount = 0; procscount < numprocesses; procscount++) {
			String procid = generateNewProcessId();
			long procstart = eventtime;
			int numevents = generator.nextInt(hievents - loevents) + loevents;
			long procend = procstart + (numevents * eventmaxmillis);
			for (int eventscount = 0; eventscount < numevents; eventscount++) {
				eventtime += generator.nextInt((int)eventmaxmillis);
				thisevent = new JSONObject();
				// process id
				thisevent.put (DEFEvent.FLD_PROCESSID, procid);
				// event id
				thisevent.put (DEFEvent.FLD_EVENTID, generateNewEventId());
				// event type
				switch (eventscount) {
				case 0:
					thisevent.put (DEFEvent.FLD_EVENTTYPE, ETYPE_PROCSTART);
					thisevent.put (DEFEvent.FLD_TIMESTAMP, tsFormatter.format(new Date(procstart)));
					break;
				case 1:
					thisevent.put (DEFEvent.FLD_EVENTTYPE, ETYPE_PROCCOMPLETE);
					thisevent.put (DEFEvent.FLD_TIMESTAMP, tsFormatter.format(new Date(procend)));
					break;
				default:
					int aindex = generator.nextInt(randomTypes.length);
					thisevent.put (DEFEvent.FLD_EVENTTYPE, randomTypes[aindex]);
					thisevent.put (DEFEvent.FLD_TIMESTAMP, tsFormatter.format(new Date(eventtime)));
					break;
				}
			}
		}
	}
	
	public static String generateNewProcessId() {
		StringBuffer strbuf = new StringBuffer(36);
		Random generator = new Random(System.currentTimeMillis());
		int alphalength = hexAlphabet.length();
		int count = 0;
		int numchars = 12;
		for (count = 0; count < numchars; count++) {
			strbuf.append (hexAlphabet.charAt(generator.nextInt(alphalength)));
		}
		strbuf.append(CHAR_DASH);
		numchars = 4;
		for (count = 0; count < numchars; count++) {
			strbuf.append (hexAlphabet.charAt(generator.nextInt(alphalength)));
		}
		strbuf.append(CHAR_DASH);
		for (count = 0; count < numchars; count++) {
			strbuf.append (hexAlphabet.charAt(generator.nextInt(alphalength)));
		}
		strbuf.append(CHAR_DASH);
		for (count = 0; count < numchars; count++) {
			strbuf.append (hexAlphabet.charAt(generator.nextInt(alphalength)));
		}
		strbuf.append(CHAR_DASH);
		numchars = 8;
		for (count = 0; count < numchars; count++) {
			strbuf.append (hexAlphabet.charAt(generator.nextInt(alphalength)));
		}
		return strbuf.toString();
	}
	
	public static String generateNewEventId() {
		StringBuffer strbuf = new StringBuffer(26);
		Random generator = new Random(System.currentTimeMillis() + 111557);
		int alphalength = hexAlphabet.length();
		int count = 0;
		int numchars = 26;
		for (count = 0; count < numchars; count++) {
			strbuf.append (hexAlphabet.charAt(generator.nextInt(alphalength)));
		}
		return strbuf.toString();
	}
}
