package mg.itu.prom16.util;

import java.io.FileWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.opencsv.bean.StatefulBeanToCsv;
import com.opencsv.bean.StatefulBeanToCsvBuilder;

public class CSVService {
    public static <T> void writeCsvFromList(List<T> list, String filePath) throws Exception {
        try (FileWriter writer = new FileWriter(filePath)) {
            StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer).build();
            beanToCsv.write(list);
        }
    }   

    public static <T> byte[] getCsvBytesFromList(List<T> list) throws Exception {
        StringWriter writer = new StringWriter();
        StatefulBeanToCsv<T> beanToCsv = new StatefulBeanToCsvBuilder<T>(writer).build();
        beanToCsv.write(list);
        writer.flush();
        return writer.toString().getBytes(StandardCharsets.UTF_8);
    }
}
