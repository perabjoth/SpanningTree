import java.io.BufferedReader;
import java.io.FileReader;

public class SpanningTree {
    public static void main( String [] args ) {
        String file = args[0];
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String currentLine;
            while((currentLine = br.readLine())!= null){
                System.out.println(currentLine);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public class Connection{
        String root;
        String child;
        public Connection(String first, String second){
            this.root = first;
            this.child = second;
        }
    }
}