package cn.edu.buaa.act.figureeight.constant;

/**
 * @author wsj
 */

public enum UnitAttribute {

    ID("id"),
    RESULTS("results"),
    CREATED_AT("created_at"),
    STATE("state"),
    DIFFICULTY("difficulty"),
    JOB_ID("job_id"),
    AGREEMENT("agreement"),
    JUDGMENTS_COUNT("judgments_count"),
    DATA("data"),
    UPDATED_AT("updated_at"),
    MISSED_COUNT("missed_count");

    private String code;

    private UnitAttribute(String property)
    {
        code = property;
    }

    @Override
    public String toString()
    {
        return code;
    }

}
