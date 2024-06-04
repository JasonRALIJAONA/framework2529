package mg.itu.prom16;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import mg.itu.prom16.annotation.Controller;
import mg.itu.prom16.annotation.JGet;
import mg.itu.prom16.util.Mapping;
import mg.itu.prom16.util.ModelView;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    ArrayList<String> controllerList;
    HashMap <String , Mapping> map = new HashMap<>();
    ArrayList<String>exceptions = new ArrayList<>();

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
            exceptions.add(e.getMessage());
        }
    }

    public void processRequest (HttpServletRequest req , HttpServletResponse res)throws ServletException, IOException{
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();

        String message = req.getRequestURL().toString();
        int lastINdex=message.lastIndexOf("/");

        String path="";

        if (lastINdex != -1) {
            path=message.substring(lastINdex + 1);
        }

        out.println("<HTML>");
        out.println("<HEAD><TITLE>HELLO WORLD</TITLE></HEAD>");
        out.println("<BODY>");

        // for (String str : this.getControllerList()){
        //     out.println("<p>"+ str +"</p>");
        // }
         
        if (getMap().containsKey(path)) {
            Mapping mp= getMap().get(path);
            out.println("<BIG> Controller: "+ mp.getClassName() +"</BIG>");
            out.println("<br>");
            out.println("<BIG> Method: "+ mp.getMethodName() +"</BIG>");

            Object result=null;
            try {
                result=invokeMethod(mp.getClassName(), mp.getMethodName());
            } catch (Exception e) {
                @SuppressWarnings("unchecked")
                ArrayList<String> exceptions = (ArrayList<String>) getServletContext().getAttribute("errors");
                exceptions.add(e.getMessage());
            }

            if (result instanceof String) {
                out.println("<br>");
                out.println("<BIG> Result: "+ result +"</BIG>");
            }else if (result instanceof ModelView){
                String url= ((ModelView)result).getUrl();
                
                HashMap<String , Object> data= ((ModelView)result).getData();
                
                for (String key : data.keySet()) {
                    Object value = data.get(key);
                    req.setAttribute(key, value);
                }

                req.getRequestDispatcher(url).forward(req, res);    
            }else{
                exceptions.add("The return type of the method is not supported.");
            }
        }else{
            out.println("<BIG> Method not found</BIG>");
        }
        
        // out.println("<p> number of classes"+ this.getControllerList().size() +"</p>");

        for (String str : exceptions){
            out.println("<p>"+ str +"</p>");
        }
        out.println("</BODY></HTML>");
    }

    public static Object invokeMethod (String className, String methodName){
        try {
            Class<?> clazz = Class.forName(className);
            Object instance = clazz.getDeclaredConstructor().newInstance();
            Method method = clazz.getMethod(methodName);
    
            return method.invoke(instance);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public void getClasses() {
        try {
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
                                        exceptions.add("The controller with an URL "+ jget.value() +" already exists.");
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
        } catch (Exception e) {
            exceptions.add(e.getMessage());
        }
     
    }

    private static List<Class<?>> findClasses(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
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