package jvstmresults;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import jvstm.util.Pair;

public class PlotExhaustive
{

	class ProcessedFile
	{

		public ProcessedFile()
		{
			this.scanLines = new HashMap<Integer, List<DataPoint>>();
			this.matrix = new HashMap<Integer, Map<Integer, DataPoint>>();
			this.sortedData = new TreeMap<Double, List<DataPoint>>();
		}

		public ProcessedFile(Map<Integer, List<DataPoint>> scanLines, TreeMap<Double, List<DataPoint>> sortedData)
		{
			this.scanLines = scanLines;
			this.sortedData = sortedData;
		}

		public Map<Integer, List<DataPoint>> getScanLines()
		{
			return scanLines;
		}

		public void setScanLines(Map<Integer, List<DataPoint>> scanLines)
		{
			this.scanLines = scanLines;
		}

		public TreeMap<Double, List<DataPoint>> getSortedData()
		{
			return sortedData;
		}

		public void setSortedData(TreeMap<Double, List<DataPoint>> sortedData)
		{
			this.sortedData = sortedData;
		}

		public Map<Integer, Map<Integer, DataPoint>> getMatrix()
		{
			return matrix;
		}

		public void setMatrix(Map<Integer, Map<Integer, DataPoint>> matrix)
		{
			this.matrix = matrix;
		}

		private Map<Integer, List<DataPoint>> scanLines;
		private Map<Integer, Map<Integer, DataPoint>> matrix;
		private TreeMap<Double, List<DataPoint>> sortedData;
	}

	protected static String nl = String.format("%n");
	/* @formatter:off */
	protected static String histogram = 
			"reset" + nl +
			"set terminal pdfcairo enh size 22in,3.5in" + nl +
			"set xlabel \"Coordinates (top-level x nested)\"" + nl +
			"set ylabel \"Execution time (ms)\"" + nl +
			"set output \"exhaustive-histogram-%s.pdf\"" + nl +
			"set yrange [0:]" + nl +
			"set boxwidth 0.2" + nl +
			"set xtics rotate out" + nl +
			"set style fill solid border" + nl +
			"set xtics font \"Arial, 9\" " + nl;
	/* @formatter:on */

	/* @formatter:off */
	protected static String histogramPlotLine = 
			"set title \"%s\"" + nl +
			"plot '%s' using 1:3:xtic(2) with boxes lc rgb\"black\" notitle" + nl;
	/* @formatter:on */

	public String getHistogramPlotLine(String file, String title)
	{
		return String.format(histogramPlotLine, title, file);
	}

	private Map<String, ProcessedFile> processedFiles = new HashMap<String, PlotExhaustive.ProcessedFile>();

	public Map<String, ProcessedFile> getProcessedFiles()
	{
		return processedFiles;
	}

	public void setProcessedFiles(Map<String, ProcessedFile> processedFiles)
	{
		this.processedFiles = processedFiles;
	}

