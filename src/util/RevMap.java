package util;

import java.util.ArrayList;
import java.util.Arrays;

public class RevMap<K,V>
{
	private ArrayList<K> keySet = new ArrayList<>();
	private ArrayList<V> valueSet = new ArrayList<>();

	public RevMap() {
	}
	public RevMap(K[] initKeys, V[] initValues) {
		keySet.addAll(Arrays.asList(initKeys));
		valueSet.addAll(Arrays.asList(initValues));
		if ( keySet.size() != valueSet.size() ) 
			throw new IllegalArgumentException("Array initializers length not equal");
	}
	public int size() {
		return keySet.size();
	}
	public boolean isEmpty() {
		return keySet.isEmpty();
	}
	public boolean containsKey(K key) {
		return keySet.contains(key);
	}
	public boolean containsValue(V value) {
		return valueSet.contains(value);
	}
	public K getKey(int i) {
		return keySet.get(i);
	}
	public K getKey(V value) {
		int i = valueSet.indexOf(value);
		if ( i < 0 ) return null;
		return keySet.get(i);
	}
	public V getValue(int i) {
		if ( i >= size() ) return null;
		return valueSet.get(i);
	}
	public V getValue(K key) {
		int i = keySet.indexOf(key);
		if ( i < 0 ) return null;
		return valueSet.get(i);
	}
	public boolean add(K key, V value) {
		if ( keySet.contains(key) || valueSet.contains(value) )
			return false;
		keySet.add(key);
		valueSet.add(value);
		return true;
	}
	public boolean removeKey(K key) {
		int i = keySet.indexOf(key);
		if ( i < 0 ) return false;
		keySet.remove(i);
		valueSet.remove(i);
		return true;
	}
	public boolean removeValue(V value) {
		int i = valueSet.indexOf(value);
		if ( i < 0 ) return false;
		keySet.remove(i);
		valueSet.remove(i);
		return true;
	}
	public void clear() {
		keySet.clear();
		valueSet.clear();
	}
	public ArrayList<K> keyList() {
		return new ArrayList<K>(keySet);
	}
	public ArrayList<V> valueList() {
		return new ArrayList<V>(valueSet);
	}
}
