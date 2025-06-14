import dto.RailLine;
import dto.RailStation;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import userControl.UserControl;
import util.DocumentParser;
import util.FileParser;
import util.graph.GraphGenerator;
import util.graph.GraphObjectGenerator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/*Code references:
 * https://graphstream-project.org/doc/
 * https://www.tutorialspoint.com/java_xml/java_dom_parse_document.htm
 * https://www.geeksforgeeks.org/java-program-to-extract-content-from-a-xml-document/
 * https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
 * https://www.geodatasource.com/resources/tutorials/how-to-calculate-the-distance-between-2-locations-using-java
 * https://www.geeksforgeeks.org/haversine-formula-to-find-distance-between-two-points-on-a-sphere/
 * https://graphstream-project.org/doc/Algorithms/Shortest-path/Dijkstra/
 * https://graphstream-project.org/doc/Tutorials/Storing-retrieving-and-displaying-data-in-graphs/#an-example-using-attributes-and-the-viewer
 * https://favtutor.com/blogs/breadth-first-search-java
 * https://www.geeksforgeeks.org/breadth-first-search-or-bfs-for-a-graph/
 */

public class EmergencySystem {
    public static void main(String[] args) throws IOException, ParserConfigurationException, SAXException {
        try{
            System.out.println("Emergency System is starting...");
            File railLinesPath = new File(args[0]);
            File railStationsPath = new File(args[1]);

            if (railLinesPath == null || railStationsPath == null) {
                System.err.println("Files not found");
                return;
            }

            FileParser parser = new FileParser();
            parser.traverse(parseDoc(railLinesPath));
            parser.traverse(parseDoc(railStationsPath));

            //set rail lines
            setRailLines(parser.getRailLines(), parser.getRailStations());
            //generate graph objects
            GraphObjectGenerator graphObjectGenerator = new GraphObjectGenerator(parser.getRailLines(), parser.getRailStations());
            graphObjectGenerator.controller();
            //generate graph
            GraphGenerator graphGenerator = new GraphGenerator(graphObjectGenerator);
            graphGenerator.generateGraph(graphObjectGenerator.getStations());
            //pass to user control
            UserControl userControl = new UserControl(graphGenerator);
            userControl.start();
        }catch (Exception e){
            System.err.println("An error occurred: " + e.getMessage());
        }
    }

    public static void setRailLines(ArrayList<RailLine> railLines, ArrayList<RailStation> railStations) {
        for (RailStation station : railStations) {
            ArrayList<RailLine> lines = new ArrayList<>();
            String stationName = station.getName();

            for (RailLine line : railLines) {
                //check if the name of the line contains the name of the station
                if (line.getName() != null && line.getName().contains(stationName)) {
                    lines.add(line);
                }
            }
            station.setRailLines(lines);
        }
    }

    private static Document parseDoc(File fileKML) throws ParserConfigurationException, IOException, SAXException {
        return DocumentParser.parseDocument(fileKML);
    }
}
