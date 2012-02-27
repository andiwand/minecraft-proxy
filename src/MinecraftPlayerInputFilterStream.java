import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Deque;
import java.util.LinkedList;


public class MinecraftPlayerInputFilterStream extends FilterInputStream {
	
	private static final String CHARSET = "utf-16be";
	
	private static byte[] toMinecraftString(String string) {
		try {
			byte[] bytes = string.getBytes(CHARSET);
			byte[] result = new byte[2 + bytes.length];
			int length = string.length();
			result[0] = (byte) ((length >> 8) & 0xff);
			result[1] = (byte) ((length >> 0) & 0xff);
			System.arraycopy(bytes, 0, result, 2, bytes.length);
			return result;
		} catch (UnsupportedEncodingException e) {
			return null;
		}
	}
	
	private final byte[] source;
	private final byte[] destination;
	private final Deque<Integer> queue = new LinkedList<Integer>();
	
	public MinecraftPlayerInputFilterStream(InputStream in, String source,
			String destination) {
		super(in);
		
		this.source = toMinecraftString(source);
		this.destination = toMinecraftString(destination);
	}
	
	@Override
	public int read() throws IOException {
		if (!queue.isEmpty()) return queue.remove();
		
		for (int i = 0; i < source.length; i++) {
			int read = in.read();
			queue.add(read);
			
			if (read != source[i]) break;
			
			if (i == (source.length - 1)) {
				queue.clear();
				for (int j = 0; j < destination.length; j++)
					queue.add((int) destination[j]);
				break;
			}
		}
		
		return queue.remove();
	}
	
	@Override
	public int read(byte[] b) throws IOException {
		return super.read(b, 0, b.length);
	}
	
	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		for (int i = 0; i < len; i++) {
			int read = read();
			if (read == -1) return (i == 0) ? -1 : i;
			b[off + i] = (byte) read;
		}
		
		return len;
	}
	
	@Override
	public int available() throws IOException {
		return queue.size();
	}
	
}