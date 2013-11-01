package Analyzer;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import RepositoryContent.CommitInfo;

public class CommitTimeAnalyzer {
	
	public static int calculateAvgCommitGap(List<CommitInfo> commitList) {
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
	
	public static Map<Integer, Integer> calculateCommitTimeTable(List<CommitInfo> commitList) {
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
	
	public static List<Integer> calculateWorkHourDistribution(Map<Integer, Integer> commitTimeTable) {
		List<Integer> workHourDistribution = Arrays.asList(
				commitTimeTable.get(9) + commitTimeTable.get(10),
				commitTimeTable.get(11) + commitTimeTable.get(12),
				commitTimeTable.get(13) + commitTimeTable.get(14),
				commitTimeTable.get(15) + commitTimeTable.get(16));
		return workHourDistribution;
	}
}
