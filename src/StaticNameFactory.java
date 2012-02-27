public class StaticNameFactory implements NameFactory {
	
	private final String name;
	
	public StaticNameFactory(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
}