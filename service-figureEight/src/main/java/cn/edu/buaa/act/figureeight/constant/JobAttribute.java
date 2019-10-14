package cn.edu.buaa.act.figureeight.constant;

/**
 * @author wsj
 */

public enum JobAttribute {

    ID("id"), PROBLEM("problem"), CML("cml"), JS("js"), DESIRED_REQUIREMENTS(
            "desired_requirements"), UNITS_REMAIN_FINALIZED(
            "units_remain_finalized"), DESIGN_VERIFIED("design_verified"), PAGES_PER_ASSIGNMENT(
            "pages_per_assignment"), UNITS_COUNT("units_count"), CONFIDENCE_FIELDS(
            "confidence_fields"), EXCLUDED_COUNTRIES("excluded_countries"), MINIMUM_REQUIREMENTS(
            "minimum_requirements"), COMPLETED_AT("completed_at"), MAX_UNIT_CONFIDENCE(
            "max_unit_confidence"), EXECUTION_MODE("execution_mode"), JUDGMENTS_PER_UNIT(
            "judgments_per_unit"), FIELDS("fields"), COMPLETED("completed"), WORKER_UI_REMIX(
            "worker_ui_remix"), JUDGMENTS_COUNT("judgments_count"), SEND_JUDGMENTS_WEBHOOK(
            "send_judgments_webhook"), STATE("state"), TITLE("title"), INCLUDED_COUNTRIES(
            "included_countries"), UNITS_PER_ASSIGNMENT("units_per_assignment"), AUTO_ORDER_THRESHOLD(
            "auto_order_threshold"), VARIABLE_JUDGMENTS_MODE(
            "variable_judgments_mode"), LANGUAGE("language"), WEBHOOK_URI(
            "webhook_uri"), MAX_JUDGMENTS_PER_UNIT("max_judgments_per_unit"), UPDATED_AT(
            "updated_at"), MINIMUM_ACCOUNT_AGE_SECONDS(
            "minimum_account_age_seconds"), GOLD_PER_ASSIGNMENT(
            "gold_per_assignment"), EXPECTED_JUDGMENTS_PER_UNIT(
            "expected_judgments_per_unit"), CUSTOM_KEY("custom_key"), MAX_JUDGMENTS_PER_WORKER(
            "max_judgments_per_worker"), ALIAS("alias"), SUPPORT_EMAIL(
            "support_email"), INSTRUCTIONS("instructions"), AUTO_ORDER(
            "auto_order"), REQUIRE_WORKER_LOGIN("require_worker_login"), PROJECT_NUMBER(
            "project_number"), MIN_UNIT_CONFIDENCE("min_unit_confidence"), MAX_JUDGMENTS_PER_IP(
            "max_judgments_per_ip"), GOLDS_COUNT("golds_count"), GOLD("gold"), CSS(
            "css"), CREATED_AT("created_at"), ORDER_APPROVED("order_approved"), AUTO_ORDER_TIMEOUT(
            "auto_order_timeout"), OPTIONS("options");

    private String code;

    private JobAttribute(String property)
    {
        code = property;
    }
    @Override
    public String toString()
    {
        return code;
    }

}
