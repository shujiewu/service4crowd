package cn.edu.buaa.act.fastwash.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * AnnotationController
 *
 * @author wsj
 * @date 2019/9/8
 */
@RestController
@RequestMapping("/annotation")
public class AnnotationController {


//    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
//    public ResponseEntity<Object> getTruthInferenceAlgorithm(@PathVariable String algorithmId) throws Exception {
//        return new ResponseEntity<Object>(truthInferenceService.queryTruthInferenceEntityById(algorithmId), HttpStatus.OK);
//    }
}
