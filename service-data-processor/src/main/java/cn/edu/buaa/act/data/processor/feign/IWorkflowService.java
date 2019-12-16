package cn.edu.buaa.act.data.processor.feign;


import com.alibaba.fastjson.JSONObject;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;


@FeignClient(value = "service-workflow")
public interface IWorkflowService {
    @RequestMapping(value = "/workflow/task/{taskId}/complete",method = RequestMethod.POST)
    public ResponseEntity<String> complete(@PathVariable(value = "taskId") String taskId, @RequestBody JSONObject request);
}
