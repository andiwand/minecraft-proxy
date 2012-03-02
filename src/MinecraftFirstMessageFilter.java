import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.Deque;
import java.util.LinkedList;


public class MinecraftFirstMessageFilter extends FilterInputStream {
	
	private final String source;
	private final NameFactory nameFactory;
	private final InetAddress clientAddress;
	private String name;
	private final String serverDestination;
	private final int serverPort;
	private Deque<Integer> queue = new LinkedList<Integer>();
	
	public MinecraftFirstMessageFilter(InputStream in, String source,
			NameFactory nameFactory, InetAddress clientAddress,
			InetSocketAddress socketAddress) {
		this(in, source, nameFactory, clientAddress,
				socketAddress.getAddress().getHostAddress(),
				socketAddress.getPort());
	}
	
	public MinecraftFirstMessageFilter(InputStream in, String source,
			NameFactory nameFactory, InetAddress clientAddress,
			String serverDestination, int serverPort) {
		super(in);
		
		this.source = source;
		this.nameFactory = nameFactory;
		this.clientAddress = clientAddress;
		this.serverDestination = serverDestination;
		this.serverPort = serverPort;
	}
	
	public String getName() {
		return name;
	}
	
	private void queueAll(byte[] buffer) {
		for (byte b : buffer)
			queue.add((int) b);
	}
	
	@Override
	public int read() throws IOException {
		if (queue == null) {
			return in.read();
		} else if (!queue.isEmpty()) {
			int result = queue.remove();
			if (queue.isEmpty()) queue = null;
			return result;
		}
		
		byte type = (byte) in.read();
		if (type != 0x02) {
			queue = null;
			return type;
		}
		
		short size = (short) (((in.read() & 0xff) << 8) | ((in.read() & 0xff) << 0));
		
		byte[] buffer = new byte[size << 1];
		in.read(buffer);
		
		String message = new String(buffer, Minecraft.CHARSET);
		String name = message.split(";")[0];
		if (name.equals(source)) name = nameFactory.getName(clientAddress);
		this.name = name;
		String newMessage = name + ";" + serverDestination + ":" + serverPort;
		
		queue.add(0x02);
		queueAll(Minecraft.toMinecraftString(newMessage));
		
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
		return (queue != null) ? queue.size() : in.available();
	}
	
}