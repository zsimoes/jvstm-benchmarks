package jvstmresults;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import jvstm.tuning.StatisticsCollector;

public class ProcessFolders
{


	public List<JVSTMLogFolder> processMultipleFolders(String mainFolderPath) throws IOException
	{
		return walk(mainFolderPath);
	}

	protected List<JVSTMLogFolder> walk(String path)
	{

		List<JVSTMLogFolder> result = new ArrayList<JVSTMLogFolder>();
		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return result;

		for (File f : list)
		{
			result.add(parseJVSTMFolder(f));
		}
		return result;
	}

	public JVSTMLogFolder parseJVSTMFolder(File directory)
	{
		JVSTMLogFolder result = new JVSTMLogFolder(directory.getName(), directory.getName());

		File[] files = directory.listFiles();
		for (File dataFile : files)
		{
			BufferedReader br = null;
			try
			{
				br = new BufferedReader(new FileReader(dataFile));
				String line;
				
				// is this a JVSTM Log? If so, parse and save it
				while ((line = br.readLine()) != null)
				{
					if (line.isEmpty())
						continue;
					if (line.startsWith(StatisticsCollector.headerJVSTMLog))
					{
						ProcessLog pf = new ProcessLog();
						result.add(pf.process(dataFile.getAbsolutePath()));
					}
				}
			} catch (IOException e)
			{
				continue;
			} finally
			{
				if (br != null)
				{
					try
					{
						br.close();
					} catch (IOException e)
					{
					}
				}
			}

		}
		return result;
	}

}
