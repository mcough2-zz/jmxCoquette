package com.newrelic.gpo.coquette;

/* newrelic agent */
import com.newrelic.agent.Agent;

public class MBeanConfig {
	
	private String mbean_name;
	private String[] mbean_attributes;
	
	MBeanConfig(String _stMBean) {
		
		try {
			
			mbean_name = _stMBean.substring(0, _stMBean.indexOf('[') - 1);
		} //try
		catch (java.lang.ArrayIndexOutOfBoundsException _aiobe) {
			
			Agent.LOG.error("[JXMCoquette] Problem derriving the name of the MBean to interrogate: " + _stMBean);
			Agent.LOG.error("[JXMCoquette] MBeans must be defined in a format similar to: mbean_0: java.lang:type=Threading [ThreadCount,PeakThreadCount,DaemonThreadCount].");
			Agent.LOG.error("[JXMCoquette] " + _aiobe.getMessage());
		} //catch

		try {
			
			mbean_attributes = (_stMBean.substring(_stMBean.indexOf('[') + 1, _stMBean.lastIndexOf(']'))).split("[,]");
		} //try
		catch (java.lang.ArrayIndexOutOfBoundsException _aiobe) {
			
			Agent.LOG.error("[JXMCoquette] Problem derriving the MBean atrributes to interrogate: " + _stMBean);
			Agent.LOG.error("[JXMCoquette] MBeans must be defined in a format similar to: mbean_0: java.lang:type=Threading [ThreadCount,PeakThreadCount,DaemonThreadCount].");
			Agent.LOG.error("[JXMCoquette] " + _aiobe.getMessage());
		} //catch
		
	} //MBeanConfig
	
	public String getMBeanName() {
		
		return(mbean_name);
	} //getMBeanName
	
	
	public String[] getMBeanAttributes() {
		
		return(mbean_attributes);
	} //getMBeanAttributes

	public void setMBeanName(String _mbean_name) {
		
		mbean_name = _mbean_name;		
	} //setMBeanName
	
} //MBeanConfig
