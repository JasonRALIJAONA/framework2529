package mg.itu.prom16.util;

public class VerbMethod{
    String MethodName, verb;

    public String getVerb() {
        return this.verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getMethodName() {
        return this.MethodName;
    }

    public void setMethodName(String MethodName) {
        this.MethodName = MethodName;
    }

    public VerbMethod(){}

    public VerbMethod (String verb ,String method){
        setVerb(verb);
        setMethodName(method);
    }
}