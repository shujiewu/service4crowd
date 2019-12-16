package cn.edu.buaa.act.management.controller;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.common.msg.ObjectRestResponse;
import cn.edu.buaa.act.common.msg.TableResultResponse;
import cn.edu.buaa.act.management.service.AlgorithmConfig;
import cn.edu.buaa.act.management.service.ProcessorConfig;
import cn.edu.buaa.act.management.service.ServiceConfig;
import cn.edu.buaa.act.management.util.FileManagerByFtp;
import cn.edu.buaa.act.management.common.ServiceType;
import cn.edu.buaa.act.management.entity.ServiceRegistration;
import cn.edu.buaa.act.management.model.ServiceRegisterPo;
import cn.edu.buaa.act.management.service.ServiceRegistry;
import cn.edu.buaa.act.management.service.impl.ExecuteService;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.catalina.startup.ExpandWar.deleteDir;
import static org.springframework.http.ResponseEntity.ok;

/**
 * ServiceRegistryController
 * 注册服务
 *
 * @author wsj
 * @date 2018/9/21
 */

@RestController
@RequestMapping("/service/registration")
public class ServiceRegistryController {
    private static final Logger logger = LoggerFactory.getLogger(ServiceRegistryController.class);

    @Autowired
    FileManagerByFtp fileManagerByFtp;

    @Autowired
    ServiceRegistry serviceRegistry;

    @Autowired
    ServiceConfig serviceConfig;

