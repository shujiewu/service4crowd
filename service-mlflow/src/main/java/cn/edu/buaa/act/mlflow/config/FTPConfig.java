package cn.edu.buaa.act.mlflow.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * FTPConfig
 *
 * @author wsj
 * @date 2018/10/16
 */
@Configuration
@Data
public class FTPConfig {
    @Value("${ftp.host:192.168.3.117}")
    private String host;
    @Value("${ftp.port:21}")
    private int port;
    @Value("${ftp.userName:wsj}")
    private String userName;
    @Value("${ftp.password:shujie1127}")
    private String password;
}
