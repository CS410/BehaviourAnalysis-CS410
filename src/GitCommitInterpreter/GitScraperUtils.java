package GitCommitInterpreter;

import org.eclipse.jgit.diff.*;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.treewalk.EmptyTreeIterator;
import org.eclipse.jgit.util.io.DisabledOutputStream;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Alan Le
 * Date: 27/10/13
 * Time: 1:28 PM
 * To change this template use File | Settings | File Templates.
 */
public class GitScraperUtils{
//    private Repository repo;
//    private RevWalk rw;
//
//    public GitScraperUtils(Repository repo){
//        this.repo = repo;
//        this.rw = new RevWalk(repo);
//    }
//
//
//    public Map<String, Author> addFileChanges(Map<String, Author> authorHash, RevCommit commit) throws IOException {
//        if (!authorHash.containsKey(commit.getAuthorIdent().getEmailAddress())) {
//            authorHash.put(commit.getAuthorIdent().getEmailAddress(), new Author(commit.getAuthorIdent().getName(), commit.getAuthorIdent().getEmailAddress()));
//        }
//
//        Author author = authorHash.get(commit.getAuthorIdent().getEmailAddress());
//        author.addCommit(listFileChanges(commit));
//        authorHash.put(commit.getAuthorIdent().getEmailAddress(), author);
//        return authorHash;
//
//    }
//
//    public CommitInfo listFileChanges(RevCommit commit) throws IOException {
//
//        CommitInfo commitInfo = new CommitInfo(commit.getAuthorIdent().getWhen());
//        List<DiffEntry> diffs;
//        DiffFormatter df = new DiffFormatter(DisabledOutputStream.INSTANCE);
//
//        df.setRepository(repo);
//        df.setDiffComparator(RawTextComparator.DEFAULT);
//        df.setDetectRenames(true);
//
//        if (commit.getParentCount() != 0) {
//            diffs = df.scan(commit.getParent(0).getTree(), commit.getTree());
//        }
//        else {
//            diffs = df.scan(new EmptyTreeIterator(), new CanonicalTreeParser(null, rw.getObjectReader(), commit.getTree()));
//        }
//
//        for (DiffEntry diff: diffs) {
//            commitInfo.addChangedFile(new FileChanges(diff.getNewPath(), findChanges("+ ", diff), findChanges("- ", diff)));
//        }
//
//        return commitInfo;
//
//    }
//
//    private List<String> findChanges(String prefix, DiffEntry diff) throws IOException {
//        List<String> changes = new ArrayList<String>();
//
//        ByteArrayOutputStream diffOutputStream = new ByteArrayOutputStream();
//        DiffFormatter df = new DiffFormatter(diffOutputStream);
//        df.setRepository(repo);
//        df.format(diff);
//        String diffOutput = diffOutputStream.toString();//new String (out.toByteArray(), "UTF-8");
//        for (String diffLine: diffOutput.split("\\n")) {
//            if (diffLine.startsWith(prefix)) {
//                changes.add(diffLine);
//            }
//        }
//        return changes;
//    }
}
