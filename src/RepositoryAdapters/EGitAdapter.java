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
	
	private final static String OAUTH2TOKEN_STRING  = "d8f6eb7051cc972f6812b01737228e361574e46d"; // change to your token value
	
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
	
	/* Get a list of all the commits in the repository */
	public List<RepositoryCommit> getCommits() {
		return commits;
	}
	
	/* Return the author map */
	public Map<String, Author> getAuthorMap() {
		return authorMap;
	}
	
	/* Build a map of Author objects linked to their login */
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
