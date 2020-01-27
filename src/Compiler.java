import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

import static java.lang.System.exit;

public class Compiler {

    //TODO: add string class, and clean up argument logic


    public static void main(String[] args) {

        if(0 < args.length) {
            int index = 0;

            // check for arguments (there is cleaner way to implement)
            for(int i = 0; i < args.length; i++) {
                // run help function
                if(args[i].equals("-h")) {
                    helpString();
                } else if(args[i].contains(".c")) {
                    index = i;
                } else {
                    System.out.println("error: the argument " + args[i] + " is not recognized.");
                }
            }

            try {
                // populate the c file and split the lines
                File file = new File(args[index]);
                Scanner readScanner = new Scanner(file);
                ArrayList<String> fileLines = new ArrayList<String>();

                while(readScanner.hasNextLine()) {
                    fileLines.add(readScanner.nextLine().trim());
                }

                // convert to string array as required by String[]
                String[] lines = fileLines.toArray(new String[0]);

                // Since tokenize is a static method, and Lexer is written as a static
                // class, you probably shouldn't make an instance of it.
                // If you want to make an instance we should probably modify Lexer
                // to be a singleton. Right now this produces a warning on call to
                // lexer.tokenize()
                Lexer lexer = new Lexer();


                // tokenize the contents of the file
                ArrayList<Token> tokens = lexer.tokenize(lines);

                // print out tokens
                System.out.println("\nTokens");
                for (Token token : tokens) {
                    System.out.println(token);
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        } else {
            // error checking for when no arguments of file is provided
            System.out.println("\033[0;31m" + "error:" + "\033[0m" + "no input files");
            System.out.println("Please use the argument '-h' for help.");
        }
    }

    static void helpString() {
        System.out.println("INFO: Built for CSE423");
        System.out.println("OPTIONS:");
        System.out.println("\t(NOT-IMPLEMENTED or DEPRECATED denoted with a astrisk *)\n");

        System.out.println("-h | -help");
        System.out.println("\tProvides a list of arguments supported by the compiler.");

        System.out.println("-t | -token");
        System.out.println("\tPrints out the token list to a file.");

        // exit if argument -h is included, and ignore everything else.
        exit(0);
    }
}
