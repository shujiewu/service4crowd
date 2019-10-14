package cn.edu.buaa.act.data.controller;

import cn.edu.buaa.act.auth.client.annotation.IgnoreClientToken;
import cn.edu.buaa.act.auth.client.annotation.IgnoreUserToken;
import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.data.common.DataPageable;
import cn.edu.buaa.act.data.entity.CustomDataEntity;
import cn.edu.buaa.act.data.model.CustomFileRepresentation;
import cn.edu.buaa.act.data.repository.CustomDataRepository;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

/**
 * CustomFileController
 *
 * @author wsj
 * @date 2018/10/23
 */
@Slf4j
@RequestMapping(value = "/data")
@RestController
public class CustomFileController {

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private CustomDataRepository customDataRepository;

    public GridFsResource convertGridFSFile2Resource(GridFSFile gridFsFile) {
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFsFile.getObjectId());
        return new GridFsResource(gridFsFile, gridFSDownloadStream);
    }

    @RequestMapping(value = "/customData/download", method = RequestMethod.GET)
    @IgnoreUserToken
    public void handleFileDownload(@RequestParam("name") String name, HttpServletRequest request, HttpServletResponse response) {
        final GridFSFile file = gridFsTemplate.findOne(new Query().addCriteria(Criteria.where("filename").is(name)));
        System.out.println(file.getId().toString());
        if (file != null) {
            try {
                String fileName = file.getFilename().replace(",", "");
                //处理中文文件名乱码
                if (request.getHeader("User-Agent").toUpperCase().contains("MSIE") ||
                        request.getHeader("User-Agent").toUpperCase().contains("TRIDENT")
                        || request.getHeader("User-Agent").toUpperCase().contains("EDGE")) {
                    fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                } else {
                    //非IE浏览器的处理：
                    fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
                }
                //response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                // 下载文件能正常显示中文
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                response.setContentLength((new Long(file.getLength()).intValue()));
                GridFsResource gridFsResource = convertGridFSFile2Resource(file);
                IOUtils.copy(gridFsResource.getInputStream(), response.getOutputStream());
            } catch (IOException ignored) {

            }
        }
    }


    @RequestMapping(value = "/customData/download/{id}", method = RequestMethod.GET)
    @IgnoreUserToken
    public void handleFileDownloadById(@PathVariable String id, HttpServletRequest request, HttpServletResponse response) {
        final GridFSFile file = gridFsTemplate.findOne(new Query().addCriteria(Criteria.where("_id").is(id)));
        System.out.println(file.getId().toString());
        if (file != null) {
            try {
                String fileName = file.getFilename().replace(",", "");
                //处理中文文件名乱码
                if (request.getHeader("User-Agent").toUpperCase().contains("MSIE") ||
                        request.getHeader("User-Agent").toUpperCase().contains("TRIDENT")
                        || request.getHeader("User-Agent").toUpperCase().contains("EDGE")) {
                    fileName = java.net.URLEncoder.encode(fileName, "UTF-8");
                } else {
                    //非IE浏览器的处理：
                    fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
                }
                //response.setHeader("content-type", "application/octet-stream");
                response.setContentType("application/octet-stream");
                // 下载文件能正常显示中文
                response.setHeader("Content-Disposition", "attachment;filename=" + fileName);
                response.setContentLength((new Long(file.getLength()).intValue()));
                GridFsResource gridFsResource = convertGridFSFile2Resource(file);
                IOUtils.copy(gridFsResource.getInputStream(), response.getOutputStream());
            } catch (IOException ignored) {

            }
        }
    }

    /**
     * 删除文件
     *
     * @param fileId
     * @return
     */
    @RequestMapping(value = "/customData/delete", method = RequestMethod.POST)
    public ResponseEntity<Map> deleteFile(@RequestParam(name = "fileId") String fileId) {
        Map<String, Object> result = new HashMap<>();

        Query query = Query.query(Criteria.where("_id").is(fileId));
        GridFSFile gfsfile = gridFsTemplate.findOne(query);
        if (gfsfile == null) {
            result.put("success", false);
            result.put("message", "删除失败,未找到文件");
        } else {
            try {
                gridFsTemplate.delete(query);
                result.put("success", true);
                result.put("message", "删除成功");
            } catch (Exception e) {
                e.printStackTrace();
                log.error("删除失败未知异常");
            }
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }
    @RequestMapping(value = "/customData/exist", method = RequestMethod.GET)
    public ResponseEntity<Map> exist(@RequestParam("name") String name){
        Map<String, Object> result = new HashMap<>();
//        Query query = Query.query(Criteria.where("metadata.originalName").is(name)); //这里缺少一个用户判断
//        GridFSFile gfsfile = gridFsTemplate.findOne(query);

        List<CustomDataEntity> customDataEntity = customDataRepository.findCustomDataEntitiesByNameAndUserId(name,BaseContextHandler.getUserID());
        if(customDataEntity!=null&&customDataEntity.size()>0){
            result.put("exist",true);
        }
        else {
            result.put("exist",false);
        }
        return new ResponseEntity<Map>(result, HttpStatus.OK);
    }

    @RequestMapping(value = "/customData/create", method = RequestMethod.POST)
    public ObjectRestResponse<CustomDataEntity> createCustomData(@RequestParam("name") String name,@RequestBody List<String> id){
        CustomDataEntity customDataEntity = new CustomDataEntity();
        customDataEntity.setUserId(BaseContextHandler.getUserID());
        customDataEntity.setCreateTime(new Date());
        customDataEntity.setFileId(new ArrayList<>(id));
        customDataEntity.setName(name);
        customDataRepository.save(customDataEntity);
        return new ObjectRestResponse<>().data(customDataEntity).status(HttpStatus.OK.value());
    }

    @RequestMapping(value = "/customData/list", method = RequestMethod.GET)
    public TableResultResponse<CustomDataEntity> customDataList(@RequestParam("page") int page, @RequestParam("limit") int limit) {
        DataPageable dataPageable = new DataPageable();
        List<Sort.Order> orders = new ArrayList<Sort.Order>();
        orders.add(new Sort.Order(Sort.Direction.DESC, "createTime"));
        dataPageable.setSort(new Sort(orders));
        dataPageable.setPagesize(limit);
        dataPageable.setPagenumber(page);
        Page<CustomDataEntity> result =customDataRepository.findCustomDataEntitiesByUserId("1",dataPageable);
        return new TableResultResponse<CustomDataEntity>(result.getTotalElements(),result.getContent());
    }

    @RequestMapping(value = "/customData/file/list", method = RequestMethod.POST)
    public TableResultResponse<CustomFileRepresentation> list(@RequestBody List<String> id,@RequestParam("page") int page, @RequestParam("limit") int limit) {
        List<CustomFileRepresentation> result = new ArrayList<>();
        GridFSFindIterable files = gridFsTemplate.find(new Query(Criteria.where("metadata.user").is(BaseContextHandler.getUserID()).and("_id").in(id)));
        for(GridFSFile fsFile:files){
            result.add(toCustomFileRepresentation(fsFile));
            // fsFile.getMetadata().
        }
        return new TableResultResponse<CustomFileRepresentation>(result.size(),result).status(HttpStatus.OK.value());
    }
    private CustomFileRepresentation toCustomFileRepresentation(GridFSFile fsFile){
        CustomFileRepresentation customFileRepresentation = new CustomFileRepresentation();
        customFileRepresentation.setId(fsFile.getObjectId().toString());
        customFileRepresentation.setName(fsFile.getFilename());
        customFileRepresentation.setLength(fsFile.getLength());
        customFileRepresentation.setUploadDate(fsFile.getUploadDate());
        customFileRepresentation.setOriginalName(fsFile.getMetadata().get("fileOriginalName").toString());
        customFileRepresentation.setType(fsFile.getMetadata().get("type").toString());
        return customFileRepresentation;
    }


    @RequestMapping(value = "/customData/upload", method = RequestMethod.POST)
    public String uploadfile(HttpServletRequest request) {
        String result = "error";
        try {
            /**
             * Servlet3.0新增了request.getParts()/getPart(String filename) api，
             * 用于获取使用multipart/form-data格式传递的http请求的请求体， 通常用于获取上传文件。
             */
            Part part = request.getPart("file");

            // 获得提交的文件名
            String filename = part.getSubmittedFileName();
            // 获得文件输入流
            InputStream ins = part.getInputStream();
            // 获得文件类型
            String contenttype = part.getContentType();
            DBObject metaData = new BasicDBObject();
            metaData.put("user", "1");
            metaData.put("type", part.getContentType());
            metaData.put("fileOriginalName",filename);
            // metaData.put("customDataName",name);
            // System.out.println(name);
            // 将文件存储到mongodb中,mongodb 将会返回这个文件的具体信息
            ObjectId gfs = gridFsTemplate.store(ins, filename, contenttype,metaData);
            result = gfs.toString();
        } catch (IOException e) {
            log.error("上传失败发生了io异常");
        } catch (ServletException e1) {
            e1.printStackTrace();
        }
        return result;
    }
}
