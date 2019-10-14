package cn.edu.buaa.act.management.common;

import java.util.*;


/**
 * @author wsj
 * 服务的状态
 */
public class ServiceStatus {

	private final String deploymentId;
	private final Map<String, ServiceInstanceStatus> instances = new HashMap<String, ServiceInstanceStatus>();

	private final DeploymentState generalState;

	protected ServiceStatus(String deploymentId, DeploymentState generalState) {
		this.deploymentId = deploymentId;
		this.generalState = generalState;
	}

	public String getDeploymentId() {
		return deploymentId;
	}

	public DeploymentState getState() {
		if (generalState != null) {
			return generalState;
		}
		Set<DeploymentState> states = new HashSet<>();
		for (Map.Entry<String, ServiceInstanceStatus> entry : instances.entrySet()) {
			states.add(entry.getValue().getState());
		}
		if (states.size() == 0) {
			return DeploymentState.unknown;
		}
		if (states.size() == 1) {
			return states.iterator().next();
		}
		if (states.contains(DeploymentState.error)) {
			return DeploymentState.error;
		}
		if (states.contains(DeploymentState.deploying)) {
			return DeploymentState.deploying;
		}
		if (states.contains(DeploymentState.deployed) || states.contains(DeploymentState.partial)) {
			return DeploymentState.partial;
		}
		if (states.contains(DeploymentState.failed)) {
			return DeploymentState.failed;
		}
		return DeploymentState.partial;
	}

	public Map<String, ServiceInstanceStatus> getInstances() {
		return Collections.unmodifiableMap(this.instances);
	}

	private void addInstance(String id, ServiceInstanceStatus status) {
		this.instances.put(id, status);
	}

	@Override
	public String toString() {
		return this.getState().name();
	}

	public static Builder of(String id) {
		return new Builder(id);
	}

	public static class Builder {

		private final String id;

		private DeploymentState generalState;

		private List<ServiceInstanceStatus> statuses = new ArrayList<>();

		private Builder(String id) {
			this.id = id;
		}

		public Builder with(ServiceInstanceStatus instance) {
			statuses.add(instance);
			return this;
		}

		public Builder generalState(DeploymentState generalState) {
			this.generalState = generalState;
			return this;
		}

		public ServiceStatus build() {
			ServiceStatus status = new ServiceStatus(id, generalState);
			for (ServiceInstanceStatus instanceStatus : statuses) {
				status.addInstance(instanceStatus.getId(), instanceStatus);
			}
			return status;
		}
	}
}
