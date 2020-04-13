package test;

import main.java.externalDatabaseParser;
import org.junit.Test;
import org.json.*;

import java.io.BufferedReader;
import java.io.FileReader;

import static main.java.externalDatabaseParser.readJsonFromUrl;

public class externalParserUnit {
    @Test
    public void testNullName() throws Exception{
        externalDatabaseParser e = new externalDatabaseParser();
        JSONObject j = null;
        assert(e.getMoleculeName(j, 0) == "");
    }

    @Test
    public void testName() throws Exception{
        JSONObject json = readJsonFromUrl("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/CID/962/record/JSON/?record_type=2d&response_type=display");
        externalDatabaseParser e = new externalDatabaseParser();
        assert(e.getMoleculeName(json, 0).equals("water"));
    }

    @Test
    public void testNumberOfVertices() throws Exception{
        JSONObject json = readJsonFromUrl("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/CID/962/record/JSON/?record_type=2d&response_type=display");
        externalDatabaseParser e = new externalDatabaseParser();
        assert(e.getNumberOfVertices(json, 0).equals(3));
    }

    @Test
    public void testGetElements() throws Exception{
        JSONObject json = readJsonFromUrl("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/CID/962/record/JSON/?record_type=2d&response_type=display");
        externalDatabaseParser e = new externalDatabaseParser();
        assert(e.getElements(json, 0).size() == 3);
    }

    @Test
    public void testGetBonds() throws Exception{
        JSONObject json = readJsonFromUrl("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/CID/962/record/JSON/?record_type=2d&response_type=display");
        externalDatabaseParser e = new externalDatabaseParser();
        assert(e.getBonds(json, 0).size() == 2);
    }
}
