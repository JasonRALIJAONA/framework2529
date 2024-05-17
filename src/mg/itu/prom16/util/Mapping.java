package mg.itu.prom16.util;

public class Mapping {
    String className;
    String MethodName;

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

    public Mapping(String className , String MethodName){
        setClassName(className);
        setMethodName(MethodName);
    }
}
