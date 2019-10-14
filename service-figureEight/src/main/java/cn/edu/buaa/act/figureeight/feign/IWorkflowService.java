package cn.edu.buaa.act.figureeight.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;


@FeignClient(value = "service-workflow")
public interface IWorkflowService {
    @RequestMapping(value = "/workflow/task/{taskId}/complete",method = RequestMethod.POST)
    public ResponseEntity<String> complete(@PathVariable(value = "taskId") String taskId, @RequestBody Map<String,Object> request);
}
