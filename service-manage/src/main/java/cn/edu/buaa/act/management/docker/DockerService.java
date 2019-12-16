package cn.edu.buaa.act.management.docker;

import cn.edu.buaa.act.common.context.BaseContextHandler;
import cn.edu.buaa.act.management.entity.ContainerVo;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.async.ResultCallback;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.EventsResultCallback;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.github.dockerjava.api.model.HostConfig.newHostConfig;

public class DockerService {
    /**
     * docker 容器列表缓存
     */
    private static final Map<String, ContainerVo> cs = new HashMap<String, ContainerVo>();

    public DockerService(){
        initClient(dockerUrl);
    }
    // @Value("${docker.url}")
    private String dockerUrl = "tcp://192.168.3.117:2375";
    private DockerClient dockerClient;
    public void initClient(String dockerUrl) {
        if (dockerClient == null) {
            System.out.println("初始化docker连接:" + dockerUrl);
            this.dockerClient = DockerClientBuilder.getInstance(dockerUrl).build();
        }
    }
    /**
     * 创建并启动容器,同时部署应用
     */
    public String createAndStartContainer(String imageName, String queueName,String userID) {
        String containerId = createContainer(imageName, queueName, userID).getId();
        //System.out.println(containerId);
        startContainer(containerId);
        EventsResultCallback callback = new EventsResultCallback() {
            @Override
            public void onNext(Event event) {
                System.out.println("Event: " + event);
                super.onNext(event);
            }
        };
        try {
            dockerClient.eventsCmd().exec(callback).awaitCompletion().close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        pushAppToCotainer(containerId, appLocalFile.getAbsolutePath());
//        String appName = appLocalFile.getName().split("\\.")[0];
//        ContainerVo containerVO = cs.get(containerId);
//        String containerName = containerVO.getName();
//        String accessUrl = getAppAccessUrl(containerId, appName);
//        containerVO.setAccessUrl(accessUrl);
//        containerVO.setCpuShare(cpu);
//        containerVO.setMemLimit(mem);
//        return containerName;
        return "1";
    }

    /**
     * 创建容器
     *
     * @return
     */
    public CreateContainerResponse createContainer(String imageName, String queueName,String userID) {
        String containerName = "runtime-15";
        Volume volume1 = new Volume("/home/service");
        CreateContainerResponse newContainer = dockerClient.createContainerCmd(imageName)// 镜像
                .withName(containerName)// 容器名称
                .withHostConfig(newHostConfig()
                        .withBinds(new Bind("/home/wsj/service4crowd/service/"+userID+"/", volume1)))
                .withCmd("python", "/home/runtime/runtime.py","--q",queueName)
                .exec();
        return newContainer;
    }
    /**
     * 启动容器
     *
     * @param containerId
     */
    public void startContainer(String containerId) {
        dockerClient.startContainerCmd(containerId).exec();
    }
    /**
     * 停止容器
     *
     * @param containerId
     */
    public void stopContainer(String containerId) {
        dockerClient.stopContainerCmd(containerId).exec();
    }
    /**
     * 删除容器
     *
     * @param containerId
     */
    public void rmContainer(String containerId) {
        dockerClient.removeContainerCmd(containerId).exec();
    }
    public void pushAppToCotainer(String containerId, String appRootPath) {
        dockerClient.copyArchiveToContainerCmd(containerId).withHostResource(appRootPath)
                .withRemotePath("/usr/local/tomcat/webapps").exec();
    }
    public void pull(){
        dockerClient.pullImageCmd("ldkobe/runtime:latest").exec(new ResultCallback<PullResponseItem>() {
            public void onStart(Closeable closeable) {
            }

            public void onNext(PullResponseItem object) {
                System.out.println(object.getStatus());
            }

            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            public void onComplete() {
                System.out.println("pull finished");
            }

            public void close() throws IOException {
            }
        });
    }
    public static void main(String[] args){
        DockerService dockerService = new DockerService();
        dockerService.createAndStartContainer("ldkobe/runtime:latest","runtime5","5");
    }
}
