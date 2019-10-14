package cn.edu.buaa.act.management.docker;

import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;

/**
 * DockerUtils
 *
 * @author wsj
 * @date 2018/10/3
 */
public class DockerUtils {
    private static String getDockerImageTag(DockerResource dockerResource) {
        try {
            String uri = dockerResource.getURI().toString().substring("docker:".length());
            DockerImage dockerImage = DockerImage.fromImageName(uri);
            String tag = dockerImage.getTag();
            Assert.isTrue(StringUtils.hasText(tag), "Could not extract tag from " +
                    dockerResource.getDescription());
            return tag;
        } catch (IOException e) {
            throw new IllegalArgumentException(
                    "Docker Resource URI is not in expected format to extract version. " +
                            dockerResource.getDescription(),
                    e);
        }
    }


    private static String generatePythonDockerfile(){
        StringBuffer sb = new StringBuffer("");
        return null;
    }
}
