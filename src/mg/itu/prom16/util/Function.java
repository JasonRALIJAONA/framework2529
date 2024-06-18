package mg.itu.prom16.util;

import java.lang.reflect.Method;
public class Function {
    public static Object invokeMethod(String className, String methodName) {
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

    public static Method findMethod(String className, String methodName) {
        try {
            Class<?> clazz = Class.forName(className);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String capitalize (String word){
        String first=word.substring(0,1);
        String others=word.substring(1);
        String result=first.toUpperCase()+others;
        return result;
    }
}
