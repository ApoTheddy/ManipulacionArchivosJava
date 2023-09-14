package app.exceptions;

public class NotFoundAttributeExceptions extends Exception {
    public NotFoundAttributeExceptions() {
        super("The attribute provided by the parameter does not exist, check the class attributes");
    }
}
