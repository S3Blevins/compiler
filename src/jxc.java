import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import org.apache.commons.cli.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import parser.Parser;

public class jxc {

    //TODO add functionality to t, to, p, po command line options

    public static void main(String[] args) {
        // create argument parser for cli library
        CommandLineParser commandParser = new DefaultParser();

        // Adding arguments for our compiler
        Options commandArgs = new Options();

        // Adding command line options.
        commandArgs.addOption("t", "token", false , "Display tokens to command line");
        commandArgs.addOption("to", "tokenout", false , "Displays tokens to command line and output file");
        commandArgs.addOption("h", "help", false , "Displays help options.");
        commandArgs.addOption("p", "parse", false, "Displays parse tree to command line.");
        commandArgs.addOption("po", "parseout", false, "Prints parse tree to output file.");
        commandArgs.addOption("f","file,", true, "File to read in from");

        //parse command line options
        CommandLine line = null;
        try {
            line = commandParser.parse( commandArgs, args);
        } catch (ParseException exp) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }

        StringBuilder str = new StringBuilder();
        ArrayList<Token> tokens = null;
        File file = null;

        //get file name
        if(line.hasOption("f")){
            try{
                file = new File(line.getOptionValue("f"));
                Scanner readScanner = new Scanner(file);
                ArrayList<String> fileLines = new ArrayList<String>();

                while(readScanner.hasNextLine()) {
                    fileLines.add(readScanner.nextLine().trim());
                }

                // convert to string array as required by String[]
                String[] lines = fileLines.toArray(new String[0]);

                // tokenize the contents of the file
                tokens = Lexer.tokenize(lines);

                // print & write out tokens
                str.append("\nTOKENS:\n");
                for (Token token : tokens) {
                    str.append(token + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            System.out.println("\033[0;31m" + "error:" + "\033[0m" + "no input files");
            System.out.println("Please use the argument '-h' for help.");
        }

        //displays helpful information
        if(line.hasOption("h")){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "ant", commandArgs );
        }

        //token options
        if(line.hasOption("t")){
            //displays tokens to command line
            System.out.println(str);

            System.out.println("RECONSTRUCTED:");
            for (Token token : tokens)
                System.out.print(token.str + " ");
            System.out.println();
        }
        if (line.hasOption("to")) {
            //displays token to command line and outputs to a file
            // initialize writer to write tokens out to a file
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter("jxc_tokens.txt"));
                writer.write(str.toString());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //parse tree options
        if(line.hasOption("p")){
            //display parse tree to command line
            System.out.println("\nPARSER:");
            Parser.Instance().Parse(tokens, file.getName());
        }
        if(line.hasOption("po")){
            //prints parse tree to output file
        }

    }
}
