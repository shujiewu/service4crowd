package cn.edu.buaa.act.data.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

/**
 * @author wsj
 */
@Getter
@Setter
public class Judgement {
    String id;
    String created_at;
    String started_at;
    String acknowledged_at;
    String  external_type;
    Boolean golden;
    String  missed;
    String  rejected;
    String  tainted;
    String  country;
    String  region;
    String  city;
    String  job_id;
    String  unit_id;
    String  worker_id;
    Double trust;
    Double  worker_trust;
    String  unit_state;
    Map<String,String> data;
    Object unit_data;
    Long interval;
}
