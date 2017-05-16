package com.newrelic.gpo.coquette;

/* java core */
import javax.management.MBeanServer;
import javax.management.MBeanInfo;
import javax.management.MBeanAttributeInfo;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/* newrelic agent */
import com.newrelic.agent.Agent;
import com.newrelic.agent.HarvestListener;
import com.newrelic.agent.service.AbstractService;
import com.newrelic.agent.service.ServiceFactory;
import com.newrelic.agent.stats.StatsEngine;
import com.newrelic.api.agent.NewRelic;
import com.newrelic.agent.config.AgentConfig;
import com.newrelic.agent.config.AgentConfigListener;

public class JMXCoquette extends AbstractService implements HarvestListener {

	private int invocationCounter = 1;
	private CoquetteConfig coquetteConfig = null;
	
	public JMXCoquette() {
		
		super(JMXCoquette.class.getSimpleName());
		Agent.LOG.info("[JXMCoquette] Initializing Service Class.");
		

	} //JMXCoquette

	@Override
	public boolean isEnabled() {
		
		//always enabled - true --> configuration of agent provided through newrelic.yml file dynamically.
		return(true);
	} //isEnabled

	@Override
	public void afterHarvest(String _appName) {
		
		Agent.LOG.fine("[JMXCoquette] after harvest event start.");
		Agent.LOG.fine("[JMXCoquette] after harvest event end.");
	} //afterHarvest

	@Override
	public void beforeHarvest(String _appName, StatsEngine _statsEngine) {
		
		Agent.LOG.fine("[JMXCoquette] before harvest event start.");
		
		try {
		
			Agent.LOG.fine("[JMXCoquette] Processing harvest event for Agent.");
			
			if (coquetteConfig != null) {
				
				if (coquetteConfig.isEnabled()) {
					
					Agent.LOG.fine("[JMXCoquette] Enabled.");
					
					//execute interval counter should the frequency be equal to the counter
					if (coquetteConfig.getFrequency() == invocationCounter) {

						Agent.LOG.fine("[JMXCoquette] Matched invocation counter ... execute ops pending ....");
						if (coquetteConfig.getMode().equals("disco")) {
							
							Agent.LOG.fine("[JMXCoquette] Starting disco jmx harvest.");
							executeDisco();
							Agent.LOG.fine("[JMXCoquette] Finished disco jmx harvest.");
					
						} //if
						else if (coquetteConfig.getMode().equals("strict")) {
						
							Agent.LOG.fine("[JMXCoquette] Starting strict jmx harvest.");
							executeStrict();
							Agent.LOG.fine("[JMXCoquette] Finished strict jmx harvest.");
						
						} //else if
						else if (coquetteConfig.getMode().equals("promiscuous")) {
							
							Agent.LOG.fine("[JMXCoquette] Starting promiscuous jmx harvest.");
							executePromiscuous();
							Agent.LOG.fine("[JMXCoquette] Finished promiscuous jmx harvest.");
						
						} //else if
						else if (coquetteConfig.getMode().equals("open")) {
							
							Agent.LOG.fine("[JMXCoquette] Starting open jmx harvest.");
							executeOpen();
							Agent.LOG.fine("[JMXCoquette] Finished open jmx harvest.");
						} //else if
						else {
							
							Agent.LOG.info("[JMXCoquette] Unknown JMXCoquette configuration mode: " + coquetteConfig.getMode() + " must be one of strict, open, disco, or promiscuous.");
						} //else
						
						//reset the invocation counter to 1.
						invocationCounter = 1;
					} //if
					else {
						
						if (invocationCounter > coquetteConfig.getFrequency()) {
							
							Agent.LOG.fine("[JMXCoquette] Invocation counter was placed in an unexpected state. Resetting to 1. ");
							invocationCounter = 1;
							
						} //if
						else {
							
							invocationCounter++;
							Agent.LOG.fine("[JMXCoquette] Skipping the harvest. " + invocationCounter);
						} //else	
						
					} //else
					
				} //if
				else {
				
					Agent.LOG.fine("[JMXCoquette] Disabled.");
				} //else
				
			} //if

		} //try
		catch (java.lang.Exception _e) {
			
			Agent.LOG.error("[JMXCoquette] Error during harvest of JMX MBeans: " + _e.getMessage());
		} //catch
		
		Agent.LOG.fine("[JMXCoquette] before harvest event end.");
		
	} //beforeHarvest

