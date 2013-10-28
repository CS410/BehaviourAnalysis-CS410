package GitInformation;

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
    private String name;
    private String email;
    private List<CommitInfo> commitList;

    public Author(String name, String email) {
        this.name = name;
        this.email = email;
        this.commitList = new ArrayList<CommitInfo>();
    }

    public List<CommitInfo> getCommitList () {
        return commitList;
    }

    public String getName() {
        return name;

    }

    public String getEmail() {
        return email;
    }

    protected void addCommit(CommitInfo newCommit) {
        commitList.add(newCommit);

    }

}
