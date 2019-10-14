package cn.edu.buaa.act.data.service.api;

import cn.buaa.act.crowd.common.entity.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface IServiceRegister {

    ServiceEntity insertServiceEntity(ServiceEntity serviceEntity);

    Page<ServiceEntity> queryPageByUser(String userId, Pageable pageable);

    List<ServiceEntity> queryAllByUser(String userId);

    ServiceEntity queryByServiceId(String serviceId);

    ServiceEntity queryByServiceTitle(String serviceTitle);

    void deleteById(String Id);
}
