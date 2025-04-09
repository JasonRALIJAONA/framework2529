package mg.itu.prom16.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

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
        Gson gson = new GsonBuilder()
        .registerTypeAdapter(LocalDate.class, new GsonAdapters.LocalDateSerializer())
        .registerTypeAdapter(LocalDate.class, new GsonAdapters.LocalDateDeserializer())
        .registerTypeAdapter(LocalDateTime.class, new GsonAdapters.LocalDateTimeSerializer())
        .registerTypeAdapter(LocalDateTime.class, new GsonAdapters.LocalDateTimeDeserializer())
        .create();
        return gson.toJson(this.data);
    }

}
