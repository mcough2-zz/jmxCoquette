package com.newrelic.gpo.coquette;

/* java core */
import java.util.Map;
import java.util.Vector;

/* newrelic agent */
import com.newrelic.agent.Agent;
import com.newrelic.agent.config.BaseConfig;

/* project */
import com.newrelic.gpo.coquette.MBeanConfig;

public class CoquetteConfig extends BaseConfig {

    public static final String ENABLED = "enabled";
    public static final Boolean DEFAULT_ENABLED = Boolean.FALSE;
    public static final String MODE = "mode";
    public static final String DEFAULT_MODE = "strict";
    public static final String EVENT_NAME = "event_name";
    public static final String DEFAULT_EVENT_NAME = "JMX";
    public static final String MBEANS = "mbeans";
    public static final String FREQUENCY = "frequency";
    public static final int DEFAULT_FREQUENCY = 1; 
    public static final String PROPERTY_NAME = "coquette";
    public static final String PROPERTY_ROOT = "newrelic.config." + PROPERTY_NAME + ".";

    private boolean isEnabled;
    private int frequency;
    private String mode;
    private String event_name;
    private MBeanConfig[] mbeans;
    
	public CoquetteConfig(Map<String, Object> _props) {
		
		super(_props, PROPERTY_ROOT);
        isEnabled = getProperty(ENABLED, DEFAULT_ENABLED).booleanValue();
        mode = getProperty(MODE, DEFAULT_MODE);
        frequency = (getProperty(FREQUENCY, DEFAULT_FREQUENCY)).intValue();
        event_name = getProperty(EVENT_NAME, DEFAULT_EVENT_NAME);

        //collect all mbean definitions and stash them as an MBeanConig array
        Vector<MBeanConfig> __vTEMP = new Vector<MBeanConfig>();
        
        for (Map.Entry<String, Object> entry : _props.entrySet()) {
            
        	if (entry.getKey().contains("mbean")) {
        		
        		__vTEMP.add(new MBeanConfig((entry.getValue()).toString()));
        	} //if
        	
        } //for
        
        mbeans = new MBeanConfig[__vTEMP.size()];
        
        for (int i=0; i < __vTEMP.size(); i++) {
        	mbeans[i] = (MBeanConfig)__vTEMP.get(i);
        }
        
       // mbeans = (MBeanConfig[])__vTEMP.toArray();
		
	} //CoquetteConfig

    public boolean isEnabled() {
        
    	return(isEnabled);
    } //isEnabled
    
    public String getMode() {
    	
    	return(mode);
    } //getMode
    
    public String getEventName() {
    	
    	return(event_name);
    } //getEventName
    
    public int getFrequency() {
    	
    	return(frequency);
    } //getFrequency
    
    public MBeanConfig[] getMBeans() {
    	
    	return(mbeans);
    } //getMBeans
    
	
} //CoquetteConfig