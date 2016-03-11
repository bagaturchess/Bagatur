package bagaturchess.uci.impl;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;


public class DummyPrintStream extends PrintStream {

	
	public DummyPrintStream() throws FileNotFoundException {
		super(new DummyOS());
	}

	
	private static class DummyOS extends OutputStream {
		@Override
		public void write(int b) throws IOException {
			//Do nothing
		}
	}
}
