package cn.edu.buaa.act.fastwash.controller;

import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import cn.edu.buaa.act.fastwash.service.api.IProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Map;

/**
 * UserController
 *
 * @author wsj
 * @date 2018/9/8
 */

@RestController
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    IProjectService projectService;

    @RequestMapping(value = "/create", method = RequestMethod.POST, produces = "application/json")
    public ResponseEntity<Object> createProject(@RequestBody ProjectEntity projectEntity) throws Exception {
        return new ResponseEntity<Object>(projectService.insertProject(projectEntity),HttpStatus.OK);
    }

    @RequestMapping(value = "/{projectName}/exist", method = RequestMethod.GET, produces = "application/json")
    public ResponseEntity<Object> projectExist(@PathVariable String projectName) throws Exception {
        return new ResponseEntity<Object>(projectService.projectExist(projectName),HttpStatus.OK);
    }
}
