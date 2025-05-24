import dto.RailLine;
import dto.RailStation;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import util.AddRailLines;
import util.FileParser;
import util.graph.GraphGenerator;
import util.graph.GraphObjectGenerator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class EmergencySystem {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {
        System.out.println("Emergency System is starting...");

        File railLinesPath = null;
        File railStationsPath = null;

        //parse arguments
        for (String arg : args) {
            if (arg.startsWith("railLines=")) {
                railLinesPath = new File(arg.substring("railLines=".length()));
            } else if (arg.startsWith("railStations=")) {
                railStationsPath = new File(arg.substring("railStations=".length()));
            }
        }

        if (railLinesPath == null || railStationsPath == null) {
            System.err.println("Files not found");
            System.exit(1);
        }

        FileParser parser = new FileParser();
        parser.traverse(parseDoc(railLinesPath));
        parser.traverse(parseDoc(railStationsPath));

        //set rail lines
        setRailLines(parser.getRailLines(), parser.getRailStations());
        //generate graph objects
        GraphObjectGenerator graphObjectGenerator = new GraphObjectGenerator(parser.getRailLines(), parser.getRailStations(), parser.getRailLinesNames());
        graphObjectGenerator.controller();
        //generate graph
        GraphGenerator graphGenerator = new GraphGenerator(graphObjectGenerator);
        graphGenerator.generateGraph(graphObjectGenerator.getStations());
        graphGenerator.printEntireMap();
    }

    private static Document parseDoc(File fileKML) throws ParserConfigurationException, IOException, SAXException {
        //parse kml
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document doc = db.parse(fileKML);
        doc.getDocumentElement().normalize();
        return doc;
    }

    private static void setRailLines(ArrayList<RailLine> railLines, ArrayList<RailStation> railStations) {
        AddRailLines addRailLines = new AddRailLines();
        addRailLines.addLines(railLines, railStations);
    }
}
