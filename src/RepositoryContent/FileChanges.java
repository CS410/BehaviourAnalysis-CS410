package RepositoryContent;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Alan Le
 * Date: 27/10/13
 * Time: 10:16 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileChanges {
    private String filename;
    private List<String> additions;
    private List<String> deletions;


    public FileChanges(String filename, List<String> additions, List<String> deletions) {
        this.filename = filename;
        this.additions = additions;
        this.deletions = deletions;
    }

    public String getFilename(){
        return filename;
    }

    public List<String> getLinesAdded() {
        return additions;
    }

    public List<String> getLinesDeleted () {
        return deletions;
    }
}
