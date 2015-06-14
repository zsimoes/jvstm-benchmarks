package stmbench7.impl.jvstm.backend;

import java.util.Iterator;
import java.util.TreeSet;

import jvstm.util.VLinkedSet;
import stmbench7.backend.LargeSet;

public class LargeSetImpl<E extends Comparable<E>> implements LargeSet<E>
{

	protected final VLinkedSet<E> elements = new VLinkedSet<E>();

	public LargeSetImpl()
	{
		// Intentionally empty
	}

	public LargeSetImpl(LargeSetImpl<E> source)
	{
		throw new Error("LargeSetImpl(LargetSetImpl<E> source) not implemented");
	}

	@Override
	public boolean add(E element)
	{
		return elements.add(element);
	}

	@Override
	public boolean remove(E element)
	{
		return elements.remove(element);
	}

	@Override
	public boolean contains(E element)
	{
		return elements.contains(element);
	}

	@Override
	public int size()
	{
		return elements.size();
	}

	@Override
	public Iterator<E> iterator()
	{
		return new TreeSet<E>(elements).iterator();
	}
}
