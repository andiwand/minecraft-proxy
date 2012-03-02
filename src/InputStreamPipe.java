import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class InputStreamPipe {
	
	private final InputStream in;
	private final OutputStream out;
	
	private Thread pipeThread = new Thread() {
		public void run() {
			try {
				int read;
				while ((read = in.read()) != -1) {
					byte[] bytes = new byte[1 + in.available()];
					bytes[0] = (byte) read;
					in.read(bytes, 1, bytes.length - 1);
					
					System.err.println(new String(bytes).replaceAll("\0", "."));
					
					out.write(bytes);
					out.flush();
				}
			} catch (IOException e) {} finally {
				try {
					in.close();
					out.close();
				} catch (IOException e) {}
			}
		}
	};
	
	public InputStreamPipe(InputStream in, OutputStream out) {
		this.in = in;
		this.out = out;
		this.pipeThread.start();
	}
	
}