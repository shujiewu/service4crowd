
package cn.edu.buaa.act.management.docker;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.ClassUtils;


public class DockerResourceLoader implements ResourceLoader {

	private final ClassLoader classLoader = ClassUtils.getDefaultClassLoader();

	@Override
	public Resource getResource(String location) {
		String image = location.replaceFirst(DockerResource.URI_SCHEME + ":\\/*", "");
		return new DockerResource(image);
	}
	@Override
	public ClassLoader getClassLoader() {
		return this.classLoader;
	}

}
