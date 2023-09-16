package parser;


import entity.Node;
import exception.FileFormatException;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataParser {
    private List<Node> nodes;
	public static String DATASET = "";
    private final String LINE_SPLITTER_IN_FILE = "---";

    public DataParser() {
        nodes = new ArrayList<>();
        readDataFromFile();
    }

    private void readDataFromFile() {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(DATASET));
            String line = null;

            int lineNumber = 0;
            boolean linkNodesFormat = false;

            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.equals(LINE_SPLITTER_IN_FILE)) {
                    linkNodesFormat = true;
                    continue;
                }

                if (!linkNodesFormat) {
                    Node newNode = extractNodeFromLine(line);
                    nodes.add(newNode);
                } else {
                    extractLinkFromLine(line, lineNumber);
                }
            }

            reader.close();
        } catch (IOException | FileFormatException ex) {
			ex.printStackTrace();
        }
    }

    private void extractLinkFromLine(String line, int lineNumber) throws FileFormatException {
        String[] linkData = line.split(" ");

        String nodeName1 = linkData[0];
        String nodeName2 = linkData[1];
        int dist = Integer.parseInt(linkData[2]);

        Node node1 = null, node2 = null;

        for (Node node : nodes) {
            if (node.getName().equals(nodeName1)) {
                node1 = node;
            } else if (node.getName().equals(nodeName2)) {
                node2 = node;
            }
        }

        if (node1 == null || node2 == null) {
            throw new FileFormatException(line, lineNumber);
        }

        node1.addNeighbor(node2, dist);
        node2.addNeighbor(node1, dist);
    }

    private Node extractNodeFromLine(String line) {
        String[] nodeData = line.split(" ");
        return new Node(nodeData[0], nodeData[1]);
    }

//    private Node extractNodeFromLine(String line) {
//        String[] nodeData = line.split(" ");
//        return new Node(nodeData[0], Integer.parseInt(nodeData[1]));
//    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void setFilePath(String filepath) {
        this.DATASET = filepath;
    }
}
