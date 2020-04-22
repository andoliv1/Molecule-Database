package main.java;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
import org.graphstream.stream.ProxyPipe;
import org.graphstream.ui.geom.Point3;
import org.graphstream.ui.graphicGraph.GraphicGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import static org.graphstream.algorithm.Toolkit.*;

public class App {
    private static final Logger log = Logger.getLogger( App.class.getName() );
    private JPanel mainPanel;
    private JButton fileButton;
    private JRadioButton findButton;
    private JRadioButton addButton;
    private JTextArea textarea;
    private JButton performActionButton;
    private JTextArea textPane1;
    private JPanel graphPanel1;
    private JPanel graphPanel2;
    private JButton button1;
    private JScrollPane textareaScrollPane;
    private JScrollPane isomorphicScrollPane;
    private JLabel mlabel1;
    private JLabel mlabel2;
    private JRadioButton mostSimilarRadioButton;
    private JLabel numMolsLabel;
    private JLabel insertTime;
    private JLabel findTime;
    Operations Ops;
    JFrame frame;
    private String moleculeFile;
    private ArrayList<String> moleculeFiles;
    private ArrayList<Integer> bijection;
    Viewer viewer1;
    Viewer viewer2;
    public App() {

        // Initialize Operations class
        Ops = new Operations();
        moleculeFile = null;
        moleculeFiles = new ArrayList<>();

        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(addButton);
        group.add(findButton);
        group.add(mostSimilarRadioButton);

        numMolsLabel.setText(""+Ops.getNumMolecules());
        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                moleculeFile = null;
                moleculeFiles = new ArrayList<>();
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File("."));
                fc.setMultiSelectionEnabled(true);
                int returnval = fc.showOpenDialog(mainPanel);
                if (returnval == JFileChooser.APPROVE_OPTION) {
                    File[] files = fc.getSelectedFiles();
                    //This is where a real application would open the file.
                    for(File file : files) {
                        try {
                            moleculeFiles.add(file.getCanonicalPath());
                            textarea.setText("Selected: " + file.getCanonicalPath() + "\n");
                        } catch (IOException e) {
                            log.warning(e.getMessage());
                        }
                    }
                    if(moleculeFiles.size() > 0)
                        moleculeFile = moleculeFiles.get(0);
                } else {
                    log.info("Open command cancelled by user.");
                }
            }
        });


        performActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (moleculeFiles.size() == 0) {
                    JFrame f = new JFrame();
                    JOptionPane.showMessageDialog(f, "No text file selected."   );
                } else {
                    if (addButton.isSelected()) {
                        textarea.setText("");
                        StringBuilder l = new StringBuilder("Added Molecules : ");
                        long startTime = System.nanoTime();
                        for(String s : moleculeFiles) {
                            File ff = new File(s);
                            l.append(ff.getName())
                                .append(",\n");
                            Ops.insert(s);
                            log.info("Added Molecule from: " + s);
                        }
                        long endTime = System.nanoTime();
                        long duration = (endTime - startTime)/1000000;
                        insertTime.setText(duration+"ms");
                        textarea.setText(l.toString().substring(0, l.length()-2));
                        createGraph(graphPanel1, new MoleculeText(moleculeFile));
                        numMolsLabel.setText(""+Ops.getNumMolecules());
                    } else if (findButton.isSelected()) {

                        // Runs outside of the Swing UI thread
                        new Thread(new Runnable() {
                            public void run() {
                                long startTime = System.nanoTime();
                                ArrayList<MoleculeAbstract> molecules = Ops.find(moleculeFile);
                                long endTime = System.nanoTime();
                                long duration = (endTime - startTime)/1000000;
                                findTime.setText(duration+"ms");
                                MoleculeText interestedMolecule = new MoleculeText(moleculeFile);
                                // Runs inside of the Swing UI thread
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        textPane1.setText("");
                                        Document doc = textPane1.getDocument();
                                        MoleculeAbstract isoMolecule = null;
                                        boolean found_iso = false;
                                        if (molecules.size() > 0) {
                                            Graph graph1 = createGraph(graphPanel1, interestedMolecule);
                                            for (MoleculeAbstract m : molecules) {
                                                try {

                                                    doc.insertString(doc.getLength(), m.moleculeName + "\n", null);
                                                } catch (BadLocationException e) {
                                                    e.printStackTrace();
                                                }

                                                // If the isomorphic
                                                if(molecules.size() == 1 || (!interestedMolecule.equals(m) && !found_iso)) {
                                                    isoMolecule = m;
                                                    found_iso = true;
                                                }
                                            }
                                            if (isoMolecule == null)
                                                isoMolecule = molecules.get(0);
                                            bijection = isoMolecule.bijection;
                                            Graph graph2 = createGraph(graphPanel2, isoMolecule);
                                        } else {
                                            try {
                                                doc.insertString(doc.getLength(), "No isomorphic molecules found.\n", null);
                                            } catch (BadLocationException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }
                                });
                                try {
                                    Thread.sleep(100);
                                }
                                catch(Exception e) { }
                                finally {
                                    moleculeFile = null;
                                    moleculeFiles = new ArrayList<>();
                                }
                            }
                        }).start();
                    }
                    else{
                        // Runs outside of the Swing UI thread
                        new Thread(new Runnable() {
                            public void run() {
                                long startTime = System.nanoTime();
                                MoleculeAbstract mostSimilarMolecule = Ops.mostSimilar(moleculeFile);
                                long endTime = System.nanoTime();
                                long duration = (endTime - startTime)/1000000;
                                findTime.setText(duration+"ms");
                                MoleculeText interestedMolecule = new MoleculeText(moleculeFile);
                                // Runs inside of the Swing UI thread
                                SwingUtilities.invokeLater(new Runnable() {
                                    public void run() {
                                        textPane1.setText("");
                                        Document doc = textPane1.getDocument();
                                        Graph graph1 = createGraph(graphPanel1, interestedMolecule);
                                        if(mostSimilarMolecule != null) {
//                                    bijection = mostSimilarMolecule.bijection;
                                            Graph graph2 = createGraph(graphPanel2, mostSimilarMolecule);
                                        }

                                    }
                                });
                                try {
                                    Thread.sleep(100);
                                } catch (Exception e) {
                                } finally {
                                    moleculeFile = null;
                                    moleculeFiles = new ArrayList<>();
                                }
                            }
                        }).start();
                    }
                }

            }
        });

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                setNodePos();
            }
        });
