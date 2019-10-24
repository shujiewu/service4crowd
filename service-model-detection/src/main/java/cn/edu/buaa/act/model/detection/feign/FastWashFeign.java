package cn.edu.buaa.act.model.detection.feign;

import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "service-fastwash",configuration = {})
public interface FastWashFeign {
    @RequestMapping(value = "/api/fastwash/project/{projectName}/images/all",method = RequestMethod.GET)
    public TableResultResponse getUserPublicKey(@PathVariable("projectName") String projectName, @RequestParam("dataSetName") String dataSetName);
}