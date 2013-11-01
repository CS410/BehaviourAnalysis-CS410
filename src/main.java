import Analyzer.CommitTimeAnalyzer;
import RepositoryAdapters.EGitAdapter;
import RepositoryAdapters.JGitAdapter;
import RepositoryContent.Author;
import RepositoryContent.CommitInfo;
import Util.FileChangesScraper;

import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.revwalk.RevCommit;

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
    	File file = new File("output.txt");
    	FileOutputStream fis = new FileOutputStream(file);
    	PrintStream out = new PrintStream(fis);
    	System.setOut(out);
    	
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
    	
//    	Set<String> authors = authorMap.keySet();
//    	for (String author : authors) {
//    		List<CommitInfo> commitInfos = authorMap.get(author).getCommitList();
//    		for (CommitInfo commitInfo : commitInfos) {
//    			System.out.println(author + ", Additions: " + commitInfo.getAdditions() + ", Deletions: " + commitInfo.getDeletions());
//    		}
//    	}
    	for (String author : authorMap.keySet()) {
    		Map<Integer, Integer> commitTimeTable = CommitTimeAnalyzer.calculateCommitTimeTable(authorMap.get(author).getCommitList());
    		List<Integer> workTimeTable = CommitTimeAnalyzer.calculateWorkHourDistribution(commitTimeTable);
    		System.out.println(CommitTimeAnalyzer.calculateAvgCommitGap(authorMap.get(author).getCommitList()));
    		System.out.println("9AM to 11AM" + ": " + workTimeTable.get(0));
    		System.out.println("11AM to 1PM" + ": " + workTimeTable.get(1));
    		System.out.println("1PM to 3PM" + ": " + workTimeTable.get(2));
    		System.out.println("3PM to 5PM" + ": " + workTimeTable.get(3));
    	}
    }
}
