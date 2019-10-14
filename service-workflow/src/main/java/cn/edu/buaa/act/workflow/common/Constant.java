package cn.edu.buaa.act.workflow.common;

import cn.edu.buaa.act.common.entity.MicroService;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author wsj
 */
public class Constant {

    public static final String TYPE_SUBPROCESS_MODEL_CHILD = "subprocess-model";
    public static final String TYPE_PROCESS_MODEL = "process-model";
    public static final String MODEL_STATUS_UNPUBLISH = "Unpublished";
    public static final String MODEL_STATUS_PUBLISH = "Published";
    public static final String MODEL_STATUS_SUSPEND = "Suspend";
    public static final String MODEL_STATUS_ERROR= "Error";
    public static final String MODEL_STATUS_COMPLETE= "Complete";


    public static final String SORT_NAME_ASC = "nameAsc";
    public static final String SORT_NAME_DESC = "nameDesc";
    public static final String SORT_MODIFIED_ASC = "modifiedAsc";
    public static final int MIN_FILTER_LENGTH = 1;


    public static final String PROCESS_NOT_FOUND_MESSAGE_KEY = "PROCESS.ERROR.NOT-FOUND";


    public static final String PARA_TYPE_URI = "uriParameters";
    public static final String PARA_TYPE_QUERY = "queryParameters";
    public static final String PARA_TYPE_BODY = "body";
    public static final String PARA_TYPE_MQ_BODY = "messageBody";

    public static ConcurrentHashMap<String,MicroService> microServiceMap = new ConcurrentHashMap<>();


    public static final String PARA_SCOPE_LOCAL = "local";
    public static final String PARA_SCOPE_PROCESS = "process";
    public static final String PARA_SCOPE_EXECUTION = "execution";

    public static final String VAR_EXPRESSION = "(?<=\\$\\{)(.+?)(?=\\})";



}
