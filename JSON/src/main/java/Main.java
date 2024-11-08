import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static java.nio.file.Files.writeString;

public class Main {

    public static void main(String[] args) {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName1 = "data.csv";
        String fileNameOut = "data.json";
        String fileNameOut2 = "data2.json";

        List<Employee> list = parseCSV(columnMapping, fileName1);
        String json = listToJson(list);
        writeString(json, fileNameOut);

        String fileName2 = "data.xml";
        try {
            List<Employee> list2 = parseXML(fileName2);
            String json2 = listToJson(list2);
            writeString(json2, fileNameOut2);


        } catch (Exception e) {
            System.out.println("Ошибка при выполнении программы: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private static List<Employee> parseXML(String pathName) throws IOException, SAXException, ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(pathName));
        Node note_ = doc.getDocumentElement();
        List<Employee> employees = new ArrayList<>();
        NodeList nodeList = ((Element) note_).getElementsByTagName("employee");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (node_.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node_;
                int id = Integer.parseInt(element.getElementsByTagName("id").item(0).getTextContent());
                String firstName = element.getElementsByTagName("firstName").item(0).getTextContent();
                String lastName = element.getElementsByTagName("lastName").item(0).getTextContent();
                String country = element.getElementsByTagName("country").item(0).getTextContent();
                int age = Integer.parseInt(element.getElementsByTagName("age").item(0).getTextContent());
                Employee employee = new Employee(id, firstName, lastName, country, age);
                employees.add(employee);
            }
        }
        return employees;
    }

    private static void writeString(String text, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(text);
            writer.append('\n');
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        builder.setPrettyPrinting();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>(){}.getType();
        return gson.toJson(list, listType);
    }


    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {

        CSVReader csvReader = null;
        try {
            csvReader = new CSVReader(new FileReader(fileName));
        } catch (FileNotFoundException ex) {
            throw new RuntimeException(ex);
        }
        ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;
    }
}