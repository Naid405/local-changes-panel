package isemenov.ide;

public class IDENotStartedException extends RuntimeException {
    public IDENotStartedException() {
        super("IDE not started yet");
    }
}
