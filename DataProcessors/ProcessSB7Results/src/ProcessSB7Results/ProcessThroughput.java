package ProcessSB7Results;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class ProcessThroughput
{
	public static String nl = String.format("\n");
	// @formatter:off
	protected static String gpCode =
			"set term png size 800, 800"+ nl 
			+"set output \"OUTPUTFILE\""+ nl 
			+"unset log"+ nl 
			+"unset label"+ nl 
			+ "unset mxtics"+ nl 
			+"set autoscale"+ nl
			+"set xlabel \"thread config (top x nested)\""+ nl 
			+"set ylabel \"Throughput (as percentage of Default Policy's throughput)\""+ nl 
			+"set yrange [0:]"+ nl 
			+"set key outside right top"+ nl 
			+"set style fill pattern border -1"+ nl 
			+"set style data histograms"+ nl 
			+"set label \"(0,0) screen\" at screen 0, screen 0" +nl
			+"set style histogram clustered gap 1" + nl + nl;
	// @formatter:on

	public ProcessThroughput()
	{
	}

	public void process(File rootDirectory, boolean normalize)
	{
		ProcessTarget sb7 = new ProcessTarget();
		sb7.process(rootDirectory);

		createFiles(rootDirectory, sb7, normalize);
	}

	public void createFiles(File rootDirectory, ProcessTarget sb7, boolean normalize)
	{
		if (!sb7.isDone())
		{
			sb7.process(rootDirectory);
		}

		// one .gp plots all load types:
		StringBuilder codeBuilder = new StringBuilder(gpCode.replace("OUTPUTFILE", "sb7.png"));

		for (String load : sb7.loadTypes)
		{
			// make a plot for each load type:
			File dataFile = new File(rootDirectory, load + ".intermediate");
			File csvFile = new File(rootDirectory, load + ".csv");
			StringBuilder dataBuilder = new StringBuilder();
			StringBuilder csvBuilder = new StringBuilder();

			// insert column headers:
			dataBuilder.append("Config ");
			csvBuilder.append("sep=,");
			csvBuilder.append(nl);
			csvBuilder.append("Config,");
			int i = sb7.policyTypes.size();
			for (String policy : sb7.policyTypes)
			{
				dataBuilder.append(policy + " ");
				i--;
				if (i == 0)
				{
					csvBuilder.append(policy);
				} else
				{
					csvBuilder.append(policy + ",");
				}

			}

			// CODE BUILDER:
			boolean first = true;
			for (int plotIndex = 2; plotIndex < sb7.policyTypes.size() + 2; plotIndex++)
			{
				if (first)
				{
					codeBuilder.append("set title \"STMBench7 Results - Load type: " + load + "\"" + nl);
					codeBuilder.append("plot '" + dataFile.getAbsolutePath() + "' using " + plotIndex
							+ ":xtic(1) fs pattern " + (plotIndex - 2) + " lc rgbcolor \"black\" title col");
					first = false;
				} else
				{
					codeBuilder.append("\t\t'' using " + plotIndex + ":xtic(1) fs pattern " + (plotIndex - 2)
							+ " lc rgbcolor \"black\" title col");
				}

				boolean lastLine = (plotIndex + 1) == (sb7.policyTypes.size() + 2);
				if (!lastLine)
				{
					codeBuilder.append(", \\" + nl);
				} else
				{
					codeBuilder.append(nl);
				}
			}
			codeBuilder.append(nl);

			// END CODE BUILDER

			// DATA+CSV BUILDER:

			for (String configType : sb7.configTypes)
			{
				//init stringbuilder lines (each configType yields a line):
				dataBuilder.append(nl);
				csvBuilder.append(nl);
				dataBuilder.append(configType + " ");
				csvBuilder.append(configType + ",");

				//fetch values
				List<Float> values = new ArrayList<>();
				for (String policy : sb7.policyTypes)
				{
					SB7Result result = sb7.result.get(load, configType, policy);
					float throughput;
					if (result == null)
					{
						System.err.println("Error fetching throughput for load:" + load + "/config:" + configType
								+ "/policy:" + policy + ". Using value 0.0.");
						throughput = 0f;
					} else
					{
						throughput = result.getThroughput();
					}

					values.add(throughput);
				}
				
				// normalize if needed, and append values 

				for (int v = 0; v < values.size(); v++)
				{
					float value = values.get(v);
					boolean lastValue = v == values.size() - 1;
					if (normalize)
					{
						float defaultPolicyThroughput = sb7.result.get(load, configType, "Default").getThroughput();
						value /= defaultPolicyThroughput;
					}
					
					dataBuilder.append(value + " ");
					csvBuilder.append(value + (lastValue ? "" : ","));
				}
			}

			PrintWriter dataWriter;
			try
			{
				dataWriter = new PrintWriter(dataFile);
				dataWriter.print(dataBuilder.toString());
				dataWriter.close();
			} catch (IOException e)
			{
				System.err.println("Error writing intermediate data file " + dataFile.getName() + ". Skipping. Cause: "
						+ e.getMessage());
				continue;
			}

			PrintWriter csvWriter;
			try
			{
				csvWriter = new PrintWriter(csvFile);
				csvWriter.print(csvBuilder.toString());
				csvWriter.close();
			} catch (IOException e)
			{
				System.err.println(
						"Error writing csv data file " + csvFile.getName() + ". Skipping. Cause: " + e.getMessage());
				continue;
			}

			// END DATA+CSV BUILDER
		}

		File codeFile = new File(rootDirectory, "sb7.gp");
		PrintWriter codeWriter;
		try
		{
			codeWriter = new PrintWriter(codeFile);
			codeWriter.print(codeBuilder.toString());
			codeWriter.close();
		} catch (FileNotFoundException e)
		{
			System.err.println(
					"Error writing gnuplot file " + codeFile.getName() + ". Skipping. Cause: " + e.getMessage());
		}

	}

}
