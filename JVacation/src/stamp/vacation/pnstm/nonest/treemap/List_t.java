package stamp.vacation.pnstm.nonest.treemap;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import epfl.Box;
import epfl.Cons;

public class List_t<E> implements Iterable<E> {

    @SuppressWarnings("unchecked")
    protected final Box<Cons<E>> elements = new Box<Cons<E>>((Cons<E>) Cons.empty());

    public List_t() {
    }

    public List_t(List_t<E> bag) {
	throw new Error("List_t(List_t<E> bag) not implemented");
    }

    public void add(E element) {
	elements.put(elements.get().cons(element));
    }

    public E find(int type, int id) {
	for (E iter : elements.get()) {
	    if (iter instanceof Reservation_Info) {
		Reservation_Info resIter = (Reservation_Info) iter;
		if (resIter.type == type && resIter.id == id) {
		    return iter;
		}
	    } else {
		assert (false);
	    }
	}
	return null;
    }

    public boolean remove(E element) {
	Cons<E> oldElems = elements.get();
	Cons<E> newElems = oldElems.removeFirst(element);

	if (oldElems == newElems) {
	    return false;
	} else {
	    elements.put(newElems);
	    return true;
	}
    }

    // Iterable<E> methods

    @Override
    public Iterator<E> iterator() {
	List<E> snapshot = new ArrayList<E>();
	for (E element : elements.get())
	    snapshot.add(element);
	Collections.reverse(snapshot);
	return snapshot.iterator();
    }
}
