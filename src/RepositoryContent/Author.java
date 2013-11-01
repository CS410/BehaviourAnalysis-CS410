package RepositoryContent;

import Metrics.CommitContentMetrics;
import Metrics.CommitPatternMetrics;
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

    private CommitContentMetrics commitContentMetrics;
    private CommitPatternMetrics commitPatternMetrics;
    private CommitTimeMetrics commitTimeMetrics;
    
    public Author(String login) {
    	this.login = login;
    	this.name = null;
    	this.email = null;
    	this.commitList = new ArrayList<CommitInfo>();

        this.commitContentMetrics = new CommitContentMetrics();
        this.commitPatternMetrics = new CommitPatternMetrics();
        this.commitTimeMetrics = new CommitTimeMetrics();
    }

    public List<CommitInfo> getCommitList () {
        return commitList;
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

    public CommitContentMetrics getCommitContentMetrics() {
        return this.commitContentMetrics;
    }

    public CommitPatternMetrics getCommitPatternMetrics() {
        return this.commitPatternMetrics;
    }

    public CommitTimeMetrics getCommitTimeMetrics() {
        return this.commitTimeMetrics;
    }

}
