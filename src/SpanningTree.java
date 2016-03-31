
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.util.Random;

public class SpanningTree {
    private static ArrayList<Connection> connections = new ArrayList<Connection>();
    private static ArrayList<Switch> switches = new ArrayList<Switch>();
    public SpanningTree(ArrayList<String> connections) {
        for (String x : connections) {
            this.connections.add(new Connection(x.split("-")[0], x.split("-")[1]));
        }
    }
    public  void traverse(){
        Switch root = getLowestSwitch();
        ArrayList<Object> adjacencies = new ArrayList<Object>();
        for(int x : root.ports){
            adjacencies.add(getSwitch(x),root.ports.indexOf(x));
        }
    }

    private Switch getLowestSwitch(){
        int switchNumber = Integer.MAX_VALUE;
        for(Switch x: switches){
            if(x.getSwitchNumber()<switchNumber){
                switchNumber = x.getSwitchNumber();
            }
        }
        for(Switch x: switches){
            if(x.getSwitchNumber() == switchNumber){
                return x;
            }
        }

        return null;
    }


    private Switch getLowestSwitch(int number){
        int switchNumber = Integer.MAX_VALUE;
        for(Switch x: switches){
            if(x.getSwitchNumber()<switchNumber && x.getSwitchNumber() > number){
                switchNumber = x.getSwitchNumber();
            }
        }
        for(Switch x: switches){
            if(x.getSwitchNumber() == switchNumber){
                return x;
            }
        }

        return null;
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
                result += "," + x;
            }
        }
        return result;
    }

    public static void reset(){
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
                System.out.print("Has " + switches + " switches. ");
                if (input.get(0).equals("R")) {
                    System.out.print("Random.");
                    System.out.println(new SpanningTree(RandomConnections(switches)).toString());
                } else {
                    System.out.print("Not random.");
                    System.out.println(new SpanningTree(input));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static ArrayList<String> RandomConnections(int switches) {
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

    public static boolean checkArrayFilled(int numbers[]) {
        for (int x : numbers) {
            if (x == 0) {
                return true;
            }
        }
        return false;
    }

    public static class Switch {
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

        public int getSwitchNumber(){
            return this.switchNumber;
        }

    }

    public static Switch getSwitch(int number){
        for(Switch x : switches){
            if(x.getSwitchNumber()==number){
                return x;
            }
        }
        return new Switch(number);
    }

    public static class Connection {
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