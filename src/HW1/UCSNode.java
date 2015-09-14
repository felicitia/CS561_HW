package HW1;

public class UCSNode extends Node{
	int[] startTimes;
	int[] endTimes;
	private int offNum;
	
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
	public int getOffNum() {
		return offNum;
	}
	public void setOffNum(int offNum) {
		this.offNum = offNum;
	}
}
