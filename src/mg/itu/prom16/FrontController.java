package mg.itu.prom16;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import mg.itu.prom16.annotation.Controller;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet {
    boolean checked;
    ArrayList<String> controllerList;

    public ArrayList<String> getControllerList() {
        return this.controllerList;
    }

    public void setControllerList(ArrayList<String> controllerList) {
        this.controllerList = controllerList;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    @Override
    public void doGet (HttpServletRequest req , HttpServletResponse res) throws ServletException, IOException {
        processRequest(req , res);
    }

    @Override
    public void doPost (HttpServletRequest req , HttpServletResponse res) throws ServletException, IOException {
        processRequest(req , res);
    }

    public void processRequest (HttpServletRequest req , HttpServletResponse res)throws ServletException, IOException{
        res.setContentType("text/html");
        PrintWriter out = res.getWriter();
        // String message = req.getRequestURL().toString();
        out.println("<HTML>");
        out.println("<HEAD><TITLE>HELLO WORLD</TITLE></HEAD>");
        out.println("<BODY>");

        if (!isChecked()) {
            try {
                getClasses();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        for (String str : this.getControllerList()){
            out.println("<p>"+ str +"</p>");
        }
        out.println("</BODY></HTML>");
    }

    public void getClasses() throws ClassNotFoundException, IOException {
        ArrayList<String> classnames=new ArrayList<>();
        ServletContext context = getServletContext();
        String packageName=context.getInitParameter("controller");
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);
        List<File> directories = new ArrayList<>();
        
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            directories.add(new File(resource.getFile()));
        }
        
        ArrayList<Class<?>> classes = new ArrayList<>();
        for (File directory : directories) {
            classes.addAll(findClasses(directory, packageName));
        }

        for (Class<?> clazz : classes) {
            if (clazz.isAnnotationPresent(Controller.class)) {
                classnames.add(clazz.getName());
            }
        }

        this.setControllerList(classnames);
     
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