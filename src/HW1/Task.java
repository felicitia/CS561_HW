package HW1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Task {

	private String algorithm;
	private String source;
	private List<String> destList;
	private int pipeNum;
	private List<Pipe> pipeList;
	private int startTime;
	private int taskNum;
	
	public Task() {
		super();
		algorithm = null;
		source = null;
		destList = new ArrayList<String>();
		pipeList = new ArrayList<Pipe>();
	}

	public void addPipe(Pipe pipe){
		pipeList.add(pipe);
	}
	
	public String getAlgorithm() {
		return algorithm;
	}

	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public List<String> getDestList() {
		return destList;
	}

	public void setDestList(List<String> destList) {
		this.destList = destList;
	}

	public int getPipeNum() {
		return pipeNum;
	}

	public void setPipeNum(int pipeNum) {
		this.pipeNum = pipeNum;
	}

	public List<Pipe> getPipeList() {
		return pipeList;
	}

	public void setPipeList(List<Pipe> pipeList) {
		this.pipeList = pipeList;
	}

	public int getStartTime() {
		return startTime;
	}

	public void setStartTime(int startTime) {
		this.startTime = startTime;
	}

	public int getTaskNum() {
		return taskNum;
	}

	public void setTaskNum(int taskNum) {
		this.taskNum = taskNum;
	}
	
}
