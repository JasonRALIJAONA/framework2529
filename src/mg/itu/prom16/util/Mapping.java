package mg.itu.prom16.util;

public class Mapping {
    String className;
    String MethodName;
    String verb = "GET";

    public String getVerb() {
        return this.verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getMethodName() {
        return this.MethodName;
    }

    public void setMethodName(String MethodName) {
        this.MethodName = MethodName;
    }

    public Mapping(){}

    public Mapping(String className , String MethodName , String verb){
        setClassName(className);
        setMethodName(MethodName);
        setVerb(verb);
    }
}
