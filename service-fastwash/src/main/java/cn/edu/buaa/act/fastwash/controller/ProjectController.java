package cn.edu.buaa.act.fastwash.controller;

import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.fastwash.common.DataPageable;
import cn.edu.buaa.act.fastwash.data.TaskItemEntity;
import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import cn.edu.buaa.act.fastwash.model.PublishRequest;
import cn.edu.buaa.act.fastwash.service.api.IProjectService;
import cn.edu.buaa.act.fastwash.data.DataItemEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

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
    public ObjectRestResponse createProject(@RequestBody ProjectEntity projectEntity) throws Exception {
        projectEntity = projectService.insertProject(projectEntity);
        if(projectEntity.getId()==null){
            return new ObjectRestResponse<Object>().success(false);
        }else{
            return new ObjectRestResponse<Object>().success(true);
        }
    }

    @RequestMapping(value = "/{projectName}/exist", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse projectExist(@PathVariable String projectName) throws Exception {
        return new ObjectRestResponse<Object>().success(!projectService.projectExist(projectName));
    }

    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<ProjectEntity> projectList(@RequestParam("page") int page, @RequestParam("limit") int limit,
                                                          @RequestParam(defaultValue = "all",required = false) String status) throws Exception {
        DataPageable dataPageable = new DataPageable();
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "createTime"));
        dataPageable.setSort(new Sort(orders));
        dataPageable.setPagesize(limit);
        dataPageable.setPagenumber(page);
        Page<ProjectEntity> projectEntityPage = null;
        if(status.equals("all")){
            projectEntityPage = projectService.findProjects(dataPageable);
        }else{
            projectEntityPage = projectService.findProjects(dataPageable,status);
        }
        return new TableResultResponse<ProjectEntity>(projectEntityPage.getTotalElements(),projectEntityPage.getContent());
    }

    @RequestMapping(value = "/{projectName}/images", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<DataItemEntity> imageList(@PathVariable String projectName, @RequestParam("page") int page, @RequestParam("limit") int limit) throws Exception {
        DataPageable dataPageable = new DataPageable();
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
        dataPageable.setSort(new Sort(orders));
        dataPageable.setPagesize(limit);
        dataPageable.setPagenumber(page);

        Page<DataItemEntity> dataItemEntityPage = projectService.findImages(projectName,dataPageable);
        return new TableResultResponse<DataItemEntity>(dataItemEntityPage.getTotalElements(),dataItemEntityPage.getContent());
    }

    @RequestMapping(value = "/{projectName}/tasks", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<TaskItemEntity> taskList(@PathVariable String projectName, @RequestParam("page") int page, @RequestParam("limit") int limit) throws Exception {
        DataPageable dataPageable = new DataPageable();
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
        dataPageable.setSort(new Sort(orders));
        dataPageable.setPagesize(limit);
        dataPageable.setPagenumber(page);

        Page<TaskItemEntity> taskItemEntityPage = projectService.findTasks(projectName,dataPageable);
        return new TableResultResponse<TaskItemEntity>(taskItemEntityPage.getTotalElements(),taskItemEntityPage.getContent());
    }

    @RequestMapping(value = "/{projectName}/images/all", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<String> imageList(@PathVariable String projectName,@RequestParam String dataSetName) throws Exception {
        List<String> dataItemEntityPage = projectService.findImages(projectName,dataSetName);
        return new TableResultResponse<String>(dataItemEntityPage.size(),dataItemEntityPage);
    }

    @RequestMapping(value = "/{projectName}/publish", method = RequestMethod.POST, produces = "application/json")
    public ObjectRestResponse publishProject(@PathVariable String projectName, @RequestParam String dataSetName, @RequestBody PublishRequest publishRequest) throws Exception {
        return new ObjectRestResponse<Object>().success(projectService.publishProject(projectName,dataSetName,publishRequest));
    }

    @RequestMapping(value = "/{projectName}/config", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse projectConfig(@PathVariable String projectName) throws Exception {
        return new ObjectRestResponse<ProjectEntity>().data(projectService.findProjectEntityByName(projectName)).success(true);
    }
}
