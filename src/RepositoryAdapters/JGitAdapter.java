package RepositoryAdapters;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.JGitInternalException;
import org.eclipse.jgit.api.errors.NoHeadException;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.storage.file.FileRepository;

public class JGitAdapter {

	private static final String REMOTE_URL = "https://github.com/reddit/reddit.git";
	private Iterable<RevCommit> revCommits;

	private Map<String, RevCommit> commitMap;
	private Repository localRepo;

    /* Constructor */
	public JGitAdapter() throws IOException, NoHeadException, JGitInternalException {
	    File localPath = new File("reddit-repo");
	    delete(localPath);
	    Git.cloneRepository()
	            .setURI(REMOTE_URL)
	            .setDirectory(localPath)
	            .call();
	    localRepo = new FileRepository(localPath.getPath()+"/.git");
	    Git git = new Git(localRepo);
	    revCommits = git.log().all().call();
	    commitMap = buildCommitMap();
	}

    /*  Returns a map of commits */
	public Map<String, RevCommit> getCommitMap() {
		return commitMap;
	}

    /* Returns the targetted repository */
	public Repository getRepo() {
		return localRepo;
	}

    /* Creates a map of <checksum, RevCommit>  */
	private Map<String, RevCommit> buildCommitMap() {
        Map<String, RevCommit> commitMap = new HashMap<String, RevCommit>();
        for (RevCommit revCommit: revCommits) {
            commitMap.put(ObjectId.toString(revCommit.getId()), revCommit);
        }
        return commitMap;
	}

    /* Removes a file */
    private void delete(File file) throws IOException{
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
