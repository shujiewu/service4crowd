package cn.edu.buaa.act.workflow.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@FeignClient(value = "service-data-core")
public interface IDataCoreService {


    @RequestMapping(value = "/data/machineTask/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> createMachineTask(@RequestBody Map<String, String> request);

    @RequestMapping(value = "/data/machineTask/complete", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> completeMachineTask(@RequestBody Map<String, String> request);

    @RequestMapping(value = "/data/crowdTask/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> createHumanTask(@RequestBody Map<String, String> request);

    @RequestMapping(value = "/data/crowdTask/complete", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> completeCrowdTask(@RequestBody Map<String, String> request);

    @RequestMapping(value = "/data/serviceResult/{serviceResultId}", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Map> getServiceResult(@PathVariable(value = "serviceResultId") String serviceResultId);

    @RequestMapping(value = "/data/serviceResult/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Map> insertServiceResult(@RequestBody Map<String, Object> request);

//    @RequestMapping(value = "/getAnswerDataByName", method = RequestMethod.GET, produces = "application/json")
//    public ResponseEntity<Map> getAnswerData(@RequestParam("answerDataName") String answerDataName);
//
//    @RequestMapping(value = "/answerDataStat", method = RequestMethod.POST, produces = "application/json")
//    public ResponseEntity<Map> answerDataStat(@RequestBody Map<String, Object> request);
//
//    @RequestMapping(value = "/aggregateCompare", method = RequestMethod.POST, produces = "application/json")
//    public ResponseEntity<Map> aggregateCompare(@RequestBody Map<String, Object> request);
//
//
//    @RequestMapping(value = "/getMetaDataByName", method = RequestMethod.GET, produces = "application/json")
//    public ResponseEntity<Map> getMetaData(@RequestParam("metaDataName") String metaDataName, @RequestParam("userId") String userId);
//
//
//    @RequestMapping(value = "/filterMetaData", method = RequestMethod.POST, produces = "application/json")
//    public ResponseEntity<Map> filterMetaData(@RequestBody Map<String, Object> params);
}
