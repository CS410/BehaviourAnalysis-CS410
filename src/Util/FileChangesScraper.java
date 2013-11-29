package Util;

import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import RepositoryContent.CommitInfo;
import RepositoryContent.FileChanges;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: Alan Le
 * Date: 27/10/13
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class FileChangesScraper{
    private Repository repo;
    private RevWalk rw;

    public FileChangesScraper(Repository repo){
        this.repo = repo;
        this.rw = new RevWalk(repo);
    }

    /* Given a commit, finds which files have been changed and the lines that have been added/removed */
    public void addListOfFileChanges(CommitInfo commitInfo, RevCommit commit) throws IOException {
        List<DiffEntry> diffs;
        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);

        df.setRepository(repo);
        df.setDiffComparator(RawTextComparator.DEFAULT);
        df.setDetectRenames(true);

        if (commit.getParentCount() != 0) {
            diffs = df.scan(commit.getParent(0).getTree(), commit.getTree());
        } else {
            diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(), commit.getTree()));
        }

        int additions = 0;
        int deletions = 0;
        
        for (DiffEntry diff: diffs) {
        	List<String> adds = findChanges("+ ", diff);
        	List<String> dels = findChanges("- ", diff);
            commitInfo.addChangedFile(new FileChanges(diff.getNewPath(), findChanges("+ ", diff), findChanges("- ", diff)));
            additions = additions + adds.size();
            deletions = deletions + dels.size();
        }
        commitInfo.setAdditions(additions);
        commitInfo.setDeletions(deletions);
    }

    /* Find line changes */
    private List<String> findChanges(String prefix, DiffEntry diff) throws IOException {
        List<String> changes = new ArrayList<String>();

        ByteArrayOutputStream diffOutputStream = new ByteArrayOutputStream();
        DiffFormatter df = new DiffFormatter(diffOutputStream);
        df.setRepository(repo);
        df.format(diff);
        String diffOutput = diffOutputStream.toString();
        for (String diffLine: diffOutput.split("\\n")) {
            if (diffLine.startsWith(prefix)) {
                changes.add(diffLine);
            }
        }
        return changes;
    }
}