	public Map<Integer, List<DataPoint>> process(String filename, boolean normalizeValues)
	{

		Map<Integer, List<DataPoint>> scanLines = new HashMap<Integer, List<DataPoint>>();
		Map<Integer, Map<Integer, DataPoint>> matrix = new HashMap<Integer, Map<Integer, DataPoint>>();
		File dataFile = new File(filename);
		try
		{
			List<String> lines = Util.readAllLines(dataFile);
			int maxCores = findMax(lines);

			double maxValue = 0;
			double minValue = Integer.MAX_VALUE;

			// Read data:
			for (int i = 0; i < lines.size(); i++)
			{
				String line = lines.get(i);
				if (line.trim().isEmpty())
				{
					continue;
				}

				// array: [0] is x, [1] is y, and [2] is execution time
				// value
				String[] strValues = split(line);
				int[] values = values(strValues);

				int x = values[0];

				// new scanline starting?
				if (!scanLines.containsKey(x))
				{
					List<DataPoint> scanLine = new ArrayList<DataPoint>();
					scanLines.put(x, scanLine);
				}

				if (!matrix.containsKey(x))
				{
					Map<Integer, DataPoint> subMatrix = new HashMap<Integer, DataPoint>();
					matrix.put(x, subMatrix);
				}

				DataPoint dp = new DataPoint(values[0], values[1], values[2]);
				scanLines.get(x).add(dp);
				matrix.get(x).put(values[1], dp);

				if (values[2] > maxValue)
				{
					maxValue = values[2];
				}

				if (values[2] < minValue)
				{
					minValue = values[2];
				}

			}

			// Normalize values and add missing points with dummy value:
			if (normalizeValues)
			{
				for (int xCoord = 1; xCoord <= maxCores; xCoord++)
				{
					List<DataPoint> scanLine = scanLines.get(xCoord);

					// normalize existing values
					for (DataPoint dp : scanLine)
					{
						dp.value = dp.value / minValue;
					}

					// add one "ghost" point to every scanline.
					// gnuplot does not account for the last point in each line
					// when building triangles, so we add this to make it work.
					if (scanLine.size() < maxCores)
					{
						// get last Y index and add one:
						int yCoord = scanLine.get(scanLine.size() - 1).y + 1;
						for (; yCoord < maxCores; yCoord++)
						{
							scanLine.add(new DataPoint(xCoord, yCoord, DataPoint.dummy));
						}
					}
				}
			}
		} catch (IOException e)
		{
			System.err.println("ERROR: " + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}

		if (processedFiles.containsKey(filename))
		{
			processedFiles.get(filename).setScanLines(scanLines);
			processedFiles.get(filename).setMatrix(matrix);
		} else
		{
			ProcessedFile pf = new ProcessedFile();
			pf.setScanLines(scanLines);
			pf.setMatrix(matrix);
			this.processedFiles.put(filename, pf);
		}

		return scanLines;
	}

	public void processSingleFile(File exhaustiveDataFile)
	{
		TreeMap<Double, List<DataPoint>> sorted = sort(exhaustiveDataFile.getAbsolutePath());

		Iterator<Entry<Double, List<DataPoint>>> iterator = sorted.entrySet().iterator();
		StringBuilder content = new StringBuilder();

		int index = 0;
		while (iterator.hasNext())
		{
			Entry<Double, List<DataPoint>> entry = iterator.next();
			for (DataPoint dp : entry.getValue())
			{
				String line = index++ + " " + dp.x + "x" + dp.y + " " + entry.getKey() + nl;
				content.append(line);
			}

		}

		int pointIndex = exhaustiveDataFile.getName().lastIndexOf('.');
		String dataFileName = exhaustiveDataFile.getName().substring(0, pointIndex);
		File newFile = new File(exhaustiveDataFile.getParent(), dataFileName + ".intermediate");
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(newFile));
			writer.write(content.toString());
			writer.flush();
			writer.close();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		File gnuplotFile = new File(exhaustiveDataFile.getParent(), dataFileName + ".gp");
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(gnuplotFile));
			writer.write(String.format(histogram, dataFileName));
			writer.write(getHistogramPlotLine(newFile.getAbsolutePath(), dataFileName));
			writer.flush();
			writer.close();
		} catch (IOException e)
		{
			throw new RuntimeException(e);
		}

		System.err.println("PlotExhaustive: Processed " + exhaustiveDataFile + " into intermediate data file " + newFile
				+ "and gnuplot file " + gnuplotFile);
	}

	public void process(String exhaustiveFolderPath)
	{

		File[] dataFiles = null;
		try
		{
			dataFiles = processFolders(exhaustiveFolderPath);
		} catch (IOException e)
		{
			System.err.println("ERROR: " + e.getMessage());
			e.printStackTrace(System.err);
			System.exit(1);
		}

		if (dataFiles.length == 0)
		{
			System.err.println("No files found");
			System.exit(0);
		}

		for (File dataFile : dataFiles)
		{
			process(dataFile.getName(), false);
		}

		System.err.println("ProcessExhaustiveData: All done.");

	}

	/*
	 * sorts data from a scanline map <lineNo, List of Points> to a sorted map
	 * <execution time, List of points>. Line numbers are ignored.
	 */
	public TreeMap<Double, List<DataPoint>> sort(String filename)
	{
		Map<Integer, List<DataPoint>> scanLines = null;
		if (processedFiles.containsKey(filename))
		{
			scanLines = processedFiles.get(filename).getScanLines();
		} else
		{
			scanLines = process(filename, false);
		}
		TreeMap<Double, List<DataPoint>> result = new TreeMap<Double, List<DataPoint>>();
		Iterator<Entry<Integer, List<DataPoint>>> it = scanLines.entrySet().iterator();
		while (it.hasNext())
		{
			Map.Entry<Integer, List<DataPoint>> pair = it.next();
			for (DataPoint dp : pair.getValue())
			{
				if (dp.isDummy)
				{
					continue;
				}
				if (result.containsKey(dp.value))
				{
					result.get(dp.value).add(dp);
				} else
				{
					List<DataPoint> newList = new ArrayList<DataPoint>();
					newList.add(dp);
					result.put(dp.value, newList);
				}
			}
		}

		if (processedFiles.containsKey(filename))
		{
			processedFiles.get(filename).setSortedData(result);
		} else
		{
			ProcessedFile pf = new ProcessedFile();
			pf.setSortedData(result);
			this.processedFiles.put(filename, pf);
		}

		return result;
	}

	/*
	 * Returns the global optimum and a list of local optima (isolated minimums)
	 */
	public Pair<DataPoint, List<DataPoint>> findOptima(String filename, int boundary)
	{
		TreeMap<Double, List<DataPoint>> tree = null;
		Map<Integer, Map<Integer, DataPoint>> matrix = null;
		if (processedFiles.containsKey(filename))
		{
			tree = processedFiles.get(filename).getSortedData();
		} else
		{
			tree = sort(filename);
		}
		matrix = processedFiles.get(filename).getMatrix();

		List<DataPoint> localOptima = new ArrayList<DataPoint>();

		DataPoint globalOptimum = tree.firstEntry().getValue().get(0);
		if (boundary > 0)
		{
			Iterator<List<DataPoint>> it = tree.values().iterator();
			if (!it.hasNext())
			{
				throw new RuntimeException("PlotExhaustive.findOptima: sorted datapoint tree is empty. Unexpected.");
			}

			while (globalOptimum.x > boundary || globalOptimum.y > boundary)
			{
				List<DataPoint> next = it.next();
				if (next.size() > 1)
				{
					for (DataPoint dp : next)
					{
						globalOptimum = dp;
						if ((dp.x < boundary && dp.y < boundary))
						{
							break;
						}
					}
				} else
				{
					globalOptimum = next.get(0);
				}

				if (!it.hasNext())
				{
					throw new RuntimeException("PlotExhaustive.findOptima: ran out of points to search, unexpected.");
				}
			}
		}

		Pair<DataPoint, List<DataPoint>> result = new Pair<DataPoint, List<DataPoint>>(globalOptimum, localOptima);

		// iterate all points:
		for (Map.Entry<Integer, Map<Integer, DataPoint>> row : matrix.entrySet())
		{
			for (Map.Entry<Integer, DataPoint> column : row.getValue().entrySet())
			{

				// if a point's neighbors all have greater values, the point is
				// a local optimum:
				DataPoint center = column.getValue();
				boolean isLocalOptimum = true;
				for (DataPoint neighbor : neighbors(center, matrix, boundary))
				{
					if (neighbor.value < center.value)
					{
						isLocalOptimum = false;
					}
				}
				if (isLocalOptimum)
				{
					if (true)
					{
						localOptima.add(center);
					}
				}
			}

		}

		return result;
	}

	protected List<DataPoint> neighbors(DataPoint center, Map<Integer, Map<Integer, DataPoint>> matrix, int boundary)
	{
		List<DataPoint> result = new ArrayList<DataPoint>();

		// three points before and three points after:
		for (int x = center.x - 1; x <= center.x + 1; x += 2)
		{
			for (int y = center.y - 1; y <= center.y + 1; y++)
			{
				if (x < 1 || y < 1 || x > boundary || y > boundary)
				{
					continue;
				}
				DataPoint neighbor = matrix.get(x).get(y);
				if (neighbor != null)
				{
					result.add(neighbor);
				}
			}

		}
		// two points, above and below

		int y = center.y - 1;
		if (!(y < 1 || y > boundary))
		{
			DataPoint neighbor = matrix.get(center.x).get(y);
			if (neighbor != null)
			{
				result.add(neighbor);
			}
		}

		y = center.y + 1;
		if (!(y < 1 || y > boundary))
		{
			DataPoint neighbor = matrix.get(center.x).get(y);
			if (neighbor != null)
			{
				result.add(neighbor);
			}
		}

		return result;
	}

	public File[] processFolders(String exhaustiveFolder) throws IOException
	{

		FileFilter vacationExhaustiveFiles = new FileFilter()
		{
			@Override
			public boolean accept(File pathname)
			{
				String[] parts = pathname.getName().split("\\.");
				String ext = parts[parts.length - 1];
				return !pathname.isDirectory() && ext.startsWith("surfacedata");
			}
		};
		File exhaustive = new File(exhaustiveFolder);
		if (!exhaustive.isDirectory())
		{
			throw new RuntimeException("ProcessExhaustiveData: expected directory with .surfacedata files, "
					+ exhaustiveFolder + " is not a directory.");
		}

		return exhaustive.listFiles(vacationExhaustiveFiles);

	}

	public static String[] split(String line)
	{
		String[] parts = line.split(" ");
		if (parts.length < 3)
		{
			throw new RuntimeException("split(): Unexpected data format while splitting line:" + line);
		}

		for (int i = 0; i < 3; i++)
		{
			String part = parts[i];
			if (!part.matches("\\d+"))
			{
				throw new RuntimeException("split(): Unexpected data format while scanning line: " + line);
			}
		}
		return parts;
	}

	public static int[] values(String[] parts)
	{
		int[] values = new int[3];
		for (int i = 0; i < 3; i++)
		{
			values[i] = Integer.parseInt(parts[i]);
		}

		return values;
	}

	public static int findMax(List<String> dataLines)
	{
		// Max no. of cores is the first integer (no. of top-levels) on the last
		// line of the file. E.g. 48 1 <some_value>
		String lastLine = null;

		for (int i = dataLines.size() - 1; i >= 0; i--)
		{
			if (dataLines.get(i).trim().isEmpty())
			{
				continue;
			}
			lastLine = dataLines.get(i);
			break;
		}

		if (lastLine == null)
		{
			throw new RuntimeException("findMax(): Invalid File, found empty lines only.");
		}

		String[] strParts = split(lastLine);
		int[] parts = values(strParts);

		return parts[0];
	}

}