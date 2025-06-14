package util;

import dto.RailLine;
import dto.RailStation;
import lombok.Getter;
import org.w3c.dom.*;

import java.util.ArrayList;

/*For file parsing code was taken from:
 * https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
 * https://www.geeksforgeeks.org/java-program-to-extract-content-from-a-xml-document/
 */
@Getter
public class FileParser {
    private ArrayList<RailLine> railLines;
    private ArrayList<RailStation> railStations;

    public FileParser() {
        this.railLines = new ArrayList<>();
        this.railStations = new ArrayList<>();
    }


    public void traverse(Document doc) {
        if(doc == null || doc.getDocumentElement() == null || doc.getElementsByTagName("kml").getLength() == 0 || doc.getElementsByTagName("Document").getLength() == 0 || doc.getElementsByTagName("name").getLength() == 0) {
            System.out.println("Document is invalid or does not contain expected elements.");
            return;
        }
        Element kmlElement = (Element) doc.getElementsByTagName("kml").item(0);
        Element documentElement = (Element) kmlElement.getElementsByTagName("Document").item(0);
        String docName = documentElement.getElementsByTagName("name").item(0).getTextContent();
        //check document name
        if ("London Train Lines".equals(docName)) {
            //parse the document for rail lines
            railLineParser(doc);
        } else if ("London stations".equals(docName)) {
            //parse the document for rail stations
            railStationParser(doc);
        } else {
            System.out.println("Unknown document name: " + docName);
        }
    }

    private void railLineParser(Document doc) {
        NodeList nodeList = doc.getElementsByTagName("Placemark");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                RailLine railLine = new RailLine();
                Element tElement = (Element) node;
                railLine.setName(tElement.getElementsByTagName("name").item(0).getTextContent());
                String coordinates = tElement.getElementsByTagName("coordinates").item(0).getTextContent().trim();
                String[] coordinatesStringArray = coordinates.split(",");
                Double[] coordinatesArray = new Double[2];
                for(int j = 0; j < coordinatesArray.length; j++) {
                    if(coordinatesStringArray[j].length() > 1){
                        if(coordinatesArray[j] == null){
                            coordinatesArray[j] = Double.parseDouble(coordinatesStringArray[j]);
                        }
                    }
                }
                railLine.setCoordinates(coordinatesArray);
                railLines.add(railLine);
            }
        }
    }

    private void railStationParser(Document doc) {
        NodeList nodeList = doc.getElementsByTagName("Placemark");
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                RailStation railStation = new RailStation();
                Element tElement = (Element) node;
                railStation.setName(tElement.getElementsByTagName("name").item(0).getTextContent());
                String coordinates = tElement.getElementsByTagName("coordinates").item(0).getTextContent().trim();
                String[] coordinatesStringArray = coordinates.split(",");
                Double[] coordinatesArray = new Double[2];
                for(int j = 0; j < coordinatesArray.length; j++) {
                    if(coordinatesStringArray[j].length() > 1){
                        if(coordinatesArray[j] == null){
                            coordinatesArray[j] = Double.parseDouble(coordinatesStringArray[j]);
                        }
                    }
                }
                railStation.setCoordinates(coordinatesArray);
                railStations.add(railStation);
            }
        }
    }
}