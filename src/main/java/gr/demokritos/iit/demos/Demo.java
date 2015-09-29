/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gr.demokritos.iit.demos;

import gr.demokritos.iit.api.API;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * @author grv
 */
public class Demo {
	
	private static final String NER = "ner";
	private static final String RE = "re";
	private static final String SAG = "isSAG";
	private static final String INPUT = "infile";
	private static final String OUTPUT = "outfile";
	private static final String PROCESS = "process";
	private static final String HELP = "help";
	private static final String DEFAULT_INFILE = "default.json";
	private static final String DEFAULT_OUTFILE = "out.json";

	public static void main(String[] args){
		try {
			Options options = new Options();
			options.addOption("h", HELP, false, "show help.");
			options.addOption("i", INPUT, true, "The file containing JSON " +
					" representations of tweets or SAG posts - 1 per line" + 
					" default file looked for is " + DEFAULT_INFILE);
			options.addOption("o", OUTPUT, true, "Where to write the output " +
					" default file looked for is " + DEFAULT_OUTFILE);
			options.addOption("p", PROCESS, true, "Type of processing to do " +
					" ner for Named Entity Recognition re for Relation Extraction" +
					" default is NER");
			options.addOption("s", SAG, false, "Whether to process as SAG posts" +
					" default is off - if passed means process as SAG posts");
			
			CommandLineParser parser = new BasicParser();
			CommandLine cmd = parser.parse(options, args);
			// DEFAULTS
			String filename = DEFAULT_INFILE;
			String outfilename = DEFAULT_OUTFILE;
			String process = NER;
			boolean isSAG = false;

			if(cmd.hasOption(HELP)){
				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("NER + RE extraction module", options);
				System.exit(0);
			}
			if(cmd.hasOption(INPUT)){
				filename = cmd.getOptionValue(INPUT);
			}
			if(cmd.hasOption(OUTPUT)){
				outfilename = cmd.getOptionValue(OUTPUT);
			}
			if(cmd.hasOption(SAG)){
				isSAG = true;
			}
			if(cmd.hasOption(PROCESS)){
				process = cmd.getOptionValue(PROCESS);
			}
			System.out.println();
			System.out.println("Reading from file: " + filename);
			System.out.println("Process type: " + process);
			System.out.println("Processing SAG: " + isSAG);
			System.out.println("Writing to file: " + outfilename);
			System.out.println();

			List <String> jsoni = new ArrayList();
			Scanner in = new Scanner(new FileReader(filename));
			while(in.hasNextLine()){
				String json = in.nextLine();
				jsoni.add(json);
			}
			PrintWriter writer = new PrintWriter(outfilename, "UTF-8");
			System.out.println("Read " + jsoni.size() + " lines from " + filename);
			if(process.equalsIgnoreCase(RE)){
				System.out.println("Running Relation Extraction");
				System.out.println();
				String json = API.RE(jsoni, isSAG);
				System.out.println(json);
				writer.print(json);
			}
			else{
				System.out.println("Running Named Entity Recognition");
				System.out.println();
				jsoni = API.NER(jsoni, isSAG);
				/*
				for(String json: jsoni){
					NamedEntityList nel = NamedEntityList.fromJSON(json);
					nel.prettyPrint();
				}
				*/
				for (String json: jsoni) {
					System.out.println(json);
					writer.print(json);
				}
			}
			writer.close();
		} catch (ParseException | UnsupportedEncodingException | FileNotFoundException ex) {
			Logger.getLogger(Demo.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}
