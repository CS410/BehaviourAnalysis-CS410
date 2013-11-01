import Analyzer.TimeAnalyzer;
import GitCommitInterpreter.Author;
import GitCommitInterpreter.CommitInfo;
import GitCommitInterpreter.GitScraperUtils;

import org.eclipse.egit.github.core.Contributor;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.User;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Paths;
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
    	// print output to file
    	File file = new File("output.txt");
    	FileOutputStream fis = new FileOutputStream(file);
    	PrintStream out = new PrintStream(fis);
    	System.setOut(out);
    	
    	final String OAUTH2TOKEN_STRING  = null; // change to your token value
        final String REMOTE_URL = "https://github.com/reddit/reddit.git";

        File localPath = new File("reddit-repo");
        delete(localPath);
        System.out.println("Cloning from " + REMOTE_URL + " to " + localPath);
        Git.cloneRepository()
                .setURI(REMOTE_URL)
                .setDirectory(localPath)
                .call();
        org.eclipse.jgit.lib.Repository localRepo = new FileRepository(localPath.getPath()+"/.git");
        Git git = new Git(localRepo);
        Iterable<RevCommit> revCommits = git.log().all().call();

        // Map of shas to revCommits
        Map<String, RevCommit> commitMap = new HashMap<String, RevCommit>();
        for (RevCommit revCommit: revCommits) {
            commitMap.put(ObjectId.toString(revCommit.getId()), revCommit);
            System.out.println(ObjectId.toString(revCommit.getId()) + revCommit);
        }
        
        GitScraperUtils scraper = new GitScraperUtils(localRepo);

    	// Connect using egit
    	RepositoryService repoService = new RepositoryService();
    	repoService.getClient().setOAuth2Token(OAUTH2TOKEN_STRING);

    	CommitService commitService = new CommitService();
    	commitService.getClient().setOAuth2Token(OAUTH2TOKEN_STRING);

        org.eclipse.egit.github.core.Repository repository = repoService.getRepository("reddit", "reddit");
    	List<Contributor> contributors = repoService.getContributors(repository, false);
    	List<RepositoryCommit> commits = commitService.getCommits(repository);

    	// Map of authors
    	Map<String, Author> authorMap = new HashMap<String, Author>();
    	for (Contributor contributor : contributors) {
    		String login = contributor.getLogin();
    		if (!authorMap.containsKey(login)) {
    			authorMap.put(login, new Author(login));
    		}
    	}

    	// Add CommitInfo to each author
    	for (RepositoryCommit commit : commits) {
    		User author = commit.getAuthor();
    		String login;
    		if(author != null) {
    			login = author.getLogin();
    			CommitInfo commitInfo = new CommitInfo(commit);
    			// Add commit stats to commitinfo
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
    }

    public static void delete(File file) throws IOException{
        if(file.isDirectory()){
            if (file.list().length == 0){
                file.delete();
            } else {
                String files[] = file.list();
                for (String temp : files) {
                    File fileDelete = new File(file, temp);
                    delete(fileDelete);
                }
                if(file.list().length == 0){
                    file.delete();
                }
            }
        } else{
            file.delete();
        }
    }
}
