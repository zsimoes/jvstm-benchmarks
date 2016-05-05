package jvstmresults;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Main
{
	protected static String exhaustiveFile = null;

	protected static FileFilter resultFilter = new FileFilter()
	{
		@Override
		public boolean accept(File pathname)
		{
			if (pathname.getName().endsWith(".data") || pathname.getName().endsWith(".log")
					|| pathname.getName().endsWith(".exhaustive"))
			{
				return true;
			} else
			{
				return false;
			}
		}
	};

	public static void main(String[] args)
	{

		Options options = new Options();

		options.addOption("exhaustive", false,
				"Process exhaustive data. Expects <target> to be a single file with exhaustive execution data (surface)");
		options.addOption("convergence", false,
				"Process convergence data. Expects <target> to be a JVSTM tuning log file");
		options.addOption("execution", false,
				"Process execution data. Expects <target> to be a folder containing one JVSTM log file for each type of policy");
		options.addOption("overhead", false,
				"Process overhead data. Expects <target> to be a file with simple overhead data");
		options.addOption("general", false, "Process convergence, execution and overhead data");
		options.addOption("all", false,
				"Process all types of data. Expects <target> to be a folder with one subfolder for each processing type, e.g. overhead, convergence, etc. Each subfolder should have the expected files for its processing type.");
		options.addOption(Option.builder("exhaustiveData")
				.desc("Provide an exaustive data file for calculating optima on convergence plots").hasArg()
				.optionalArg(true).argName("datafile").build());

		CommandLineParser parser = new DefaultParser();
		CommandLine commandLine = null;
		try
		{
			commandLine = parser.parse(options, args);
		} catch (ParseException e)
		{
			System.out.println(e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ProcessData <OPTIONS> <TARGET>", options);
			System.exit(1);
		}

		String[] arguments = commandLine.getArgs();
		if (arguments.length != 1)
		{
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("ProcessData <OPTIONS> <TARGET>", options);
			System.exit(1);
		}

		File target = new File(arguments[0]);

		if (!target.exists())
		{
			System.out.println("Invalid target: " + arguments[0]);
			System.exit(1);
		}

		if (commandLine.hasOption("all"))
		{
			all(target, commandLine.getOptionValue("exhaustiveData"), true);
		} else if (commandLine.hasOption("general"))
		{
			all(target, commandLine.getOptionValue("exhaustiveData"), false);
		} else
		{
			if (commandLine.hasOption("exhaustive"))
			{
				exhaustive(target);
			}
			if (commandLine.hasOption("overhead"))
			{
				overhead(target);
			}
			if (commandLine.hasOption("convergence"))
			{
				convergence(target, commandLine.getOptionValue("exhaustiveData"));
			}
			if (commandLine.hasOption("execution"))
			{
				execution(target);
			}
		}
		System.err.println("Done.");
	}

	public static void all(File target, String exhaustiveData, boolean doExhaustive)
	{
		if (!target.isDirectory())
		{
			System.out.println("(Process all) Invalid target folder: " + target);
			System.exit(1);
		}
		HashMap<String, File> subfolders = new HashMap<String, File>();
		File[] subf = target.listFiles();
		for (File f : subf)
		{
			subfolders.put(f.getName(), f);
		}

		boolean valid;

		valid = (doExhaustive ? subfolders.containsKey("exhaustive") : true) && subfolders.containsKey("overhead")
				&& subfolders.containsKey("convergence") && subfolders.containsKey("execution");
		if (!valid)
		{
			System.out.println("(Process all) Invalid subfolder structure: " + subfolders.keySet());
			System.exit(1);
		}

		if (doExhaustive)
		{
			target = subfolders.get("exhaustive");
			exhaustive(target);
		}
		target = subfolders.get("overhead");
		overhead(target);
		target = subfolders.get("convergence");
		convergence(target, exhaustiveData);
		target = subfolders.get("execution");
		execution(target);
	}

	protected static File getSingleFile(File directory, String source)
	{
		File[] files = directory.listFiles();
		if (files.length < 1)
		{
			System.out.println("(Process" + source + ") No jvstm log files found.");
			System.exit(1);
		} else if (files.length > 1)
		{
			File[] list = directory.listFiles(resultFilter);

			if (list.length > 0)
			{
				System.err.println("  (INFO) Process " + source + " - Multiple files found. Using first file: "
						+ list[0].getName());
				return list[0];
			}

			System.err.println("  (INFO) Process " + source
					+ " - Multiple files found, but no \".data\", \".log\" or \".exhaustive\" extensions. Exiting.");
			System.exit(1);
		}
		return files[0];
	}

	public static void execution(File target)
	{
		PlotMultipleExecutions pm = new PlotMultipleExecutions();
		pm.process(target);
	}

	public static void convergence(File target, String exhaustiveDataPath)
	{
		PlotConvergence pc = new PlotConvergence();
		
		if (target.isFile() && resultFilter.accept(target))
		{
			pc.process(target, exhaustiveDataPath);
		} else if (target.isDirectory())
		{
			File[] files = target.listFiles(resultFilter);
			if (files.length < 1)
			{
				System.err.println("(Main) error: no convergence files found in target - " + target.getAbsolutePath());
			}
			for (File exh : files)
			{
				pc.process(exh, exhaustiveDataPath);
			}
		} else
		{
			throw new RuntimeException("(Main) error filtering convergence files for processing.");
		}
		
	}

	public static void overhead(File target)
	{
		PlotOverhead po = new PlotOverhead();
		
		if (target.isFile() && resultFilter.accept(target))
		{
			po.process(target);
		} else if (target.isDirectory())
		{
			File[] files = target.listFiles(resultFilter);
			if (files.length < 1)
			{
				System.err.println("(Main) error: no overhead files found in target - " + target.getAbsolutePath());
			}
			for (File exh : files)
			{
				po.process(exh);
			}
		} else
		{
			throw new RuntimeException("(Main) error filtering overhead files for processing.");
		}
		
	}

	public static void exhaustive(File target)
	{

		PlotExhaustive pe = new PlotExhaustive();
		if (target.isFile() && resultFilter.accept(target))
		{
			pe.processSingleFile(target);
		} else if (target.isDirectory())
		{
			File[] files = target.listFiles(resultFilter);
			if (files.length < 1)
			{
				System.err.println("(Main) error: no exhaustive files found in target - " + target.getAbsolutePath());
			}
			for (File exh : files)
			{
				pe.processSingleFile(exh);
			}
		} else
		{
			throw new RuntimeException("(Main) error filtering exhaustive files for processing.");
		}
	}

}
