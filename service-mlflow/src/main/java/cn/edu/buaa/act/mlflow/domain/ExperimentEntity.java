package cn.edu.buaa.act.mlflow.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Id;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Table;
import java.util.Date;

/**
 * ExperimentEntity
 *
 * @author wsj
 * @date 2018/10/9
 */
@Entity
@Getter
@Setter
@Table(name = "experiment")
public class ExperimentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String experimentId;
    private String name;
    private String artifactLocation;
    private String lifecycleStage;
    private Date creationTime;
    private Date lastUpdateTime;
    private String userId;
}
