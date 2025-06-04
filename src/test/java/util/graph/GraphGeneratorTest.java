package java.util.graph;

import dto.RailStation;
import dto.Station;
import org.graphstream.graph.Graph;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import util.graph.GraphGenerator;
import util.graph.GraphObjectGenerator;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GraphGeneratorTest {

    @Mock
    private GraphObjectGenerator graphObjectGenerator;

    private GraphGenerator graphGenerator;
    private List<Station> stations;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        stations = new ArrayList<>();
        Station station1 = mock(Station.class);
        Station station2 = mock(Station.class);
        Station station3 = mock(Station.class);

        RailStation railStation1 = mock(RailStation.class);
        RailStation railStation2 = mock(RailStation.class);
        RailStation railStation3 = mock(RailStation.class);

        when(railStation1.getName()).thenReturn("Station A");
        when(railStation2.getName()).thenReturn("Station B");
        when(railStation3.getName()).thenReturn("Station C");

        when(station1.getRailStation()).thenReturn(railStation1);
        when(station2.getRailStation()).thenReturn(railStation2);
        when(station3.getRailStation()).thenReturn(railStation3);

        stations.add(station1);
        stations.add(station2);
        stations.add(station3);

        graphGenerator = new GraphGenerator(graphObjectGenerator);

        when(graphObjectGenerator.getStations()).thenReturn(stations);
    }

    @Test
    public void testGenerateGraph_ValidStations() {
        Graph result = graphGenerator.generateGraph(stations);
        assertNotNull("Generated graph should not be null", result);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGenerateGraph_NullStations() {
        graphGenerator.generateGraph(null);
    }
}