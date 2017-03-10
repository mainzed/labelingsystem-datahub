package v1.rest;

import exceptions.Logging;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.rdf4j.query.BindingSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rdf.RDF;
import rdf.RDF4J_20;
import v1.utils.config.ConfigProperties;

@Path("/labels")
public class LabelsResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response getUsedLabels() {
		try {
			// get all concept URIs in the data hub and its thumbnails and translations
			RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
			// get concepts from data hub
			String query = rdf.getPREFIXSPARQL() + "SELECT DISTINCT ?concept WHERE { ?s oa:hasBody ?concept . }";
			List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
			List<String> conceptURIs = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "concept");
			String url = "http://" + ConfigProperties.getPropertyParam("host") + "/api/v1/sparql";
			String sparql = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
					+ "SELECT ?uri ?pl WHERE { "
					+ "?uri skos:prefLabel ?pl . "
					+ "FILTER ( ";
			for (String item : conceptURIs) {
				sparql += " ?uri=<"+item+"> || ";
			}
			sparql = sparql.substring(0, sparql.length()-3);
			sparql += ") } ORDER BY ASC(?pl)";
			URL obj = new URL(url);
			HttpURLConnection con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("Accept", "application/sparql-results+json");
			String urlParameters = "query=" + sparql;
			con.setDoOutput(true);
			DataOutputStream wr = new DataOutputStream(con.getOutputStream());
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(wr, "UTF-8"));
			writer.write(urlParameters);
			writer.close();
			wr.close();
			BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF8"));
			String inputLine;
			StringBuilder response = new StringBuilder();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			// parse SPARQL results json
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.toString());
			JSONObject resultsObject = (JSONObject) jsonObject.get("results");
			JSONArray bindingsArray = (JSONArray) resultsObject.get("bindings");
			// init output
			JSONArray outArray = new JSONArray();
			for (Object binding : bindingsArray) {
				JSONObject bindingObject = (JSONObject) binding;
				JSONObject uriObject = (JSONObject) bindingObject.get("uri");
				JSONObject plObject = (JSONObject) bindingObject.get("pl");
				JSONObject labelObj = new JSONObject();
				labelObj.put("key", uriObject.get("value"));
				labelObj.put("value", plObject.get("value")+"@"+plObject.get("xml:lang"));
				outArray.add(labelObj);
			}
			return Response.ok(outArray).header("Content-Type", "application/json;charset=UTF-8").build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.LabelsResource"))
					.header("Content-Type", "application/json;charset=UTF-8").build();
		}
	}

}
