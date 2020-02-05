package lexer;

import java.io.*;
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

                // tokenize the contents of the file
                ArrayList<Token> tokens = Lexer.tokenize(lines);

                // initialize writer to write tokens out to a file
                BufferedWriter writer = new BufferedWriter(new FileWriter("jxc_tokens.txt"));

                // print & write out tokens
                System.out.println("\nTokens");
                for (Token token : tokens) {
                    System.out.println(token);
                    writer.write(token.toString() + "\n");
                }
                writer.close();

                System.out.println("\nReconstructed");
                for (Token token : tokens)
                    if(token.tokenType == TokenType.TK_SEMICOLON)
                        System.out.println(token.str + "\n");
                    else
                        System.out.print(token.str + " ");

            } catch (IOException e) {
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
