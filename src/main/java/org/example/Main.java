package org.example;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        String fileName2 = "data.xml";
        String jsonName = "data.json";
        String jsonName2 = "data2.json";
        //1
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, jsonName);
        //2
        list = parseXML(fileName2);
        json = listToJson(list);
        writeString(json, jsonName2);
        //3
        json = readString(jsonName);
        list = jsonToList(json);
        for (Object i : list) {
            System.out.println(i.toString());
        }
    }

    private static List<Employee> jsonToList(String json) {
        JSONParser parser = new JSONParser();
        List<Employee> list = new ArrayList<>();
        try {
            Object obj = parser.parse(json);
            JSONArray jsonObject = (JSONArray) obj;

            GsonBuilder builder = new GsonBuilder();
            Gson gson = builder.create();
            for (Object i : jsonObject) {
                list.add(gson.fromJson(i.toString(), Employee.class));
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    private static String readString(String file) throws IOException {
        String json = new BufferedReader(new FileReader(file)).readLine();
        return json;
    }


    private static void read(Node node) {
        NodeList nodeList = node.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                System.out.println("Текущий узел: " + node_.getNodeName());
                Element element = (Element) node_;
                NamedNodeMap map = element.getAttributes();
                for (int a = 0; a < map.getLength(); a++) {
                    String attrName = map.item(a).getNodeName();
                    String attrValue = map.item(a).getNodeValue();
                    System.out.println("Атрибут: " + attrName + "; значение: " + attrValue);
                }
                read(node_);
            }
        }
    }

    private static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));
        Node root = doc.getDocumentElement();

        List<Employee> list = new ArrayList<>();

        NodeList nodeList = root.getChildNodes();
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node_ = nodeList.item(i);
            if (Node.ELEMENT_NODE == node_.getNodeType()) {
                Employee l = new Employee();

                Map<String, String> mapAttr = new HashMap<>();

                for (int o = 0; o < node_.getChildNodes().getLength(); o++) {

                    if (Node.ELEMENT_NODE == node_.getChildNodes().item(o).getNodeType()) {
                        mapAttr.put(node_.getChildNodes().item(o).getNodeName(),
                                node_.getChildNodes().item(o).getTextContent());
                        o++;
                    }
                }
                l.setId(Integer.parseInt(mapAttr.get("id")));
                l.setAge(Integer.parseInt(mapAttr.get("age")));
                l.setCountry(mapAttr.get("country"));
                l.setFirstName(mapAttr.get("firstName"));
                l.setLastName(mapAttr.get("lastName"));
                list.add(l);
            }
        }
        return list;
    }

    public static void writeString(String json, String jsonName) {
        try (FileWriter file = new
                FileWriter(jsonName)) {

            file.write(json);
            file.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private static String listToJson(List<Employee> list) {
        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.create();
        Type listType = new TypeToken<List<Employee>>() {
        }.getType();
        String json = gson.toJson(list, listType);
        return json;
    }

    private static List<Employee> parseCSV(String[] columnMapping, String fileName) {
        try (CSVReader reader = new CSVReader(new FileReader(fileName))) {
            ColumnPositionMappingStrategy<Employee> strategy =
                    new ColumnPositionMappingStrategy<>();
            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(reader)
                    .withMappingStrategy(strategy)
                    .build();

            return csv.parse();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }
}

//data.json