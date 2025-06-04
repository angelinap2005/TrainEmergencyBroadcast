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
        // Create test graph
        testGraph = new SingleGraph("TestGraph");
        testGraph.addNode("A");
        testGraph.addNode("B");
        testGraph.addNode("C");
        testGraph.addNode("D");

        testGraph.addEdge("AB", "A", "B");
        testGraph.addEdge("BC", "B", "C");
        testGraph.addEdge("CD", "C", "D");

        // Initialize real MessageSender instance
        messageSender = new MessageSender(testGraph);
    }

    @Test
    public void testMessageSenderInitialization() {
        assertNotNull("MessageSender should be initialized", messageSender);
    }

    @Test
    public void testControlWithEmptyMessage() {
        boolean result = messageSender.control("");
        assertFalse("Control should return false with empty message", result);
    }

    @Test
    public void testControlWithNullMessage() {
        boolean result = messageSender.control(null);
        assertFalse("Control should return false with null message", result);
    }
}