package Analyzer;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import Metrics.CommitTimeMetrics;
import RepositoryContent.Author;
import RepositoryContent.CommitInfo;

public class CommitTimeAnalyzer {
	
	public static void generateCommitTimeMetrics(Author author) {
		CommitTimeMetrics commitTimeMetrics = author.getCommitTimeMetrics();
		calculateRunningAvg(author, commitTimeMetrics);
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
            Date date = commit.getCommittedDate();
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
