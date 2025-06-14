package util.graph;

import java.util.*;
import java.util.List;

import dto.RailLine;
import dto.RailStation;
import lombok.Getter;
import lombok.Setter;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import dto.Route;
import dto.Station;

/*Code references for graph generation:
 * https://graphstream-project.org/doc/
 * https://graphstream-project.org/doc/Tutorials/Storing-retrieving-and-displaying-data-in-graphs/#an-example-using-attributes-and-the-viewer
 */

@Getter
@Setter
public class GraphGenerator {
    private Graph graph;
    private Set<String> processedEdges;
    private GraphObjectGenerator graphObjectGenerator;
    private List<Station> stations;

    public GraphGenerator(GraphObjectGenerator graphObjectGenerator) {
        this.graphObjectGenerator = graphObjectGenerator;
        setSystemProperties();
    }

    private void setSystemProperties() {
        this.graph = new SingleGraph("Train Graph");
        this.processedEdges = new HashSet<>();

        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
    }

    public Graph generateGraph(List<Station> stations) {
        this.stations = stations;
        //check if stations list is null
        if (stations == null) {
            throw new IllegalArgumentException("Stations list cannot be null");
        }
        Graph graph = new SingleGraph("RailNetwork");

        graph.setAttribute("ui.quality");
        graph.setAttribute("ui.antialias");

        for (Station station : stations) {
            if (isValidStation(station)) {
                RailStation railStation = station.getRailStation();
                Node node = graph.addNode(railStation.getName());

                //set node attributes
                Double[] coords = railStation.getCoordinates();
                if (coords != null && coords.length >= 2) {
                    double scalingFactor = 1.5;
                    node.setAttribute("x", coords[0] * scalingFactor);
                    node.setAttribute("y", -coords[1] * scalingFactor);
                    node.setAttribute("layout.frozen");
                }

                node.setAttribute("ui.label", railStation.getName());
            }
        }
        try {
            addNodes(stations);
            addEdges(stations);
        } catch (Exception e) {
            System.err.println("Error generating graph: " + e.getMessage());
        }

        return graph;
    }

    private void addNodes(List<Station> stations) {
        for (Station station : stations) {
            if (isValidStation(station)) {
                String nodeName = station.getRailStation().getName();
                try {
                    //check if node already exists
                    Node node = graph.addNode(nodeName);
                    node.setAttribute("ui.label", nodeName);
                } catch (IdAlreadyInUseException e) {
                    System.err.println("Node " + nodeName + " already exists");
                }
            }
        }
    }

    private void addEdges(List<Station> stations) {
        for (Station station : stations) {
            if (!isValidStation(station)) continue;

            String sourceStation = station.getRailStation().getName();

            for (Route route : station.getRoutes()) {
                try {
                    //check if route is valid
                    if (isValidRoute(route)) {
                        //check if destination station is null
                        String destStation = route.getDestination().getName();
                        addEdge(sourceStation, destStation);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing route from " + sourceStation + ": " + e.getMessage());
                }
            }
        }
    }

    private boolean isValidStation(Station station) {
        //check if station is null and has valid attributes
        return station != null && station.getRailStation() != null && station.getRailStation().getName() != null && station.getRoutes() != null;
    }

    private boolean isValidRoute(Route route) {
        //check if route is null and has valid attributes
        return route != null && route.getRailLine() != null && route.getRailLine().getName() != null && route.getDestination() != null && route.getDestination().getName() != null;
    }


    private void addEdge(String sourceStation, String destStation) {
        String edgeId = sourceStation + "--" + destStation;
        String reverseEdgeId = destStation + "--" + sourceStation;

        //check if the edge has already been processed
        if (processedEdges.contains(edgeId) || processedEdges.contains(reverseEdgeId)) {
            return;
        }

        try {
            Edge edge = graph.addEdge(edgeId, sourceStation, destStation, false);
            if (edge != null) {
                //set edge attributes
                Double weight = graphObjectGenerator.getStationDistances().getOrDefault(sourceStation, new HashMap<>()).getOrDefault(destStation, 1.0);
                edge.setAttribute("length", weight);

                String lineColour = getEdgeColour(sourceStation, destStation);
                if (lineColour != null) {
                    edge.setAttribute("ui.style", "fill-color: " + lineColour + ";");
                    edge.setAttribute("original.color", lineColour);
                }

                processedEdges.add(edgeId);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not create edge between " + sourceStation + " and " + destStation);
        }
    }

    private String getEdgeColour(String sourceStation, String destStation) {
        for (Station station : stations) {
            //check if station is null
            if (station.getRailStation().getName().equals(sourceStation)) {
                for (Route route : station.getRoutes()) {
                    //check if route is null
                    if (route.getDestination().getName().equals(destStation)) {
                        RailLine railLine = route.getRailLine();
                        if (railLine != null && railLine.getColour() != null) {
                            //return the colour of the rail line
                            return railLine.getColour();
                        }
                    }
                }
            }
        }
        return "#0000FF";
    }
}