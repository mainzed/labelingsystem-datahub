package v1.utils.trigger;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import v1.utils.config.ConfigProperties;

public class Triggers {

	public static JSONObject getLatLngFromGeoNames(String uri) throws MalformedURLException, IOException {
		JSONObject out = new JSONObject();
		uri = uri.replace("http://sws.geonames.org/", "");
		uri = "http://api.geonames.org/get?geonameId=" + uri + "&username=" + ConfigProperties.getPropertyParam("geonames");
		// query for xml
		URL obj = new URL(uri);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		String urlParameters = "";
		con.setDoOutput(true);
		DataOutputStream wr = new DataOutputStream(con.getOutputStream());
		wr.writeBytes(urlParameters);
		wr.flush();
		wr.close();
		BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF8"));
		String inputLine;
		StringBuilder response = new StringBuilder();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		// parse xml response
		Double lat = -1.0;
		int startTagLat = response.indexOf("<lat>");
		int endTagLat = response.indexOf("</lat>");
		if (startTagLat != -1 && endTagLat != -1) {
			lat = Double.parseDouble(response.substring(startTagLat, endTagLat).replace("<lat>", ""));
		}
		Double lng = -1.0;
		int startTagLng = response.indexOf("<lng>");
		int endTagLng = response.indexOf("</lng>");
		if (startTagLng != -1 && endTagLng != -1) {
			lng = Double.parseDouble(response.substring(startTagLng, endTagLng).replace("<lng>", ""));
		}
		if (lat > 0.0 && lng > 0.0) {
			out.put("lat", lat);
			out.put("lng", lng);
		}
		return out;
	}

	public static JSONObject getBeginEndFromChronOntology(String uri) throws MalformedURLException, IOException, ParseException {
		JSONObject out = new JSONObject();
		uri = uri.replace("/period", "/data/period");
		// query for json
		URL obj = new URL(uri);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		int rc = con.getResponseCode();
		if (rc == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF8"));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// parse json
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.toString());
			JSONObject resourceObject = (JSONObject) jsonObject.get("resource");
			JSONArray timespanArray = (JSONArray) resourceObject.get("hasTimespan");
			if (timespanArray != null) {
				JSONObject firstTimespanObject = (JSONObject) timespanArray.get(0);
				JSONObject beginObject = (JSONObject) firstTimespanObject.get("begin");
				JSONObject endObject = (JSONObject) firstTimespanObject.get("end");
				int begin = Integer.parseInt((String) beginObject.get("at"));
				int end = Integer.parseInt((String) endObject.get("at"));
				out.put("begin", begin);
				out.put("end", end);
			}
		}
		return out;
	}

}
