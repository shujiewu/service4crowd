package cn.edu.buaa.act.workflow.common;


import org.activiti.engine.form.AbstractFormType;

public class FormFileType extends AbstractFormType {

    /**
     * 把字符串的值转换为集合对象
     * @param propertyValue
     * @return
     */
    @Override
    public Object convertFormValueToModelValue(String propertyValue) {
            //String[] split = StringUtils.split(propertyValue, ",");
            //return Arrays.asList(split);
        return  propertyValue;
    }
    private static final long serialVersionUID = 1L;

    public FormFileType() {
    }
    /**
     * 把集合对象的值转换为字符串
     * @param modelValue
     * @return
     */
    @Override
    public String convertModelValueToFormValue(Object modelValue) {
        if(modelValue==null){
            return null;
        }
        return modelValue.toString();
    }

    /**
     * 定义表单类型的标识符
     * @return
     */
    @Override
    public String getName() {
        return "file";
    }
}