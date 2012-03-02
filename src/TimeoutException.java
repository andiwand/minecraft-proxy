public class TimeoutException extends RuntimeException {
	
	private static final long serialVersionUID = 705788143955048766L;
	
	public TimeoutException() {}
	
	public TimeoutException(String message) {
		super(message);
	}
	
	public TimeoutException(Throwable cause) {
		super(cause);
	}
	
	public TimeoutException(String message, Throwable cause) {
		super(message, cause);
	}
	
}