package cn.edu.buaa.act.management.docker;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class DockerContainer {
	private String type = "DOCKER";
	private String image;
	private String network;
	private List<DockerVolume> volumes;
	private Boolean forcePullImage;

	@Getter
	@Setter
	public static class DockerVolume {

		private String containerPath;
		private String hostPath;
		private String mode;
	}
}
