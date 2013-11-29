package Metrics;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Henry
 * Date: 01/11/13
 * Time: 8:10 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommitTimeMetrics {
	
	private List<Integer> runningAvg;
	private List<Double> runningStdev;
	
	public void setRunningAvg(List<Integer> runningAvg) {
		this.runningAvg = runningAvg;
	}
	
	public void setRunningStdev(List<Double> runningStdev) {
		this.runningStdev = runningStdev;
	}
	
	public List<Integer> getRunningAvg() {
		return this.runningAvg;
	}
	
	public List<Double> getRunningStdev() {
		return this.runningStdev;
	}
}
