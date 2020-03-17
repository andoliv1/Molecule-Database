import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;


public class MoleculeText {
    String moleculeName;
    Integer numVertices;
    String[] atoms;
    LinkedList<Integer>[] adjacencyList;

    public MoleculeText(String filename){
        parseFile(filename);
    }

    private void parseFile(String filename) {
        try {
            System.out.println("Parsing...");
            BufferedReader br = new BufferedReader(new FileReader(filename));
            // First line is always the molecule name
            this.moleculeName = br.readLine();
            System.out.println(moleculeName);

            // Second line is always the number of vertices
            this.numVertices = Integer.parseInt(br.readLine());
            System.out.println(numVertices);
            // The next numVertices line are the atoms in the molecule
            this.atoms = new String[this.numVertices];
            this.adjacencyList = new LinkedList[this.numVertices];
            for (int ii = 0; ii<this.numVertices; ii++){
                this.atoms[ii] = br.readLine();
                System.out.println(this.atoms[ii]);
                this.adjacencyList[ii] = new LinkedList<>();
            }

            // Remainder of file is the adjacency list
            String st;
            String[] delimited;
            int vertex1, vertex2;
            while ((st = br.readLine()) != null){
                delimited = st.split("\\W+");
                vertex1 = Integer.parseInt(delimited[0]);
                vertex2 = Integer.parseInt(delimited[1]);

                this.adjacencyList[vertex1].add(vertex2);
            }
            System.out.println("Adjacency List");
            for (int ii = 0; ii<this.numVertices; ii++){
                System.out.print("Vertex " + ii + ":\t");
                for (int s : adjacencyList[ii]) {
                    System.out.print(s +"\t");
                }
                System.out.println();
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}