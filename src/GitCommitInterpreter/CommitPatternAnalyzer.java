package GitCommitInterpreter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.CommitStats;
import org.eclipse.egit.github.core.RepositoryCommit;

public class CommitPatternAnalyzer {

	private int avgAdds;
	
	private int total = 0;
	
	private int avgDels;
	
	private int avgCombined;
	
	private List<CommitInfo> list;
	
	private int counter = 0;
	
	private List<Integer> avgRunningAdditions = new ArrayList<Integer>();
	private List<Integer> avgRunningDeletions = new ArrayList<Integer>();
	private List<Integer> avgRunningCombined = new ArrayList<Integer>();
	
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
	          for(CommitInfo info : list)
	          {
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
	          for(CommitInfo info : list)
	          {
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