package util.graph.message;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.Viewer;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

/*Sources for graph visualisation:
* https://graphstream-project.org/doc/Tutorials/Graph-Visualisation/
* https://www.geeksforgeeks.org/breadth-first-search-or-bfs-for-a-graph/
* https://github.com/LuisRobaina/BFS-DFS-DEMO/blob/master/Animation.java
* https://graphstream-project.org/gs-core/index.html?org%2Fgraphstream%2Fui%2Flayout%2FLayout.html=
* https://graphstream-project.org/doc/Tutorials/Getting-Started/
*/

public class MessageSender {
    private String message;
    private Graph graph;
    public MessageSender(Graph graph) {
        this.graph = graph;
    }


    public boolean control(String message) {
        boolean result = false;
        this.message = message;
        sendMessage();
        result = broadcastMessageVisualisation();
        return result;
    }

    private void sendMessage() {
        Timestamp start;
        Timestamp end;
        int totalTime;
        if (message != null && !message.isEmpty()) {
            System.out.println("Broadcasting message: " + message);
            start = new Timestamp(System.currentTimeMillis());
            graph.nodes().forEach(node -> {
                // Here you would implement the logic to send the message to each node
                System.out.println("Sending to station: " + node.getId());
            });
            end = new Timestamp(System.currentTimeMillis());
            totalTime = (int) (end.getTime() - start.getTime());
            System.out.println("Message sent successfully in " + totalTime + " milliseconds.");
        } else {
            System.out.println("No message to send.");
        }
    }

    private boolean broadcastMessageVisualisation() {
        if (message == null || message.isEmpty()) {
            System.out.println("No message to visualise.");
            return false;
        }

        //reset graph styles
        graph.nodes().forEach(node -> {
            node.setAttribute("ui.style", "fill-color: #666666; size: 10px;");
            node.setAttribute("ui.label", node.getId());
        });

        graph.edges().forEach(edge -> {
            edge.setAttribute("ui.style", "fill-color: #333333; size: 2px;");
        });

        //display the graph
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        //get start node
        Node startNode = graph.nodes().findFirst().orElse(null);
        if (startNode == null) return false;

        //create a thread for the animation
        new Thread(() -> {
            try {
                //mark the start node
                startNode.setAttribute("ui.style", "fill-color: red; size: 15px;");
                startNode.setAttribute("ui.label", "START: " + message);
                Thread.sleep(400);

                //BFS animation
                Queue<Node> queue = new LinkedList<>();
                Set<String> visited = new HashSet<>();

                queue.add(startNode);
                visited.add(startNode.getId());

                while (!queue.isEmpty()) {
                    Node current = queue.poll();

                    //process each edge from the current node
                    for (Edge edge :current.edges().toList()) {
                        Node neighbor = edge.getOpposite(current);

                        if (!visited.contains(neighbor.getId())) {
                            //animate edge
                            edge.setAttribute("ui.style", "fill-color: red; size: 3px;");
                            Thread.sleep(200);

                            //animate node receiving the message
                            neighbor.setAttribute("ui.style", "fill-color: red; size: 12px;");
                            neighbor.setAttribute("ui.label", "Emergency: " + message);

                            visited.add(neighbor.getId());
                            queue.add(neighbor);

                            Thread.sleep(100);
                        }
                    }
                }

                System.out.println("Message animation completed on all stations.");
            } catch (InterruptedException e) {
                System.err.println("Animation interrupted: " + e.getMessage());
            }
        }).start();

        return true;
    }
}
