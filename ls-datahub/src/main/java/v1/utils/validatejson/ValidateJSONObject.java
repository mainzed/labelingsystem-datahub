package v1.utils.validatejson;

import exceptions.AccessDeniedException;
import exceptions.ValidateJSONObjectException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import v1.utils.crypt.Crypt;

public class ValidateJSONObject {

	public static String validateProject(String json) throws ParseException, ValidateJSONObjectException, UnsupportedEncodingException, FileNotFoundException, IOException, NoSuchAlgorithmException {
		String newToken = "";
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
		// MENDATORY KEYS
		if (!jsonObject.containsKey("title")) {
			throw new ValidateJSONObjectException("title missing");
		}
		if (!jsonObject.containsKey("publisher")) {
			throw new ValidateJSONObjectException("publisher missing");
		}
		if (!jsonObject.containsKey("description")) {
			throw new ValidateJSONObjectException("description missing");
		}
		if (!jsonObject.containsKey("license")) {
			throw new ValidateJSONObjectException("license missing");
		}
		if (!jsonObject.containsKey("token")) {
			throw new ValidateJSONObjectException("token missing");
		}
		// special behavoir
		// token in list?
		String token = (String) jsonObject.get("token");
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(ValidateJSONObject.class.getClassLoader().getResource("keys.json").getFile()), "UTF8"));
		String inputLine;
		StringBuilder response = new StringBuilder();
		while ((inputLine = br.readLine()) != null) {
			response.append(inputLine);
		}
		br.close();
		JSONArray keys = (JSONArray) new JSONParser().parse(response.toString());
		int error = -1;
		for (Object item : keys) {
			JSONObject object = (JSONObject) item;
			if (object.get("key").equals(token)) {
				error = 0;
				String hash = (String) object.get("hash");
				String salt = hash.substring(0, 25);
				String echo = salt + Crypt.SHA1(salt + token);
				newToken = echo;
			}
		}
		// if key is not available
		if (error != 0) {
			throw new ValidateJSONObjectException("token is not in list");
		}
		// publisher starts with http
		if (!jsonObject.get("publisher").toString().startsWith("http")) {
			throw new ValidateJSONObjectException("publisher do not start with HTTP");
		}
		// license starts with http
		if (!jsonObject.get("license").toString().startsWith("http")) {
			throw new ValidateJSONObjectException("license do not start with HTTP");
		}
		// if dump -> starts with http
		if (!jsonObject.get("dump").toString().startsWith("http")) {
			throw new ValidateJSONObjectException("dump do not start with HTTP");
		}
		// if sparql -> starts with http
		if (!jsonObject.get("sparql").toString().startsWith("http")) {
			throw new ValidateJSONObjectException("sparql do not start with HTTP");
		}
		// REMOVE ALL possible keys
		if (jsonObject.containsKey("title")) {
			jsonObject.remove("title");
		}
		if (jsonObject.containsKey("publisher")) {
			jsonObject.remove("publisher");
		}
		if (jsonObject.containsKey("description")) {
			jsonObject.remove("description");
		}
		if (jsonObject.containsKey("license")) {
			jsonObject.remove("license");
		}
		if (jsonObject.containsKey("dump")) {
			jsonObject.remove("dump");
		}
		if (jsonObject.containsKey("sparql")) {
			jsonObject.remove("sparql");
		}
		if (jsonObject.containsKey("token")) {
			jsonObject.remove("token");
		}
		if (!jsonObject.isEmpty()) {
			throw new ValidateJSONObjectException("found unsupported key");
		}
		return newToken;
	}

}
