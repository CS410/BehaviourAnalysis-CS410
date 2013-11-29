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
	
	/* Set the running average of commit times */
	public void setRunningAvg(List<Integer> runningAvg) {
		this.runningAvg = runningAvg;
	}
	
	/* Set the running standard deviation of commit times */
	public void setRunningStdev(List<Double> runningStdev) {
		this.runningStdev = runningStdev;
	}
	
	/* Get the running averages of a author's commit times */
	public List<Integer> getRunningAvg() {
		return this.runningAvg;
	}
	
	/* Get the running standard deviations of a author's commit times */
	public List<Double> getRunningStdev() {
		return this.runningStdev;
	}
}
