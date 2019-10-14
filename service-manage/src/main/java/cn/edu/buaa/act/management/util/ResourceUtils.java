package cn.edu.buaa.act.management.util;

import cn.edu.buaa.act.management.docker.DockerResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Utils
 *
 * @author wsj
 * @date 2018/10/5
 */
public class ResourceUtils {

    public static Resource getResource(String uriString) {
        Assert.isTrue(StringUtils.hasText(uriString), "Resource URI must not be empty");
        try {
            URI uri = new URI(uriString);
            String scheme = uri.getScheme();
            Assert.notNull(scheme, "a scheme (prefix) is required");
            if (scheme.equals("docker")) {
                String dockerUri = uriString.replaceFirst("docker:\\/*", "");
                return new DockerResource(dockerUri);
            }
            else {
                ResourceLoader resourceLoader = null;
                if (!scheme.equalsIgnoreCase("http") && !scheme.equalsIgnoreCase("https")) {
                    resourceLoader = new DefaultResourceLoader();
                }
//                else {
//                    resourceLoader = new DownloadingUrlResourceLoader();
//                }
                return resourceLoader.getResource(uriString);
            }
        }
        catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
