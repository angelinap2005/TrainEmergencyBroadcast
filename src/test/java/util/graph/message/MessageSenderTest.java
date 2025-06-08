package util.graph.message;

import org.graphstream.graph.*;
import org.graphstream.graph.implementations.SingleGraph;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class MessageSenderTest {

    private MessageSender messageSender;
    private Graph testGraph;

    @Before
    public void setUp() {
        testGraph = new SingleGraph("TestGraph");
        testGraph.addNode("A");
        testGraph.addNode("B");
        testGraph.addNode("C");
        testGraph.addNode("D");

        testGraph.addEdge("AB", "A", "B");
        testGraph.addEdge("BC", "B", "C");
        testGraph.addEdge("CD", "C", "D");

        messageSender = new MessageSender(testGraph);
    }

    @Test
    public void messageSenderInitialisationTest() {
        //check if messageSender is initialised
        assertNotNull("MessageSender should be initialised", messageSender);
    }

    @Test
    public void controlWithEmptyMessageTest() {
        //test control method with empty message
        boolean result = messageSender.control("");
        assertFalse("Control should return false with empty message", result);
    }

    @Test
    public void controlWithNullMessageTest() {
        //test control method with null message
        boolean result = messageSender.control(null);
        assertFalse("Control should return false with null message", result);
    }
}