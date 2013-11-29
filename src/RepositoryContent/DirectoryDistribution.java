package RepositoryContent;

/**
 * Contains the directory distribution of the lines modified
 * in a single commit.
 * 
 * @author Jeremy
 */
public class DirectoryDistribution {
	
	private int modelLines;
	private int viewLines;
	private int controllerLines;
	private int libraryLines;
	private int otherLines;
	
	/* Get the lines of code modified or the model component */
	public int getModelLines() {
		return modelLines;
	}
	
	/* Set the number of modified lines for model */
	public void setModelLines(int modelLines) {
		this.modelLines = modelLines;
	}
	
	/* Get the lines of code modified for the view component */
	public int getViewLines() {
		return viewLines;
	}
	
	/* Set the number of modified lines for view */
	public void setViewLines(int viewLines) {
		this.viewLines = viewLines;
	}
	
	/* Get the lines of code modified for the controller component */
	public int getControllerLines() {
		return controllerLines;
	}
	
	/* Set the number of modified lines for controller */
	public void setControllerLines(int controllerLines) {
		this.controllerLines = controllerLines;
	}
	
	/* Get the lines of code modified for the library */
	public int getLibraryLines() {
		return libraryLines;
	}
	
	/* Set the number of modified lines for library */
	public void setLibraryLines(int libraryLines) {
		this.libraryLines = libraryLines;
	}
	
	/* Get the lines of code modified in other parts */
	public int getOtherLines() {
		return otherLines;
	}
	
	/* Set the number of modified lines for other */
	public void setOtherLines(int otherLines) {
		this.otherLines = otherLines;
	}
}
