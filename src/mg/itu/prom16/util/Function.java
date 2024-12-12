package mg.itu.prom16.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collections;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import mg.itu.prom16.Exception.ValidationException;
import mg.itu.prom16.annotation.field.Number;
import mg.itu.prom16.annotation.field.Range;

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

    public static Method findMethod(String className, VerbMethod vm) {
        try {
            Class<?> clazz = Class.forName(className);
            Method[] methods = clazz.getMethods();
            for (Method method : methods) {
                if (method.getName().equals(vm.getMethodName())) {
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

    public static void checkField(Field field , Object obj)throws Exception{
        ValidationException validationException = new ValidationException();
        if(field.isAnnotationPresent(Number.class)){
           double value=((java.lang.Number)obj).doubleValue();
            if (field.isAnnotationPresent(Range.class)) {
                Range range = field.getAnnotation(Range.class);
                if (value < range.min()) {
                    validationException.addError("La valeur est en dessous du minimum qui est : "+range.min());
                }
                if (value > range.max()) {
                    validationException.addError("La valeur est au dessus du maximum qui est : "+range.max());
                }
            }
        }

        if (field.isAnnotationPresent(Range.class) && !field.isAnnotationPresent(Number.class)) {
            validationException.addError("L'annotation Range ne peut être utilisée que quand l'annotation Number est utilisée");
        }

        if (!validationException.getErrors().isEmpty()) {
            throw validationException;
        }
    }
}
