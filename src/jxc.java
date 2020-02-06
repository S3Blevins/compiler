import org.apache.commons.cli.*;

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
        commandArgs.addOption("p", "parsetree", false, "Displays parse tree to command line.");
        commandArgs.addOption("po", "parsetreeout", false, "Prints parse tree to output to a file.");

        //parse command line options
        CommandLine line = null;
        try {
            line = commandParser.parse( commandArgs, args);
        } catch (ParseException exp) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }

        //displays helpful information
        if(line.hasOption("h")){
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp( "ant", commandArgs );
        }

        //parse tree options
        if(line.hasOption("p")){
            //display parse tree to command line
        }

        if(line.hasOption("po")){
            //prints parse tree to output file
        }

        //token options
        if(line.hasOption("t")){
            //displays tokens to command line
        }

        if (line.hasOption("to")) {
            //displays token to command line and outputs to a file
        }

    }
}
