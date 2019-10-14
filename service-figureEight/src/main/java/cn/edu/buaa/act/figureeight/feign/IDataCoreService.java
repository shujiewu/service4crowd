package cn.edu.buaa.act.figureeight.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;


@FeignClient(value = "service-data-core")
public interface IDataCoreService {
    @RequestMapping(value = "/data/insertAnswerEntity", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> insertAnswerEntity(@RequestBody Map<String, Object> params);

    @RequestMapping(value = "/data/insertLabelList", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> insertLabelList(@RequestBody Map<String, Object> request);

    @RequestMapping(value = "/data/serviceResult/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> insertServiceResult(@RequestBody Map<String, Object> request);
}
