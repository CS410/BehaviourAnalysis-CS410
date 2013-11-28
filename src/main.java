import RepositoryAdapters.EGitAdapter;
import RepositoryAdapters.JGitAdapter;
import RepositoryContent.Author;
import RepositoryContent.CommitInfo;
import Util.FileChangesScraper;
import Util.JSONConvert;

import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;
import org.json.simple.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Alan Le
 * Date: 23/10/13
 * Time: 11:15 PM
 * To change this template use File | Settings | File Templates.
 */

public class main {
    public static void main(String[] args) throws IOException, GitAPIException {
    	// print output to file
    	File file = new File("visualization/jsondata.js");
    	FileOutputStream fis = new FileOutputStream(file);
    	PrintStream out = new PrintStream(fis);

        JGitAdapter jgitAdapter = new JGitAdapter();
        EGitAdapter egitAdapter = new EGitAdapter();
        FileChangesScraper scraper = new FileChangesScraper(jgitAdapter.getRepo());
        
        List<RepositoryCommit> commits = egitAdapter.getCommits();
        Map<String, RevCommit> commitMap = jgitAdapter.getCommitMap();
        Map<String, Author> authorMap = egitAdapter.getAuthorMap();

    	// Add CommitInfo to each author
    	for (RepositoryCommit commit : commits) {
    		User author = commit.getAuthor();
    		String login;
    		if(author != null) {
    			login = author.getLogin();
    			CommitInfo commitInfo = new CommitInfo(commit);
    			// Add file changes to commitinfo
    			if (commitMap.containsKey(commitInfo.getSha())) {
    				scraper.addListOfFileChanges(commitInfo, commitMap.get(commitInfo.getSha()));
    			}
    			authorMap.get(login).addCommit(commitInfo);
    		} else {
    			login = "null";
    		}
    	}
    	
    	// Prints Json to file

    	JSONObject jsonMap = JSONConvert.mapToJSONByDay(authorMap);

        System.setOut(out);
        System.out.println("var jsondata = " + jsonMap.toString());
    }
}
