import java.io.UnsupportedEncodingException;


public class MinecraftUtil {
	
	public static final String CHARSET = "utf-16be";
	
	public static byte[] toMinecraftString(String string) {
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
	
}