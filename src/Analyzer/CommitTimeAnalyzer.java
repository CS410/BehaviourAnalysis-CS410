package Analyzer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Metrics.CommitTimeMetrics;
import RepositoryContent.Author;
import RepositoryContent.CommitInfo;

public class CommitTimeAnalyzer {
	
	public static void generateCommitTimeMetrics(Author author) {
		CommitTimeMetrics commitTimeMetrics = author.getCommitTimeMetrics();
		List<CommitInfo> commitList = author.getCommitList();
		Map<Integer, Integer> commitTimeTable = calculateCommitTimeTable(commitList);
		commitTimeMetrics.setAverageCommitGap(calculateAvgCommitGap(commitList));
		commitTimeMetrics.setCommitTimeTable(commitTimeTable);
		commitTimeMetrics.setWorkHourDistribution(calculateWorkHourDistribution(commitTimeTable));
		calculateRunningAvg(author, commitTimeMetrics);
	}
	
	private static int calculateAvgCommitGap(List<CommitInfo> commitList) {
		long total = 0;
		long prev = 0;
		Collections.reverse(commitList);
		for (CommitInfo commit : commitList) {
			long curr = commit.getCommittedDate().getTime() / 1000;
			if (prev != 0 && curr != 0) {
				total = total + (curr - prev);
			}
			prev = curr;
		}
		return ((int) total / commitList.size()) / 3600;
	}
	
	private static Map<Integer, Integer> calculateCommitTimeTable(List<CommitInfo> commitList) {
		Map<Integer, Integer> commitTimeTable = new HashMap<Integer, Integer>();
		for (int i = 0; i <= 23; i++) {
			commitTimeTable.put(i, 0);
		}
		Calendar calendar = Calendar.getInstance();
		for (CommitInfo commit : commitList) {
			Date date = commit.getCommittedDate();
			calendar.setTime(date);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
			commitTimeTable.put(hour, commitTimeTable.get(hour) + 1);
		}
		return commitTimeTable;
	}
	
	private static List<Integer> calculateWorkHourDistribution(Map<Integer, Integer> commitTimeTable) {
		List<Integer> workHourDistribution = Arrays.asList(
				commitTimeTable.get(9) + commitTimeTable.get(10),
				commitTimeTable.get(11) + commitTimeTable.get(12),
				commitTimeTable.get(13) + commitTimeTable.get(14),
				commitTimeTable.get(15) + commitTimeTable.get(16));
		return workHourDistribution;
	}
	
	private static void calculateRunningAvg(Author author, CommitTimeMetrics commitTimeMetrics) {
		List<CommitInfo> commitList = author.getCommitList();
		List<Integer> runningAvg = new ArrayList<Integer>();
		List<Double> runningStdev = new ArrayList<Double>();
		Calendar calendar = Calendar.getInstance();
		int count = 1;
        int s1 = 0;
        int s2 = 0;
        for (int i = commitList.size() - 1; i >=0; i --) {
            CommitInfo commit = commitList.get(i);
            Date date = commit.getAuthoredDate();
			calendar.setTime(date);
			int hour = calendar.get(Calendar.HOUR_OF_DAY);
            
            int currentSum;
            if (i + 1 == commitList.size()) {
                currentSum = hour;
                s1 = currentSum;
                s2 = currentSum*currentSum;

                runningAvg.add(currentSum);
                runningStdev.add(0.0);
            }
            else {
                currentSum = hour;
                s1 = s1 + currentSum;
                s2 = s2 + currentSum*currentSum;

                int inRoot = count * s2 - s1*s1;
                runningAvg.add(s1/count);
                runningStdev.add(Math.sqrt(inRoot)/count);
            }
            count++;
        }
        commitTimeMetrics.setRunningAvg(runningAvg);
        commitTimeMetrics.setRunningStdev(runningStdev);
	}
}