    /**
     * @param pageable
     * @param request
     * @param type
     * @param name
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public TableResultResponse<ServiceRegistration> list(Pageable pageable, HttpServletRequest request, @RequestParam(required = false) String type, @RequestParam(required = false) String name) {
        if (type == null && name == null) {
            Page<ServiceRegistration> pagedRegistrations = this.serviceRegistry.findAll(pageable);
            return new TableResultResponse<>(pagedRegistrations.getTotalElements(), pagedRegistrations.getContent());
        } else if (type != null && name != null) {
            Page<ServiceRegistration> pagedRegistrations = this.serviceRegistry.findAllByTypeAndNameIsLike(type, name, pageable);
            return new TableResultResponse<>(pagedRegistrations.getTotalElements(), pagedRegistrations.getContent());
        } else if (type != null) {
            Page<ServiceRegistration> pagedRegistrations = this.serviceRegistry.findAllByType(type, pageable);
            return new TableResultResponse<>(pagedRegistrations.getTotalElements(), pagedRegistrations.getContent());
        } else {
            Page<ServiceRegistration> pagedRegistrations = this.serviceRegistry.findAllByName(name, pageable);
            return new TableResultResponse<>(pagedRegistrations.getTotalElements(), pagedRegistrations.getContent());
        }
    }

    @RequestMapping(value = "/{type}/{name}/{version}", method = RequestMethod.DELETE)
    @ResponseStatus(HttpStatus.OK)
    public void unregister(@PathVariable("type") String type, @PathVariable("name") String name, @PathVariable("version") String version) {
        serviceRegistry.delete(name, type, version);
    }

    @RequestMapping(value = "/{type}/{name}", method = RequestMethod.GET)
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<ServiceRegistration> info(@PathVariable("type") String type, @PathVariable("name") String name) {
        return new ResponseEntity<ServiceRegistration>(HttpStatus.OK);
    }

    @Value("${storage.dest:/home/LAB/wusj/service4crowd/service}")
    private String dictionary;

    @RequestMapping(value = "/{type}/{name}", method = RequestMethod.POST)
    public ObjectRestResponse register(@PathVariable("type") String type, @PathVariable("name") String name, @RequestBody ServiceRegisterPo serviceRegisterPo) {
        ServiceRegistration serviceRegistration = new ServiceRegistration();
        serviceRegistration.setName(name);
        serviceRegistration.setType(type);
        serviceRegistration.setVersion(serviceRegisterPo.getVersion());
        serviceRegistration.setUserId(BaseContextHandler.getUserID());
        serviceRegistration.setDescription(serviceRegisterPo.getDescription());
        serviceRegistration.setPropertyId(serviceRegisterPo.getPropertyId());
        serviceRegistration.setCreateTime(new Date());
        serviceRegistration.setLastUpdatedTime(new Date());
        try {
            if (serviceRegistration.getType().equals(ServiceType.WEB)) {
                serviceRegistration.setUri(new URI(serviceRegisterPo.getUri()));
            }
            String localFile = dictionary+"/"+BaseContextHandler.getUserID()+"/" + type + "/" + name + "/" + serviceRegisterPo.getVersion();
            serviceRegistration.setMetaDataUri(new URI(localFile));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Map<String, Object> result = new HashMap<>();
        if (serviceRegistry.save(serviceRegistration, serviceRegisterPo.getForce()) != null) {
            result.put("success", true);
        } else {
            result.put("success", false);
        }
        return new ObjectRestResponse<>().data(result).status(HttpStatus.CREATED.value());
    }


    @RequestMapping(value = "/defaultVersion/{type}/{name}/{version}", method = RequestMethod.GET)
    public TableResultResponse<ServiceRegistration> setDefaultVersion(@PathVariable("type") String type, @PathVariable("name") String name, @PathVariable("version") String version) {
        Map<String, Object> result = new HashMap<>();
        List<ServiceRegistration> registrationList = serviceRegistry.findAllByTypeAndName(type, name);
        registrationList = registrationList.stream().peek(registration -> {
            if (registration.getDefaultVersion()) {
                registration.setDefaultVersion(false);
            }
            if (registration.getVersion().equals(version)) {
                registration.setDefaultVersion(true);
            }
            serviceRegistry.save(registration);
        }).collect(Collectors.toList());
        return new TableResultResponse<>(registrationList.size(), registrationList);
    }

    @Autowired
    ExecuteService executeService;

    @Autowired
    AlgorithmConfig algorithmConfig;

    @Autowired
    ProcessorConfig processorConfig;

    @RequestMapping(value = "/upload/{type}/{serviceName}/{version}", method = RequestMethod.POST)
    public ObjectRestResponse upload(@PathVariable String type, @PathVariable String serviceName, @PathVariable String version, @RequestParam(value = "force") Boolean force, @RequestParam("file") MultipartFile file) {
        Map map = new HashMap();
        map.put("success", false);
        String userID = BaseContextHandler.getUserID();
        if (file != null) {
            String packageName = file.getOriginalFilename();
            //是zip压缩文件
            if ("WEB".equals(type) && (packageName != null && packageName.matches(".*\\.json"))) {
                String dir = "/"+userID+ "/" + type + "/" + serviceName + "/" + version + "/";
                try {
                    InputStream inputStream = file.getInputStream();
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    StringBuilder lineTxt = new StringBuilder();
                    String line;
                    while((line = bufferedReader.readLine()) != null){
                        lineTxt.append(line);
                    }
                    String propertyId = serviceConfig.save(lineTxt.toString(),"1");
                    map.put("propertyId",propertyId);
                    map.put("filePath", dir);
                    map.put("success",true);
                    inputStream.close();
                    inputStreamReader.close();
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return new ObjectRestResponse<>().data(map);
            }
            if("ALGORITHM".equals(type)&& (packageName != null && packageName.matches(".*\\.zip"))){
                String dir = "/"+userID+ "/" + type + "/" + serviceName + "/" + version + "/";
                try {
                    InputStream inputStream = file.getInputStream();
                    Map result = executeService.executeUploadMetaData(dir,packageName,inputStream,force,true);
                    String config;
                    if((config = (String) result.get("configuration"))!=null){
                        JSONObject jsonConfig = JSONObject.parseObject(config);
                        jsonConfig.put("name",serviceName);
                        jsonConfig.put("version",version);
                        String propertyId = algorithmConfig.save(jsonConfig.toString(),"1");
                        if(propertyId!=null){
                            map.put("propertyId",propertyId);
                        }
                    }
                    if((Boolean)result.get("success")){
                        map.put("filePath", dir);
                        map.put("success",true);
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                return new ObjectRestResponse<>().data(map);
            }
            if("PROCESSOR".equals(type)&& (packageName != null && packageName.matches(".*\\.zip"))){
                String dir = "/"+userID+ "/" + type + "/" + serviceName + "/" + version + "/";
                try {
                    InputStream inputStream = file.getInputStream();
                    Map result = executeService.executeUploadMetaData(dir,packageName,inputStream,force,true);
                    String config;
                    if((config = (String) result.get("configuration"))!=null){
                        JSONObject jsonConfig = JSONObject.parseObject(config);
                        jsonConfig.put("name",serviceName);
                        jsonConfig.put("version",version);
                        String propertyId = processorConfig.save(jsonConfig.toString(),"1");
                        if(propertyId!=null){
                            map.put("propertyId",propertyId);
                        }
                    }
                    if((Boolean)result.get("success")){
                        map.put("filePath", dir);
                        map.put("success",true);
                    }
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
                return new ObjectRestResponse<>().data(map);
            }
        }
        return new ObjectRestResponse<>().data(map);
    }


    //windows上传版本
//    @RequestMapping(value = "/upload/{type}/{serviceName}/{version}",method = RequestMethod.POST)
//    public ObjectRestResponse<Map> upload(@PathVariable String type, @PathVariable String serviceName, @PathVariable String version,@RequestParam(value = "force") Boolean force, @RequestParam("file") MultipartFile file){
//        Map map = new HashMap();
//        if(file != null) {
//            String packageName = file.getOriginalFilename();
//            //是zip压缩文件
//            if(packageName.matches(".*\\.zip")){
//                String dir = "D://servicedata/1/" + type + "/" + serviceName + "/" + version+"/";
//                File des = new File(dir);
//                if(des.exists()){
//                    if(force){
//                        deleteDir(des);
//                    }
//                    else{
//                        throw new ServiceAlreadyRegisteredException(serviceName,type,version);
//                    }
//                }
//                if(!des.exists()){
//                    des.mkdirs();
//                }
//                File dest = new File(des.getAbsolutePath()+"/"+file.getOriginalFilename());
//                try {
//                    file.transferTo(dest);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//                if(ZipUtils.unZip(dest, dir,  new ArrayList<>())){
//                    map.put("filePath", dir);
//                    map.put("success",true);
//                    return new ObjectRestResponse<>().data(map);
//                }
//            }
//        }
//        map.put("success",false);
//        return new ObjectRestResponse<>().data(map);
//    }
//    private static boolean deleteDir(File dir) {
//        if (dir.isDirectory()) {
//            String[] children = dir.list();
//            if (children != null) {
//                for (int i=0; i<children.length; i++) {
//                    boolean success = deleteDir(new File(dir, children[i]));
//                    if (!success) {
//                        return false;
//                    }
//                }
//            }
//        }
//        // 目录此时为空，可以删除
//        return dir.delete();
//    }
}
