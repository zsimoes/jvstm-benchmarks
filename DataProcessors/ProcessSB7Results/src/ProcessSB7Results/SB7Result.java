package ProcessSB7Results;

import java.util.regex.Pattern;

public class SB7Result
{

	// values:
	// "$load,${top}x${nest},$policy,$TOTAL_ERROR,$TOTAL_ERROR_INCL_FAILED,$THROUGHPUT,$THROUGHPUT_INCL_FAILED,$TIME"
	// example : rw,1x1,HierarchicalGD,88.89%,88.89%,3.74,3.74,24.06
	private String load;
	private String threadConfig;
	private String policy;
	private Float totalError;
	private Float totalErrorInclFailed;
	private Float throughput;
	private Float throughputInclFailed;
	private Float time;

	public SB7Result(String line)
	{
		String patt = "\\w+,\\d+x\\d+,\\w+,\\d+.\\d+%,\\d+.\\d+%,\\d+.\\d+,\\d+.\\d+,\\d+.\\d+";
		Pattern valid = Pattern.compile(patt);
		if (!valid.matcher(line).matches())
		{
			throw new RuntimeException("Invalid line: " + line + ". Expected format " + patt);
		}

		String[] parts = line.split(",");

		this.load = parts[0];
		this.threadConfig = parts[1];
		this.policy = parts[2];

		try
		{
			this.totalError = Float.parseFloat(parts[3].replace("%", "")) / 100.0f;
			this.totalErrorInclFailed = Float.parseFloat(parts[4].replace("%", "")) / 100.0f;
			this.throughput = Float.parseFloat(parts[5]);
			this.throughputInclFailed = Float.parseFloat(parts[6]);
			this.time = Float.parseFloat(parts[7]);
		} catch (NumberFormatException n)
		{
			System.err.println("Number Format Exception: " + n.getMessage());
		}
	}

	public SB7Result(String load, String threadConfig, String policy, Float totalError, Float totalErrorInclFailed,
			Float throughput, Float throughputInclFailed, Float time)
	{
		this.load = load;
		this.threadConfig = threadConfig;
		this.policy = policy;
		this.totalError = totalError;
		this.totalErrorInclFailed = totalErrorInclFailed;
		this.throughput = throughput;
		this.throughputInclFailed = throughputInclFailed;
		this.time = time;
	}

	public String getLoad()
	{
		return load;
	}

	public void setLoad(String load)
	{
		this.load = load;
	}

	public String getThreadConfig()
	{
		return threadConfig;
	}

	public void setThreadConfig(String threadConfig)
	{
		this.threadConfig = threadConfig;
	}

	public String getPolicy()
	{
		return policy;
	}

	public void setPolicy(String policy)
	{
		this.policy = policy;
	}

	public Float getTotalError()
	{
		return totalError;
	}

	public void setTotalError(Float totalError)
	{
		this.totalError = totalError;
	}

	public Float getTotalErrorInclFailed()
	{
		return totalErrorInclFailed;
	}

	public void setTotalErrorInclFailed(Float totalErrorInclFailed)
	{
		this.totalErrorInclFailed = totalErrorInclFailed;
	}

	public Float getThroughput()
	{
		return throughput;
	}

	public void setThroughput(Float throughput)
	{
		this.throughput = throughput;
	}

	public Float getThroughputInclFailed()
	{
		return throughputInclFailed;
	}

	public void setThroughputInclFailed(Float throughputInclFailed)
	{
		this.throughputInclFailed = throughputInclFailed;
	}

	public Float getTime()
	{
		return time;
	}

	public void setTime(Float time)
	{
		this.time = time;
	}

	@Override
	public String toString()
	{
		return "SB7Result [load=" + load + ", threadConfig=" + threadConfig + ", policy=" + policy + ", totalError="
				+ totalError + ", totalErrorInclFailed=" + totalErrorInclFailed + ", throughput=" + throughput
				+ ", throughputInclFailed=" + throughputInclFailed + ", time=" + time + "]";
	}

}
