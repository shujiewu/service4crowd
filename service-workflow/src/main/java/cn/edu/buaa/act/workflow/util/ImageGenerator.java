package cn.edu.buaa.act.workflow.util;

import org.activiti.bpmn.model.BpmnModel;
import org.activiti.image.ProcessDiagramGenerator;
import org.activiti.image.exception.ActivitiImageException;
import org.activiti.image.impl.DefaultProcessDiagramGenerator;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageGenerator {

	public static BufferedImage createImage(BpmnModel bpmnModel) {
        ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator();
        BufferedImage diagramImage = diagramGenerator.generatePngImage(bpmnModel, 1.0);
        return diagramImage;
	}
	
	public static BufferedImage createImage(BpmnModel bpmnModel, double scaleFactor) {
        ProcessDiagramGenerator diagramGenerator = new DefaultProcessDiagramGenerator(scaleFactor);
        BufferedImage diagramImage = diagramGenerator.generatePngImage(bpmnModel, scaleFactor);
        return diagramImage;
	}
	
	public static byte[] createByteArrayForImage(BufferedImage image, String imageType) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
	    try {
	    	ImageIO.write(image, imageType, out);
	      
	    } catch (IOException e) {
	      throw new ActivitiImageException("Error while generating byte array for process image", e);
	    } finally {
	    	try {
	    		if (out != null) {
	    			out.close();
	    		}
	    	} catch(IOException ignore) {
	    		// Exception is silently ignored
	    	}
	    }
	    return out.toByteArray();
	}
}
