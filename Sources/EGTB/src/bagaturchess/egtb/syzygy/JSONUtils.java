package bagaturchess.egtb.syzygy;


import java.util.ArrayList;
import java.util.List;

import bagaturchess.egtb.syzygy.OnlineSyzygy.Logger;


public class JSONUtils {

	
	public static String extractFirstJSONArray(Logger logger, String json_text) {
		
		int start_index = json_text.indexOf("[");
		
		if (start_index == -1) {
			
			return null;
		}
		
		int end_index = json_text.indexOf("]", start_index);
		
		if (end_index == -1) {
			
			return null;
		}
		
		String attribute_value = json_text.substring(start_index, end_index + 1);
		
		return attribute_value;
	}
	
	
	public static String[] extractJSONArrayElements(Logger logger, String json_array) {
		
		List<String> array_elements_list = new ArrayList<String>();
		
		char[] chars = json_array.toCharArray();
		
		for (int i = 0; i < chars.length; i++) {
			
			char cur_char1 = chars[i];
			
			if (cur_char1 == '{') {
				
				for (int j = i; j < chars.length; j++) {
					
					char cur_char2 = chars[j];
					
					if (cur_char2 == '}') {
						
						array_elements_list.add(json_array.substring(i, j + 1));
						
						i = j;
						
						break;
					}
				}
			}
		}
		
		return array_elements_list.toArray(new String[0]);
	}
	
	
	public static String extractJSONAttribute(Logger logger, String json_object, String attribute_name) {
		
		int start_index = json_object.indexOf(attribute_name);
		
		if (start_index == -1) {
			
			return null;
		}
		
		logger.addText("OnlineSyzygy.extractJSONAttribute: attribute_name=" + attribute_name + " found");
		
		int possible_end_index1 = json_object.indexOf(",", start_index);
		
		int possible_end_index2 = json_object.indexOf("}", start_index);
		
		int end_index = 0;
		
		if (possible_end_index1 != -1 && possible_end_index2 != -1) {
			
			end_index = Math.min(possible_end_index1, possible_end_index2);
			
		} else if (possible_end_index1 != -1) {
			
			end_index = possible_end_index1;
			
		} else if (possible_end_index2 != -1) {
			
			end_index = possible_end_index2;
			
		} else {
			
			return null;
		}		
		
		String attribute_value = json_object.substring(start_index + attribute_name.length(), end_index);
		
		logger.addText("OnlineSyzygy.extractJSONAttribute: attribute_value=" + attribute_value);
		
		return attribute_value;
	}
}
