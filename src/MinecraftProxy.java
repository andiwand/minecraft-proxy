import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;


public class MinecraftProxy extends Thread {
	
	private static final int DEFAULT_PORT = 25565;
	private static final String PLAYER = "Player";
	
	private class Worker {
		public Worker(Socket proxySocket, String name) throws IOException {
			Socket clientSocket = new Socket(socketAddress.getAddress(),
					socketAddress.getPort());
			
			InputStream proxyIn = proxySocket.getInputStream();
			proxyIn = new MinecraftPlayerInputFilterStream(proxyIn, PLAYER,
					name);
			OutputStream proxyOut = proxySocket.getOutputStream();
			InputStream clientIn = clientSocket.getInputStream();
			clientIn = new MinecraftPlayerInputFilterStream(clientIn, name,
					PLAYER);
			OutputStream clientOut = clientSocket.getOutputStream();
			
			new InputStreamPipe(proxyIn, clientOut);
			new InputStreamPipe(clientIn, proxyOut);
		}
	}
	
	private final NameFactory nameFactory;
	private final ServerSocket serverSocket;
	private final InetSocketAddress socketAddress;
	private final Map<InetAddress, String> nameForAddress;
	
	public MinecraftProxy(int port, InetSocketAddress socketAddress)
			throws IOException {
		this(new CountingNameFactory(), port, socketAddress);
	}
	
	public MinecraftProxy(String username, int port,
			InetSocketAddress socketAddress) throws IOException {
		this(new StaticNameFactory(username), port, socketAddress);
	}
	
	public MinecraftProxy(NameFactory nameFactory, int port,
			InetSocketAddress socketAddress) throws IOException {
		this.nameFactory = nameFactory;
		this.serverSocket = new ServerSocket(port);
		this.socketAddress = socketAddress;
		this.nameForAddress = new HashMap<InetAddress, String>();
		
		Properties staticNames = new Properties();
		staticNames.load(new FileInputStream("static-players.properties"));
		for (Entry<Object, Object> entry : staticNames.entrySet()) {
			nameForAddress.put(Inet4Address.getByName((String) entry.getKey()), (String) entry.getValue());
		}
		
		start();
	}
	
	@Override
	public void run() {
		while (true) {
			try {
				Socket socket = serverSocket.accept();
				InetAddress address = socket.getInetAddress();
				// TODO: plus socket.getPort() for shared public ips?
				
				String name;
				if (nameForAddress.containsKey(address)) {
					name = nameForAddress.get(address);
				} else {
					name = nameFactory.getName();
				}
				
				new Worker(socket, name);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args) throws Throwable {
		InetSocketAddress socketAddress;
		int port;
		
		try {
			String[] socketAddressString = args[0].split(":");
			socketAddress = new InetSocketAddress(
					socketAddressString[0],
					(socketAddressString.length > 1) ? Integer.parseInt(socketAddressString[1])
							: 25565);
			port = (args.length > 1) ? Integer.parseInt(args[1]) : DEFAULT_PORT;
		} catch (Exception e) {
			e.printStackTrace();
			System.err.println();
			System.err.println("usage: <program> <srvAddress>:<srvPort> [<proxyPort> [<staticName>]]");
			System.exit(-1);
			return;
		}
		
		if (args.length > 2) new MinecraftProxy(args[2], port, socketAddress);
		else new MinecraftProxy(port, socketAddress);
		
		System.out.println("proxy server started...");
	}
	
}