import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;


public class FilterZeroOutputStream extends FilterOutputStream {
	
	public FilterZeroOutputStream(OutputStream out) {
		super(out);
	}
	
	@Override
	public void write(int b) throws IOException {
		if (b == 0) b = '.';
		out.write(b);
	}
	
	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++)
			write(b[off + i]);
	}
	
}