package cn.edu.buaa.act.auth.center.controller;

import cn.edu.buaa.act.auth.center.configuration.KeyConfiguration;
import cn.edu.buaa.act.auth.center.service.ClientService;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClientController
 *
 * @author wsj
 * @date 2018/9/9
 */
@RestController
@RequestMapping("api/auth")
public class ClientController {
    @Autowired
    private ClientService clientService;

    @Autowired
    private KeyConfiguration keyConfiguration;
    @RequestMapping(value = "/client/userPubKey",method = RequestMethod.POST)
    public ObjectRestResponse<byte[]> getUserPublicKey(@RequestParam("clientId") String clientId, @RequestParam("secret") String secret) throws Exception {
        clientService.validate(clientId, secret);
        return new ObjectRestResponse<byte[]>().data(keyConfiguration.getUserPubKey());
    }

    @RequestMapping(value = "/client/register",method = RequestMethod.POST)
    public ObjectRestResponse<byte[]> registerClient(@RequestParam("clientId") String clientId){
        return new ObjectRestResponse<byte[]>();
    }
}