	@Override
	protected void doStart() throws Exception {
		
		// Extension Service is running before Harvest Service is initialized
		// So this code waits 120 seconds, otherwise you could get NPE
		final JMXCoquette coquette = this;
		
		new java.util.Timer().schedule(
	    
			new java.util.TimerTask() {
	            
				@Override
	            public void run() {
	            
					ServiceFactory.getHarvestService().addHarvestListener(coquette);
					Agent.LOG.info("[JMXCoquette] The service has been registered to the harvest listener");		
	            }
	        },
	        
	        60000
		);
		
		ServiceFactory.getConfigService().addIAgentConfigListener(configListener);
		
		//load the coquette configuration
		getCoquetteConfig(null);
		
	} //doStart

	@Override
	protected void doStop() throws Exception {
	
		Agent.LOG.info("[JMXCoquette] The service is stopping.");	
	} //doStop

	@SuppressWarnings("unchecked")
	private void getCoquetteConfig(AgentConfig _agentConfig) {
		
		try {
			
			if (_agentConfig != null) {
				
				coquetteConfig = new CoquetteConfig((Map<String, Object>)_agentConfig.getProperty("coquette"));
			} //if
			else {
				
				coquetteConfig = new CoquetteConfig((Map<String, Object>)(ServiceFactory.getConfigService().getLocalAgentConfig()).getProperty("coquette"));
			} //else
			
			Agent.LOG.fine("[JMXCoquette] Successfully loaded JMXCoquette Configuration");
		} //try
		catch(java.lang.Exception _e) {
			
			Agent.LOG.error("[JMXCoquette] Problem loading the coquette config from newrelic.yml. JMXCoquette set to enabled: false.");
			Agent.LOG.error("[JMXCoquette] Message: " + _e.getMessage());

			Map<String, Object> __disabledConfig = new HashMap<String, Object>();
			__disabledConfig.put("enabled", "false");
			coquetteConfig = new CoquetteConfig(__disabledConfig);
		} //catch
		
	} //getCouquetteConfig
	
	private void executePromiscuous() {
		
		salope();
	} //executePromiscuous
	
	private void executeStrict() {
		
		MBeanServer __mbeanServer = ManagementFactory.getPlatformMBeanServer();
		MBeanConfig[] __mbeans = coquetteConfig.getMBeans();
		ObjectName __tempMBean = null;
		Map<String, Object> __tempEventAttributes = null;
		String[] __stAttributes = null;
		Iterator<ObjectInstance> __oiIterator = null;
		Set<ObjectInstance> __oiSet = null;
		ObjectInstance __oiInstance = null;	
		Hashtable<?, ?> __htOIProperties = null;
		
		for (int i = 0; i < __mbeans.length; i++) {
			
			try {	
				
				/* determine if the mbean definition is using a leading wildcard - this has to be escaped by a special
				 * character because there is an issue with yml properties in the format "*:" - which could represent a valid
				 * MBean query string value.
				 */
				if (__mbeans[i].getMBeanName().charAt(0) == '\\') {
					
					__mbeans[i].setMBeanName(__mbeans[i].getMBeanName().substring(1));
				} //if
				
				__tempMBean = new ObjectName(__mbeans[i].getMBeanName());
				__oiSet = __mbeanServer.queryMBeans(__tempMBean, null);
				__oiIterator = __oiSet.iterator();
				
				  while(__oiIterator.hasNext()){
					   
					  __oiInstance = __oiIterator.next();
					  __tempEventAttributes = new HashMap<String, Object>();
					  					  
					  __tempEventAttributes.put("MBean", __oiInstance.getObjectName().getCanonicalName());
					  __htOIProperties = __oiInstance.getObjectName().getKeyPropertyList();
					  __tempEventAttributes.put("MBeanInstanceName", __htOIProperties.get("name"));
					  __tempEventAttributes.put("MBeanInstanceType", __htOIProperties.get("type"));
					  
					  //might want to add an MBean Instance
					  __stAttributes = __mbeans[i].getMBeanAttributes();
					  
					  for (int ii = 0; ii < __stAttributes.length; ii++) {
						 
						 handleAttributeValue(__mbeanServer.getAttribute(__oiInstance.getObjectName(), __stAttributes[ii]), __stAttributes[ii] , __tempEventAttributes);
					  } //for
						
					  NewRelic.getAgent().getInsights().recordCustomEvent(coquetteConfig.getEventName(), __tempEventAttributes);
						
				  } //while

			} //try
			catch(java.lang.Exception _e) {
				
				Agent.LOG.info("[JMXCoquette] Problem loading the mbean: " + __mbeans[i].getMBeanName());
				Agent.LOG.info("[JMXCoquette] Message MBean Fail: " + _e.getMessage());
			} //catch
		} //for			

	} //executeStrict
	
