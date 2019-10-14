package cn.edu.buaa.act.management.common;

import java.util.Map;

/**
 * @author wsj
 */
public interface ServiceInstanceStatus {

	/**
	 * @return
	 */
	String getId();

	/**
	 * @return
	 */
	DeploymentState getState();

	/**
	 * @return
	 */
	Map<String, String> getAttributes();
}