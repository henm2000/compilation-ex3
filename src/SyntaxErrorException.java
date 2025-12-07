public class SyntaxErrorException extends RuntimeException 
{
    private final int line;

    public SyntaxErrorException(int line) {
        this.line = line;
    }

    public int getLine() {
        return line;
    }
}