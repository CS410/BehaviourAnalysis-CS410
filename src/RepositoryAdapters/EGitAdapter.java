package RepositoryAdapters;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.egit.github.core.Contributor;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.RepositoryCommit;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.RepositoryService;

import RepositoryContent.Author;

public class EGitAdapter {
	
	private final static String OAUTH2TOKEN_STRING  = "1020f70c94ac8241831535e8561bd0d43066809e"; // change to your token value
	
	private RepositoryService repoService;
	private CommitService commitService;
    private Repository repository;
    
	public List<Contributor> contributors;
	public List<RepositoryCommit> commits;
	public Map<String, Author> authorMap;
	
	public EGitAdapter() throws IOException {
		repoService = new RepositoryService();
		repoService.getClient().setOAuth2Token(OAUTH2TOKEN_STRING);
		commitService = new CommitService();
		commitService.getClient().setOAuth2Token(OAUTH2TOKEN_STRING);
		repository = repoService.getRepository("reddit", "reddit");
		contributors = repoService.getContributors(repository, false);
		commits = commitService.getCommits(repository);
		authorMap = buildAuthorMap();
	}
	
	public List<Contributor> getContributors() {
		return contributors;
	}
	
	public List<RepositoryCommit> getCommits() {
		return commits;
	}
	
	public Map<String, Author> getAuthorMap() {
		return authorMap;
	}
	
	private Map<String, Author> buildAuthorMap() {
    	Map<String, Author> authorMap = new HashMap<String, Author>();
    	for (Contributor contributor : contributors) {
    		String login = contributor.getLogin();
    		if (!authorMap.containsKey(login)) {
    			authorMap.put(login, new Author(login));
    		}
    	}
    	return authorMap;
	}
}
