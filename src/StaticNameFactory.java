import java.net.InetAddress;

public class StaticNameFactory implements NameFactory {
	
	private final String name;
	
	public StaticNameFactory(String name) {
		this.name = name;
	}
	
	public String getName(InetAddress address) {
		return name;
	}
	
}