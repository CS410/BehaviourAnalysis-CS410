package Metrics;

/**
 * Created with IntelliJ IDEA.
 * User: Henry
 * Date: 01/11/13
 * Time: 4:12 AM
 * To change this template use File | Settings | File Templates.
 */
public class CommitContentMetrics {

    private int numCommittedTodos;
    private int numCommittedHacks;
    private int numFixedTodos;
    private int numFixedHacks;

    public CommitContentMetrics() {
        numCommittedTodos = 0;
        numCommittedHacks = 0;
        numFixedTodos = 0;
        numFixedHacks = 0;
    }

    public void committedTodo() {
        this.numCommittedTodos++;
    }

    public void committedHack() {
        this.numCommittedHacks++;
    }

    public void fixedTodo() {
        this.numFixedTodos++;
    }

    public void fixedHack() {
        this.numFixedHacks++;
    }

    public int getNumCommittedTodos() {
        return this.numCommittedTodos;
    }

    public int getNumCommittedHacks() {
        return this.numCommittedHacks;
    }

    public int getNumFixedTodos() {
        return this.numFixedTodos;
    }

    public int getNumFixedHacks() {
        return this.numFixedHacks;
    }
}

