package Util;

import Analyzer.CommitTimeAnalyzer;
import RepositoryContent.Author;
import RepositoryContent.CommitInfo;
import RepositoryContent.FileChanges;

import org.json.simple.JSONArray;
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
            int commitSize = commitList.size();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            JSONObject commitByDayJSON = new JSONObject();

            Date currentDate = commitList.get(commitList.size()-1).getCommittedDate();
            Date lastDate = commitList.get(0).getCommittedDate();
           // authorJSON.put("firstCommit", authorObject.getEmail());



            Calendar incrementCalendar = Calendar.getInstance();
            incrementCalendar.setTime(currentDate);

            System.out.println(author);
            System.out.println("Number of commits: " + commitList.size());
            System.out.println("First: " + currentDate.toString());
            authorJSON.put("firstCommit", sdf.format(currentDate));

            System.out.println("Last: " + lastDate.toString());
            while (incrementCalendar.getTime().before(lastDate) || compareDates(incrementCalendar.getTime(), lastDate)) {
                if (!(count < commitList.size())) {
                    System.out.println(count + " " + commitList.size());
                    break;
                }
                JSONObject dayJSON = new JSONObject();
                CommitInfo commitInfo = commitList.get(commitSize-count-1);

                System.out.println(incrementCalendar.getTime().toString());
                JSONArray commitListJSON = new JSONArray();

                while (compareDates(incrementCalendar.getTime(), commitInfo.getCommittedDate())) {

                    //JSONObject commitJSON = new JSONObject();
                    JSONObject commitJSON = convertCommitList(commitInfo);
                    commitJSON.put("average", authorObject.getRunningAvgCombined().get(count));
                    commitJSON.put("standardDeviation", authorObject.getRunningStdevCombined().get(count));
                    commitJSON.put("timeAverage", authorObject.getCommitTimeMetrics().getRunningAvg().get(count));
                    commitJSON.put("timeStdev", authorObject.getCommitTimeMetrics().getRunningStdev().get(count));
                    //commitJSON.put("commitInfo", convertCommitList(commitInfo));
                    commitListJSON.add(commitJSON);
                    //dayJSON.put(commitInfo.getCommittedDate(), commitJSON);

                    count++;
                    if (count < commitList.size()) {
                        commitInfo = commitList.get(commitSize - count-1);
                    }
                    else {
                        break;
                    }
                }

                commitByDayJSON.put(sdf.format(incrementCalendar.getTime()), commitListJSON);
                incrementCalendar.add(Calendar.DATE, 1);

            }
            System.out.println("commits made: " + count );

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
