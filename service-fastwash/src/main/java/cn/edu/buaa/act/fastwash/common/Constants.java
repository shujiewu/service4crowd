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
    public static final String IMAGE_STATUS_MACHINE_ANNOTATED = "machineAnnotated";
    public static final String IMAGE_STATUS_CROWD_ANNOTATED = "crowdAnnotated";

    public static final String MACHINE_ANNOTATION_REQUEST = "MACHINE_ANNOTATION_REQUEST";
    public static final String MACHINE_ANNOTATION_RESPONSE = "MACHINE_ANNOTATION_RESPONSE";
    public static final String MODEL_TRAINING_REQUEST = "MODEL_TRAINING_REQUEST";
}
