package cn.edu.buaa.act.figureeight.constant;

/**
 * JudgementAttribute
 *
 * @author wsj
 * @date 2018/10/18
 */
public enum  JudgementAttribute {
    /**
     * Read/Write attributes
     */
    webhook_sent_at("webhook_sent_at"), reviewed("reviewed"),
    missed("missed"), tainted("tainted"), country("country"), region("region"),
    city("city"), golden("golden"), unit_state("unit_state"),

    /**
     * Read only attributes
     */
    started_at("started_at"), created_at("created_at"), job_id("job_id"),
    worker_id("worker_id"), unit_id("unit_id"), judgment("judgment"),
    external_type("external_type"), rejected("rejected"),
    ip("ip"), id("id"), data("data");
    private String code;

    private JudgementAttribute(String property)
    {
        code = property;
    }

    @Override
    public String toString()
    {
        return code;
    }
}
