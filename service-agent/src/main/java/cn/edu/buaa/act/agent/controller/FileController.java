package cn.edu.buaa.act.agent.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/agent/registration")
public class FileController {

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public ResponseEntity<Void> upload(@RequestParam("file") MultipartFile file) {

        return new ResponseEntity<>(HttpStatus.OK);
    }
}
