package Analyzer;

import Metrics.CommitContentMetrics;
import RepositoryContent.Author;
import RepositoryContent.CommitInfo;
import RepositoryContent.FileChanges;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: Henry
 * Date: 01/11/13
 * Time: 4:17 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommitContentAnalyzer {

    public static void parseTodosAndHacks (List<CommitInfo> commitList, Map<String, Author> authorMap)  {
        for (CommitInfo commitInfo : commitList) {
            Author author = authorMap.get(commitInfo.getAuthorLogin());
            CommitContentMetrics ccm = author.getCommitContentMetrics();
            List<FileChanges> filesChanged = commitInfo.getFilesChanged();

            for (FileChanges changes : filesChanged) {
                for (String lineAdded : changes.getLinesAdded()) {
                    if (lineAdded.toLowerCase().contains("todo")) {
                        ccm.committedTodo();
                    } else if (lineAdded.toLowerCase().contains("hack")) {
                        ccm.committedHack();
                    }
                }
                for (String lineDeleted : changes.getLinesDeleted()) {
                    if (lineDeleted.toLowerCase().contains("todo")) {
                        ccm.fixedTodo();
                    } else if (lineDeleted.toLowerCase().contains("hack")) {
                        ccm.fixedHack();
                    }
                }
            }

        }
    }
}
