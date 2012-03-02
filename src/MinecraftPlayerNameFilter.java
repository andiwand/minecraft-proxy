import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Deque;
import java.util.LinkedList;


public class MinecraftPlayerNameFilter extends FilterInputStream {
	
	private static final int TIMEOUT = 100;
	
	private final TimeoutInputStream timeoutIn;
	
	private final byte[] source;
	private MinecraftFirstMessageFilter firstMessageFilter;
	private byte[] destination;
	private final Deque<Integer> queue = new LinkedList<Integer>();
	
	public MinecraftPlayerNameFilter(InputStream in, String source,
			MinecraftFirstMessageFilter firstMessageFilter) {
		super(in);
		
		this.timeoutIn = new TimeoutInputStream(in, TIMEOUT);
		this.source = Minecraft.toMinecraftString(source);
		this.firstMessageFilter = firstMessageFilter;
	}
	
	private void queueAll(byte[] buffer) {
		for (int i = 0; i < buffer.length; i++)
			queue.add((int) buffer[i]);
	}
	
	@Override
	public int read() throws IOException {
		if (!queue.isEmpty()) return queue.remove();
		
		int read = timeoutIn.readBlocked();
		queue.add(read);
		
		for (int i = 0; (read == source[i]) && (i < source.length); i++) {
			if (i == (source.length - 1)) {
				if (destination == null) {
					destination = Minecraft.toMinecraftString(firstMessageFilter.getName());
				}
				
				queue.clear();
				queueAll(destination);
				break;
			}
			
			try {
				read = timeoutIn.read();
				queue.add(read);
			} catch (TimeoutException e) {
				break;
			}
		}
		
		return queue.remove();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return read(b, 0, b.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			int read = read();
			if (read == -1) return (i == 0) ? -1 : (i + 1);
			b[off + i] = (byte) read;
		}
		
		return len;
	}
	
	@Override
	public int available() throws IOException {
		return queue.size();
	}
	
}