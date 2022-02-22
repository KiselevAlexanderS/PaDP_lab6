
public class AnonymizerApp {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.out.println("Usage: AnonymizerApp <host> <post>");
            System.exit(-1);
        }

        String host = args[0];
        String port = args[1];
        int port = Integer.parseInt(args[1]);
    }
}
