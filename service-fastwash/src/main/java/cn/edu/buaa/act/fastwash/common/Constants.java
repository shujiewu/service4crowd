package cn.edu.buaa.act.fastwash.common;

/**
 * Constant
 *
 * @author wsj
 * @date 2018/9/9
 */
public class Constants {
    public static final int PROJECT_INVALID_CODE = 50001;

    public static final String PROJECT_STATUS_CREATE = "created";
    public static final String PROJECT_STATUS_PUBLISH = "published";
    public static final String PROJECT_STATUS_COMPLETE = "completed";

    public static final String IMAGE_STATUS_UNANNOTATED = "unannotated";
    public static final String IMAGE_STATUS_ANNOTATING = "annotating";
    public static final String IMAGE_STATUS_MACHINE_ANNOTATED = "machineAnnotated";
    public static final String IMAGE_STATUS_CROWD_ANNOTATED = "crowdAnnotated";
    public static final String IMAGE_STATUS_COMPLETED = "completed";

    public static final String MACHINE_ANNOTATION_REQUEST = "MACHINE_ANNOTATION_REQUEST";
    public static final String MACHINE_ANNOTATION_RESPONSE = "MACHINE_ANNOTATION_RESPONSE";
    public static final String MODEL_TRAINING_REQUEST = "MODEL_TRAINING_REQUEST";

    public static final int ANNOTATION_MAX_PER_CLASS = 4;
    public static final int ANNOTATION_MAX_PER_CLASS_PER_WORKER = 1;
    public static final int TASK_QUEUE_DEFAULT_SIZE = 100;
    public static final int TASK_QUEUE_MIN_SIZE = 20;

    public static final String TASK_STATUS_UNANNOTATED = "TASK_STATUS_UNANNOTATED";
    public static final String TASK_STATUS_MACHINE_ANNOTATED = "TASK_STATUS_MACHINE_ANNOTATED";
    public static final String TASK_STATUS_CROWD_ANNOTATED = "TASK_STATUS_CROWD_ANNOTATED";
    public static final String TASK_STATUS_CROWD_RUNTIME = "TASK_STATUS_CROWD_RUNTIME";
    public static final String TASK_STATUS_MACHINE_RUNTIME = "TASK_STATUS_MACHINE_RUNTIME";
    public static final String TASK_STATUS_COMPLETED = "TASK_STATUS_COMPLETED";


    public static final String TRAINING_ITEM_GROUND_TRUTH = "GROUND_TRUTH";
    public static final String TRAINING_ITEM_CROWD = "CROWD";
    public static final String TRAINING_ITEM_EXPERT = "EXPERT";

    public static final String PUBLISH_RANDOM = "random";
    public static final String PUBLISH_FIXED = "fixed";
}
