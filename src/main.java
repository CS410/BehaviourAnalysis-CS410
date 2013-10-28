import GitInformation.Author;
import GitInformation.GitScraperUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

import java.io.IOException;
import java.util.HashMap;
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
        String localPath = "D://Temp/reddit/.git";
        Repository repo = new FileRepository(localPath);
        Git git = new Git(repo);
        Iterable<RevCommit> commits = git.log().all().call();

        Map<String, Author> authorHash = new HashMap<String, Author>();

        int count = 0;
        GitScraperUtils scraper = new GitScraperUtils(repo);
        for (RevCommit commit: commits) {
            System.out.println(commit.getFullMessage());
            authorHash = scraper.addFileChanges(authorHash, commit);
            count ++;
        }
    }

}
