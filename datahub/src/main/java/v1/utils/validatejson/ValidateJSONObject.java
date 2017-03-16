package v1.utils.validatejson;

import exceptions.SparqlParseException;
import exceptions.SparqlQueryException;
import exceptions.ValidateJSONObjectException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import org.eclipse.rdf4j.query.BindingSet;
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.repository.RepositoryException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rdf.RDF;
import rdf.RDF4J_20;
import v1.utils.config.ConfigProperties;
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
		if (jsonObject.containsKey("id")) {
			jsonObject.remove("id");
		}
		if (jsonObject.containsKey("datasets")) {
			jsonObject.remove("datasets");
		}
		if (!jsonObject.isEmpty()) {
			throw new ValidateJSONObjectException("found unsupported key");
		}
		return newToken;
	}

	public static String validateDataset(String json) throws ParseException, ValidateJSONObjectException, UnsupportedEncodingException, FileNotFoundException, IOException, NoSuchAlgorithmException, RepositoryException, MalformedQueryException, QueryEvaluationException, SparqlQueryException, SparqlParseException {
		String newToken = "";
		JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
		// MENDATORY KEYS
		if (!jsonObject.containsKey("project")) {
			throw new ValidateJSONObjectException("project missing");
		}
		if (!jsonObject.containsKey("token")) {
			throw new ValidateJSONObjectException("token missing");
		}
		if (!jsonObject.containsKey("dataset")) {
			throw new ValidateJSONObjectException("dataset missing");
		}
		if (!jsonObject.containsKey("label")) {
			throw new ValidateJSONObjectException("label missing");
		}
		if (!jsonObject.containsKey("title")) {
			throw new ValidateJSONObjectException("title missing");
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
		// project available?
		String projectID = (String) jsonObject.get("project");
		RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
		String query = rdf.getPREFIXSPARQL();
		query += "SELECT * WHERE { lsdh-p:" + projectID + " ?p ?o. }";
		List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
		List<String> results = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
		int z = 0;
		if (results.size() < 1 || results == null) {
			throw new ValidateJSONObjectException("project not available");
		}
		// dataset starts with http
		if (!jsonObject.get("dataset").toString().startsWith("http")) {
			throw new ValidateJSONObjectException("dataset do not start with HTTP");
		}
		// label starts with http://143.93.114.135
		if (!jsonObject.get("label").toString().startsWith("http://143.93.114.135")) {
			throw new ValidateJSONObjectException("label do not start with http://143.93.114.135");
		}
		// if relation -> starts with http
		if (!jsonObject.get("relation").toString().startsWith("http")) {
			throw new ValidateJSONObjectException("relation do not start with HTTP");
		}
		// if depiction -> starts with http
		if (!jsonObject.get("depiction").toString().startsWith("http")) {
			throw new ValidateJSONObjectException("depiction do not start with HTTP");
		}
		// REMOVE ALL possible keys
		if (jsonObject.containsKey("project")) {
			jsonObject.remove("project");
		}
		if (jsonObject.containsKey("relation")) {
			jsonObject.remove("relation");
		}
		if (jsonObject.containsKey("dataset")) {
			jsonObject.remove("dataset");
		}
		if (jsonObject.containsKey("label")) {
			jsonObject.remove("label");
		}
		if (jsonObject.containsKey("title")) {
			jsonObject.remove("title");
		}
		if (jsonObject.containsKey("description")) {
			jsonObject.remove("description");
		}
		if (jsonObject.containsKey("coverage")) {
			jsonObject.remove("coverage");
		}
		if (jsonObject.containsKey("token")) {
			jsonObject.remove("token");
		}
		if (jsonObject.containsKey("temporal")) {
			jsonObject.remove("temporal");
		}
		if (jsonObject.containsKey("depiction")) {
			jsonObject.remove("depiction");
		}
		if (jsonObject.containsKey("begin")) {
			jsonObject.remove("begin");
		}
		if (jsonObject.containsKey("end")) {
			jsonObject.remove("end");
		}
		if (jsonObject.containsKey("lat")) {
			jsonObject.remove("lat");
		}
		if (jsonObject.containsKey("lng")) {
			jsonObject.remove("lng");
		}
		if (jsonObject.containsKey("id")) {
			jsonObject.remove("id");
		}
		if (!jsonObject.isEmpty()) {
			throw new ValidateJSONObjectException("found unsupported key");
		}
		return newToken;
	}

}
