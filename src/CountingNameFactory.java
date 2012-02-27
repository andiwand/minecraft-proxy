public class CountingNameFactory implements NameFactory {
	
	private static final String PREFIX = "Player";
	
	private final String prefix;
	private int id;
	
	public CountingNameFactory() {
		this(PREFIX);
	}
	
	public CountingNameFactory(String prefix) {
		this.prefix = prefix;
	}
	
	public String getName() {
		return prefix + (++id);
	}
	
}