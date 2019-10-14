package cn.edu.buaa.act.mlflow.config;

import org.mlflow.tracking.MlflowClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MlFlowConfiguration
 *
 * @author wsj
 * @date 2018/10/8
 */
@Configuration
public class MlFlowConfiguration {


    @Value("${mlflow.trackingUri}")
    private String trackingUri;
    @Bean
    public MlflowClient getMlflowClient() {
        if (trackingUri!=null) {
            return new MlflowClient(trackingUri);
        }
        return new MlflowClient();
    }
}
