package RepositoryContent;

import Metrics.CommitTimeMetrics;

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

    private CommitTimeMetrics commitTimeMetrics;

    /* Constructor */
    public Author(String login) {
    	this.login = login;
    	this.name = null;
    	this.email = null;
    	this.commitList = new ArrayList<CommitInfo>();

        this.commitTimeMetrics = new CommitTimeMetrics();
    }

    /* Returns authors list of commits */
    public List<CommitInfo> getCommitList () {
        return commitList;
    }

    /* Returns userid of contributer */
    public String getLogin() {
    	return login;
    }

    /* Returns name of contributer */
    public String getName() {
        return name;
    }

    /* Returns e-mail of contributer */
    public String getEmail() {
        return email;
    }

    /* Adds a CommitInfo object to the contributer */
    public void addCommit(CommitInfo newCommit) {
        commitList.add(newCommit);
    }

    /* Returns time metrics */
    public CommitTimeMetrics getCommitTimeMetrics() {
        return this.commitTimeMetrics;
    }

}
