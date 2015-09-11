package HW1;

import java.util.ArrayList;
import java.util.List;

public class Pipe{
	private String start;
	private String end;
	private int length;
	private int offNum;
	int[] startTimes;
	int[] endTimes;
	
	public Pipe() {
		super();
		start = null;
		end = null;
	}

	public String getStart() {
		return start;
	}

	public void setStart(String start) {
		this.start = start;
	}

	public String getEnd() {
		return end;
	}

	public void setEnd(String end) {
		this.end = end;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getOffNum() {
		return offNum;
	}

	public void setOffNum(int offNum) {
		this.offNum = offNum;
	}

	public int[] getStartTimes() {
		return startTimes;
	}

	public void setStartTimes(int[] startTimes) {
		this.startTimes = startTimes;
	}

	public int[] getEndTimes() {
		return endTimes;
	}

	public void setEndTimes(int[] endTimes) {
		this.endTimes = endTimes;
	}
	

}