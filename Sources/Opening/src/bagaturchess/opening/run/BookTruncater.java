package bagaturchess.opening.run;


import bagaturchess.bitboard.impl.datastructs.HashMapLongObject;
import bagaturchess.opening.api.OpeningBook;
import bagaturchess.opening.api.OpeningBookFactory;
import bagaturchess.opening.impl.model.Entry_BaseImpl;


public class BookTruncater {
	
	
	public static void main(String args[]) {
		try {
			
			int size = 10;
			String input = "b.ob";
			String output = "b10.ob";
			
			OpeningBook ob = OpeningBookFactory.load(input);
			
			HashMapLongObject<Entry_BaseImpl> init = ((bagaturchess.opening.impl.model.OpeningBookImpl_FullEntries)ob).entries;
			
			System.out.println("initial = " + init.size());
			
			((bagaturchess.opening.impl.model.OpeningBookImpl_FullEntries)ob).entries = remove(init, size);
			
			ob.store(output);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	private static HashMapLongObject<Entry_BaseImpl> remove(HashMapLongObject<Entry_BaseImpl> keys, int threshold) {
		HashMapLongObject<Entry_BaseImpl> newKeys = new HashMapLongObject<Entry_BaseImpl>(); //keys.containsKey(key);
		
		for (long key: keys.getAllKeys()) {
			Entry_BaseImpl e = keys.get(key);
			if (e.getWeight() > threshold) {
				newKeys.put(key, e);
			}
		}
		
		System.out.println("> " + threshold + ", " + newKeys.size());
		
		return newKeys;
	}
}
