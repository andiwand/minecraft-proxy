import java.net.InetAddress;

public class IncrementingNameFactory implements NameFactory {
	
	private static final String PREFIX = "Player";
	
	private final String prefix;
	private int id;
	
	public IncrementingNameFactory() {
		this(PREFIX);
	}
	
	public IncrementingNameFactory(String prefix) {
		this.prefix = prefix;
	}
	
	public String getName(InetAddress address) {
		return prefix + (++id);
	}
	
}