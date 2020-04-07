package main.java;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import org.json.*;


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
        public static String getMoleculeName(JSONObject json, int moleculeCounter){
                JSONArray arr = json.getJSONArray("PC_Compounds");
                JSONObject obj = arr.getJSONObject(moleculeCounter);
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
        public static Integer getNumberOfVertices(JSONObject json, int moleculeCounter){
                JSONArray arr = json.getJSONArray("PC_Compounds");
                JSONObject obj = arr.getJSONObject(moleculeCounter);
                obj = obj.getJSONObject("atoms");
                Integer vertexCount = 0;
                JSONArray element = obj.getJSONArray("element");
                vertexCount = element.length();

                return vertexCount;
        }

        // Grab atomic elements in the compound
        public static ArrayList<Integer> getElements(JSONObject json, int moleculeCounter)
        {
                JSONArray arr = json.getJSONArray("PC_Compounds");
                JSONObject obj = arr.getJSONObject(moleculeCounter);
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
        public static ArrayList<String> getBonds(JSONObject json, int moleculeCounter)
        {
                try {
                        JSONArray arr = json.getJSONArray("PC_Compounds");
                        JSONObject obj = arr.getJSONObject(moleculeCounter);
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

        public static void writeFiles(ArrayList<String> numberOfVerticesFile,
                                      ArrayList<String> moleculeNameFile,
                                      ArrayList<ArrayList<String>> bondArrayListFile,
                                      ArrayList<ArrayList<Integer>> elementsArrayListFile) throws IOException {
                File file = null;
                String[][] periodicTable = importPeriodicTable("periodicTable.csv");
                FileWriter filewriter = null;
                try {
                        for(int i = 0; i < moleculeNameFile.size(); i++) {
                                System.out.println(moleculeNameFile.get(i));
                                file = new File("molecules/" + moleculeNameFile.get(i));
                                filewriter = new FileWriter(file);
                                filewriter.write(moleculeNameFile.get(i) + '\n');
                                filewriter.write(numberOfVerticesFile.get(i) + '\n');
                                for (int j = 0; j < elementsArrayListFile.get(i).size(); j++) {
                                        filewriter.write(periodicTable[elementsArrayListFile.get(i).get(j) - 1][2] + '\n');
                                }
                                for (int j = 0; j < bondArrayListFile.get(i).size(); j++) {
                                        filewriter.write(bondArrayListFile.get(i).get(j).trim() + '\n');
                                }
                                filewriter.close();
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

        public static void main(String[] args) throws JSONException, IOException {
                // Number of molecules to grab
                int numOfMolecules = 5;
                int moleculeCount = 0;
                ArrayList<String> numberOfVerticesFile = new ArrayList<>();
                ArrayList<String> moleculeNameFile = new ArrayList<>();
                ArrayList<ArrayList<String>> bondArrayListFile = new ArrayList<>();
                ArrayList<ArrayList<Integer>> elementsArrayListFile = new ArrayList<>();
                int cidCount = 1;
                while (moleculeCount < numOfMolecules) {
                        String countString = "";
                        int limit = numOfMolecules;
                        if (numOfMolecules >= 150) {
                                limit = 150;
                        }
                        for (int j = cidCount; j <= limit+cidCount-1; j++) {
                                if (j == limit+cidCount-1)
                                        countString += j;
                                else
                                        countString += j + ",";
                        }
                        cidCount += limit;
                        try {
                                int count = countString.split(",").length;
                                JSONObject json = readJsonFromUrl("https://pubchem.ncbi.nlm.nih.gov/rest/pug/compound/CID/" + countString + "/record/JSON/?record_type=2d&response_type=display");
                                int counter = 0;
                                while (counter <= count - 1) {
                                        String moleculeName = getMoleculeName(json, counter);
                                        if (moleculeName.length() > 200)
                                        {
                                                moleculeName = moleculeName.substring(0, 199);
                                        }
                                        String numberOfVertices = Integer.toString(getNumberOfVertices(json, counter));
                                        ArrayList<Integer> elementsArrayList = getElements(json, counter);
                                        ArrayList<String> bondArrayList = getBonds(json, counter);
                                        if (bondArrayList != null && moleculeName.length() != 0) {
                                                moleculeNameFile.add(moleculeName);
                                                elementsArrayListFile.add(elementsArrayList);
                                                bondArrayListFile.add(bondArrayList);
                                                numberOfVerticesFile.add(numberOfVertices);
                                                moleculeCount += 1;
                                        }
                                        counter += 1;
                                }
                        } catch (Exception e) {
                                e.printStackTrace();
                        }

                }
                writeFiles(numberOfVerticesFile, moleculeNameFile, bondArrayListFile, elementsArrayListFile);
        }
}