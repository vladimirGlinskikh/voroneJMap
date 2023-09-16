package algorithms;

import entity.Node;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.implementations.SingleGraph;
import parser.DataParser;

import java.util.*;


public class PathFinder {
    private static List<Node> nodes;
    private static Graph graph;
    private static DataParser parser;
    private long DELAY = 150;

    public List<Node> initializeGraph() {
        setupNodes();

        // настроить график и его свойства
        graph = new SingleGraph("PathFinder");

        graph.setAutoCreate(true);
        graph.setStrict(false);
        graph.display();

        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");
        graph.addAttribute("ui.stylesheet", "url('resources/style.css')");

        setupGraphNodes();

        return nodes;
    }

    private static void setupNodes() {
        parser = new DataParser();

        nodes = parser.getNodes();
    }

    public void runAStar(String nodeName, String nodeName1) {
        clearNodeColors();
        Node startNode = getNode(nodeName);
        resetNode(startNode);
        AStarSearch(startNode, getGoalNode(nodeName1));
    }

    public void runBreadthFirst(String nodeName, String nodeName1) {
        clearNodeColors();
        Node startNode = getNode(nodeName);
        resetNode(startNode);
        BreadthFirstSearch(startNode, getGoalNode(nodeName1));
    }

    private void setupGraphNodes() {
        for (Node node : nodes) {
            if (graph.getNode(node.getName()) == null) {
                org.graphstream.graph.Node graphNode = graph.addNode(node.getName());
                graphNode.addAttribute("ui.label", node.getName());

                sleep(DELAY);
            }

            for (Map.Entry<Node, Integer> entry : node.getNeighbors().entrySet()) {
                Node neighbor = entry.getKey();
                Integer cost = entry.getValue();

                if (graph.getNode(neighbor.getName()) == null) {
                    org.graphstream.graph.Node graphNeighbor = graph.addNode(neighbor.getName());
                    graphNeighbor.addAttribute("ui.label", neighbor.getName());

                    StringBuilder edgeName = new StringBuilder();
                    edgeName.append(node.getName());
                    edgeName.append(neighbor.getName());

                    Edge edge = graph.addEdge(edgeName.toString(), node.getName(), neighbor.getName());
                    edge.addAttribute("ui.label", cost.toString());

                    sleep(DELAY);
                } else {
                    if (graph.getNode(node.getName()).getEdgeBetween(neighbor.getName()) == null) {
                        StringBuilder edgeName = new StringBuilder();
                        edgeName.append(node.getName());
                        edgeName.append(neighbor.getName());

                        Edge edge = graph.addEdge(edgeName.toString(), node.getName(), neighbor.getName());
                        edge.addAttribute("ui.label", cost.toString());

                        sleep(DELAY);
                    }
                }
            }
        }
    }

    private void AStarSearch(Node start, Node goal) {
        int NODES_EXPANDED = 0;
        Map<Node, Integer> nodeQueue = new HashMap<>();
        List<Node> expanded = new ArrayList<>();

        int startScore = start.getHeuristic() + 0;
        nodeQueue.put(start, startScore);
        Node current = start;

        graph.getNode(current.getName()).addAttribute("ui.class", "start");
        sleep(DELAY);

        while (!current.getName().equals(goal.getName())) {

            for (Node neighbor : current.getNeighbors().keySet()) {
                if (expanded.contains(neighbor)) {
                    continue;
                }

                if (!start.getName().equals(neighbor.getName())) {
                    graph.getNode(neighbor.getName()).addAttribute("ui.class", "neighbor");
                    sleep(DELAY);
                }

                if (nodeQueue.containsKey(neighbor)) {

                    int tempNeighborScore = nodeQueue.get(current)
                            - current.getHeuristic()
                            + current.getNeighbors().get(neighbor)
                            + neighbor.getHeuristic();

                    int existedNodeScore = nodeQueue.get(neighbor);

                    if (tempNeighborScore < existedNodeScore) {
                        neighbor.setSource(current);
                        nodeQueue.put(neighbor, tempNeighborScore);
                    }
                } else {
                    neighbor.setSource(current);

                    int neighborScore = nodeQueue.get(neighbor.getSource())
                            - neighbor.getSource().getHeuristic()
                            + neighbor.getSource().getNeighbors().get(neighbor)
                            + neighbor.getHeuristic();

                    nodeQueue.put(neighbor, neighborScore);
                }
            }

            for (Node node : current.getNeighbors().keySet()) {
                if (!node.getName().equals(start.getName()) && !graph.getNode(node.getName()).getAttribute("ui.class").equals("visited")) {
                    graph.getNode(node.getName()).removeAttribute("ui.class");
                }
            }

            NODES_EXPANDED++;
            expanded.add(current);
            nodeQueue.remove(current);

            if (!start.getName().equals(current.getName())) {
                graph.getNode(current.getName()).addAttribute("ui.class", "visited");
            }

            current = getMinValue(nodeQueue);

            if (!start.getName().equals(current.getName())) {
                graph.getNode(current.getName()).addAttribute("ui.class", "current");
                sleep(DELAY);
            }
        }

        graph.getNode(current.getName()).addAttribute("ui.class", "goal");
        sleep(DELAY);

        Node tracker = current;

        System.out.println("ПРОЙДЕННЫЕ ПУНКТЫ ВО ВРЕМЯ РАБОТЫ A-star АЛГОРИТМА: " + NODES_EXPANDED);
        System.out.print("Путь: " + goal.getName());

        while (tracker.getSource() != null) {
            System.out.print(" <-- " + tracker.getSource().getName());
            tracker = tracker.getSource();
            graph.getNode(tracker.getName()).addAttribute("ui.class", "start");
            sleep(DELAY);
        }
        System.out.println();
    }

