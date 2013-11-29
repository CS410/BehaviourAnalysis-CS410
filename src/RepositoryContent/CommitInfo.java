package RepositoryContent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.egit.github.core.RepositoryCommit;

/**
 * Created with IntelliJ IDEA.
 * User: Alan Le
 * Date: 23/10/13
 * Time: 11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommitInfo {

	private RepositoryCommit commit;
    private List<FileChanges> filesChangedList;
    private int additions;
    private int deletions;
    private DirectoryDistribution directoryDistribution; 
    
    /* Constructor */
    public CommitInfo(RepositoryCommit commit) {
    	this.commit = commit;
        this.filesChangedList = new ArrayList<FileChanges>();
    }

    /* Returns SHA-1 checksum */
    public String getSha() {
    	return commit.getSha();
    }

    /* Returns author login id */
    public String getAuthorLogin() {
    	return commit.getAuthor().getLogin();
    }

    /* Returns Authored Date*/
    public Date getAuthoredDate() {
    	return commit.getCommit().getAuthor().getDate();
    }

    /* Returns committed date */
    public Date getCommittedDate() {
    	return commit.getCommit().getCommitter().getDate();
    }

    /* Set number of lines added for the commit */
    public void setAdditions(int additions) {
    	this.additions = additions;
    }

    /* Returns number of lines added for the commit */
    public int getAdditions() {
    	return additions;
    }

    /* Set number of lines removed for the commit */
    public void setDeletions(int deletions) {
    	this.deletions = deletions;
    }

    /* Returns number of lines removed for the commit */
    public int getDeletions() {
    	return deletions;
    }

    /* Add a file that has been changed in this commit */
    public void addChangedFile(FileChanges changedFile) {
    	filesChangedList.add(changedFile);
    }

    /* Get list of files this commit has changed */
    public List<FileChanges> getFilesChanged() {
    	return filesChangedList;
    }

    /* Return when this commit has been changed on the day in seconds */
    public int getCommittedDateInSeconds() {
    	Date committedDate = this.getAuthoredDate();
    	Calendar calendar = GregorianCalendar.getInstance();
    	calendar.setTime(committedDate);
    	return calendar.get(Calendar.SECOND) + calendar.get(Calendar.MINUTE) * 60 + calendar.get(Calendar.HOUR_OF_DAY) * 3600;
    }
    
    /* Set the directory distribution of the lines changed in a commit */
    public void setDirectoryDistribution(DirectoryDistribution directoryDistribution) {
    	this.directoryDistribution = directoryDistribution;
    }
    
    /* Return the directory distribution */
    public DirectoryDistribution getDirectoryDistribution() {
    	return directoryDistribution;
    }
}
