import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;
import java.io.FileReader;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException,
            SAXException, IOException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";

        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json,"data.json");

        String fileNameXml = "data.xml";
        List<Employee> listXml = parseXML(fileNameXml);
        String jsonXML = listToJson(listXml);
        writeString(jsonXML, "data1.json");
    }

    public static List<Employee> parseCSV(String[] columnMapping, String filename){
        try (CSVReader csvReader = new CSVReader(new FileReader(filename))) {
            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);
            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();
            List<Employee> staff = csv.parse();
            return staff;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String listToJson(List<Employee> list){
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    public static void writeString (String text, String fileName) {
        try (FileWriter writer = new FileWriter(fileName, false)) {
            writer.write(text);
            writer.flush();
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException,
            SAXException, IOException{
        List<Employee> staff = new ArrayList<>();
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        NodeList employeeElements = doc.getDocumentElement().getElementsByTagName("employee");
        for(int i = 0; i < employeeElements.getLength(); i++){
            Node employee = employeeElements.item(i);
            NamedNodeMap attributes = employee.getAttributes();

            String tempId = attributes.getNamedItem("id").getNodeValue();
            long id = Long.parseLong(tempId);
            String firstName = attributes.getNamedItem("firstName").getNodeValue();
            String lastName = attributes.getNamedItem("lastName").getNodeValue();
            String country = attributes.getNamedItem("country").getNodeValue();
            String tempAge = attributes.getNamedItem("age").getNodeValue();
            int age = Integer.parseInt(tempAge);

            staff.add(new Employee(id,firstName,lastName,country,age));

        }
        return staff;
    }
}
