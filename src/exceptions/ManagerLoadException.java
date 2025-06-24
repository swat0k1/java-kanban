package exceptions;

public class ManagerLoadException extends RuntimeException {

    public ManagerLoadException(String message, Throwable e) {
        super(message, e);
    }

}
