/**
 * Created by Perabjoth Singh Bajwa Spring 2016 CSCI 4311
 * ******
 * **********
 * *************
 * ***************
 * **   *****  ***
 * ***************
 * ****** ******
 * ***********
 * *********
 * ***********
 * *************
 */

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class SpanningTree {
    private static ArrayList<Connection> connections = new ArrayList<Connection>();
    private static ArrayList<Switch> switches = new ArrayList<Switch>();

    public SpanningTree(ArrayList<String> connections) {
        for (String x : connections) {
            this.connections.add(new Connection(x.split("-")[0], x.split("-")[1]));
        }
    }

    private class Node {
        int node, connection, distance;
        HashMap<Integer, LinkedList> visitedNodes = new HashMap<Integer, LinkedList>();

        public Node(int node, int connection, int distance) {
            this.node = node;
            this.connection = connection;
            int[] mapping = new int[]{connection, 0};
            this.addVisitor(node, mapping);
            this.distance = distance;
        }

        public void increment(int position) {
            if (visitedNodes.get(position) == null) {
                distance++;
            }
        }
        @SuppressWarnings("unchecked")
        public void addVisitor(int position, int[] numbers) {
            this.increment(numbers[0]);
            this.connection = numbers[0];
            if (visitedNodes.get(position) != null) {
                visitedNodes.get(position).add(numbers);
            } else {
                LinkedList linkedList = new LinkedList();
                linkedList.add(numbers);
                visitedNodes.put(position, linkedList);
            }

        }

        public LinkedList getVisitor(int number) {
            if (visitedNodes.get(number) != null)
                return visitedNodes.get(number);
            else return new LinkedList();
        }

        public String toString() {
            return "(" + node + "," + connection + "," + distance + ")";
        }
    }

    private void traverse() {
        LinkedList<Node> nodes = new LinkedList<Node>();
        int rounds = 0;
        for (Switch x : switches) {
            Node node = new Node(x.getSwitchNumber(), x.getSwitchNumber(), 0);
            nodes.add(node);
        }
        nodes = sortNodes(nodes);
        Switch rootNode = getLowestSwitch();
        System.out.println("Node " + rootNode.getSwitchNumber() + " elected root node.");
        rounds++;

        System.out.print("Round " + rounds + ": ");

        for (Node x : nodes) {
            System.out.print(x + " ");
        }

        System.out.println();

        while (checkNodes(nodes)) {
            rounds++;
            if (rounds > nodes.size() * nodes.size()) {
                System.out.println("Can't connect all to root.");
                break;
            }

            for (Node x : nodes) {
                if (x.connection != rootNode.getSwitchNumber()) {
                    getLowestSwitchConnected(x, x.connection, x.getVisitor(x.connection));
                }
            }

            System.out.print("Round " + rounds + ": ");
            for (Node x : nodes) {
                System.out.print(x + " ");
            }
            System.out.println();
        }
    }

    private LinkedList<Node> sortNodes(LinkedList<Node> nodes) {
        LinkedList<Node> sortedNodes = new LinkedList<Node>();
        while (nodes.size() > 0) {
            Node currentLowest = nodes.get(0);
            for (Node x : nodes) {
                if (x.node < currentLowest.node) {
                    currentLowest = x;
                }
            }
            nodes.remove(currentLowest);
            sortedNodes.add(currentLowest);
        }
        return sortedNodes;
    }

    private boolean checkNodes(LinkedList<Node> nodes) {
        int rootSwitchNumber = getLowestSwitch().getSwitchNumber();
        for (Node x : nodes) {
            if (x.connection != rootSwitchNumber) {
                return true;
            }
        }
        return false;
    }

    private Switch getLowestSwitch() {
        int switchNumber = Integer.MAX_VALUE;
        Switch lowestSwitch = null;
        for (Switch x : switches) {
            if (x.getSwitchNumber() < switchNumber) {
                switchNumber = x.getSwitchNumber();
                lowestSwitch = x;
            }
        }
        return lowestSwitch;
    }

    private Switch getLowestSwitchConnected(Node node, int number, LinkedList nodes) {
        int switchNumber = Integer.MAX_VALUE;
        Switch currentSwitch = null;
        for (Switch x : switches) {
            if (x.getSwitchNumber() == number) {
                currentSwitch = x;
            }
        }
        ArrayList<Integer> ports = currentSwitch.ports;
        Integer portNumber = 0;
        for (int i = 0; i < ports.size(); i++) {
            Integer x = ports.get(i);
            if (x < switchNumber && checkPortAndSwitch(nodes, i, x)) {
                switchNumber = x;
                portNumber = i;
            }
        }

        for (Switch x : switches) {
            if (x.getSwitchNumber() == switchNumber) {
                int nextSwitchNumber = x.getSwitchNumber();
                int[] mapping = new int[]{nextSwitchNumber, portNumber};
                node.addVisitor(number, mapping);
                return x;
            }
        }

        return null;
    }

    private static boolean checkPortAndSwitch(LinkedList nodes, int portNumber, int switchNumber) {
        for (int i = 0; i < nodes.size(); i++) {
            int[] numbers = (int[]) nodes.get(i);
            if (numbers[0] == switchNumber && numbers[1] == portNumber) {
                return false;
            }
        }
        return true;
    }

    public String toString() {
        ArrayList<String> values = new ArrayList<>();
        for (Connection x : connections) {
            values.add(x.toString());
        }
        String result = "";
        for (int i = 0; i < values.size(); i++) {
            String x = values.get(i);
            if (i == 0) {
                result += x;
            } else {
                result += " " + x;
            }
        }
        return result;
    }

    private static void reset() {
        connections = new ArrayList<Connection>();
        switches = new ArrayList<Switch>();
    }

    public static void main(String[] args) {
        String file = args[0];
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                reset();
                ArrayList<String> input = new ArrayList<String>(Arrays.asList(currentLine.split(" ")));
                Integer switches = Integer.parseInt(input.get(0));
                input.remove(0);
                System.out.print("Configuration: " + switches + " ");
                SpanningTree spanningTree;
                if (input.get(0).equals("R")) {
                    spanningTree = new SpanningTree(RandomConnections(switches));
                } else {
                    spanningTree = new SpanningTree(input);
                }
                System.out.println(spanningTree);
                spanningTree.traverse();
                System.out.println();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private static ArrayList<String> RandomConnections(int switches) {
        ArrayList<String> connections = new ArrayList<String>();
        int counter = 0;
        Random random = new Random();
        int numbers[] = new int[switches];
        while (counter < (switches * 2) || checkArrayFilled(numbers)) {
            int first = 0;
            int second = 0;
            while (first == second) {
                first = random.nextInt(switches) + 1;
                second = random.nextInt(switches) + 1;
            }
            numbers[first - 1] = 1;
            numbers[second - 1] = 1;
            connections.add(String.valueOf(first) + "-" + String.valueOf(second));
            counter++;
        }

        return connections;
    }

    private static boolean checkArrayFilled(int numbers[]) {
        for (int x : numbers) {
            if (x == 0) {
                return true;
            }
        }
        return false;
    }

    private static class Switch {
        int switchNumber;
        int occupiedPorts = 0;
        ArrayList<Integer> ports = new ArrayList<>();

        public Switch(int number) {
            switchNumber = number;
        }

        public void addPort(Switch x) {
            this.ports.add(this.occupiedPorts, x.switchNumber);
            this.occupiedPorts++;
            x.ports.add(x.occupiedPorts, this.switchNumber);
            x.occupiedPorts++;
        }

        public int getSwitchNumber() {
            return this.switchNumber;
        }

    }

    private static Switch getSwitch(int number) {
        for (Switch x : switches) {
            if (x.getSwitchNumber() == number) {
                return x;
            }
        }
        Switch newSwitch = new Switch(number);
        switches.add(newSwitch);
        return newSwitch;
    }

    private static class Connection {
        Switch root;
        Switch child;

        public Connection(String first, String second) {
            this.root = getSwitch(Integer.parseInt(first));
            this.child = getSwitch(Integer.parseInt(second));
            this.root.addPort(this.child);
        }

        public String toString() {
            return root.switchNumber + "-" + child.switchNumber;
        }
    }
}