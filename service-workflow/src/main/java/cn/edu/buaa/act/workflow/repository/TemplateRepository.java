package cn.edu.buaa.act.workflow.repository;


import cn.edu.buaa.act.workflow.domain.Template;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface TemplateRepository extends JpaRepository<Template, String> {
    @Query("from Template as template where template.createdBy = :user")
    List<Template> findTemplatesByCreatedBy(@Param("user") String createdBy, Sort sort);

    @Query("from Template as template where template.createdBy = :user")
    Page<Template> findPageTemplatesByCreatedBy(@Param("user") String createdBy, Pageable pageable);

    @Query("from Template as template where template.createdBy = :user and "
            + "(lower(template.name) like :filter)")
    List<Template> findTemplatesCreatedBy(@Param("user") String createdBy, @Param("filter") String filter, Sort sort);

    @Query("from Template as template where template.createdBy = :user and "
            + "(lower(template.name) like :filterName)")
    Page<Template> findPageTemplatesCreatedBy(@Param("user") String createdBy, @Param("filterName") String filterName, Pageable pageable);
}
