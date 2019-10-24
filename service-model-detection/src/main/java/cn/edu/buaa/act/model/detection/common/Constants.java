package cn.edu.buaa.act.model.detection.common;

/**
 * Constant
 *
 * @author wsj
 * @date 2018/9/9
 */
public class Constants {
    public static final String MODEL_TRAINING_REQUEST = "MODEL_TRAINING_REQUEST";
    public static final String MODEL_TRAINING_RESPONSE= "MODEL_TRAINING_RESPONSE";

    public static final String MACHINE_ANNOTATION_REQUEST = "MACHINE_ANNOTATION_REQUEST";
    public static final String MACHINE_ANNOTATION_RESPONSE = "MACHINE_ANNOTATION_RESPONSE";

    public static final String INFERENCE_TASK_SUCCESS = "INFERENCE_TASK_SUCCESS";
    public static final String INFERENCE_TASK_CREATED = "INFERENCE_TASK_CREATED";
    public static final String INFERENCE_TASK_FAILED = "INFERENCE_TASK_FAILED";

    public static final String TRAINING_TASK_SUCCESS = "TRAINING_TASK_SUCCESS";
    public static final String TRAINING_TASK_CREATED = "TRAINING_TASK_CREATED";
    public static final String TRAINING_TASK_FAILED = "TRAINING_TASK_FAILED";

    public static final String PROJECT_STATUS_CREATE = "created";
    public static final String PROJECT_STATUS_PUBLISH = "published";
    public static final String PROJECT_STATUS_COMPLETE = "completed";

    public static final String IMAGE_STATUS_UNANNOTATED = "unannotated";
    public static final String IMAGE_STATUS_MACHINE_ANNOTATED = "machineAnnotated";
    public static final String IMAGE_STATUS_CROWD_ANNOTATED = "crowdAnnotated";


    public static final String TASK_STATUS_UNANNOTATED = "TASK_STATUS_UNANNOTATED";
    public static final String TASK_STATUS_MACHINE_ANNOTATED = "TASK_STATUS_MACHINE_ANNOTATED";
    public static final String TASK_STATUS_CROWD_ANNOTATED = "TASK_STATUS_CROWD_ANNOTATED";
    public static final String TASK_STATUS_CROWD_RUNTIME = "TASK_STATUS_CROWD_RUNTIME";
    public static final String TASK_STATUS_MACHINE_RUNTIME = "TASK_STATUS_MACHINE_RUNTIME";
    public static final String TASK_STATUS_COMPLETED = "TASK_STATUS_COMPLETED";

    public static final int ANNOTATION_MAX_PER_CLASS = 4;
    public static final int ANNOTATION_MAX_PER_CLASS_PER_WORKER = 1;
    public static final int TASK_QUEUE_DEFAULT_SIZE = 100;
    public static final int TASK_QUEUE_MIN_SIZE = 20;


}
