package Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import RepositoryContent.CommitInfo;
import RepositoryContent.FileChanges;

public class DirectoryScraper {
	
	
	public static Map<String, Integer> getDirectoryDistribution(CommitInfo commitInfo) {
		int modelLines = 0;
		int controllerLines = 0;
		int viewLines = 0;
		int otherLines = 0;
		
		List<FileChanges> filesChanged = commitInfo.getFilesChanged();
		
		for (FileChanges fileChanges : filesChanged) {
			String filename = fileChanges.getFilename();
			int linesChanged = fileChanges.getLinesAdded().size() + fileChanges.getLinesDeleted().size();
			
			if (filename.startsWith("r2/r2/models/")) {
				modelLines = modelLines + linesChanged;
			}
			if (filename.startsWith("r2/r2/controllers/")) {
				controllerLines = controllerLines + linesChanged;
			}
			if (filename.startsWith("r2/r2/public/") || 
					filename.startsWith("r2/r2/templates/")) {
				viewLines = viewLines + linesChanged;
			} else {
				otherLines = otherLines + linesChanged;
			}
		}
		Map<String, Integer> directoryDistribution = new HashMap<String, Integer>();
		
		directoryDistribution.put("Model", modelLines);
		directoryDistribution.put("Controller", controllerLines);
		directoryDistribution.put("View", viewLines);
		directoryDistribution.put("Other", otherLines);
		
		return directoryDistribution;
	}
}
