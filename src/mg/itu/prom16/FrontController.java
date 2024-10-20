package mg.itu.prom16;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import mg.itu.prom16.annotation.Controller;
import mg.itu.prom16.annotation.JPost;
import mg.itu.prom16.annotation.JRequestFile;
import mg.itu.prom16.annotation.JRequestObject;
import mg.itu.prom16.annotation.JRequestParam;
import mg.itu.prom16.annotation.Restapi;
import mg.itu.prom16.annotation.Url;
import mg.itu.prom16.util.Function;
import mg.itu.prom16.util.JFile;
import mg.itu.prom16.util.JSession;
import mg.itu.prom16.util.Mapping;
import mg.itu.prom16.util.ModelView;
import mg.itu.prom16.util.VerbMethod;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.google.gson.Gson;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

@MultipartConfig
public class FrontController extends HttpServlet {
    ArrayList<String> controllerList;
    HashMap <String , Mapping> map = new HashMap<>();

    public HashMap<String , Mapping>  getMap (){
        return this.map;
    }

    public void setMap(HashMap<String , Mapping> mp){
        this.map=mp;
    }

    public ArrayList<String> getControllerList() {
        return this.controllerList;
    }

    public void setControllerList(ArrayList<String> controllerList) {
        this.controllerList = controllerList;
    }

    @Override
    public void doGet (HttpServletRequest req , HttpServletResponse res) throws ServletException, IOException {
        processRequest(req , res);
    }

    @Override
    public void doPost (HttpServletRequest req , HttpServletResponse res) throws ServletException, IOException {
        processRequest(req , res);
    }