	private void salope() {
		
		MBeanServer __mbeanServer = ManagementFactory.getPlatformMBeanServer();
		Set<ObjectInstance> __mbeanInstances = __mbeanServer.queryMBeans(null, null);
		Iterator<ObjectInstance> __iterator = __mbeanInstances.iterator();
		Map<String, Object> __tempEventAttributes = null;
		ObjectInstance __oiInstance = null;	
		Hashtable<?, ?> __htOIProperties = null;
		MBeanAttributeInfo[] __mbaiAttributes = null;
		MBeanInfo __mbiTempInfo = null;
		
		//loop each of the available MBeans
        while (__iterator.hasNext()) {

        	__oiInstance = __iterator.next();
        	__tempEventAttributes = new HashMap<String, Object>();
        	__tempEventAttributes.put("MBean", __oiInstance.getObjectName().toString());	  
        	__htOIProperties = __oiInstance.getObjectName().getKeyPropertyList();
        	__tempEventAttributes.put("MBeanInstanceName", __htOIProperties.get("name"));
        	__tempEventAttributes.put("MBeanInstanceType", __htOIProperties.get("type"));
        
        	try {
				  
        		__mbiTempInfo = __mbeanServer.getMBeanInfo(__oiInstance.getObjectName());
        		__mbaiAttributes = __mbiTempInfo.getAttributes();
				  
        		for (int i = 0; i < __mbaiAttributes.length; i++) { 
					  
        			if (__mbaiAttributes[i].isReadable()) {
						  
        				handleAttributeValue(__mbeanServer.getAttribute(__oiInstance.getObjectName(), __mbaiAttributes[i].getName()), __mbaiAttributes[i].getName(), __tempEventAttributes);
						  
        			} //if
        			else {
						  
        				Agent.LOG.info("[JMXCoquette] Attribute " + __mbaiAttributes[i].getName() + " is not readable. ");
        			} //else
					  
        		} //for
				  	  
			  } //try
			  catch (java.lang.Exception _e) {
				  
					Agent.LOG.info("[JMXCoquette] Problem interrogating mbean: " + __oiInstance.getObjectName().toString() + " during promiscuous harvest.");
					Agent.LOG.info("[JMXCoquette] Message MBean Access Fail: " + _e.getMessage());	
			  } //catch
			  
			  NewRelic.getAgent().getInsights().recordCustomEvent(coquetteConfig.getEventName(), __tempEventAttributes);
			  
        } //while
		
		Agent.LOG.info("[JMXCoquette] WARNING ::: JMXCoquette is set to promiscuous mode. This will harvest data from all available MBeans (which can be a lot of data). "
				+ "Please take caution when enabling this mode for your application. The next promiscuous havest will take place in " 
				+ coquetteConfig.getFrequency() + " minute(s).");
		
	} //salope
	
	
	private void executeOpen() {
		
		MBeanServer __mbeanServer = ManagementFactory.getPlatformMBeanServer();
		MBeanConfig[] __mbeans = coquetteConfig.getMBeans();
		ObjectName __tempMBean = null;
		Map<String, Object> __tempEventAttributes = null;
		
		Iterator<ObjectInstance> __oiIterator = null;
		Set<ObjectInstance> __oiSet = null;
		ObjectInstance __oiInstance = null;	
		Hashtable<?, ?> __htOIProperties = null;
		MBeanAttributeInfo[] __mbaiAttributes = null;
		MBeanInfo __mbiTempInfo = null;
		
		for (int i = 0; i < __mbeans.length; i++) {
			
			try {
				
				/* determine if the mbean definition is using a leading wildcard - this has to be escaped by a special
				 * character because there is an issue with yml properties in the format "*:" - which could represent a valid
				 * MBean query string value.
				 */
				if (__mbeans[i].getMBeanName().charAt(0) == '\\') {
					
					__mbeans[i].setMBeanName(__mbeans[i].getMBeanName().substring(1));
				} //if

				
				__tempMBean = new ObjectName(__mbeans[i].getMBeanName());
				__oiSet = __mbeanServer.queryMBeans(__tempMBean, null);
				__oiIterator = __oiSet.iterator();
				
				  while(__oiIterator.hasNext()){
					   
					  __oiInstance = __oiIterator.next();
					  __tempEventAttributes = new HashMap<String, Object>();
//					  __tempEventAttributes.put("MBean", __mbeans[i].getFormattedMBeanName());
					  __tempEventAttributes.put("MBean", __oiInstance.getObjectName().getCanonicalName());
					  
					  __htOIProperties = __oiInstance.getObjectName().getKeyPropertyList();
					  __tempEventAttributes.put("MBeanInstanceName", __htOIProperties.get("name"));
					  __tempEventAttributes.put("MBeanInstanceType", __htOIProperties.get("type"));
					  
					  try {
						  
						  __mbiTempInfo = __mbeanServer.getMBeanInfo(__oiInstance.getObjectName());
						  __mbaiAttributes = __mbiTempInfo.getAttributes();
						  
						  for (int ii = 0; ii < __mbaiAttributes.length; ii++) { 
							  
							  if (__mbaiAttributes[ii].isReadable()) {
								  
								  handleAttributeValue(__mbeanServer.getAttribute(__oiInstance.getObjectName(), __mbaiAttributes[ii].getName()), __mbaiAttributes[ii].getName(), __tempEventAttributes);
								  
							  } //if
							  else {
								  
								  Agent.LOG.info("[JMXCoquette] Attribute " + __mbaiAttributes[ii].getName() + " is not readable. ");
							  } //else
							  
						  } //for
						  	  
					  } //try
					  catch (java.lang.Exception _e) {
						  
							Agent.LOG.info("[JMXCoquette] Problem interrogating mbean: " + __oiInstance.getObjectName().toString() + " during promiscuous harvest.");
							Agent.LOG.info("[JMXCoquette] Message MBean Access Fail: " + _e.getMessage());	
					  } //catch
						
					  NewRelic.getAgent().getInsights().recordCustomEvent(coquetteConfig.getEventName(), __tempEventAttributes);
						
				  } //while

			} //try
			catch(java.lang.Exception _e) {
				
				Agent.LOG.info("[JMXCoquette] Problem loading the mbean: " + __mbeans[i].getMBeanName());
				Agent.LOG.info("[JMXCoquette] Message MBean Fail: " + _e.getMessage());
			} //catch
		} //for			

	} //executeOpen
	
	
	private void executeDisco() {
		
		  MBeanServer __mbeanServer = ManagementFactory.getPlatformMBeanServer();
		  Set<ObjectInstance> __mbeanInstances = __mbeanServer.queryMBeans(null, null);
		  Iterator<ObjectInstance> __iterator = __mbeanInstances.iterator();
		  
		  try {
			  
			  while (__iterator.hasNext()) {
				
				  ObjectInstance instance = __iterator.next();
				  Agent.LOG.info("[AdobeJXMService] MBean Found:");
				  Agent.LOG.info("[AdobeJXMService] Object Name: " + instance.getObjectName());		
				            
		          ObjectName objectName = instance.getObjectName();
		          Agent.LOG.info("[AdobeJXMService] Object Name CanonicalName: " + objectName.getCanonicalName());
		          Agent.LOG.info("[AdobeJXMService] Object Name Domain: " + objectName.getDomain());
				            
		          MBeanInfo info = __mbeanServer.getMBeanInfo(objectName);
		          MBeanAttributeInfo[] __mbai = info.getAttributes();
				  
		          for (int i = 0; i < __mbai.length; i++) {
				            		
		        	  Agent.LOG.info("[AdobeJXMService] Attribute Name: " + __mbai[i].getName());
		        	  Agent.LOG.info("[AdobeJXMService] Attribute Type: " + __mbai[i].getType());
		        	  Agent.LOG.info("[AdobeJXMService] Attribute Description: " + __mbai[i].getDescription());
		        	  Agent.LOG.info("[AdobeJXMService] Is Attribute Readable: " + __mbai[i].isReadable());
			
		          } //for	
		          
		          Agent.LOG.info("[AdobeJXMService] *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** *** ");
		          
			  } //while
			  
		  } //try
		  catch(java.lang.Exception _e) {
			  
				Agent.LOG.info("[JMXCoquette] Problem discovering MBeans and Attributes.");
				Agent.LOG.fine("[JMXCoquette] Message: " + _e.getMessage());
		  } //catch
	            	
	} //executeDisco
	
