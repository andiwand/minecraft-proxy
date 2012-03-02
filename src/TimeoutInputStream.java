import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;


public class TimeoutInputStream extends FilterInputStream {
	
	private class Reader extends Thread {
		public Reader() {
			read = null;
			start();
		}
		
		@Override
		public void run() {
			try {
				read = in.read();
			} catch (IOException e) {
				exception = e;
			}
		}
	}
	
	private final int timeout;
	
	private Reader reader;
	
	private Integer read;
	private IOException exception;
	
	public TimeoutInputStream(InputStream in, int timeout) {
		super(in);
		
		this.timeout = timeout;
	}
	
	private void handleException() throws IOException {
		if (exception != null) {
			IOException tmp = exception;
			exception = null;
			throw tmp;
		}
	}
	
	@Override
	public int read() throws IOException {
		return read(timeout);
	}
	
	public int read(int timeout) throws IOException {
		if ((reader == null) || !reader.isAlive()) reader = new Reader();
		
		try {
			reader.join(timeout);
		} catch (InterruptedException e) {
			throw new IOException("read was interrupted", e);
		}
		
		handleException();
		if (read == null) throw new TimeoutException();
		return read;
	}
	
	public int readBlocked() throws IOException {
		return read(0);
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	public int read(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			int read = read();
			if (read == -1) return (i == 0) ? -1 : (i + 1);
			b[off + i] = (byte) read;
		}
		
		return len;
	}
	
}