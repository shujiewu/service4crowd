package cn.edu.buaa.act.auth.client;

import cn.edu.buaa.act.auth.client.config.ClientConfig;
import cn.edu.buaa.act.auth.client.config.UserAuthConfig;
import cn.edu.buaa.act.auth.client.feign.UserAuthFeign;
import cn.edu.buaa.act.common.msg.BaseResponse;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author wsj
 * 获取用户认证的公钥
 */
@Configuration
@Slf4j
public class AuthClientRunner implements CommandLineRunner {


    @Autowired
    private UserAuthConfig userAuthConfig;

    @Autowired
    private ClientConfig clientConfig;

    @Autowired
    private UserAuthFeign userAuthFeign;

    @Override
    public void run(String... args) throws Exception {
        log.info("初始化加载用户pubKey");
        try {
            refreshUserPubKey();
        } catch (Exception e) {
            log.error("初始化加载用户pubKey失败,1分钟后自动重试!", e);
        }
    }

    @Scheduled(cron = "0 0/1 * * * ?")
    public void refreshUserPubKey() {
        BaseResponse resp = userAuthFeign.getUserPublicKey(clientConfig.getClientId(), clientConfig.getClientSecret());
        if (resp.getStatus() == HttpStatus.OK.value()) {
            ObjectRestResponse<byte[]> userResponse = (ObjectRestResponse<byte[]>) resp;
            this.userAuthConfig.setPubKeyByte(userResponse.getData());
        }
    }

}