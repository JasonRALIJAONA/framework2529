package mg.itu.prom16.util;

import java.util.HashMap;

import com.google.gson.Gson;

public class ModelView {
    String url;
    HashMap<String, Object> data = new HashMap<>();

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setData (HashMap <String , Object> data){
        this.data=data;
    }

    public HashMap<String , Object> getData(){
        return this.data;
    }

    public ModelView(){}

    public ModelView (String url){
        this.setUrl(url);
    }

    public void addObject (String key , Object obj){
        this.getData().put(key, obj);
    }

    public String getDataAsJson() {
        Gson gson = new Gson();
        return gson.toJson(this.data);
    }

}
