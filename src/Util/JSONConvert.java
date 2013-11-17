package Util;

import RepositoryContent.Author;
import RepositoryContent.CommitInfo;
import RepositoryContent.FileChanges;
import org.json.simple.JSONObject;

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


    private static JSONObject convertCommitList(CommitInfo commitInfo) {

        JSONObject commitInfoJSON = new JSONObject();
        JSONObject filesChangedJSON = new JSONObject();


        commitInfoJSON.put("authorLogin", commitInfo.getAuthorLogin());
        commitInfoJSON.put("authoredDate", commitInfo.getAuthoredDate().toString());
//        commitInfoJSON.put("committerLogin", commitInfo.getCommitterLogin());
        commitInfoJSON.put("committedDate", commitInfo.getCommittedDate().toString());
        commitInfoJSON.put("additions", commitInfo.getAdditions());
        commitInfoJSON.put("deletions", commitInfo.getDeletions());

        List<FileChanges> fileChangesList = commitInfo.getFilesChanged();
        int commitCount = 0;

        for (FileChanges changes: fileChangesList) {
            JSONObject fileChangesJSON = new JSONObject();
            JSONObject child = new JSONObject();
            fileChangesJSON.put("filename", changes.getFilename());

            int count = 0;
            for (String lineAdded: changes.getLinesAdded()) {
                child.put(count, lineAdded);
                count++;
            }
            fileChangesJSON.put("linesAdded", child);

            child = new JSONObject();
            count = 0;

            for (String lineDeleted: changes.getLinesDeleted()) {
                child.put(count, lineDeleted);
                count++;
            }
            fileChangesJSON.put("linesDeleted", child);
            filesChangedJSON.put(commitCount, fileChangesJSON);
            commitCount++;
        }

        commitInfoJSON.put("filesChanged", filesChangedJSON);

        return commitInfoJSON;
    }

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
            //System.out.println("Averages: " + authorObject.getRunningAvgCombined().size());
            //System.out.println("Standard Deviation: " + authorObject.getRunningStdevCombined().size());
            //System.out.println("Commit Size: " + commitList.size());



        }

        return authorMapJSON;
    }

}