    private void BreadthFirstSearch(Node start, Node goal) {

        int NODES_EXPANDED = 0;
        Queue<Node> nodeQueue = new LinkedList<>();
        List<Node> expanded = new ArrayList<>();

        Node current = start;
        graph.getNode(current.getName()).addAttribute("ui.class", "start");
        sleep(DELAY);

        while (!current.getName().equals(goal.getName())) {
            for (Node neighbor : current.getNeighbors().keySet()) {
                if (expanded.contains(neighbor) || nodeQueue.contains(neighbor)) {
                    continue;
                }

                neighbor.setSource(current);
                nodeQueue.add(neighbor);

                graph.getNode(neighbor.getName()).addAttribute("ui.class", "neighbor");
                sleep(DELAY);
            }

            NODES_EXPANDED++;
            expanded.add(current);
            current = nodeQueue.remove();

            graph.getNode(current.getName()).addAttribute("ui.class", "current");
            sleep(DELAY);
        }

        graph.getNode(current.getName()).addAttribute("ui.class", "goal");
        sleep(DELAY);

        Node tracker = current;

        System.out.println("ПРОЙДЕННЫЕ ПУНКТЫ ВО ВРЕМЯ РАБОТЫ BFS АЛГОРИТМА: " + NODES_EXPANDED);
        System.out.print("Путь: " + goal.getName());

        while (tracker.getSource() != null) {
            System.out.print(" <-- " + tracker.getSource().getName());
            tracker = tracker.getSource();

            graph.getNode(tracker.getName()).addAttribute("ui.class", "start");
            sleep(DELAY);
        }
        System.out.println();
    }

    private Node getMinValue(Map<Node, Integer> treeMap) {
        Node minNode = null;
        int min = Integer.MAX_VALUE;

        for (Map.Entry<Node, Integer> entry : treeMap.entrySet()) {
            Node node = entry.getKey();
            Integer value = entry.getValue();

            if (value < min) {
                min = value;
                minNode = node;
            }
        }
        return minNode;
    }

    private Node getGoalNode(String nodeNameEnd) {
        for (Node node : nodes) {
            if (node.getName().equals(nodeNameEnd)) {
                return node;
            }
        }
        return null;
    }

    private Node getNode(String nodeNameStart) {
        for (Node node : nodes) {
            if (node.getName().equals(nodeNameStart)) {
                return node;
            }
        }
        return null;
    }

    private void clearNodeColors() {
        for (org.graphstream.graph.Node node : graph.getNodeSet()) {
            node.removeAttribute("ui.class");
        }
    }

    private void resetNode(Node startNode) {
        if (startNode.getSource() != null) {
            startNode.setSource(null);
        }
    }

    private void sleep(long ms) {
        try {
            Thread.sleep(ms);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setVisualizationDelay(int value) {
        this.DELAY = value;
    }

    public void setGraphFontSize(int fontSize) {
        for (org.graphstream.graph.Node node : graph.getNodeSet()) {
            node.addAttribute("ui.style", "size: " + fontSize + "px;");
            node.addAttribute("ui.style", "text-size: " + (fontSize - 5) + "px;");
        }

        for (Edge edge : graph.getEdgeSet()) {
            edge.addAttribute("ui.style", "text-size: " + (fontSize - 5) + "px;");
        }
    }
}