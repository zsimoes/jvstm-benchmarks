package jvstmresults;

import java.util.LinkedList;
import java.util.List;

import org.joda.time.DateTime;

import jvstm.tuning.TuningPoint;
import jvstm.tuning.policy.TuningPolicy;
import jvstm.tuning.policy.PointProvider.TuningRoundInfo;
import jvstm.tuning.policy.TuningPolicy.MeasurementType;

public class JVSTMLog
{
	public boolean noStats;
	public String logFile;

	public int interval;
	public int maxThreads;
	public TuningPoint initialConfig;
	public Class<? extends TuningPolicy> policy;

	public MeasurementType measurementType;
	public String stubFile;
	public TuningPoint stubOptimum;
	public boolean logDistances;

	public String contention;
	public String aditionalBenchmarkInfo;

	public DateTime timeStamp;
	
	public int executionTime;

	public List<TuningRoundInfo> roundInfo = new LinkedList<TuningRoundInfo>();

	public List<TuningPoint> tuningPath = new LinkedList<TuningPoint>();

	public List<Float> throughput = new LinkedList<Float>();

	public List<Float> tcr = new LinkedList<Float>();

	public List<Float> distances = new LinkedList<Float>();

	public JVSTMLog()
	{

	}

	@Override
	public String toString()
	{
		String nl = String.format("%n");
		return "JVSTMLog [noStats=" + noStats + nl + ", logFile=" + logFile + nl + ", interval=" + interval + nl
				+ ", maxThreads=" + maxThreads + nl + ", initialConfig=" + initialConfig + nl + ", policy=" + policy
				+ nl + ", measurementType=" + measurementType + nl + ", stubFile=" + stubFile + nl + ", stubOptimum="
				+ stubOptimum + nl + ", logDistances=" + logDistances + nl + ", contention=" + contention + nl + ", aditionalBenchmarkInfo="
				+ aditionalBenchmarkInfo + nl + ", timeStamp=" + timeStamp + nl + ", roundInfo=" + listString(roundInfo) + nl
				+ ", tuningPath=" + tuningPath+ nl + ", throughput=" + throughput + nl + ", tcr=" + tcr + nl
				+ ", distances=" + distances + "]";
	}

	public String listString(List<?> l)
	{
		StringBuilder sb = new StringBuilder("[");
		String nl = String.format("%n");
		for (Object o : l)
		{
			sb.append("   " + o + nl);
		}
		sb.append("]");
		return sb.toString();
	}

}
