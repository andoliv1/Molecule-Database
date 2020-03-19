import java.io.*;
import java.net.URL;
import org.json.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class externalDatabaseParser {

        public static String readResponse(Reader r) throws IOException {
                StringBuilder s = new StringBuilder();
                int cp;
                while ((cp = r.read()) != -1) {
                        s.append((char) cp);
                }
                return s.toString();
        }

        public static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
                InputStream stream = new URL(url).openStream();
                try {
                        BufferedReader buffer = new BufferedReader(new InputStreamReader(stream, Charset.forName("UTF-8")));
                        String jsonText = readResponse(buffer);
                        JSONObject json = new JSONObject(jsonText);
                        return json;
                } finally {
                        stream.close();
                }
        }

        // Grab the molecule name. Only IUAPC field has the correct name so we search for it
        public static String getMoleculeName(JSONObject json){
                JSONArray arr = json.getJSONArray("PC_Compounds");
                JSONObject obj = arr.getJSONObject(0);
                arr = obj.getJSONArray("props");
                String molecularName = "";
                for(int index=0; index<arr.length(); index++){
                        obj = arr.getJSONObject(index);
                        if(obj.has("urn")) {
                                JSONObject urn = obj.getJSONObject("urn");
                                if (urn.has("label")) {
                                        Object o = urn.get("label");
                                        if(o.equals("IUPAC Name")) {
                                                JSONObject value = obj.getJSONObject("value");
                                                molecularName = value.get("sval").toString();
                                                break;
                                        }
                                }
                        }
                }
                return molecularName;
        }

        // Grab atomic elements and get the length of the array for # of vertices
        public static Integer getNumberOfVertices(JSONObject json){
                JSONArray arr = json.getJSONArray("PC_Compounds");
                JSONObject obj = arr.getJSONObject(0);
                obj = obj.getJSONObject("atoms");
                Integer vertexCount = 0;
                JSONArray element = obj.getJSONArray("element");
                vertexCount = element.length();

                return vertexCount;
        }

        // Grab atomic elements in the compound
        public static ArrayList<Integer> getElements(JSONObject json, String [][] peridicTable)
        {
                JSONArray arr = json.getJSONArray("PC_Compounds");
                JSONObject obj = arr.getJSONObject(0);
                obj = obj.getJSONObject("atoms");
                JSONArray element = obj.getJSONArray("element");
                ArrayList<Integer> elementArrayList = new ArrayList<>();
                for (int i=0;i<element.length();i++){
                        elementArrayList.add(element.getInt(i));
                }
                return elementArrayList;
        }

        // Get bond info. The order tells you how many repetition of bond connections between
        // atom 1 to atom 2
        public static ArrayList<String> getBonds(JSONObject json, String [][] periodicTable)
        {
                try {
                        JSONArray arr = json.getJSONArray("PC_Compounds");
                        JSONObject obj = arr.getJSONObject(0);
                        JSONObject bonds = obj.getJSONObject("bonds");
                        JSONArray aid1 = bonds.getJSONArray("aid1");
                        JSONArray aid2 = bonds.getJSONArray("aid2");
                        JSONArray order = bonds.getJSONArray("order");
                        ArrayList<String> bondArrayList = new ArrayList<>();

                        for (int i = 0; i < order.length(); i++) {
                                int count = order.getInt(i);
                                while (count != 0) {
                                        count -= 1;
                                        int a1 = aid1.getInt(i) - 1;
                                        int a2 = aid2.getInt(i) - 1;
                                        String cat = Integer.toString(a1) + ' ' + Integer.toString(a2);
                                        bondArrayList.add(cat);
                                }
                        }
                        return bondArrayList;
                }
                catch (Exception e){
                        return null;
                }
        }

        // Imports CSV file of periodic elements. Only atomic number, element name, and symbols are available
        public static String[][] importPeriodicTable(String fileName) throws IOException {
                String thisLine;
                BufferedReader buffer = new BufferedReader(new FileReader(fileName));

                List<String[]> lines = new ArrayList<String[]>();
                while ((thisLine = buffer.readLine()) != null) {
                        lines.add(thisLine.split(","));
                }

                String[][] array = new String[lines.size()][0];
                lines.toArray(array);
                return array;
        }

        public static void main(String[] args) throws JSONException {
                // Number of molecules to grab
                int numOfMolecules = 5;
                int moleculeCount = 0;
                int moleculeCounter = 1;
                File file = null;
                FileWriter filewriter = null;
                try {
                        while(moleculeCount != numOfMolecules) {
                                JSONObject json = readJsonFromUrl("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/CID/" + Integer.toString(moleculeCounter) + "/record/JSON/?record_type=2d&response_type=display");
                                String moleculeName = getMoleculeName(json);
                                if (!moleculeName.contains("[") && !moleculeName.contains("(") && moleculeName.length() > 0) {
                                        String[][] periodicTable = importPeriodicTable("periodicTable.csv");
                                        String numberOfVertices = Integer.toString(getNumberOfVertices(json));
                                        ArrayList<Integer> elementsArrayList = getElements(json, periodicTable);
                                        ArrayList<String> bondArrayList = getBonds(json, periodicTable);
                                        if (bondArrayList != null) {
                                                file = new File(".\\molecules\\" + moleculeName);
                                                filewriter = new FileWriter(file);
                                                filewriter.write(moleculeName + '\n');
                                                filewriter.write(numberOfVertices + '\n');
                                                for (int i = 0; i < elementsArrayList.size(); i++) {
                                                        filewriter.write(periodicTable[elementsArrayList.get(i) - 1][2] + '\n');
                                                }
                                                for (int i = 0; i < bondArrayList.size(); i++) {
                                                        filewriter.write(bondArrayList.get(i).trim() + '\n');
                                                }
                                                filewriter.close();
                                                moleculeCount += 1;
                                        }
                                }
                                moleculeCounter += 1;
                        }
                }
                catch (Exception e) {
                        e.printStackTrace();
                } finally {
                        try {
                                if (filewriter != null) {
                                        filewriter.close();
                                }
                        } catch (Exception e) {
                                e.printStackTrace();
                        }
                }
        }
}