import ir.IRBuilder;
import ir.IRExpression;
import ir.IRList;
import ir.Instruction;

import lexer.Lexer;
import lexer.Token;
import org.apache.commons.cli.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

import parser.Node;
import parser.NodePrinter;
import parser.Parser;


import static java.lang.System.exit;

public class jxc {

    // TODO add optional arguments for file outputs

    public static void main(String[] args) {
        // create argument parser for cli library
        CommandLineParser commandParser = new DefaultParser();
        Node root = null;

        // Adding arguments for our compiler
        Options commandArgs = new Options();

        // Adding command line options.
        commandArgs.addOption("t", "token", false, "Display tokens to command line");
        Option optionalArgument = Option.builder("to")
                .optionalArg(true)
                .numberOfArgs(1)
                .desc("Tokens in the c-program are written to a file specified by user or to default file")
                .build();
        commandArgs.addOption(optionalArgument);
        commandArgs.addOption("h", "help", false, "Displays help options.");
        commandArgs.addOption("p", "parse", false, "Displays parse tree to command line.");
        commandArgs.addOption("s", "symbol", false, "Displays symbol table to command line.");
        optionalArgument = Option.builder("so")
                .optionalArg(true)
                .numberOfArgs(1)
                .desc("Print symbol table to output file specified by user or to default file")
                .build();
        commandArgs.addOption(optionalArgument);
        optionalArgument = Option.builder("po")
                .optionalArg(true)
                .numberOfArgs(1)
                .desc("Prints parse tree to output file specified by user or to default file.")
                .build();
        commandArgs.addOption(optionalArgument);
        commandArgs.addOption("f", "file,", true, "File to read in from");
        commandArgs.addOption("i", "irprint,", false, "Print out the intermediate representation");
        commandArgs.addOption("r", "readir", true, "Read in an intermediate representation");
        optionalArgument = Option.builder("io")
                .optionalArg(true)
                .numberOfArgs(1)
                .desc("Print IR to output file")
                .build();
        commandArgs.addOption(optionalArgument);

        //parse command line options
        CommandLine line = null;

        try {
            line = commandParser.parse(commandArgs, args);
        } catch (ParseException exp) {
            System.err.println("ERROR: " + exp.getMessage());
            exit(1);
        }

        StringBuilder str = new StringBuilder();
        ArrayList<Token> tokens = null;
        File file = null;

        if (line.hasOption("r")) {
            /* Up to stage 2 of the compile, the command line cannot have the supported
             *  flags since those flags generate the step being read in now. */
            if(line.hasOption("f")) {
                System.err.println("ERROR: The -r flag cannot be used in conjunction with the -f flag");
                exit(1);
            }

            System.out.println("read in an ir");

            // Where we house the IRs
            IRList irList = new IRList();

            // Else, parse the input file.
            try {
                file = new File(line.getOptionValue("r"));

                if(!file.exists()) {
                    System.err.println("ERROR: file '" + file + "' does not exist!");
                    exit(1);
                }

                Scanner in = new Scanner(file);

                while (in.hasNextLine()) {

                    // Split the current IR by spaces.
                    String curLine = in.nextLine();

                    if(curLine.isEmpty()) {
                        continue;
                    }

                    // Remove unnecessary characters.
                    curLine = curLine.replace("(", "");
                    curLine = curLine.replace(")", "");
                    curLine = curLine.replace(",", "");
                    curLine = curLine.replace("\t", "");

                    String[] curIR = curLine.split(" ");

                    Instruction instr = null;
                    for (int i = 0; i < Instruction.values().length; i++) {
                        if (curIR[0].equals(Instruction.values()[i].toString())) {
                            instr = Instruction.values()[i];
                            break;
                        }
                    }

                    switch (curIR.length) {
                        case 1:
                            irList.IRExprList.add(new IRExpression(curIR[0]));
                            break;
                        case 2:
                            irList.IRExprList.add(new IRExpression(instr, new Token(curIR[1])));
                            break;
                        case 3:
                            irList.IRExprList.add(new IRExpression(instr, new Token(curIR[1]), new Token(curIR[2])));
                            break;
                        case 4:
                            irList.IRExprList.add(new IRExpression(instr, new Token(curIR[1]), new Token(curIR[2]), new Token(curIR[3])));
                            break;
                        default:
                            ArrayList<Token> list = new ArrayList<>();
                            for (int i = 1; i < curIR.length - 1; i++) {
                                list.add(new Token((curIR[i])));
                            }

                            irList.IRExprList.add(new IRExpression(instr, new Token(curIR[curIR.length - 1]), list));
                            break;
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }

            System.out.println("Input read in\n");

            System.out.println(irList.printIR());

            // This will change upon implementation of assignment 3 of compiler.
            exit(0);
        } else if (line.hasOption("f")) {
            try {
                file = new File(line.getOptionValue("f"));

                if(!file.exists()) {
                    System.err.println("ERROR: file '" + file + "' does not exist!");
                    exit(1);
                }

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
                //e.printStackTrace();
            }

        } else {
            System.out.println("\033[0;31m" + "error:" + "\033[0m" + "no input files");
            System.out.println("Please use the argument '-h' for help.");
        }

        //displays helpful information
        if (line.hasOption("h")) {
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp("ant", commandArgs);
        }

        /** TOKEN ARGUMENTS **/

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

            String fileName = "jxc_tokens.txt";

            if(line.getOptionValue("to") != null) {
                fileName = line.getOptionValue("to");
            }

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                writer.write(str.toString());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        root = Parser.Instance().Parse(tokens, file.getName());

        /** PARSE TREE ARGUMENTS **/

        //parse tree options
        if (line.hasOption("p")) {

            //display parse tree to command line
            System.out.println("\n\nPARSER:");

            NodePrinter printer = new NodePrinter();
            root.accept(printer);

            System.out.println(printer.getTree());
        }

        if (line.hasOption("po")) {
            //prints parse tree to output file

            String fileName = "jxc_parse_tree.txt";

            if(line.getOptionValue("po") != null){
                fileName = line.getOptionValue("po");
            }

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                writer.write("\n\nPARSER:");
                NodePrinter printer = new NodePrinter();
                root.accept(printer);

                writer.write(printer.getTree());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /** SYMBOL TABLE ARGUMENTS **/

        //displays symbol table to the commandline.
        if (line.hasOption("s")) {
            System.out.println("\n\nSYMBOL TABLE:");
            System.out.println(Parser.Instance().getTable());
        }

        //prints symbol table to output file
        if(line.hasOption("so")){
            String fileName = "jxc_symbol_table.txt";

            if(line.getOptionValue("so") != null){
                fileName = line.getOptionValue("so");
            }

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));

                writer.write(Parser.Instance().getTable());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        /** IR BUILDER ARGUMENTS **/

        IRBuilder irBuilder = new IRBuilder();
        root.accept(irBuilder);

        if (line.hasOption("i")) {
            System.out.println("\nIntermediate Representation");
            System.out.println(irBuilder.IRs.printIR());
        }

        if(line.hasOption("io")) {

            String fileName = "jxc_IR_expressions.txt";

            if (line.getOptionValue("io") != null) {
                fileName = line.getOptionValue("io");
            }

            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                NodePrinter printer = new NodePrinter();

                writer.write(irBuilder.IRs.printIR());
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
