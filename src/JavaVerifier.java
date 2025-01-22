import java.io.IOException;
import java.util.Objects;

public class JavaVerifier {

    public static void main(String[] args) throws IOException {

        //open the file
        if (args.length < 1) {
            System.out.println("Error: Didnt get path");
        }
        String filePath = args[0];

//        try (FileReader fileReader = new FileReader("my_file.txt");
//             BufferedReader bufferedReader = new BufferedReader(fileReader))
//        {
//
//        } catch (IOException e) {
//            System.out.println(e.getMessage());

        FunctionsTable functionsTable = new FunctionsTable();
        PreProcessor preProcessor = new PreProcessor(filePath, functionsTable);
        String output = preProcessor.run();
        if (!Objects.equals(output, "")){
            VerificationEngine engine = new VerificationEngine(output, functionsTable);
        }
//        System.out.println(output);

    }
}

