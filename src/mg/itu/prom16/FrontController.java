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
import mg.itu.prom16.annotation.JGet;
import mg.itu.prom16.annotation.JRequestObject;
import mg.itu.prom16.annotation.JRequestParam;
import mg.itu.prom16.util.Function;
import mg.itu.prom16.util.Mapping;
import mg.itu.prom16.util.ModelView;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.thoughtworks.paranamer.AdaptiveParanamer;
import com.thoughtworks.paranamer.Paranamer;

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
            try {
                Method method=Function.findMethod(mp.getClassName(), mp.getMethodName());
                if (method != null) {
                    Paranamer paranamer = new AdaptiveParanamer();
                    Parameter[] parameters=method.getParameters();
                    String[] parameterNames=paranamer.lookupParameterNames(method);

                    Object[] args=new Object[parameters.length];

                    for (int i = 0; i < parameters.length; i++) {
                        Parameter parameter = parameters[i];
                        if (parameter.getType().isPrimitive()) {
                            if(!(parameter.isAnnotationPresent(JRequestParam.class))) {
                                // get by the parameter name
                                String value = req.getParameter(parameterNames[i]);
                                args[i]=value;
                            }else if(parameter.isAnnotationPresent(JRequestParam.class)) {
                                JRequestParam jrp=parameter.getAnnotation(JRequestParam.class);
                                String value=req.getParameter(jrp.value());
                                args[i]=value;
                            }
                        }else{
                            String prefix="";
                            if(!(parameter.isAnnotationPresent(JRequestObject.class))) {
                               prefix=parameterNames[i];
                            }else {
                               prefix=parameter.getAnnotation(JRequestObject.class).value();
                            }

                            Object obj=parameter.getType().getDeclaredConstructor().newInstance();
                            // get the attributes of the object
                            Field[] fields=parameter.getType().getDeclaredFields();
                            for (Field field : fields) {
                                String value=req.getParameter(prefix+"."+field.getName());
                                Method meth=parameter.getType().getMethod("set"+Function.capitalize(field.getName()), field.getType());
                                meth.invoke(obj, value);
                            }

                            args[i]=obj;
                        }
                    }

                    result=method.invoke(Class.forName(mp.getClassName()).getDeclaredConstructor().newInstance(), args);
                }

            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException(e);
            }

            if (result instanceof String) {
                out.println("Controller: "+ mp.getClassName());
                out.println("Method: "+ mp.getMethodName());
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
        }else{
            // do a 404 error
            res.sendError(HttpServletResponse.SC_NOT_FOUND);
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
                    if (method.isAnnotationPresent(JGet.class)) {
                        JGet jget=method.getAnnotation(JGet.class);
                        if (jget.value().isEmpty() == false) {
                            Mapping mapping=new Mapping(clazz.getName(), method.getName());
                            if (this.getMap().containsKey(jget.value())) {
                                throw new Exception("The URL \""+ jget.value() +"\" is already used.");
                            }else{
                                this.getMap().put(jget.value(), mapping);
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