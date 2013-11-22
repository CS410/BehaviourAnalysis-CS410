package Analyzer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import RepositoryContent.Author;
import RepositoryContent.CommitInfo;

public class CommitPatternAnalyzer {

	private int avgAdds;
	private int avgDels;
	private int avgCombined;
	
	private List<CommitInfo> list;

    private int total = 0;
    private int counter = 0;
	
	private List<Integer> avgRunningAdditions = new ArrayList<Integer>();
	private List<Integer> avgRunningDeletions = new ArrayList<Integer>();
	private List<Integer> avgRunningCombined = new ArrayList<Integer>();


    public static Map<String, Author> calcRunningAverages(Map<String, Author> authorMap) {

        for (String author : authorMap.keySet()) {
            List<Integer> runningAvgCombined = new ArrayList<Integer>();
            List<Double> runningStdevCombined = new ArrayList<Double>();
            Author authorObject = authorMap.get(author);
            List<CommitInfo> list = authorObject.getCommitList();
            int count = 1;
            int s1 = 0;
            int s2 = 0;
            for (int i = list.size() - 1; i >=0; i --) {
                CommitInfo info = list.get(i);
                int currentSum;
                if (i + 1 == list.size()) {
                    currentSum = info.getAdditions() + info.getDeletions();
                    s1 = currentSum;
                    s2 = currentSum*currentSum;

                    runningAvgCombined.add(currentSum);
                    runningStdevCombined.add(0.0);
                }
                else {
                    currentSum = info.getAdditions() + info.getDeletions();
                    s1 = s1 + currentSum;
                    s2 = s2 + currentSum*currentSum;

                    int inRoot = count * s2 - s1*s1;
                    runningAvgCombined.add(s1/count);
                    runningStdevCombined.add(Math.sqrt(inRoot)/count);
                }
                count++;

            }

            authorObject.setRunningAvgCombined(runningAvgCombined);
            authorObject.setRunningStdevCombined(runningStdevCombined);
            authorMap.put(author, authorObject);

        }
        return authorMap;

    }
	
	public void calcAvgAdditions(Map<String, Author> authorMap)
	{
        for (String authors : authorMap.keySet()) {
            list = authorMap.get(authors).getCommitList();
            for(CommitInfo info : list)
            {
                counter++;
                avgAdds = (info.getAdditions() + total ) / counter;
                total = total + info.getAdditions();
                avgRunningAdditions.add(avgAdds);

          }
          //resets
          counter = 0;
          total = 0;
        }
	}
	
	public void calcAvgDeletions(Map<String, Author> authorMap){
        for (String authors : authorMap.keySet()) {
            list = authorMap.get(authors).getCommitList();
            for(CommitInfo info : list) {
                counter++;

                avgDels = (info.getDeletions() + total ) / counter;
                total = total + info.getDeletions();

                avgRunningDeletions.add(avgDels);
            }
            //resets
            counter = 0;
            total = 0;
        }
	}
	
	public void calcAvgCombined(Map<String, Author> authorMap){
        for (String authors : authorMap.keySet()) {
            list = authorMap.get(authors).getCommitList();
            for(CommitInfo info : list) {
              counter++;

              avgCombined = ((info.getAdditions() + info.getDeletions()) + total ) / counter;
              total = total + (info.getAdditions() + info.getDeletions());

              avgRunningCombined.add(avgCombined);
            }
            //resets
            counter = 0;
            total = 0;
        }
	}
}