//        addMolecule("molecules/fake_mol1_13atom.txt");
        drawGraph(graphPanel1, new SingleGraph("Empty Graph", false, true));
        drawGraph(graphPanel2, new SingleGraph("Empty Graph", false, true));


    }

    public Graph createGraph(JPanel graphPanel, MoleculeAbstract m){
        if(graphPanel == graphPanel1){
            mlabel1.setText(m.getMoleculeName());
        } else
            mlabel2.setText(m.getMoleculeName());
        Graph g = new MultiGraph(m.getMoleculeName(), false, true);
        URL url = App.class.getResource("/public/GUIStyle.css");
        g.addAttribute("ui.stylesheet","url("+url.getFile()+")");

        int vertex;
        for (int i = 0; i < m.numVertices; i++){
//            System.out.println(m.getAtomList().get(i));
            if(m.bijection.size() != 0){
                vertex = m.bijection.get(i);
            } else{
                vertex = i;
            }
            String atom = m.getAtomList().get(i);
//            System.out.println(m.moleculeName + "   " + atom+vertex);
            Node n = g.addNode( ""+vertex); //C0, H1, ...

            n.addAttribute("ui.label", atom);
            n.addAttribute("ui.color", (float) vertex / m.numVertices);
            if(vertex < m.numVertices/2) {
                n.addAttribute("ui.style", "text-color:white;");
            }
            else{
                n.addAttribute("ui.style", "text-color:black;");
            }
        }

        for (int vertex1 = 0; vertex1< m.getNumVertices(); vertex1++)
            for (int vertex2 = 0; vertex2 < m.getNumVertices(); vertex2++) {
                // no repeats
                if (vertex1 < vertex2) {
                    for (int ii = 0; ii < m.getAdjacencyMatrix()[vertex1][vertex2]; ii++)
                        g.addEdge(m.atoms.get(vertex1) + vertex1
                                        + m.atoms.get(vertex2) + vertex2
                                        + "_" + ii,
                                vertex1,
                                vertex2
                                );
                }
            }

        drawGraph(graphPanel, g);
        return g;
    }

    public void drawGraph(JPanel graphPanel, Graph graph) {
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
        if (graphPanel == graphPanel2) {
            viewer2 = viewer;
        }
        else {
            viewer1 = viewer;
        }
        viewer.enableAutoLayout();

        ViewPanel viewPanel = viewer.addDefaultView(false);
        viewPanel.setLayout(new BorderLayout());
        viewPanel.setPreferredSize(new Dimension(800,600));
//        viewPanel.setSize(1000,1000);
        graphPanel.removeAll();
        graphPanel.add(viewPanel, BorderLayout.CENTER);
        graphPanel.revalidate();
    }

    public boolean setNodePos(){
        if(viewer1 == null || viewer2 == null)
            return false;
        GraphicGraph graph1 = viewer1.getGraphicGraph();
        GraphicGraph graph2 = viewer2.getGraphicGraph();

        if(graph1.getNodeCount() != graph2.getNodeCount())
            return false;
        System.out.println(Arrays.deepToString(new ArrayList[]{bijection}));

        for(int i = 0; i < graph1.getNodeCount(); i++) {
            Node n1 = graph1.getNode(""+i);
            Node n2 = graph2.getNode(""+i);

            double[] xy = nodePosition(n1);
//            System.out.println(n1.toString() + "   " + n2.toString());
            n2.addAttribute("xy", xy[0], xy[1]);
        }
//        drawGraph(graphPanel2, graph2);
        return true;
    }

    public static void main(String[] args) {
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        JFrame frame = new JFrame("App");
        frame.setContentPane(new App().mainPanel);
//        frame.add(mainPanel);
//        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
