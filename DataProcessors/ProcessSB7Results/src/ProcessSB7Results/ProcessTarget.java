package ProcessSB7Results;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.collections4.map.MultiKeyMap;

public class ProcessTarget
{

	public Set<String> loadTypes = new TreeSet<>();
	public Set<String> configTypes = new TreeSet<>();
	public Set<String> policyTypes = new TreeSet<>();
	public MultiKeyMap<String, SB7Result> result;
	
	private boolean done = false;
	
	public boolean isDone() {
		return done;
	}

	public MultiKeyMap<String, SB7Result> process(File rootDirectory)
	{
		if (!(rootDirectory.exists() && rootDirectory.isDirectory()))
		{
			throw new RuntimeException("FolderStructure.process(File rootDirectory) : invalid root folder");
		}

		MultiKeyMap<String, SB7Result> sb7Results = new MultiKeyMap<>();

		File[] loadFolders = rootDirectory.listFiles(new FileFilter()
		{

			@Override
			public boolean accept(File pathname)
			{
				if (pathname.isDirectory())
					return true;
				return false;
			}
		});

		for (File loadFolder : loadFolders)
		{
			File[] configFolders = loadFolder.listFiles(new FileFilter()
			{

				@Override
				public boolean accept(File pathname)
				{
					if (pathname.isDirectory())
						return true;
					return false;
				}
			});

			for (File configFolder : configFolders)
			{
				File[] files = configFolder.listFiles(new FileFilter()
				{

					@Override
					public boolean accept(File pathname)
					{
						return pathname.getName().endsWith("all");
					}
				});
				if (files.length > 1)
				{
					System.err.println("Multiple \".all\" files found in " + configFolder.getPath()
							+ ". Using first file: " + files[0].getName());
				} else if (files.length < 1)
				{
					throw new RuntimeException(configFolder.getPath() + ": No \".all\" files found.");
				}
				File allResults = files[0];
				List<String> lines = null;
				try
				{
					lines = Files.readAllLines(allResults.toPath());
				} catch (IOException e)
				{
					throw new RuntimeException(e);
				}

				for (String line : lines)
				{
					SB7Result result = null;
					try
					{
						result = new SB7Result(line);
					} catch (RuntimeException r)
					{
						System.err.println("Error on file " + allResults.getPath() + ". Cause: " + r.getMessage());
						continue;
					}
					sb7Results.put(result.getLoad(), result.getThreadConfig(), result.getPolicy(), result);
					loadTypes.add(result.getLoad());
					configTypes.add(result.getThreadConfig());
					policyTypes.add(result.getPolicy());

				}
			}

		}

		result = sb7Results;
		done = true;
		return sb7Results;

	}

}
