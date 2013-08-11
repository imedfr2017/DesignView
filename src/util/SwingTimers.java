package util;

import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import javax.swing.Timer;

public class SwingTimers
{
	private static Map<ActionListener,Timer> listenerMap = new HashMap<ActionListener,Timer>();

	public static void scheduleOnce(ActionListener listener, int delay)
	{
		Timer timer = new Timer(delay,listener);
		timer.setRepeats(false);
		timer.start();
	}
	public static void connectTimer(ActionListener listener, int increment)
	{
		Timer timer = new Timer(increment,listener);
		synchronized(listenerMap) {
			if ( !listenerMap.containsKey(listener) ) {
				listenerMap.put(listener,timer);
			}
		}
	}
	public static void connectTimer(ActionListener listener, int delay,
			int increment)
	{
		Timer timer = new Timer(increment,listener);
		timer.setInitialDelay(delay);
		synchronized(listenerMap) {
			if ( !listenerMap.containsKey(listener) ) {
				listenerMap.put(listener,timer);
			}
		}
	}
	public static void start(ActionListener listener)
	{
		synchronized(listenerMap) {
			if ( listenerMap.containsKey(listener) ) {
				Timer timer = listenerMap.get(listener);
				if ( !timer.isRunning() ) timer.start();
			}
		}
	}
	public static void stop(ActionListener listener)
	{
		synchronized(listenerMap) {
			if ( listenerMap.containsKey(listener) ) {
				Timer timer = listenerMap.get(listener);
				if ( timer.isRunning() ) timer.stop();
			}
		}
	}
	public static void remove(ActionListener listener)
	{
		synchronized(listenerMap) {
			if ( listenerMap.containsKey(listener) ) {
				Timer timer = listenerMap.get(listener);
				if ( timer.isRunning() ) timer.stop();
				timer = null;
				listenerMap.remove(listener);
			}
		}
	}
	public static void stopAll()
	{
		synchronized(listenerMap) {
			for(Timer timer : listenerMap.values() ) {
				if ( timer.isRunning() ) timer.stop();
				timer = null;
			}
		}
	}
}
