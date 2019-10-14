package cn.edu.buaa.act.workflow.exception;

import java.util.HashMap;
import java.util.Map;


/**
 * @author wsj
 */
public class BaseModelerRestException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	protected String messageKey;
	protected Map<String, Object> customData;
    

    public BaseModelerRestException() {
        super();
    }
    
    public BaseModelerRestException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseModelerRestException(String message) {
        super(message);
    }

    public BaseModelerRestException(Throwable cause) {
        super(cause);
    }
    
    public void setMessageKey(String messageKey) {
        this.messageKey = messageKey;
    }
    
    public String getMessageKey() {
        return messageKey;
    }

	public Map<String, Object> getCustomData() {
		return customData;
	}

	public void setCustomData(Map<String, Object> customData) {
		this.customData = customData;
	}
    
	public void addCustomData(String key, Object data) {
		if (customData == null) {
			customData = new HashMap<String, Object>();
		}
		customData.put(key, data);
	}
}
