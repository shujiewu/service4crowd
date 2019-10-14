package cn.edu.buaa.act.management.repository;

import cn.edu.buaa.act.management.common.ServiceType;
import cn.edu.buaa.act.management.entity.ServiceRegistration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author wsj
 */
@Repository
public interface RegistrationRepository extends JpaRepository<ServiceRegistration, String> {

    /**
     * @param name
     * @param type
     * @return
     */
    List<ServiceRegistration> findAllByTypeAndName(String type,String name);

//    @Query("select c from ServiceRegistration c where c.name=:name and c.type=:type")
//    List<ServiceRegistration> findAllByNameAndType(@Param("name") String name, @Param("type") String type);

    Page<ServiceRegistration> findServiceRegistrationsByType(String type,Pageable pageable);

    Page<ServiceRegistration> findServiceRegistrationsByNameIsLike(String name, Pageable pageable);

    Page<ServiceRegistration> findServiceRegistrationsByTypeAndNameIsLike(String type,String name, Pageable pageable);

    void deleteServiceRegistrationByNameAndTypeAndVersion(String name, String type, String version);

    void deleteServiceRegistrationByNameAndTypeAndDefaultVersionIsTrue(String name, String type);

    ServiceRegistration findServiceRegistrationByNameAndTypeAndDefaultVersionIsTrue(String name, String type);

    ServiceRegistration findServiceRegistrationByNameAndTypeAndVersion(String name, String type, String version);
}