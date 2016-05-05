package jvstmresults;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

class JVSTMLogFolder
{
	public String relativeFolder;
	public List<JVSTMLog> resultFiles;
	public Set<String>  jvstmPolicies;
	protected Set<String> contentionTypes;

	public JVSTMLogFolder(String relativeFolder, String jvstmType)
	{
		super();
		this.resultFiles = new ArrayList<JVSTMLog>();
		this.contentionTypes = new TreeSet<String>();
		this.jvstmPolicies = new TreeSet<String>();
		this.relativeFolder = relativeFolder;
	}

	public Set<String> getContentionTypes()
	{
		return contentionTypes;
	}

	public void add(JVSTMLog resultFile)
	{
		contentionTypes.add(resultFile.contention);
		jvstmPolicies.add(resultFile.policy.getName());
		resultFiles.add(resultFile);
	}
	
	public Set<String> getJvstmPolicies()
	{
		return jvstmPolicies;
	}
	
	public List<JVSTMLog> getResults() {
		return resultFiles;
	}
	
	public List<JVSTMLog> getResultsFor(String policy, String contention)
	{
		List<JVSTMLog> result = new ArrayList<JVSTMLog>();
		for (JVSTMLog file : this.resultFiles)
		{
			if (file.policy.getName().equals(policy) && file.contention.equals(contention))
			{
				result.add(file);
			}
		}
		return result;
	}

	public List<JVSTMLog> getResultsForPolicy(String policy)
	{
		List<JVSTMLog> result = new ArrayList<JVSTMLog>();
		for (JVSTMLog file : this.resultFiles)
		{
			if (file.policy.getName().equals(policy))
			{
				result.add(file);
			}
		}
		return result;
	}

	public List<JVSTMLog> getResultsForContention(String contention)
	{
		List<JVSTMLog> result = new ArrayList<JVSTMLog>();
		for (JVSTMLog file : this.resultFiles)
		{
			if (file.contention.equals(contention))
			{
				result.add(file);
			}
		}
		return result;
	}

}