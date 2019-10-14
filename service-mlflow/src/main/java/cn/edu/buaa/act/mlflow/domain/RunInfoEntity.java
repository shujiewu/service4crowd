package cn.edu.buaa.act.mlflow.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * RunInfoEntity
 *
 * @author wsj
 * @date 2018/10/9
 */
@Entity
@Table(name = "runInfo")
@Getter
@Setter
public class RunInfoEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String runUuid;
    private Long experimentId;
    private String name;
    private String sourceType;
    private String sourceName;
    private String userId;
    private String status;
    private Date startTime;
    private Date endTime;
    private String sourceVersion;
    private String entryPointName;
    private String artifactUri;
    private String lifecycleStage;
    private String property;
}
