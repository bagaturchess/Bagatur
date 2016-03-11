package bagaturchess.opening.run;


import java.util.HashMap;
import java.util.Map;

import bagaturchess.opening.api.IOpeningEntry;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.opening.api.OpeningBookFactory;
import bagaturchess.opening.impl.model.Entry_BaseImpl;


public class BookTruncater {
	
	private static Map<Long, Entry_BaseImpl> remove(Map<Long, Entry_BaseImpl> keys, int threshold) {
		Map<Long, Entry_BaseImpl> newKeys = new HashMap<Long, Entry_BaseImpl>(); //keys.containsKey(key);
		
		for (Long key: keys.keySet()) {
			Entry_BaseImpl e = keys.get(key);
			if (e.getWeight() > threshold) {
				newKeys.put(key, e);
			}
		}
		
		System.out.println("> " + threshold + ", " + newKeys.size());
		
		return newKeys;
	}
	
	public static void main(String args[]) {
		try {
			
			int size = 2;
			//String input = "./../OpeningGenerator/w.ob";
			//String output = "./../OpeningGenerator/w2.ob";
			String input = "./../OpeningGenerator/b.ob";
			String output = "./../OpeningGenerator/b2.ob";
			
			OpeningBook ob = OpeningBookFactory.load(input);
			
			Map<Long, Entry_BaseImpl> init = ((bagaturchess.opening.impl.model.OpeningBookImpl_FullEntries)ob).entries;
			
			System.out.println("initial = " + init.size());
			
			((bagaturchess.opening.impl.model.OpeningBookImpl_FullEntries)ob).entries = remove(init, size);
			
			ob.store(output);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
