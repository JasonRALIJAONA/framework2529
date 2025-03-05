package mg.itu.prom16.util;

import java.lang.reflect.Field;

public class JService {
    public static boolean isValid (Object o){
        Field[] fields=o.getClass().getDeclaredFields();

        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Function.checkField(field, field.get(o));
                field.setAccessible(false);
            } catch (Exception e) {
                return false;
            }   
        }

        return true;
    }


}
