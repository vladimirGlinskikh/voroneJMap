package entity;

import java.util.LinkedHashMap;
import java.util.Map;

public class Node {
    private String name;
    private String name1;
    private Map<Node, Integer> neighbors;
    private Node source;
    private int heuristic;

    public Node(String name, String name1) {
        this.name = name;
        this.name1 = name1;
        neighbors = new LinkedHashMap<>();
    }

    public String getName() {
        return name;
    }

    public void setSource(Node source) {
        this.source = source;
    }

    public Node getSource() {
        return source;
    }

    public int getHeuristic() {
        return heuristic;
    }

    public void addNeighbor(Node neighbor, int distance) {
        this.neighbors.put(neighbor, distance);
    }

    public Map<Node, Integer> getNeighbors() {
        return neighbors;
    }

    @Override
    public String toString() {
        String out = "";

        out = "[" + " name = " + this.name + ", neighbors = [ ";
        for (Node node : this.neighbors.keySet()) {
            out += node.getName() + " ";
        }
        out += "] ]";

        return out;
    }
}

