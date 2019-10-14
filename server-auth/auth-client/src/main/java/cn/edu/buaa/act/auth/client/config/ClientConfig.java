package cn.edu.buaa.act.auth.client.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;

/**
 * ClientConfig
 *
 * @author wsj
 * @date 2018/9/9
 */
@Data
public class ClientConfig {
    @Value("${auth.client.id:null}")
    private String clientId;
    @Value("${auth.client.secret}")
    private String clientSecret;
}
