package jfl;

public class FListException extends Exception{
    String message;
    
    public FListException(String errorMessage) {
        message=errorMessage;
    }
    
    @Override public String toString() {
        return "FListException: "+message;
    }
}
