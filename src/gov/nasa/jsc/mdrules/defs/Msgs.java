package gov.nasa.jsc.mdrules.defs;

import java.util.HashMap;
import java.util.Map;

public class Msgs {

	static Map<String, String> msgs;
	static public Map<String, String> getMsgs() {
		return msgs;
	}

	public static final String OPD_DEMO_STARTUP_COMPLETE = "OPD_DEMO_STARTUP_COMPLETE";
	 
	static
    {
        msgs = new HashMap<String, String>();   
        msgs.put(OPD_DEMO_STARTUP_COMPLETE, "OPD demo startup complete");
    }
	
}
