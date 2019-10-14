package cn.edu.buaa.act.figureeight.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

/**
 * JudgementController
 *
 * @author wsj
 * @date 2018/10/27
 */
@RestController
@RequestMapping("/figure-eight")
public class JudgementController {


//    @RequestMapping(value = "/judgement", method = RequestMethod.GET)
//    public String getServiceResult(@RequestParam("serviceId") String serviceId) {
//        if (futureResult.isDone()) {
//            try {
//                if (futureResult.get().equals("UpLoadData Finished"))
//                    return "success";
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//                return "fail";
//            } catch (ExecutionException e) {
//                e.printStackTrace();
//                return "fail";
//            }
//        }
//        return "fail";
//    }
}
