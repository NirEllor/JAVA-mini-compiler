package ex5.main;

import ex5.main.engine.VerificationEngine;
import ex5.main.preprocessor.PreProcessor;
import ex5.main.tables.FunctionsTable;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

/**
 * This class creates all the needed objects for the verification progress
 */
public class Sjavac {

    public static final String EXTENTION = ".sjava";
    public static final String IO_ERROR = "2";
    public static final String IOEXCEPTION_WRONG_FILE_FORMAT_NOT_SJAVA = "IOException: Wrong file format (not sjava).";
    public static final String IOEXCEPTION_ILLEGAL_NUMBER_OF_ARGUMENTS_FOR_THE_PROGRAM = "IOException: Illegal Number of arguments for the program";
    public static final int LEGGAL_LENGTH = 1;
    public static final int PATH_INDEX = 0;
    public static final String ENPTY_STR = "";

    /**
     * The main function of the program. Preprocess the file, and verifies it using PreProcessor
     * and VerificationEngine
     * @param args - The variables given from the user in the call for the program
     */
    public static void main(String[] args) throws IOException {

        //open the file
        if (args.length < LEGGAL_LENGTH) {
            System.out.println(IO_ERROR);
            throw new IOException(IOEXCEPTION_ILLEGAL_NUMBER_OF_ARGUMENTS_FOR_THE_PROGRAM);
        }

        String filePath = args[PATH_INDEX];

        Path path = Paths.get(filePath);
        String fileName = path.getFileName().toString();

//         Check if the file has the .sjava extension
        if (!fileName.endsWith(EXTENTION)) {
            System.out.println(IO_ERROR);
            throw new IOException(IOEXCEPTION_WRONG_FILE_FORMAT_NOT_SJAVA);
        }

        FunctionsTable functionsTable = new FunctionsTable();

        PreProcessor preProcessor = new PreProcessor(filePath, functionsTable);

        String output = preProcessor.run();

        if (!Objects.equals(output, ENPTY_STR)){
            new VerificationEngine(output, functionsTable);
        }
    }
}

