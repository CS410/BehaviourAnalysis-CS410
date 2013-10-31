import GitInformation.Author;
import GitInformation.CommitInfo;
import GitInformation.GitScraperUtils;

import org.eclipse.egit.github.core.Contributor;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: Alan Le
 * Date: 23/10/13
 * Time: 11:15 PM
 * To change this template use File | Settings | File Templates.
 */



public class main {
    public static void main(String[] args) throws IOException, GitAPIException {
    	
    	final String OAUTH2TOKEN_STRING  = null; // change to your token value
    	
// Code for jgit
//    	
//        String localPath = "D://CPSC/CS410/reddit/.git";
//        Repository repo = new FileRepository(localPath);
//        Git git = new Git(repo);
//        Iterable<RevCommit> commits = git.log().all().call();
//
//        Map<String, Author> authorHash = new HashMap<String, Author>();
//        
//        File file = new File("test1.txt");  
//        FileOutputStream fis = new FileOutputStream(file);  
//        PrintStream out = new PrintStream(fis);  
//        System.setOut(out);  
//
//        GitScraperUtils scraper = new GitScraperUtils(repo);
//        for (RevCommit commit: commits) {
//            //System.out.println(commit.getFullMessage());
//        	
//        	//System.out.println(commit.getAuthorIdent().getTimeZone());
//        	if(commit.getAuthorIdent().getName().equals("Andre D")){
//        		System.out.println("Authored: " + String.valueOf(commit.getAuthorIdent()) + ", Committed: " + String.valueOf(commit.getCommitterIdent()));
//        	}
//            authorHash = scraper.addFileChanges(authorHash, commit);
//        }


    	// print output to file
    	File file = new File("output.txt");  
    	FileOutputStream fis = new FileOutputStream(file);  
    	PrintStream out = new PrintStream(fis);  
    	System.setOut(out);  

    	
    	RepositoryService repoService = new RepositoryService();
    	repoService.getClient().setOAuth2Token(OAUTH2TOKEN_STRING);

    	CommitService commitService = new CommitService();
    	commitService.getClient().setOAuth2Token(OAUTH2TOKEN_STRING);

    	org.eclipse.egit.github.core.Repository repo = repoService.getRepository("reddit", "reddit");
    	List<Contributor> contributors = repoService.getContributors(repo, false);
    	List<RepositoryCommit> commits = commitService.getCommits(repo);

    	Map<String, Author> authors = new HashMap<String, Author>();

    	for (Contributor contributor : contributors) {
    		String login = contributor.getLogin();
    		if (!authors.containsKey(login)) {
    			authors.put(login, new Author(login));
    		}
    	}

    	for (RepositoryCommit commit : commits) {
    		User author = commit.getAuthor();
    		String login;
    		if(author != null) {
    			login = author.getLogin();
    			authors.get(login).addCommit(new CommitInfo(commit));
    		} else {
    			login = "null";
    		}
    	}

    	Set<String> keys = authors.keySet();
    	for (String key : keys) {
    		System.out.println("Author: " + key + ", # of commits: " + authors.get(key).getCommitList().size());
    	}
    }
}
