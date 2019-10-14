
package cn.edu.buaa.act.management.marathon;

import cn.edu.buaa.act.management.common.DeploymentState;
import cn.edu.buaa.act.management.common.ServiceInstanceStatus;
import mesosphere.marathon.client.model.v2.App;
import mesosphere.marathon.client.model.v2.HealthCheckResult;
import mesosphere.marathon.client.model.v2.HealthCheckResults;
import mesosphere.marathon.client.model.v2.Task;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * @author wsj
 * 每个service实例的状态
 */
public class MarathonAppInstanceStatus implements ServiceInstanceStatus {

	private final App app;

	private final Task task;

	private MarathonAppInstanceStatus(App app, Task task) {
		this.app = app;
		this.task = task;
	}

	static MarathonAppInstanceStatus up(App app, Task task) {
		return new MarathonAppInstanceStatus(app, task);
	}

	static MarathonAppInstanceStatus down(App app) {
		return new MarathonAppInstanceStatus(app, null);
	}


	@Override
	public String getId() {
		return task != null ? task.getId() : (app.getId() + "-failed-" + new Random().nextInt());
	}

	@Override
	public DeploymentState getState() {
		if (task == null) {
			if (app.getLastTaskFailure() == null) {
				return DeploymentState.unknown;
			}
			else {
				return DeploymentState.failed;
			}
		}
		else {
			if (app.getInstances().intValue() > app.getTasksRunning().intValue()) {
				return DeploymentState.deploying;
			}
			else {
				Collection<HealthCheckResults> healthCheckResults = task.getHealthCheckResults();
				boolean alive = healthCheckResults != null && healthCheckResults.iterator().next().getAlive();
				if (!alive && app.getLastTaskFailure() != null) {
					return DeploymentState.failed;
				}
				return alive ? DeploymentState.deployed : DeploymentState.deploying;
			}
		}
	}

	@Override
	public Map<String, String> getAttributes() {
		HashMap<String, String> result = new HashMap<>();
		if (task != null) {
			result.put("staged_at", task.getStagedAt());
			result.put("started_at", task.getStartedAt());
			result.put("host", task.getHost());
			result.put("ports", StringUtils.collectionToCommaDelimitedString(task.getPorts()));
		}
		return result;
	}
}
