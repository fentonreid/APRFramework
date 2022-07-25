package GP.GP;

/**
 * The UnmodifiedProgramException class is a custom exception that mutation operators throw when they encounter a known issue with the program.
 * For instance: no nodes of a required type could be found in the AST so further modification would not be possible.
 */
public class UnmodifiedProgramException extends RuntimeException {

    /**
     * The constructor passes the error message on to the RunTimeException class to handle the logic.
     *
     * @param message   The reasoning behind why the UnmodifiedProgramException was called
     */
    public UnmodifiedProgramException(String message) {
        super(message);
    }
}