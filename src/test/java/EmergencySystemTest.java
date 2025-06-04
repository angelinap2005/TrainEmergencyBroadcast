import dto.RailLine;
import dto.RailStation;
import org.junit.Test;
import org.junit.Before;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import util.DocumentParser;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class EmergencySystemTest {

    private File validLinesFile;
    private File validStationsFile;
    private File invalidFile;

    @Before
    public void setUp() {
        validLinesFile = new File("src/test/resources/testLinesFile.kml");
        validStationsFile = new File("src/test/resources/testStationFile.kml");
        invalidFile = new File("src/test/resources/invalidFile.kml");
    }

    @Test
    public void testParseDocWithValidFile(){
        Document doc;
        try {
            doc = DocumentParser.parseDocument(validLinesFile);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
        assertNotNull("Document should not be null for valid file", doc);
        assertEquals("kml", doc.getDocumentElement().getNodeName());
    }

    @Test(expected = RuntimeException.class)
    public void testParseDocWithNonExistentFile(){
        try {
            DocumentParser.parseDocument(new File("nonexistent.kml"));
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (SAXException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testSetRailLines() {
        ArrayList<RailLine> railLines = new ArrayList<>();
        ArrayList<RailStation> railStations = new ArrayList<>();

        RailLine line = new RailLine();
        line.setName("Weaver - Stoke Newington to Stamford Hill");
        railLines.add(line);

        RailStation station = new RailStation();
        station.setName("Stoke Newington");
        station.setRailLines(new ArrayList<>());
        railStations.add(station);

        EmergencySystem.setRailLines(railLines, railStations);

        assertEquals(1, station.getRailLines().size());
        assertEquals("Weaver - Stoke Newington to Stamford Hill", station.getRailLines().get(0).getName());
    }

    @Test
    public void testSetRailLinesWithEmptyData() {
        ArrayList<RailLine> railLines = new ArrayList<>();
        ArrayList<RailStation> railStations = new ArrayList<>();

        EmergencySystem.setRailLines(railLines, railStations);
    }

    @Test
    public void testMainMethodArgumentValidation() {
        String[] emptyArgs = {};
        try {
            EmergencySystem.main(emptyArgs);
        } catch (Exception e) {
            assertTrue(e instanceof ArrayIndexOutOfBoundsException);
        }
    }
}