package jvstmresults;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jvstm.tuning.TuningPoint;
import jvstm.util.Pair;

public class PlotConvergence
{
	protected static String nl = String.format("%n");
	protected static String localOptimaExt = ".localoptima";
	protected static String globalOptimaExt = ".globaloptima";
	protected String policy;
	protected String config;
	protected String targetFileName;
	protected String pathFileName;
	protected String parentPath;

	/* @formatter:off */
	protected static String gnuplotCode1 = 
			"# This file was generated by ProcessConvergence.java. The next code section is preset:" + nl
			+ "set term gif animate delay 8 size 600, 600" + nl 
			+ "set output \"path-OUTPUTNAME.gif\"" + nl
			+ "set style line 1 lw 2 lc rgb 'black'" + nl 
			+ "set style line 2 lw 1 lc rgb '#FFDCDCDC'" + nl
			+ "set style line 3 lw 1 lc rgb 'gray' pt 7 ps 0.5 " + nl  
			+ "set style line 4 lw 1 lc rgb 'black' pt 6 ps 3" + nl
			+ "set style line 5 lw 1 lc rgb '#FF646464' pt 6 ps 2" + nl
			+ "rgb(a) = int(255-(a*255))*65536 + int(255-(a*255))*256 + int(255-(a*255))" + nl 
			+ "" + nl
			+ "do for [n=0:%d] {" + nl
			+ "	plot [0:%d][0:%d] ";
	protected static String gnuplotOptima = 
			"\"%s\" u 1:2 w p ls 4 t \"Static global optima for (x,y)<(%d,%d)\", \\" + nl 
			+ "\"%s\" u 1:2 w p ls 5 t \"Static local optima for (x,y)<(%d,%d)\", \\" + nl;
	protected static String gnuplotCode2 =
			  "  \"INPUTNAME\" u 2:3 every ::::n-2 w l ls 2 notitle, \\" + nl
			+ "  \"INPUTNAME\" u 2:3 every ::n-1::n w l ls 1 t sprintf(\"%s, %s ,n=%%i\", n), \\" + nl
			+ "  \"INPUTNAME\" u 2:3:(rgb($4)) every ::::n w p pt 7 ps 1 lw 2 lc rgb variable notitle, \\" + nl
			+ "  \"INPUTNAME\" u 2:3 every ::n::n w p ls 3 notitle;" + nl
			+ "}" + nl;
	/* @formatter:on */

	private void buildGnuPlotFile(JVSTMLog jvstmLog, String exhaustiveFile)
	{
		String formattedGnuPlot = String.format(gnuplotCode1, jvstmLog.tuningPath.size(), jvstmLog.maxThreads,
				jvstmLog.maxThreads);
		
		String outputName = targetFileName + ".gif";  
		formattedGnuPlot = formattedGnuPlot.replace("OUTPUTNAME", outputName);
		if (exhaustiveFile != null)
		{
			formattedGnuPlot += String.format(gnuplotOptima, exhaustiveFile + globalOptimaExt, jvstmLog.maxThreads,
					jvstmLog.maxThreads, exhaustiveFile + localOptimaExt, jvstmLog.maxThreads, jvstmLog.maxThreads);
		}
		String gp2 = String.format(gnuplotCode2, jvstmLog.policy.getName(), jvstmLog.contention);
		gp2 = gp2.replace("INPUTNAME", pathFileName);
		formattedGnuPlot += gp2;

		String gpName = targetFileName + ".gp";  
		File gnuplot = new File(parentPath, gpName);
		try
		{
			PrintWriter writer = new PrintWriter(new FileWriter(gnuplot));
			writer.print(formattedGnuPlot);
			writer.close();
		} catch (IOException e)
		{
			System.err.println("ERROR: " + e.getMessage());
			throw new RuntimeException(e);
		}
		
		System.err.println("PlotConvergence - Generated file " + gnuplot.getParentFile().getName() + "\\" + gnuplot.getName());

	}

