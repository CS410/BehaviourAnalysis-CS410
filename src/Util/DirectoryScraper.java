package Util;

import java.util.List;

import RepositoryContent.CommitInfo;
import RepositoryContent.DirectoryDistribution;
import RepositoryContent.FileChanges;

public class DirectoryScraper {
	
	
	public static DirectoryDistribution getDirectoryDistribution(CommitInfo commitInfo) {
		int modelLines = 0;
		int controllerLines = 0;
		int viewLines = 0;
		int libraryLines = 0;
		int otherLines = 0;
		
		List<FileChanges> filesChanged = commitInfo.getFilesChanged();
		
		for (FileChanges fileChanges : filesChanged) {
			String filename = fileChanges.getFilename();
			
			int linesChanged = fileChanges.getLinesAdded().size() + fileChanges.getLinesDeleted().size();
			
			if (filename.startsWith("r2/r2/models/")) {
				modelLines = modelLines + linesChanged;
			} else if (filename.startsWith("r2/r2/controllers/")) {
				controllerLines = controllerLines + linesChanged;
			} else if (filename.startsWith("r2/r2/lib/")) {
				libraryLines = libraryLines + linesChanged;
			} else if (filename.startsWith("r2/r2/public/") || 
					filename.startsWith("r2/r2/templates/")) {
				viewLines = viewLines + linesChanged;
			} else {
				otherLines = otherLines + linesChanged;
			}
		}
		DirectoryDistribution directoryDistribution = new DirectoryDistribution();
		
		directoryDistribution.setModelLines(modelLines);
		directoryDistribution.setViewLines(viewLines);
		directoryDistribution.setControllerLines(controllerLines);
		directoryDistribution.setLibraryLines(libraryLines);
		directoryDistribution.setOtherLines(otherLines);
		
		return directoryDistribution;
	}
}
