package cn.edu.buaa.act.workflow.feign;

import cn.edu.buaa.act.common.entity.MicroService;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author wsj
 */
@FeignClient(value = "service-manage")
public interface IServiceManage {
    @RequestMapping(value = "/service/configuration/info/list", method = RequestMethod.GET, produces = "application/json")
    public JSONObject serviceConfigList();
}
