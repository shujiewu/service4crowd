package cn.edu.buaa.act.data.model;

import java.util.List;

/**
 * @author wsj
 * 仿真结果展示
 */
public class SimResultRepresentation {
	private String id;
	
	private int labelsNum;
	private int workersNum;
	private int itemsNum;
	private int classesNum;
	
	private double[] alpha;
	private double[] beta;
	
	private int responsesCorrect;
	private int responsesWrong;
	
	private List<Integer> tasksPerWorker;
	private List<Integer> workersPerTask;
	
	private List<String> categName;
	private List<Integer> categFrequency;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getWorkersNum() {
		return workersNum;
	}

	public void setWorkersNum(int workersNum) {
		this.workersNum = workersNum;
	}

	public int getItemsNum() {
		return itemsNum;
	}

	public void setItemsNum(int itemsNum) {
		this.itemsNum = itemsNum;
	}

	public int getClassesNum() {
		return classesNum;
	}

	public void setClassesNum(int classesNum) {
		this.classesNum = classesNum;
	}

	public int getLabelsNum() {
		return labelsNum;
	}

	public void setLabelsNum(int labelsNum) {
		this.labelsNum = labelsNum;
	}

	public double[] getAlpha() {
		return alpha;
	}

	public void setAlpha(double[] alpha) {
		this.alpha = alpha;
	}

	public double[] getBeta() {
		return beta;
	}

	public void setBeta(double[] beta) {
		this.beta = beta;
	}

	public int getResponsesCorrect() {
		return responsesCorrect;
	}

	public void setResponsesCorrect(int responsesCorrect) {
		this.responsesCorrect = responsesCorrect;
	}

	public int getResponsesWrong() {
		return responsesWrong;
	}

	public void setResponsesWrong(int responsesWrong) {
		this.responsesWrong = responsesWrong;
	}

	public List<Integer> getTasksPerWorker() {
		return tasksPerWorker;
	}

	public void setTasksPerWorker(List<Integer> tasksPerWorker) {
		this.tasksPerWorker = tasksPerWorker;
	}

	public List<Integer> getWorkersPerTask() {
		return workersPerTask;
	}

	public void setWorkersPerTask(List<Integer> workersPerTask) {
		this.workersPerTask = workersPerTask;
	}

	public List<String> getCategName() {
		return categName;
	}

	public void setCategName(List<String> categName) {
		this.categName = categName;
	}

	public List<Integer> getCategFrequency() {
		return categFrequency;
	}

	public void setCategFrequency(List<Integer> categFrequency) {
		this.categFrequency = categFrequency;
	}
	

}
