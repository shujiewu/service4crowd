package cn.edu.buaa.act.auth.center.service;


import java.util.List;


public interface ClientService {
    /**
     * @param clientId
     * @param secret
     * @return
     * @throws Exception
     */
    public String apply(String clientId, String secret) throws Exception;
    public List<String> getAllowedGroup(String serviceId, String secret);
    public void registryClient();

    /**
     * @description 验证是否可以给该客户端用户公钥
     * @date 2018/9/9
     * @param clientId
     * @param secret
     * @throws Exception
     */
    public void validate(String clientId, String secret) throws Exception;
}
