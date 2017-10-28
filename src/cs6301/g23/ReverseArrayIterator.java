package cs6301.g23;
import java.util.Iterator;
import java.lang.UnsupportedOperationException;

public class ReverseArrayIterator<T> implements Iterator<T> {

	    T[] arr;
	    int startIndex, endIndex, cursor;

	    public ReverseArrayIterator(T[] a) {
		arr = a;
		startIndex = a.length-1;
		endIndex = 0;
		cursor = a.length;
	    }

	    public ReverseArrayIterator(T[] a, int start, int end) {
		arr = a;
		startIndex = start;
		endIndex = end;
		cursor = start + 1;
	    }

	    public boolean hasNext() {
		return cursor > endIndex;
	    }

	    public T next() {
		return arr[--cursor];
	    }

	    public void remove() {
		throw new UnsupportedOperationException();
	    }
	}
