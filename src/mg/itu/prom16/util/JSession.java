package mg.itu.prom16.util;

import java.util.HashMap;

public class JSession {
    HashMap<String,Object> values = new HashMap<String,Object>();

    public void add(String key, Object value){
        values.put(key,value);
    }

    public Object get(String key){
        return values.get(key);
    }

    public void remove(String key){
        values.remove(key);
    }   

    public void clear(){
        values.clear();
    }

    
}