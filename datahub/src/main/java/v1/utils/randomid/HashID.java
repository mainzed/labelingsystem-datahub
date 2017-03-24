package v1.utils.randomid;

import java.util.UUID;
import org.hashids.Hashids;

public class HashID {
	
	public static String getHASHID() {
		// https://github.com/jiecao-fm/hashids-java
		Hashids hashids = new Hashids(getUUID(), 12);
		String hash = hashids.encode(1234567L);
		return hash;
	}
	
	private static String getUUID() {
		UUID newUUID = UUID.randomUUID();
		return newUUID.toString();
	}

}