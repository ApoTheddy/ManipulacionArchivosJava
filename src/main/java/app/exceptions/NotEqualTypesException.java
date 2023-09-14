package app.exceptions;

public class NotEqualTypesException extends Exception {
    public NotEqualTypesException() {
        super("The attribute passed by parameter does not match the value of the parameter");
    }

}