	private void handleAttributeValue(Object _oAttributeValue, String _stAttributeName, Map<String, Object> _mEventHolder) {
		
		  try {
			  
			  if (_oAttributeValue instanceof java.lang.Number) { 
				  						  
				  _mEventHolder.put(_stAttributeName, _oAttributeValue);
				  
			  } //if
			  else if (_oAttributeValue instanceof java.lang.String) { 
				  
				  _mEventHolder.put(_stAttributeName, _oAttributeValue);
			
			  } //else if
			  
			  //else if (__mbaiAttributes[i].getType().equals("boolean")) { 
			  else if (_oAttributeValue instanceof java.lang.Boolean) { 	  

				  _mEventHolder.put(_stAttributeName, _oAttributeValue);
				  
			  } //else if
			  else if (_oAttributeValue instanceof java.util.Date) { 	  
		
				  java.util.Date __dateAttribute = (java.util.Date)_oAttributeValue;
				  java.util.Calendar __calendarHelper = (java.util.Calendar.getInstance());
				  __calendarHelper.setTime(__dateAttribute);
				  _mEventHolder.put(_stAttributeName, new Long(__calendarHelper.getTimeInMillis()));
				  
			  } //else if
			  else {
				  
				  Agent.LOG.fine("[JMXCoquette] Attribute Check :: Unsupported attribute type: " + _oAttributeValue.getClass() + " for: " + _stAttributeName);
			  } //else							  
		  } //try
		  catch (java.lang.Exception _e) {
			  
			  Agent.LOG.info("[JMXCoquette] Problem interrogating mbean attribute: " + _stAttributeName + " during harvest.");
			  Agent.LOG.info("[JMXCoquette] Message MBean Attribute Access Fail: " + _e.getMessage());
		  } //catch
		  
	} //handleAttribute
	
	//listen to the agent configuration and reload if we need to - allows dynamic configuration of jmxcoquette
    protected final AgentConfigListener configListener = new AgentConfigListener() {
        @Override
        public void configChanged(String _appName, AgentConfig _agentConfig) {
            
            //reload the coquette configuration
        	Agent.LOG.fine("[JMXCoquette] Reloading JMXCoquette Configuration");
    		getCoquetteConfig(_agentConfig);
    		
        } //configChanged
    }; //AgentConfigListener
    
} //JMXCoquette