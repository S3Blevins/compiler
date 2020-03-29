import ir.IRBuilder;
import ir.IRExpression;
import ir.IRList;
import ir.Instruction;
import lexer.Lexer;
import lexer.Token;
import lexer.TokenType;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import parser.Node;
import parser.NodePrinter;
import parser.Parser;

public class jxc {

    //TODO add functionality to t, to, p, po command line options

    public static void main(String[] args) {
        // create argument parser for cli library
        CommandLineParser commandParser = new DefaultParser();
        Node root = null;

        // Adding arguments for our compiler
        Options commandArgs = new Options();

        // Adding command line options.
        commandArgs.addOption("t", "token", false, "Display tokens to command line");
        commandArgs.addOption("to", "tokenout", false, "Displays tokens to command line and output file");
        commandArgs.addOption("h", "help", false, "Displays help options.");
        commandArgs.addOption("p", "parse", false, "Displays parse tree to command line.");
        commandArgs.addOption("s", "symbol", false, "Displays symbol table to command line.");
        commandArgs.addOption("po", "parseout", false, "Prints parse tree to output file.");
        commandArgs.addOption("f", "file,", true, "File to read in from");
        commandArgs.addOption("i", "ir,", false, "Print out the intermediate representation");
        commandArgs.addOption("r", "ri", true, "Read in an intermediate representation");
        commandArgs.addOption("O0", "no-opt,", false, "Compile with no optimization");
        commandArgs.addOption("O1", "with-opt,", false, "Compile with optimization");

        //parse command line options
        CommandLine line = null;
        try {
            line = commandParser.parse(commandArgs, args);
        } catch (ParseException exp) {
            System.err.println("Parsing failed.  Reason: " + exp.getMessage());
        }

        StringBuilder str = new StringBuilder();
        ArrayList<Token> tokens = null;
        File file = null;

        if (line.hasOption("r")) {
            /* Up to stage 2 of the compile, the command line cannot have the supported
             *  flags since those flags generate the step being read in now. */
            System.out.println("read in an ir");

            // Where we house the IRs
            IRList irList = new IRList();

            if (line.getOptions().length > 1) {
                System.err.println("Cannot utilize (-r/-ri) w/ flags -O0 -O1 -s -po -p -i -to -t -h -f");
                System.exit(1);
            }

            // Else, parse the input file.
            try {
                file = new File(line.getOptionValue("r"));
                Scanner in = new Scanner(file);

                while (in.hasNextLine()) {

                    // Split the current IR by spaces.
                    String curLine = in.nextLine();

                    // Remove unnecessary characters.
                    curLine = curLine.replace("(", "");
                    curLine = curLine.replace(")", "");
                    curLine = curLine.replace(",", "");

                    String[] curIR = curLine.split(" ");

                    /*
                    for (String s : curIR)
                            System.out.println("s = " + s);
                    System.out.println();
                     */

                    switch (curIR.length) {
                        case 1:
                            irList.IRExprList.add(new IRExpression(curIR[0]));
                            break;
                        case 2:
                            Instruction instr3 = null;
                            for (int i = 0; i < Instruction.values().length; i++) {
                                if (curIR[0].equals(Instruction.values()[i].toString())) {
                                    instr3 = Instruction.values()[i];
                                    break;
                                }
                            }

                            irList.IRExprList.add(new IRExpression(instr3, new Token(curIR[1])));
                            break;
                        case 3:
                            Instruction instr = null;
                            for (int i = 0; i < Instruction.values().length; i++) {
                                if (curIR[0].equals(Instruction.values()[i].toString())) {
                                    instr = Instruction.values()[i];
                                    break;
                                }
                            }

                            irList.IRExprList.add(new IRExpression(instr, new Token(curIR[1]), new Token(curIR[2])));
                            break;
                        case 4:
                            Instruction instr2 = null;
                            for (int i = 0; i < Instruction.values().length; i++) {
                                if (curIR[0].equals(Instruction.values()[i].toString())) {
                                    instr2 = Instruction.values()[i];
                                    break;
                                }
                            }
                            irList.IRExprList.add(new IRExpression(instr2, new Token(curIR[1]), new Token(curIR[2]), new Token(curIR[3])));
                            break;
                    }
                }

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            System.out.println("Input read in");
            irList.printIR();
        }

        //get file name
        if (line.hasOption("f")) {
            try {
                file = new File(line.getOptionValue("f"));
                Scanner readScanner = new Scanner(file);
                ArrayList<String> fileLines = new ArrayList<String>();

                while (readScanner.hasNextLine()) {
                    fileLines.add(readScanner.nextLine().trim());
                }

                // convert to string array as required by String[]
                String[] lines = fileLines.toArray(new String[0]);

                // tokenize the contents of the file
                tokens = Lexer.Instance().tokenize(lines);

                // print & write out tokens
                str.append("\nTOKENS:\n");
                for (Token token : tokens) {
                    str.append(token + "\n");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else if (line.hasOption("r") || line.hasOption("ri")) {

        } else {
            System.out.println("\033[0;31m" + "error:" + "\033[0m" + "no input files");
            System.out.println("Please use the argument '-h' for help.");
        }

        //displays helpful information
        if (line.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ant", commandArgs);
        }

        //token options
        if (line.hasOption("t")) {
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
        if (line.hasOption("p")) {

            root = Parser.Instance().Parse(tokens, file.getName());

            //display parse tree to command line
            System.out.println("\n\nPARSER:");

            NodePrinter printer = new NodePrinter();
            root.accept(printer);

            System.out.println(printer.getTree());
        }

        if (line.hasOption("po")) {
            //prints parse tree to output file
        }

        if (line.hasOption("s")) {
            System.out.println("\n\nSYMBOL TABLE:");
            Parser.Instance().printTable();
        }

        if (line.hasOption("O0")) {
            System.out.println("compile with no optimization");

            IRBuilder irBuilder = new IRBuilder();
            root.accept(irBuilder);

            irBuilder.IRs.printIR();
        }

        if (line.hasOption("O1")) {
            System.out.println("compile with optimization");
        }

        if (line.hasOption("i")) {
            System.out.println("print out the IR");
        }
    }
}
