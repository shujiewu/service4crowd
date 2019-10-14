package cn.edu.buaa.act.workflow.domain;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

@Entity
@Table(name="ACT_DE_TEMPLATE")
@Getter
@Setter
public class Template {
    @Id
    @GeneratedValue(generator = "templateIdGenerator")
    @GenericGenerator(name = "templateIdGenerator", strategy = "uuid2")
    @Column(name = "id", unique = true)
    protected String id;

    @Column(name = "name")
    protected String name;

    @Column(name = "description")
    protected String description;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created")
    protected Date created;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated")
    protected Date updated;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "version")
    protected int version;

    @Column(name = "model_editor_json")
    protected String modelEditorJson;

    @Column(name="thumbnail")
    private byte[] thumbnail;
}
