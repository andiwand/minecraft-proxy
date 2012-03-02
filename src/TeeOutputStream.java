

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class TeeOutputStream extends FilterOutputStream {
	
	private final OutputStream tee;
	
	public TeeOutputStream(OutputStream out, OutputStream tee) {
		super(out);
		
		this.tee = tee;
	}
	
	@Override
	public void write(int b) throws IOException {
		out.write(b);
		tee.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
		tee.write(b, off, len);
	}
	
	@Override
	public void flush() throws IOException {
		out.flush();
		tee.flush();
	}
	
}