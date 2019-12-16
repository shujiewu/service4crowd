package cn.edu.buaa.act.data.processor.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;


@FeignClient(value = "service-data-core")
public interface IDataCoreService {
    @RequestMapping(value = "/data/serviceResult/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> insertServiceResult(@RequestBody Map<String, Object> request);
}
