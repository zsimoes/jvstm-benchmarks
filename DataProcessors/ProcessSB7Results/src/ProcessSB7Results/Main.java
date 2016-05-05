package ProcessSB7Results;

import java.io.File;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main
{

	private static Options options = new Options();

	static
	{
		/* @formatter:off
		options.addOption("exhaustive", false,
				"Process exhaustive data. Expects <target> to be a single file with exhaustive execution data (surface)");
		options.addOption(Option.builder("exhaustiveData")
				.desc("Provide an exaustive data file for calculating optima on convergence plots").hasArg()
				.optionalArg(true).argName("datafile").build());
		 @formatter:on */
	}

	public static void main(String[] args)
	{

		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine = null;
		try
		{
			commandLine = parser.parse(options, args);
		} catch (ParseException e)
		{
			System.out.println(e.getMessage());
			printHelpAndExit();
		}

		String[] arguments = commandLine.getArgs();
		if (arguments.length != 1)
		{
			printHelpAndExit();
		}

		File target = new File(arguments[0]);

		if (!(target.exists() && target.isDirectory()))
		{
			System.out.println("Invalid target directory: " + arguments[0]);
			System.exit(1);
		}

		//not needed for now:
		/* @formatter:off
		if (commandLine.hasOption("throughput"))
		{
			all(target, commandLine.getOptionValue("exhaustiveData"), true);
		} 
		@formatter:on */

		throughput(target);
		System.err.println("All Done.");
	}
	
	protected static void throughput(File rootDirectory) {
		ProcessThroughput pt = new ProcessThroughput();
		pt.process(rootDirectory, true);

	}

	public static void printHelpAndExit()
	{
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("ProcessData <OPTIONS> <TARGET>", options);
		System.exit(1);
	}

}
