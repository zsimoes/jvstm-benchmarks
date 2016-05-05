package jvstmresults;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.List;

public class PlotExecution
{

	protected static String nl = System.getProperty("line.separator");

	protected static final String gnuplotCode = "# This file was generated by ProcessExecution.java." + nl + "reset"
			+ nl + "set terminal pdfcairo" + nl + "unset for [i=1:8] label i" + nl
			+ "set title \"jvstm execution time comparison\"" + nl + "set xlabel \"configuration\"" + nl
			+ "set bmargin 6" + nl + "set offset .05, .05" + nl

			+ "set style line 1 lt -1 lc rgb \"black\" pi -1 pt 4 ps 0.4 lw 2" + nl
			+ "set style line 2 lt -1 lc rgb \"black\" pi -1 pt 5 ps 0.4 lw 2" + nl
			+ "set style line 3 lt -1 lc rgb \"black\" pi -1 pt 6 ps 0.4 lw 2" + nl
			+ "set style line 4 lt -1 lc rgb \"black\" pi -1 pt 7 ps 0.4 lw 2" + nl
			+ "set style line 5 lt -1 lc rgb \"black\" pi -1 pt 8 ps 0.4 lw 2" + nl
			+ "set style line 6 lt -1 lc rgb \"black\" pi -1 pt 9 ps 0.4 lw 2" + nl + nl
			+ "# The following sections generate plots for the result file data:" + nl + nl;

	protected static final String startPlot = "plot ";

	protected String getPlotLine(JVSTMLog resultFile, int linestyle, boolean endPlot)
	{
		String result = " '" + resultFile.logFile + "' " + "using 2:xticlabels(1) with linespoints ls " + +linestyle
				+ " title '" + resultFile.policy + "-" + resultFile.contention + "' " + (endPlot ? "" : ", \\") + nl;
		return result;
	}

	protected String getPlotLine(JVSTMLog resultFile, String subFolderName, int linestyle, boolean endPlot)
	{
		String result = " '" + subFolderName + File.separator + resultFile.logFile + "' "
				+ "using 2:xticlabels(1) with linespoints ls " + linestyle + " title '" + resultFile.policy + "-"
				+ resultFile.contention + "' " + (endPlot ? "" : ", \\") + nl;
		return result;
	}

	protected String getOutputString(String targetFile)
	{
		return "set output \"" + targetFile + "\"" + nl;
	}

	public void process(JVSTMLogFolder jvstmFolder)
	{
		StringBuilder gnuplotBuilder = new StringBuilder();

		if (jvstmFolder.getJvstmPolicies().size() != 1)
		{
			throw new RuntimeException("ProcessPolicyExecution: Expected single policy on all logs.");
		}

		String policy = jvstmFolder.jvstmPolicies.iterator().next();

		gnuplotBuilder.append(gnuplotCode);
		gnuplotBuilder.append("#JVSTM type: " + policy + nl);
		gnuplotBuilder.append(getOutputString("execution" + policy + ".pdf") + nl);

		List<JVSTMLog> results = jvstmFolder.getResults();
		gnuplotBuilder.append(startPlot);

		for (int i = 0; i < results.size(); i++)
		{
			boolean endPlot = (i == (results.size() - 1));
			int linestyle = i + 1;
			gnuplotBuilder.append(getPlotLine(results.get(i), linestyle, endPlot));
		}
		gnuplotBuilder.append(nl);
		gnuplotBuilder.append(nl);

		String executionGPResult = "execution" + policy + ".gp";
		File gnuplot = new File(executionGPResult);
		try
		{
			Writer w = new FileWriter(gnuplot, false);
			w.write(gnuplotBuilder.toString());
			w.close();
		} catch (IOException e)
		{
			System.err.println("ERROR: " + e.getMessage());
			throw new RuntimeException(e);
		}

		System.err.println("Done - generated file " + "execution-" + policy + ".gp");
	}

}