package cn.edu.buaa.act.fastwash.controller;

import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.fastwash.common.DataPageable;
import cn.edu.buaa.act.fastwash.entity.DataItemEntity;
import cn.edu.buaa.act.fastwash.entity.ProjectEntity;
import cn.edu.buaa.act.fastwash.service.api.IProjectService;
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
    public TableResultResponse<ProjectEntity> projectList(@RequestParam("page") int page, @RequestParam("limit") int limit) throws Exception {
        DataPageable dataPageable = new DataPageable();
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "createTime"));
        dataPageable.setSort(new Sort(orders));
        dataPageable.setPagesize(limit);
        dataPageable.setPagenumber(page);

        Page<ProjectEntity> projectEntityPage = projectService.findAllProjects(dataPageable);
        return new TableResultResponse<ProjectEntity>(projectEntityPage.getTotalElements(),projectEntityPage.getContent());
    }

    @RequestMapping(value = "/{projectName}/images", method = RequestMethod.GET, produces = "application/json")
    public TableResultResponse<DataItemEntity> imageList(@PathVariable String projectName,@RequestParam("page") int page, @RequestParam("limit") int limit) throws Exception {
        DataPageable dataPageable = new DataPageable();
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "id"));
        dataPageable.setSort(new Sort(orders));
        dataPageable.setPagesize(limit);
        dataPageable.setPagenumber(page);

        Page<DataItemEntity> dataItemEntityPage = projectService.findImages(projectName,dataPageable);
        return new TableResultResponse<DataItemEntity>(dataItemEntityPage.getTotalElements(),dataItemEntityPage.getContent());
    }

    @RequestMapping(value = "/{projectName}/publish", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse publishProject(@PathVariable String projectName) throws Exception {
        return new ObjectRestResponse<Object>().success(projectService.publishProject(projectName));
    }

    @RequestMapping(value = "/{projectName}/config", method = RequestMethod.GET, produces = "application/json")
    public ObjectRestResponse projectConfig(@PathVariable String projectName) throws Exception {
        return new ObjectRestResponse<ProjectEntity>().data(projectService.findProjectEntityByName(projectName)).success(true);
    }
}
