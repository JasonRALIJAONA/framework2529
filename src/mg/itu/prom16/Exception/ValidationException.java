package mg.itu.prom16.Exception;

import java.util.ArrayList;
import java.util.List;

public class ValidationException extends Exception{
    List<String> errors = new ArrayList<String>();

    public List<String> getErrors() {
        return this.errors;
    }

    public void addError(String error) {
        this.getErrors().add(error);
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public ValidationException (){
        super();
    }      

    public ValidationException (String message){
        super(message);
    }

    public ValidationException (String message, Throwable cause){
        super(message, cause);
    }

    public ValidationException (Throwable cause){
        super(cause);
    }


}
