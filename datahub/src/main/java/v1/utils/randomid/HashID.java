package v1.utils.randomid;

import java.util.Random;
import org.hashids.Hashids;

public class HashID {
	
	public static String getHASHID() {
		// https://github.com/jiecao-fm/hashids-java
		Random random = new Random();
		Hashids hashids = new Hashids(random.toString(), 12);
		String hash = hashids.encode(1234567L);
		return hash;
	}

}