	public void process(File target, String exhaustiveOptimaFilePath)
	{
		String[] parts = target.getName().split("\\.|-");
		this.policy = parts[1];
		this.config = parts[2];
		targetFileName = policy + "-" + config;
		
		JVSTMLog jvstmLog = new ProcessLog().process(target);
		parentPath = target.getParent();
		String optimaFile = null;
		if (exhaustiveOptimaFilePath != null)
		{
			optimaFile = buildOptimaFile(jvstmLog, exhaustiveOptimaFilePath);
		}
		buildPathFile(jvstmLog);
		buildGnuPlotFile(jvstmLog, optimaFile);
	}

	public String buildOptimaFile(JVSTMLog jvstmLog, String exhaustiveFile)
	{
		PlotExhaustive exhaustive = new PlotExhaustive();
		int restrict = jvstmLog.maxThreads;

		Pair<DataPoint, List<DataPoint>> optima = exhaustive.findOptima(exhaustiveFile, restrict);

		File exh = new File(exhaustiveFile);
		int idx = exh.getName().lastIndexOf('.');
		String filename = exh.getName().substring(0, idx);
		try
		{
			File globalFile = new File(parentPath, filename + globalOptimaExt);
			File localFile = new File(parentPath, filename + localOptimaExt);
			System.err.println("  (PlotConvergence - intermediate data) Writing optima files: " + nl + "\t"
					+ localFile.getParentFile().getName() + "\\" + localFile.getName() + nl + "\t"
					+ globalFile.getParentFile().getName() + "\\" + globalFile.getName());

			PrintWriter globalWriter = new PrintWriter(new FileWriter(globalFile));

			globalWriter.println(optima.first.x + " " + optima.first.y);
			globalWriter.close();

			PrintWriter localWriter = new PrintWriter(new FileWriter(localFile));
			for (DataPoint dp : optima.second)
			{
				localWriter.println(dp.x + " " + dp.y);
			}
			localWriter.close();
			return filename;
		} catch (IOException e)
		{
			System.err.println("ERROR: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	public void buildPathFile(JVSTMLog jvstmLog)
	{
		File pathFile = new File(parentPath, "path-" + targetFileName + ".dat");
		pathFileName = pathFile.getName();
		System.err.println("  (PlotConvergence - intermediate data) Writing tuning path file: " + nl
				+ "\t" + pathFile.getParentFile().getName() + "\\" + pathFile.getName());

		StringBuilder pathBuilder = new StringBuilder();
		List<TuningPoint> path = jvstmLog.tuningPath;

		String nl = String.format("%n");
		pathBuilder.append("t x y incidence" + nl);

		Map<TuningPoint, Float> incidence = new HashMap<TuningPoint, Float>();
		float bestIncidence = 0;

		// count occurrences:
		for (int i = 0; i < path.size(); i++)
		{
			TuningPoint point = path.get(i);
			if (incidence.containsKey(path.get(i)))
			{
				float inc = incidence.get(point) + 1;
				incidence.put(point, inc);
				if (inc > bestIncidence)
				{
					bestIncidence = inc;
				}
			} else
			{
				incidence.put(point, 1F);
			}
		}

		bestIncidence /= path.size();
		// normalize:
		for (Map.Entry<TuningPoint, Float> entry : incidence.entrySet())
		{
			float value = entry.getValue();
			float pointIncidence = value / path.size();
			float normalizedIncidence = pointIncidence / bestIncidence;
			entry.setValue(normalizedIncidence);
		}

		// print:
		for (int i = 0; i < path.size(); i++)
		{
			TuningPoint point = path.get(i);
			pathBuilder.append(i + " " + point.first + " " + point.second + " " + incidence.get(point) + nl);
		}

		try
		{
			Writer w = new FileWriter(pathFile, false);
			w.write(pathBuilder.toString());
			w.close();
		} catch (IOException e)
		{
			System.err.println("ERROR: " + e.getMessage());
			throw new RuntimeException(e);
		}
	}

}