package bagaturchess.engines.searchtune;


import java.lang.reflect.Field;


public class SearchConfig1_MTD_Impl_TUNE extends SearchConfig1_MTD_Impl_LKG {
	
	
	public SearchConfig1_MTD_Impl_TUNE(String[] args) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
		
		super(args);
		
		//String allArgs = "";
		for (int i=0; i<args.length; i++) {
			String current = args[i];
			if (current == null) {
				throw new IllegalStateException("Argument is null");
			}
			if (!current.contains("=")) {
				throw new IllegalStateException("Argument does not contains '=' simbol");
			}
			
			//allArgs += current;
			
			String[] pair = current.split("=");
			String fieldName = pair[0];
			String stringValue = pair[1];
			Object value = parseValue(stringValue);
			setField(fieldName, value);
			//System.out.println(fieldName + "=" + value);
			
			
		}
		//if (true) throw new IllegalStateException(args.length + " " + allArgs);
	}
	
	
	private Object parseValue(String stringValue) {
		Object val = null;
		try {
			val = Integer.parseInt(stringValue);
		} catch(NumberFormatException nfe) {
			val = Boolean.parseBoolean(stringValue);
		}
		return val;
	}
	
	
	private void setField(String fieldName, Object fieldValue) throws NoSuchFieldException, IllegalAccessException {
		Field field = this.getClass().getField(fieldName);
		field.set(this, fieldValue);
	}
	
	
	public static void main(String[] args) {
		try {
			SearchConfig1_MTD_Impl_TUNE o = new SearchConfig1_MTD_Impl_TUNE(new String[] {"pv_pruning_mate_distance=true"});
			System.out.println("pv_reduction_lmr1=" + o.pv_reduction_lmr1);
			System.out.println("pv_pruning_mate_distance=" + o.pv_pruning_mate_distance);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