    @Override
    public void init() throws ServletException {
        super.init();
        try {
            getClasses();
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    public void processRequest (HttpServletRequest req , HttpServletResponse res)throws ServletException, IOException{
        PrintWriter out = res.getWriter();

        String message = req.getRequestURL().toString();
        int lastINdex=message.lastIndexOf("/");

        String path="";

        if (lastINdex != -1) {
            path=message.substring(lastINdex + 1);
        }

        // for (String str : this.getControllerList()){
        //     out.println("<p>"+ str +"</p>");
        // }
         
        if (getMap().containsKey(path)) {
            Mapping mp= getMap().get(path);

            Object result=null;
            boolean estRestApi=false;
            try {
                VerbMethod single =  mp.getSingleVerbMethod(req.getMethod());

                Method method=Function.findMethod(mp.getClassName(), single);

                if (method == null) {
                    throw new ServletException("ETU002529 : La methode "+single.getMethodName()+" avec le verbe "+req.getMethod()+" n'existe pas");
                }

                estRestApi = method.isAnnotationPresent(Restapi.class);
                if (method != null) {
                    Paranamer paranamer = new AdaptiveParanamer();
                    Parameter[] parameters=method.getParameters();
                    String[] parameterNames=paranamer.lookupParameterNames(method);

                    Object[] args=new Object[parameters.length];

                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        if (parameter.getType().isPrimitive() || parameter.getType().equals(String.class)) {
                            if(!(parameter.isAnnotationPresent(JRequestParam.class))) {
                                throw new ServletException("ETU002529 : le parametre:\"" +parameterNames[i]+"\" n'est pas annotee");
                            }else if(parameter.isAnnotationPresent(JRequestParam.class)) {
                                JRequestParam jrp=parameter.getAnnotation(JRequestParam.class);
                                String value="";
                                if (jrp.value().isEmpty()) {
                                    value=req.getParameter(parameterNames[i]);
                                }else{
                                    value=req.getParameter(jrp.value());
                                }
                                Object convertedValue = Function.convertStringToType(value, parameter.getType());
                                args[i]=convertedValue;
                            }
                        }else if(parameter.getType().equals(JSession.class)){
                            args[i]=Function.HttpToJSession(req);
                        }else if (parameter.getType().equals(JFile.class)) {
                            if (!(parameter.isAnnotationPresent(JRequestFile.class))) {
                                throw new ServletException("ETU002529 : le parametre:\"" +parameterNames[i]+"\" n'est pas annotee");    
                            }else{
                                JRequestFile jrf=parameter.getAnnotation(JRequestFile.class);
                                JFile jf=new JFile();
                                String name="";
                                if (jrf.value().isEmpty()) {
                                    name=parameterNames[i];
                                }else{
                                    name=jrf.value();
                                }
                                jf.setFilename(req.getPart(name).getSubmittedFileName());
                                jf.setFilecontent(req.getPart(name).getInputStream());
                                args[i]=jf;
                            }
                        }else{
                            String prefix="";
                            if(!(parameter.isAnnotationPresent(JRequestObject.class))) {
                                throw new ServletException("ETU002529 : le parametre:\"" +parameterNames[i]+"\" n'est pas annotee");
                            }else {
                                if (parameter.getAnnotation(JRequestObject.class).value().isEmpty()) {
                                    prefix=parameterNames[i];
                                }else{
                                    prefix=parameter.getAnnotation(JRequestObject.class).value();
                                }
                            }

                            Object obj=parameter.getType().getDeclaredConstructor().newInstance();
                            // get the attributes of the object
                            Field[] fields=parameter.getType().getDeclaredFields();
                            for (Field field : fields) {
                                Method meth=parameter.getType().getMethod("set"+Function.capitalize(field.getName()), field.getType());
                                String value=req.getParameter(prefix+"."+field.getName());
                                Object convertedValue = Function.convertStringToType(value, field.getType());
                                meth.invoke(obj, convertedValue);
                            }

                            args[i]=obj;
                        }
                    }

                    result=method.invoke(Class.forName(mp.getClassName()).getDeclaredConstructor().newInstance(), args);

                    // return to httpsession
                    for (Object obj: args){
                        if (obj instanceof JSession) {
                            Function.JSessionToHttp((JSession)obj, req);
                        }
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException(e);
            }
            if (estRestApi) {
                res.setContentType("application/json");

                if (result instanceof String) {
                    Gson gson = new Gson();
                    String json = gson.toJson(result);

                    out.println(json);
                }else if (result instanceof ModelView){
                    String json= ((ModelView)result).getDataAsJson();

                    out.println(json);                    
                }else{
                    throw new ServletException("Invalid return type.");
                }
                
            }else{
                if (result instanceof String) {
                    out.println("Controller: "+ mp.getClassName());
                    out.println("Method: "+ mp.getVerbMethods().get(0).getMethodName());
                    out.println("Result: "+ result);
                }else if (result instanceof ModelView){
                    String url= ((ModelView)result).getUrl();
                    
                    HashMap<String , Object> data= ((ModelView)result).getData();
                    
                    for (String key : data.keySet()) {
                        Object value = data.get(key);
                        req.setAttribute(key, value);
                    }
    
                    req.getRequestDispatcher(url).forward(req, res);    
                }else{
                    throw new ServletException("Invalid return type.");
                }
            }
        }else{
            res.setStatus(HttpServletResponse.SC_NOT_FOUND);
    
            // Write a custom message to the response body
            res.setContentType("text/plain");
            out.println("404 tsy hita : Tsy hita anaty serveur ny rohy nangatahinao");
            out.flush();
        }
        
        // out.println("number of classes"+ this.getControllerList().size());
    }


    public void getClasses() throws Exception{

        ArrayList<String> classnames=new ArrayList<>();
        ServletContext context = getServletContext();
        String packageName=context.getInitParameter("controller");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> directories = new ArrayList<>();
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String decodePath=URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8.name());
            decodePath=decodePath.substring(1);
            directories.add(new File(decodePath));
        }
        
        if (directories.isEmpty()) {
            throw new Exception("The controller package is empty or does not exists.");
        }

        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : directories) {
            classes.addAll(findClasses(directory, packageName));
        }

        // for (Class<?> clazz : classes) {
        //     if (clazz.isAnnotationPresent(Controller.class)) {
        //         classnames.add(clazz.getName());
        //     }
        // }

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                Method[] methods=clazz.getDeclaredMethods();

                for (Method method : methods) {
                    if (method.isAnnotationPresent(Url.class)) {

                        String verb = "GET";
                        if (method.isAnnotationPresent(JPost.class)) {
                            verb = "POST";
                        }

                        Url url = method.getAnnotation(Url.class);
                        if (url.value().isEmpty() == false) {
                            VerbMethod vm = new VerbMethod(verb , method.getName());
                            Mapping mapping=new Mapping(clazz.getName(),vm);
                            if (this.getMap().containsKey(url.value())) {
                                Mapping temp =this.getMap().get(url.value());

                                // throw exception if the url is in another class
                                if (temp.getClassName().equals(clazz.getName()) == false) {
                                    throw new Exception("The URL \""+ url.value() +"\" is used in more than one class : "+ clazz.getName() + " and "+ temp.getClassName());
                                }

                                if (temp.hasVerbMethod(vm) == false) {
                                    temp.addVerbMethod(vm);
                                }else{
                                    throw new Exception("The URL \""+ url.value() +"\" is already used with the verb '"+verb+"'");
                                }
                            }else{
                                this.getMap().put(url.value(), mapping);
                            }
                        }
                    }
                }
                classnames.add(clazz.getName());
            }
        }

        this.setControllerList(classnames);
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws Exception {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            throw new Exception("Directory :" + directory.getName() + " does not exist.");
        }
        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
            }
        }
        return classes;
    }
}