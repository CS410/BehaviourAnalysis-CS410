package Util;

import Analyzer.CommitTimeAnalyzer;
import RepositoryContent.Author;
import RepositoryContent.CommitInfo;
import RepositoryContent.FileChanges;

import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
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
        JSONObject filesChangedJSON = new JSONObject();


        commitInfoJSON.put("authorLogin", commitInfo.getAuthorLogin());
        commitInfoJSON.put("authoredDate", commitInfo.getAuthoredDate().toString());
        commitInfoJSON.put("committedDate", commitInfo.getCommittedDate().toString());
        commitInfoJSON.put("additions", commitInfo.getAdditions());
        commitInfoJSON.put("deletions", commitInfo.getDeletions());
        commitInfoJSON.put("authoredDateInSeconds", commitInfo.getAuthoredDateInSeconds());

        commitInfoJSON.put("modelLines", DirectoryScraper.getDirectoryDistribution(commitInfo).get("Model"));
        commitInfoJSON.put("controllerLines", DirectoryScraper.getDirectoryDistribution(commitInfo).get("Controller"));
        commitInfoJSON.put("viewLines", DirectoryScraper.getDirectoryDistribution(commitInfo).get("View"));
        commitInfoJSON.put("otherLines", DirectoryScraper.getDirectoryDistribution(commitInfo).get("Other"));
        
//        List<FileChanges> fileChangesList = commitInfo.getFilesChanged();
//        int commitCount = 0;
//
//        for (FileChanges changes: fileChangesList) {
//            JSONObject fileChangesJSON = new JSONObject();
//            JSONObject child = new JSONObject();
//            fileChangesJSON.put("filename", changes.getFilename());
//
//            int count = 0;
//            for (String lineAdded: changes.getLinesAdded()) {
//                child.put(count, lineAdded);
//                count++;
//            }
//            fileChangesJSON.put("linesAdded", child);
//
//            child = new JSONObject();
//            count = 0;
//
//            for (String lineDeleted: changes.getLinesDeleted()) {
//                child.put(count, lineDeleted);
//                count++;
//            }
//            fileChangesJSON.put("linesDeleted", child);
//            filesChangedJSON.put(commitCount, fileChangesJSON);
//            commitCount++;
//        }
//
//        commitInfoJSON.put("filesChanged", filesChangedJSON);

        return commitInfoJSON;
    }

    public static int compareDates(Calendar c1, Calendar c2) {

        if (c1.get(Calendar.YEAR) != c2.get(Calendar.YEAR))
            return c1.get(Calendar.YEAR) - c2.get(Calendar.YEAR);
        if (c1.get(Calendar.MONTH) != c2.get(Calendar.MONTH))
            return c1.get(Calendar.MONTH) - c2.get(Calendar.MONTH);
        return c1.get(Calendar.DAY_OF_MONTH) - c2.get(Calendar.DAY_OF_MONTH);

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
        JSONObject authorMapJSON = new JSONObject();

        for (String author : authorCommits.keySet() ) {
            JSONObject authorJSON = new JSONObject();

            Author authorObject = authorCommits.get(author);
            
            CommitTimeAnalyzer.generateCommitTimeMetrics(authorObject);

            authorJSON.put("name", authorObject.getName());
            authorJSON.put("login", authorObject.getLogin());
            authorJSON.put("email", authorObject.getEmail());

            List<CommitInfo> commitList = authorObject.getCommitList();
            int count = 0;
            Date currentCommitDate = new Date();
            int currentCommitCount = commitList.size()-1;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            JSONObject commitByDayJSON = new JSONObject();

            CommitInfo commitInfoObject = commitList.get(currentCommitCount);

            while(currentCommitCount >=0) {
                JSONObject dayJSON = new JSONObject();

                if (count == 0) {
                    currentCommitDate = commitList.get(currentCommitCount).getAuthoredDate();

                }
                else {
                    Calendar tempCalendar = Calendar.getInstance();
                    tempCalendar.setTime(currentCommitDate);
                    tempCalendar.add(Calendar.DAY_OF_YEAR, 1);
                    currentCommitDate = tempCalendar.getTime();

                }

                Calendar currentCal = Calendar.getInstance();
                currentCal.setTime(currentCommitDate);

                Calendar commitCal = Calendar.getInstance();
                commitCal.setTime(commitInfoObject.getAuthoredDate());

                while (compareDates(currentCal, commitCal) == 0 ) {
                    JSONObject commitJSON = new JSONObject();
                    commitJSON.put("average", authorObject.getRunningAvgCombined().get(count));
                    commitJSON.put("standardDeviation", authorObject.getRunningStdevCombined().get(count));
                    commitJSON.put("timeAverage", authorObject.getCommitTimeMetrics().getRunningAvg().get(count));
                    commitJSON.put("timeStdev", authorObject.getCommitTimeMetrics().getRunningStdev().get(count));
                    commitJSON.put("commitInfo", convertCommitList(commitInfoObject));
                    dayJSON.put(commitInfoObject.getAuthoredDate(), commitJSON);

                    count++;
                    currentCommitCount--;

                    if (currentCommitCount >=0) {
                        commitInfoObject = commitList.get(currentCommitCount);
                        commitCal.setTime(commitInfoObject.getAuthoredDate());
                    }
                    else {
                        break;
                    }
                }

                commitByDayJSON.put(sdf.format(currentCommitDate), dayJSON);

                if (compareDates(currentCal, commitCal) > 0) {
                    break;
                }
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
