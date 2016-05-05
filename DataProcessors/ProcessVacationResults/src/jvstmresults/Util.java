package jvstmresults;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.collections4.trie.PatriciaTrie;

import jvstm.tuning.TuningPoint;
import jvstm.tuning.policy.PointProvider.TuningRecord;

public class Util
{
	static List<String> readAllLines(File f) throws IOException
	{
		BufferedReader r;
		try
		{
			r = new BufferedReader(new FileReader(f));
		} catch (FileNotFoundException e)
		{
			throw new IOException(e);
		}
		String line;
		List<String> lines = new ArrayList<String>();

		while ((line = r.readLine()) != null)
		{
			lines.add(line.trim());
		}
		r.close();
		return lines;
	}

	/**
	 * header and footer lines are excluded. Empty lines are skipped.
	 * 
	 * @param lines
	 * @param header
	 * @param footer
	 * @return
	 */
	public static List<String> getSectionLines(List<String> lines, String header, String footer)
	{
		List<String> result = new ArrayList<String>();

		int i = 0;
		String line;
		try
		{
			while (true)
			{
				line = lines.get(i);
				if (line.startsWith(header))
				{
					i++;
					break;
				}
				i++;
			}
			while (true)
			{
				line = lines.get(i);
				i++;
				if (line.startsWith(footer))
				{
					break;
				}
				if (line.isEmpty())
					continue;
				result.add(line);
			}
		} catch (IndexOutOfBoundsException e)
		{
			throw new RuntimeException(
					"getLinesBetween() error: Strings TO or FROM  not found. To: " + footer + "    From: " + header);
		}

		return result;
	}

	/**
	 * Assumes FOOTER string is "##"
	 * 
	 * @param lines
	 * @param header
	 * @return
	 */
	public static List<String> getSectionLines(List<String> lines, String header)
	{
		return getSectionLines(lines, header, "##");
	}

	public static String lineBreak()
	{
		return System.getProperty("line.separator");
	}

	public static String getValueByKeyPrefix(PatriciaTrie<String> trie, String prefix)
	{
		SortedMap<String, String> prefixMap = trie.prefixMap(prefix);
		if(prefixMap.isEmpty()) {
			return null;
		}
		if (prefixMap.size() != 1)
		{
			throw new RuntimeException(
					"Multiple Keys returned on getByPrefix. Prefix: " + prefix + "  values: " + prefixMap.values());
		}
		String whole = prefixMap.get(prefixMap.firstKey());
		int idx = whole.indexOf(':');
		return whole.substring(idx + 1).trim();
	}

	public static TuningPoint pointFromString(String point)
	{
		// example: [4,2]
		if(point == null) return null;
		
		String pattern = "[^\\d]*(\\d+),(\\d+)[^\\d]*";
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(point);
		if (!m.matches())
		{
			throw new RuntimeException("Error creating point from string: " + point);
		}

		try
		{
			return new TuningPoint(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)));
		} catch (Exception e)
		{
			throw new RuntimeException("Error creating point from string: " + point);
		}
	}

	public static TuningRecord recordFromString(String tuningRecord)
	{
		if(tuningRecord == null) return null;
		
		String recordPattern = "\\[(\\d+),(\\d+)\\] \\{(-?\\d+((,|\\.)\\d+)?)\\}";
		Pattern p = Pattern.compile(recordPattern);

		Matcher m = p.matcher(tuningRecord);
		if (!m.matches())
		{
			throw new RuntimeException("Error creating Tuning Record from string: " + tuningRecord);
		}
		try
		{
			return new TuningRecord(new TuningPoint(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2))),
					Float.parseFloat(m.group(3).replace(',', '.')), -1);
		} catch (Exception e)
		{
			throw new RuntimeException("Error creating point from string: " + tuningRecord);
		}
	}

}
