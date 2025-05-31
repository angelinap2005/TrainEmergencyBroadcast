package util.graph;

import dto.Route;
import dto.Station;
import lombok.Getter;
import lombok.Setter;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.IdAlreadyInUseException;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.SingleGraph;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/*For graph generation, code was taken from
* https://graphstream-project.org/doc/Tutorials/Getting-Started/
* https://graphstream-project.org/doc/Tutorials/Graph-Visualisation/
* https://stackoverflow.com/questions/67331322/show-the-names-of-nodes-and-edges-using-graphstream-in-scala
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
        System.setProperty("org.graphstream.ui", "swing");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        this.graph = new SingleGraph("Train Graph");
        this.processedEdges = new HashSet<>();
    }


    public Graph generateGraph(List<Station> stations) {
        this.stations = stations;
        if (stations == null) {
            throw new IllegalArgumentException("Stations list cannot be null");
        }

        try {
            //clear the graph
            graph.clear();
            processedEdges.clear();

            //add graph nodes
            addNodes(stations);
            //add graph edges
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
                    Node node = graph.addNode(nodeName);
                    //set the node label to station name
                    node.setAttribute("ui.label", nodeName);
                } catch (IdAlreadyInUseException e) {
                    //if node already exists, ignore
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
                    if (isValidRoute(route)) {
                        String destStation = route.getDestination().getName();
                        addEdgeSafely(sourceStation, destStation);
                    }
                } catch (Exception e) {
                    System.err.println("Error processing route from " + sourceStation + ": " + e.getMessage());
                }
            }
        }
    }

    private boolean isValidStation(Station station) {
        return station != null &&
                station.getRailStation() != null &&
                station.getRailStation().getName() != null &&
                station.getRoutes() != null;
    }

    private boolean isValidRoute(Route route) {
        return route != null && route.getRailLine() != null && route.getRailLine().getName() != null && route.getDestination() != null && route.getDestination().getName() != null;
    }

    private void addEdgeSafely(String sourceStation, String destStation) {
        String edgeId = sourceStation + "--" + destStation;
        String reverseEdgeId = destStation + "--" + sourceStation;

        if (processedEdges.contains(edgeId) || processedEdges.contains(reverseEdgeId)) {
            //if edge already exists, ignore
            return;
        }

        try {
            Edge edge = graph.addEdge(edgeId, sourceStation, destStation, false);
            if (edge != null) {
                //get weight from distance map
                Double weight = graphObjectGenerator.getStationDistances().getOrDefault(sourceStation, new HashMap<>()).getOrDefault(destStation, 1.0);

                //set length as weight attribute
                edge.setAttribute("length", weight);
                processedEdges.add(edgeId);
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not create edge between " + sourceStation + " and " + destStation);
        }
    }
}