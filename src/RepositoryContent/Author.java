package RepositoryContent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Alan Le
 * Date: 27/10/13
 * Time: 12:13 PM
 * To change this template use File | Settings | File Templates.
 */
public class Author {
	
	private String login;
    private String name;
    private String email;
    private List<CommitInfo> commitList;
    private List<String> commitShaList;
    
    public Author(String login) {
    	this.login = login;
    	this.name = null;
    	this.email = null;
    	this.commitList = new ArrayList<CommitInfo>();
    	this.commitShaList = new ArrayList<String>();
    }

    public List<CommitInfo> getCommitList () {
        return commitList;
    }
    
    public List<String> getCommitShaList() {
    	return commitShaList;
    }

    public String getLogin() {
    	return login;
    }
    
    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public void addCommit(CommitInfo newCommit) {
        commitList.add(newCommit);
    }
    
    public void addCommitSha(String commitSha) {
    	commitShaList.add(commitSha);
    }

}
