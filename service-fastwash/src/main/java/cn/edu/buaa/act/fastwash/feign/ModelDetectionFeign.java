package cn.edu.buaa.act.fastwash.feign;

import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.fastwash.config.FeignConfiguration;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "service-model-detection",configuration = { FeignConfiguration.class})
public interface ModelDetectionFeign {
    @RequestMapping(value = "/annotation/model/inference",method = RequestMethod.POST)
    public ObjectRestResponse inference(@RequestParam(value = "projectName") String projectName, @RequestParam(value = "dataSetName") String dataSetName,
                                        @RequestBody List<String> imageIdList);
}