package main.java;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import java.net.URL;
import java.util.ArrayList;
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
    private JLabel label;
    private JButton performActionButton;
    private JTextPane textPane1;
    private JPanel graphPanel1;
    private JPanel graphPanel2;
    private JButton button1;
    Operations Ops;
    JFrame frame;
    private String moleculeFile;
    Viewer viewer1;
    Viewer viewer2;
    public App() {

        // Initialize Operations class
        Ops = new Operations();
        moleculeFile = null;

        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(addButton);
        group.add(findButton);

        fileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                JFileChooser fc = new JFileChooser();
                int returnval = fc.showOpenDialog(mainPanel);
                if (returnval == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    //This is where a real application would open the file.
                    log.info("Opening: " + file.getName() + ".");
                    try {
                        moleculeFile = file.getCanonicalPath();
                    } catch (IOException e) {
                        moleculeFile = null;
                    }
                    label.setText("Selected: " + moleculeFile);


                } else {
                    log.info("Open command cancelled by user.");
                }
            }
        });
        performActionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (moleculeFile == null) {
                    JFrame f = new JFrame();
                    JOptionPane.showMessageDialog(f, "No text file selected.");
                } else {
                    if (addButton.isSelected()) {
                        label.setText("Added Molecule from: " + moleculeFile);
                        Ops.insert(moleculeFile);
                        createGraph(graphPanel1, new MoleculeText(moleculeFile));
                    } else if (findButton.isSelected()) {
                        ArrayList<MoleculeAbstract> molecules = Ops.find(moleculeFile);
                        textPane1.setText("");
                        StyledDocument doc = textPane1.getStyledDocument();

                        if (molecules.size() > 0) {
                            Graph graph1 = createGraph(graphPanel1, new MoleculeText(moleculeFile));
                            for (MoleculeAbstract m : molecules) {
                                try {

                                    doc.insertString(doc.getLength(), m.moleculeName + "\n", null);
                                } catch (BadLocationException e) {
                                    e.printStackTrace();
                                }
                            }
                            Graph graph2 = createGraph(graphPanel2, molecules.get(0));
                        } else{
                            try {
                                doc.insertString(doc.getLength(), "No isomorphic molecules found.\n", null);
                            } catch (BadLocationException e) {
                                e.printStackTrace();
                            }
                        }
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

        Graph g = new MultiGraph(m.getMoleculeName(), false, true);
        URL url = App.class.getResource("/public/GUIStyle.css");
        g.addAttribute("ui.stylesheet","url("+url.getFile()+")");


        for (int vertex = 0; vertex < m.numVertices; vertex++){
            System.out.println(m.getAtomList().get(vertex));
            String atom = m.getAtomList().get(vertex);
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
        System.out.println("-=--------");

        for (int vertex1 = 0; vertex1< m.getNumVertices(); vertex1++)
            for (int vertex2 = 0; vertex2 < m.getNumVertices(); vertex2++) {
                // no repeats
                if (vertex1 < vertex2) {
                    for (int i = 0; i < m.getAdjacencyMatrix()[vertex1][vertex2]; i++)
                        g.addEdge(m.atoms.get(vertex1) + vertex1
                                        + m.atoms.get(vertex2) + vertex2
                                        + "_" + i,
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
            viewer.disableAutoLayout();
            viewer2 = viewer;
        }
        else {
            viewer.enableAutoLayout();
            viewer1 = viewer;
        }
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

        for(int i = 0; i < graph1.getNodeCount(); i++) {
            Node n1 = graph1.getNode(""+i);
            Node n2 = graph2.getNode(""+i);

            double[] xy = nodePosition(n1);
            System.out.println(xy[0] + "   " + xy[1]);
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
