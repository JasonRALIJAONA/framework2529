package mg.itu.prom16.util;

import java.lang.reflect.Method;
import java.util.Collections;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

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

    public static Object convertStringToType(String value, Class<?> type) {
        if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value);
        } else if (type == long.class || type == Long.class) {
            return Long.parseLong(value);
        } else if (type == double.class || type == Double.class) {
            return Double.parseDouble(value);
        } else if (type == float.class || type == Float.class) {
            return Float.parseFloat(value);
        } else if (type == boolean.class || type == Boolean.class) {
            return Boolean.parseBoolean(value);
        } else if (type == byte.class || type == Byte.class) {
            return Byte.parseByte(value);
        } else if (type == short.class || type == Short.class) {
            return Short.parseShort(value);
        } else if (type == char.class || type == Character.class) {
            return value.charAt(0);
        } else {
            return value;
        }
    }

    public static JSession HttpToJSession(HttpServletRequest request){
        HttpSession session=request.getSession();
        JSession jSession=new JSession();
        
        Collections.list(session.getAttributeNames()).forEach(key -> {
            jSession.add(key, session.getAttribute(key));
        });

        return jSession;
    }

    public static void JSessionToHttp(JSession jSession, HttpServletRequest request){
        HttpSession session = request.getSession();
        jSession.values.forEach((key, value) -> {
            session.setAttribute(key, value);
        });
        
        // Remove any attributes that are not present in jSession
        Collections.list(session.getAttributeNames()).forEach(key -> {
            if (!jSession.values.containsKey(key)) {
                session.removeAttribute(key);
            }
        });
    }
}
