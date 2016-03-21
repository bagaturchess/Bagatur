package bagaturchess.uci.impl.commands.options;


import bagaturchess.uci.api.IChannel;
import bagaturchess.uci.impl.Channel_Console;
import bagaturchess.uci.impl.Protocol;


/**
	setoption name <id> [value <x>]
                   	this is sent to the engine when the user wants to change the internal parameters
                   	of the engine. For the "button" type no value is needed.
                   	One string will be sent for each parameter and this will only be sent when the engine is waiting.
                   	The name and value of the option in <id> should not be case sensitive and can include spaces.
                   	The substrings "value" and "name" should be avoided in <id> and <x> to allow unambiguous parsing,
                   	for example do not use <name> = "draw value".
                   	Here are some strings for the example below:
                   	   "setoption name Nullmove value true\n"
                         "setoption name Selectivity value 3\n"
                   	   "setoption name Style value Risky\n"
                   	   "setoption name Clear Hash\n"
                   	   "setoption name NalimovPath value c:\chess\tb\4;c:\chess\tb\5\n"
*/
public class SetOption extends Protocol {


	private String commandLine;
	private String name;
	private Object value;

	private IChannel channel;
	
	
	public SetOption(IChannel _channel, String _commandLine) {
		channel = _channel;
		commandLine = _commandLine.trim();
		parse();
	}
	
	
	private void parse() {
		/**
		 * Parse value
		 */
		String line = commandLine;
		int valueTextStartIndex = line.indexOf(COMMAND_TO_ENGINE_SETOPTION_VALUE_STR);
		if (valueTextStartIndex != -1) {
			int valueStartIndex = line.indexOf(IChannel.WHITE_SPACE, valueTextStartIndex + 1);
			if (valueStartIndex == -1) {
				channel.dump("Incorrect 'setoption' command (there is no space after 'value' string): " + commandLine);
			} else {
				//int valueEndIndex = line.indexOf(Channel.WHITE_SPACE, valueStartIndex + 1);
				//if (valueEndIndex == -1) {
					int valueEndIndex = line.length();
				//} else {
				//	Channel.dump("WARNING: Incorrect 'setoption' command (there is white space inside the value): " + commandLine);
				//}
				
				String valueStr = line.substring(valueStartIndex + 1, valueEndIndex).toLowerCase().trim();
				value = valueStr;
			}
		} else {
			//TODO: Button type option and the value will be null. Consider what to do.
		}
		
		/**
		 * Parse name
		 */
		line = commandLine.substring(0, valueTextStartIndex > 0 ? valueTextStartIndex : commandLine.length());
		int nameTextStartIndex = line.indexOf(COMMAND_TO_ENGINE_SETOPTION_NAME_STR);
		if (nameTextStartIndex != -1) {
			int nameStartIndex = line.indexOf(IChannel.WHITE_SPACE, nameTextStartIndex + 1);
			if (nameStartIndex == -1) {
				channel.dump("Incorrect 'setoption' command (there is no space after 'name' string): " + commandLine);
			} else {
				int nameEndIndex = line.length();
				String nameStr = line.substring(nameStartIndex + 1, nameEndIndex).toLowerCase().trim();
				name = nameStr;
			}
		} else {
			channel.dump("Incorrect 'setoption' command (there is no 'name' string): " + commandLine);
		}
	}
	
	
	@Override
	public String toString() {
		String result = "";
		result += "[" + name + "->" + value + "]"; 
		return result;
	}
	
	
	public String getName() {
		return name;
	}


	public Object getValue() {
		return value;
	}
	
	
	public static void main(String[] args) {
		SetOption setoption = new SetOption(new Channel_Console(), "setoption name Evaluation [Piece-Square Endgame] value 10 min 0 max 20");
		System.out.println(setoption);
	}
}
