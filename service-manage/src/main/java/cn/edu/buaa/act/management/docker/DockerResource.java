package cn.edu.buaa.act.management.docker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.AbstractResource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;


/**
 * @author wsj
 */
public class DockerResource extends AbstractResource {

	public static String URI_SCHEME = "docker";

	private URI uri;

	private static final Logger logger = LoggerFactory.getLogger(DockerResource.class);


	public DockerResource(String imageName) {
		if(imageName==null){
			logger.warn("image name is null");
		}
		this.uri = URI.create(URI_SCHEME + ":" + imageName);
	}

	public DockerResource(URI uri) {
		if(!DockerResource.URI_SCHEME.equals(uri.getScheme())){
			logger.warn("A 'docker' scheme is required");
		}
		this.uri = uri;
	}
	@Override
	public String getDescription() {
		return "Docker Resource [" + uri + "]";
	}

	@Override
	public InputStream getInputStream() throws IOException {
		throw new UnsupportedOperationException("getInputStream not supported");
	}

	@Override
	public URI getURI() throws IOException {
		return uri;
	}

}
