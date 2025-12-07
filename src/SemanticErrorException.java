public class SemanticErrorException extends RuntimeException 
{
    private final int line;

    public SemanticErrorException(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}