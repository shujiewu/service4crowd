package cn.edu.buaa.act.management.config;

import cn.edu.buaa.act.management.docker.DockerResourceLoader;
import cn.edu.buaa.act.management.marathon.MarathonDeployProperties;
import mesosphere.marathon.client.Marathon;
import mesosphere.marathon.client.MarathonClient;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.StringUtils;

/**
 * @author wsj
 */
@Configuration
@AutoConfigureOrder(Ordered.HIGHEST_PRECEDENCE)
@EnableConfigurationProperties({MarathonDeployProperties.class})
public class ServiceManageConfiguration {
    @Bean
    public DockerResourceLoader resourceLoader() {
        return new DockerResourceLoader();
    }

    @Bean
    public Marathon marathon(MarathonDeployProperties marathonProperties) {
        System.out.println(1111+marathonProperties.getApiEndpoint());
        return MarathonClient.getInstance(marathonProperties.getApiEndpoint());
    }
}