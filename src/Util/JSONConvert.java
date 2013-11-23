package Util;

import Analyzer.CommitTimeAnalyzer;
import RepositoryContent.Author;
import RepositoryContent.CommitInfo;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Alan Le
 * Date: 16/11/13
 * Time: 7:05 PM
 * To change this template use File | Settings | File Templates.
 */
public class JSONConvert {


	/**
	 * Converts a CommitInfo object into a JSON Object.
	 * 
	 * @param commitInfo
	 * @return JSONObject
	 */
    @SuppressWarnings("unchecked")
	private static JSONObject convertCommitList(CommitInfo commitInfo) {
        JSONObject commitInfoJSON = new JSONObject();

        commitInfoJSON.put("authorLogin", commitInfo.getAuthorLogin());
        commitInfoJSON.put("authoredDate", commitInfo.getAuthoredDate().toString());
        commitInfoJSON.put("committedDate", commitInfo.getCommittedDate().toString());
        commitInfoJSON.put("additions", commitInfo.getAdditions());
        commitInfoJSON.put("deletions", commitInfo.getDeletions());
        commitInfoJSON.put("committedDateInSeconds", commitInfo.getCommittedDateInSeconds());

        Map<String, Integer> dirDistribution = DirectoryScraper.getDirectoryDistribution(commitInfo);
        
        commitInfoJSON.put("modelLines", dirDistribution.get("Model"));
        commitInfoJSON.put("controllerLines", dirDistribution.get("Controller"));
        commitInfoJSON.put("viewLines", dirDistribution.get("View"));
        commitInfoJSON.put("libLines", dirDistribution.get("Library"));
        commitInfoJSON.put("otherLines", dirDistribution.get("Other"));

        return commitInfoJSON;
    }

    public static int compareDates(Calendar c1, Calendar c2) {
        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
            return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
            return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);
    }

    public static boolean compareDates(Date d1, Date d2) {
        Calendar c1 = Calendar.getInstance();
        Calendar c2 = Calendar.getInstance();
        c1.setTime(d1);
        c2.setTime(d2);

        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
            return false;
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
            return false;
        if (c1.get(Calendar.DAY_OF_MONTH) != c2.get(Calendar.DAY_OF_MONTH)) {
            return false;
        }
        return true;
    }


    /**
     * Converts a Map<String, Author> to a JSONObject with a map of commits
     * ordered by day.
     * 
     * @param authorCommits  map of authors
     * @return JSONObject
     */
    @SuppressWarnings("unchecked")
	public static JSONObject mapToJSONByDay(Map<String, Author> authorCommits) {
        // JSONObject that holds a map of authors along with their commits
    	JSONObject authorMapJSON = new JSONObject();

        for (String author : authorCommits.keySet() ) {
        	// JSONObject that holds an author and their commits
            JSONObject authorJSON = new JSONObject();

            Author authorObject = authorCommits.get(author);
            authorJSON.put("name", authorObject.getName());
            authorJSON.put("login", authorObject.getLogin());
            authorJSON.put("email", authorObject.getEmail());
            
            // Generate the commit time metrics for the given author
            CommitTimeAnalyzer.generateCommitTimeMetrics(authorObject);

            List<CommitInfo> commitList = authorObject.getCommitList();
            // Reserve the commit list to chronological order
            Collections.reverse(commitList);
            
            // Format dates as yyyy-mm-ddd
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Calendar incrementCalendar = Calendar.getInstance();

            // Set the current date as the date of the first commit
            Date currentDate = commitList.get(0).getCommittedDate();
            // Set the last date as the date of the last commit
            Date lastDate = commitList.get(commitList.size()-1).getCommittedDate();

            // Set the first commit date to the calendar
            incrementCalendar.setTime(currentDate);

            authorJSON.put("firstCommit", sdf.format(currentDate));
            
            // Map of commits sorted according to date
            JSONObject commitByDayJSON = new JSONObject();
            
            int count = 0;
            while (incrementCalendar.getTime().before(lastDate) || compareDates(incrementCalendar.getTime(), lastDate)) {
            	// Array of commits of a given day
                JSONArray commitListJSON = new JSONArray();
                CommitInfo commitInfo = commitList.get(count);
                
                while (compareDates(incrementCalendar.getTime(), commitInfo.getCommittedDate())) {
                    JSONObject commitJSON = convertCommitList(commitInfo);
                    commitJSON.put("average", authorObject.getRunningAvgCombined().get(count));
                    commitJSON.put("standardDeviation", authorObject.getRunningStdevCombined().get(count));
                    commitJSON.put("timeAverage", authorObject.getCommitTimeMetrics().getRunningAvg().get(count));
                    commitJSON.put("timeStdev", authorObject.getCommitTimeMetrics().getRunningStdev().get(count));
                    commitListJSON.add(commitJSON);

                    count++;
                    // Get the next commit in the list
                    if (count < commitList.size()) {
                        commitInfo = commitList.get(count);
                    }
                    else {
                        break;
                    }
                }
                // Insert the list of commits of a certain day into the commit map
                commitByDayJSON.put(sdf.format(incrementCalendar.getTime()), commitListJSON);
                // Set the calendar to the next day
                incrementCalendar.add(Calendar.DATE, 1);
            }
            authorJSON.put("commitList", commitByDayJSON);
            authorMapJSON.put(authorObject.getLogin(), authorJSON);
        }
        return authorMapJSON;
    }

    @SuppressWarnings("unchecked")
	public static JSONObject mapToJSON(Map<String, Author> authorCommits) {
        JSONObject authorMapJSON = new JSONObject();

        JSONObject childJSON;

        for (String author : authorCommits.keySet() ) {
            JSONObject authorJSON = new JSONObject();
            childJSON = new JSONObject();

            Author authorObject = authorCommits.get(author);
            int count = 0;

            authorJSON.put("name", authorObject.getName());
            authorJSON.put("login", authorObject.getLogin());
            authorJSON.put("email", authorObject.getEmail());

            for (int avg: authorObject.getRunningAvgCombined()) {
                childJSON.put(count, avg);
                count ++;
            }
            authorJSON.put("averages", childJSON);

            childJSON = new JSONObject();
            count = 0;

            for (double stdev : authorObject.getRunningStdevCombined()) {
                childJSON.put(count, stdev);
            }
            authorJSON.put("standardDeviations", childJSON);

            List<CommitInfo> commitList = authorObject.getCommitList();

            JSONObject commitJSON = new JSONObject();

            for (int i = commitList.size()-1; i>=0; i--) {
                commitJSON.put("commitInfo", convertCommitList(commitList.get(i)));
            }

            authorJSON.put("commitList", commitJSON);

            authorMapJSON.put(authorObject.getLogin(), authorJSON);
        }

        return authorMapJSON;
    }
}
