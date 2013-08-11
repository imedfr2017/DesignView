package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ABSet<A,B>
{
	private ArrayList<A> aSet = new ArrayList<>();
	private ArrayList<B> bSet = new ArrayList<>();

	public ABSet() {
	}
	public ABSet(A[] initA, B[] initB) {
		aSet.addAll(Arrays.asList(initA));
		bSet.addAll(Arrays.asList(initB));
		if ( aSet.size() != bSet.size() ) 
			throw new IllegalArgumentException("Array initializers' length not equal");
	}
	public int size() {
		return aSet.size();
	}
	public boolean isEmpty() {
		return aSet.isEmpty();
	}
	public boolean containsA(A aValue) {
		return aSet.contains(aValue);
	}
	public boolean containsB(B bValue) {
		return bSet.contains(bValue);
	}
	public A getA(int i) {
		return aSet.get(i);
	}
	public A getA(B bValue) {
		int i = bSet.indexOf(bValue);
		if ( i < 0 ) return null;
		return aSet.get(i);
	}
	public B getB(int i) {
		if ( i >= size() ) return null;
		return bSet.get(i);
	}
	public B getB(A aValue) {
		int i = aSet.indexOf(aValue);
		if ( i < 0 ) return null;
		return bSet.get(i);
	}
	public boolean add(A aValue, B vBalue) {
		if ( aSet.contains(aValue) || bSet.contains(vBalue) )
			return false;
		aSet.add(aValue);
		bSet.add(vBalue);
		return true;
	}
	public void remove(int i) {
		aSet.remove(i);
		bSet.remove(i);
	}
	public void removeA(A aValue) {
		int i = aSet.indexOf(aValue);
		if ( i < 0 ) return;
		aSet.remove(i);
		bSet.remove(i);
	}
	public void removeB(B bValue) {
		int i = bSet.indexOf(bValue);
		if ( i < 0 );
		aSet.remove(i);
		bSet.remove(i);
	}
	public void removeIndices(List<Integer> indices) {
		for(Integer i : indices) remove(i);
	}
	public List<A> aListOf(List<Integer> indices) {
		List<A> listOf = new ArrayList<>();
		for(Integer i : indices) {
			A value = getA(i);
			if ( value != null ) listOf.add(value);
		}
		return listOf;
	}
	public List<B> bListOf(List<Integer> indices) {
		List<B> listOf = new ArrayList<>();
		for(Integer i : indices) {
			B value = getB(i);
			if ( value != null ) listOf.add(value);
		}
		return listOf;
	}
	public void clear() {
		aSet.clear();
		bSet.clear();
	}
	public List<A> aList() {
		return new ArrayList<A>(aSet);
	}
	public List<B> bList() {
		return new ArrayList<B>(bSet);
	}

	public Iterator<A> iteratorA() {
		return aSet.iterator();
	}
	public Iterator<B> iteratorB() {
		return bSet.iterator();
	}
}
