package cn.edu.buaa.act.auth.center.service.impl;

import cn.edu.buaa.act.auth.center.dao.ClientMapper;
import cn.edu.buaa.act.auth.center.exception.ClientInvalidException;
import cn.edu.buaa.act.auth.center.model.Client;
import cn.edu.buaa.act.auth.center.service.ClientService;
//import com.github.wxiaoqi.security.auth.bean.ClientInfo;
//
//import com.github.wxiaoqi.security.auth.service.AuthClientService;
//import com.github.wxiaoqi.security.auth.util.client.ClientTokenUtil;
//
//import com.github.wxiaoqi.security.common.util.UUIDUtils;
import cn.edu.buaa.act.common.util.UUIDUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;


@Service
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientMapper clientMapper;
//    @Autowired
//    private ClientTokenUtil clientTokenUtil;
    @Autowired
    private DiscoveryClient discovery;
    private ApplicationContext context;

    @Autowired
    public ClientServiceImpl(ApplicationContext context) {
        this.context = context;
    }

    @Override
    public String apply(String clientId, String secret) throws Exception {
        Client client = getClient(clientId, secret);
        return "";//clientTokenUtil.generateToken(new ClientInfo(client.getCode(),client.getName(),client.getId().toString()));
    }

    private Client getClient(String clientId, String secret) {
        Client client = new Client();
        client.setCode(clientId);
        client = clientMapper.selectOne(client);
        if(client==null||!client.getSecret().equals(secret)){
            throw new ClientInvalidException("Client not found or Client secret is error!");
        }
        return client;
    }

    @Override
    public void validate(String clientId, String secret) throws Exception {
        Client client = new Client();
        client.setCode(clientId);
        client = clientMapper.selectOne(client);
        if(client==null||!client.getSecret().equals(secret)){
            throw new ClientInvalidException("Client not found or Client secret is error!");
        }
    }

    @Override
    public List<String> getAllowedGroup(String clientId, String secret) {
        Client info = this.getClient(clientId, secret);
        List<String> clients = clientMapper.selectAllowedClient(info.getId() + "");
        if(clients==null) {
            new ArrayList<String>();
        }
        return clients;
    }


    private Client getClient(String clientId) {
        Client client = new Client();
        client.setCode(clientId);
        client = clientMapper.selectOne(client);
        return client;
    }

    @Override
    @Scheduled(cron = "0 0/1 * * * ?")
    public void registryClient() {
        // 自动注册节点
        discovery.getServices().forEach((name) ->{
            Client client = new Client();
            client.setName(name);
            client.setCode(name);
            Client dbClient = clientMapper.selectOne(client);
            if(dbClient==null) {
                client.setSecret(UUIDUtils.generateShortUuid());
                clientMapper.insert(client);
            }
        });
    }
}
