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

}
