package jvstmresults;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.trie.PatriciaTrie;
import org.joda.time.DateTime;

import jvstm.tuning.StatisticsCollector;
import jvstm.tuning.TuningPoint;
import jvstm.tuning.policy.PointProvider.TuningRecord;
import jvstm.tuning.policy.PointProvider.TuningRoundInfo;
import jvstm.tuning.policy.TuningPolicy;
import jvstm.tuning.policy.TuningPolicy.MeasurementType;

public class ProcessLog
{

	protected static boolean skippedDistances = false;

	public JVSTMLog process(String targetPath)
	{
		File targetFile = new File(targetPath);
		return process(targetFile);
	}

	public JVSTMLog process(File target)
	{

		if (!(target.exists() && target.isFile()))
		{
			throw new RuntimeException("Invalid log file: Is directory or non-existant.");
		}

		JVSTMLog result = new JVSTMLog();

		List<String> logLines = null;
		try
		{
			logLines = Util.readAllLines(target);
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		processHeader(logLines, result);

		/*
		 * ROUND INFO
		 */

		processRoundInfo(logLines, result);

		/*
		 * PATH
		 */

		processTuningPath(logLines, result);

		/*
		 * THROUGHPUT
		 */

		processThroughput(logLines, result);

		/*
		 * TCR
		 */

		processTCR(logLines, result);

		/*
		 * DISTANCES
		 */

		processDistances(logLines, result);

		return result;

	}

	@SuppressWarnings("unused")
	private void processDistances(List<String> logLines, JVSTMLog result)
	{
		result.distances = new LinkedList<Float>();
		try
		{
			
			List<String> lines = Util.getSectionLines(logLines, StatisticsCollector.headerDistance);
		} catch (RuntimeException e)
		{
			if (!skippedDistances)
			{
				System.err.println("  (INFO) ProcessLog -Skipped distances log - not found. This step will be phased out soon, skipping distances for entire run.");
				skippedDistances = true;
			}
			return;
		}
	}

	private void processTCR(List<String> logLines, JVSTMLog result)
	{
		result.tcr = new LinkedList<Float>();
		List<String> lines = Util.getSectionLines(logLines, StatisticsCollector.headerTCR);
		String[] tcrs = lines.get(0).split(" ");
		for (String tcrString : tcrs)
		{
			float tcr = Float.parseFloat(tcrString.replace(",", "."));
			result.tcr.add(tcr);
		}

	}

	private void processThroughput(List<String> logLines, JVSTMLog result)
	{
		result.throughput = new LinkedList<Float>();
		List<String> lines = Util.getSectionLines(logLines, StatisticsCollector.headerThroughput);
		if (lines.size() != 1)
		{
			throw new RuntimeException("Error: Expected only one line on throughput section.;");
		}
		String[] throughputs = lines.get(0).split(" ");
		for (String thr : throughputs)
		{
			float throughput = Float.parseFloat(thr.replace(",", "."));
			result.throughput.add(throughput);
		}

	}

	private void processTuningPath(List<String> logLines, JVSTMLog result)
	{
		result.tuningPath = new LinkedList<TuningPoint>();
		List<String> lines = Util.getSectionLines(logLines, StatisticsCollector.headerTuningPath);
		if (lines.size() != 1)
		{
			throw new RuntimeException("Error: Expected only one line on tuning path section.;");
		}
		String[] points = lines.get(0).split(" ");
		for (String point : points)
		{
			TuningPoint tuningPoint = Util.pointFromString(point);
			result.tuningPath.add(tuningPoint);
		}

	}

	private void processRoundInfo(List<String> logLines, JVSTMLog result)
	{
		// Format example:
		// BestPoint - [4,2] {31,00} - Alts - [4,2] {0,00} , [4,1] {0,00}

		result.roundInfo = new LinkedList<TuningRoundInfo>();

		List<String> lines = Util.getSectionLines(logLines, StatisticsCollector.headerTuningRoundLogs);

		String recordPattern = "\\[\\d+,\\d+\\] \\{-?\\d+((,|\\.)\\d+)?\\}";
		Pattern p = Pattern.compile(recordPattern);

		for (String roundLine : lines)
		{
			TuningRoundInfo tuningRound = new TuningRoundInfo();
			Matcher m = p.matcher(roundLine);

			int found = 0;
			while (m.find())
			{
				found++;
				TuningRecord record = Util.recordFromString(m.group());
				if (found == 1)
				{
					// best point
					tuningRound.setBest(record);
				} else
				{
					tuningRound.add(record);
				}
			}

			if (found < 2)
			{
				throw new RuntimeException("Error reading round info line (too few points - " + found + "): " + roundLine);
			}

			result.roundInfo.add(tuningRound);
		}

	}

	/**
	 * Fills JVSTMLog header.
	 * 
	 * @param logLines
	 * @param result
	 */
	@SuppressWarnings("unchecked")
	protected void processHeader(List<String> logLines, JVSTMLog result)
	{
		// store header lines:

		List<String> headerLines = Util.getSectionLines(logLines, StatisticsCollector.headerParameters);

		PatriciaTrie<String> trie = new PatriciaTrie<String>();
		for (String s : headerLines)
		{
			trie.put(s, s);
		}

		result.noStats = Boolean.parseBoolean(Util.getValueByKeyPrefix(trie, "NoStats"));

		result.logFile = Util.getValueByKeyPrefix(trie, "LogFile");

		try
		{
			result.interval = Integer.parseInt(Util.getValueByKeyPrefix(trie, "Interval"));
		} catch (NumberFormatException n)
		{
			result.interval = -1;
		}
		try
		{
			result.maxThreads = Integer.parseInt(Util.getValueByKeyPrefix(trie, "MaxThreads"));
		} catch (NumberFormatException n)
		{
			result.interval = -1;
		}
		result.initialConfig = Util.pointFromString(Util.getValueByKeyPrefix(trie, "InitialConfig"));
		try
		{
			result.policy = (Class<? extends TuningPolicy>) Class.forName(Util.getValueByKeyPrefix(trie, "Policy"));
		} catch (ClassNotFoundException e)
		{
			throw new RuntimeException(e);
		}

		result.measurementType = MeasurementType.valueOf(Util.getValueByKeyPrefix(trie, "MeasurementType"));
		result.stubFile = Util.getValueByKeyPrefix(trie, "StubFile");
		result.stubOptimum = Util.pointFromString(Util.getValueByKeyPrefix(trie, "StubOptimum"));
		result.logDistances = Boolean.parseBoolean(Util.getValueByKeyPrefix(trie, "LogDistances"));

		result.contention = Util.getValueByKeyPrefix(trie, "Contention");

		result.aditionalBenchmarkInfo = Util.getValueByKeyPrefix(trie, "AditionalBenchmarkInfo");

		String timestamp = Util.getValueByKeyPrefix(trie, "Timestamp");
		try
		{
			long millis = Long
					.parseLong(timestamp.substring(timestamp.lastIndexOf('(') + 1, timestamp.lastIndexOf(')')));
			result.timeStamp = new DateTime(millis);
		} catch (NumberFormatException n)
		{

		}

		try
		{
			result.executionTime = Integer.parseInt(Util.getValueByKeyPrefix(trie, "ExecutionTime"));
		} catch (NumberFormatException e)
		{
			throw new RuntimeException(e);
		}

	}

}
