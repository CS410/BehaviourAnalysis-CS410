package GitInformation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Alan Le
 * Date: 23/10/13
 * Time: 11:45 PM
 * To change this template use File | Settings | File Templates.
 */
public class CommitInfo {

    private List<FileChanges> filesChangedList;
    private Date commitTime;


    protected CommitInfo(Date time) {
        this.filesChangedList = new ArrayList<FileChanges>();
        this.commitTime = time;


    }

    public void addChangedFile(FileChanges changedFile) {
        filesChangedList.add(changedFile);

    }

    public List<FileChanges> getFilesChanged() {
        return filesChangedList;
    }

}
