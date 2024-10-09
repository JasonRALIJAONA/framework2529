package mg.itu.prom16.util;

import java.util.ArrayList;

public class Mapping {
    String className;
    ArrayList<VerbMethod> verbMethods = new ArrayList<>();

    public String getClassName() {
        return this.className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public ArrayList<VerbMethod> getVerbMethods() {
        return this.verbMethods;
    }

    public void setVerbMethods(ArrayList<VerbMethod> verbMethods) {
        this.verbMethods = verbMethods;
    }

    public void addVerbMethod (VerbMethod vm){
        getVerbMethods().add(vm);
    }


    public Mapping () {};

    public Mapping (String classname , VerbMethod vm){
        setClassName(classname);
        getVerbMethods().add(vm);
    }

    public boolean hasVerbMethod (VerbMethod vm){
        for (VerbMethod verbMethod : getVerbMethods()) {
            if (vm.getMethodName().compareTo(verbMethod.getMethodName()) == 0 && vm.getVerb().compareTo(verbMethod.getVerb())==0) {
                return true;
            }
        }

        return false;
    }

    public VerbMethod getSingleVerbMethod (String verb){
        for (VerbMethod verbMethod : verbMethods) {
            if (verbMethod.getVerb().compareToIgnoreCase(verb) == 0) {
                return verbMethod;
            }
        }

        return null;
    }
}
