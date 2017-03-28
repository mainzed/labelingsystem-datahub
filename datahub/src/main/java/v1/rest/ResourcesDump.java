package v1.rest;

import exceptions.Logging;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.rdf4j.query.BindingSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rdf.RDF4J_20;
import v1.utils.config.ConfigProperties;

@Path("/resourcesdump")
public class ResourcesDump {

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response createReseourcesDump() {
		try {
			String dumpFile = writeResourcesDumpFile();
			JSONObject out = new JSONObject();
			out.put("file", dumpFile);
			return Response.ok(out).header("Content-Type", "application/json;charset=UTF-8").build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.ResourcesDump"))
					.header("Content-Type", "application/json;charset=UTF-8").build();
		}
	}

	public static String writeResourcesDumpFile() {
		try {
			JSONArray outArray = new JSONArray();
			// get resources dump
			// query labeling system of concepts related to this resource
			String query = "SELECT ?resource WHERE { "
					+ "?concept ?p ?resource . "
					+ "FILTER (?p=<http://www.w3.org/2004/02/skos/core#closeMatch> || ?p=<http://www.w3.org/2004/02/skos/core#exactMatch> || ?p=<http://www.w3.org/2004/02/skos/core#relatedMatch> || ?p=<http://www.w3.org/2004/02/skos/core#broadMatch> || ?p=<http://www.w3.org/2004/02/skos/core#narrowMatch>) "
					+ "}";
			List<BindingSet> result = RDF4J_20.SPARQLquery("labelingsystem", ConfigProperties.getPropertyParam("ts_server"), query);
			HashSet<String> resources = RDF4J_20.getValuesFromBindingSet_UNIQUESET(result, "resource");
			boolean resourcesfull = true;
			for (String item : resources) {
				if (!resourcesfull) {
					outArray.add(item);
				} else {
					String url_string = "http://ls-dev.i3mainz.hs-mainz.de/api/v1/resourceinfo?uri=" + item;
					URL url = new URL(url_string);
					HttpURLConnection conn = (HttpURLConnection) url.openConnection();
					conn.setReadTimeout(5000);
					int status = conn.getResponseCode();
					if (status == 200) {
						BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
						String inputLine;
						StringBuilder response = new StringBuilder();
						while ((inputLine = br.readLine()) != null) {
							response.append(inputLine);
						}
						br.close();
						// fill objects
						JSONObject jsonObject = (JSONObject) new JSONParser().parse(response.toString());
						outArray.add(jsonObject);
					}
				}
			}
			// create file
			String fileName = "resources-latest.json";
			PrintWriter writer = new PrintWriter(ConfigProperties.getPropertyParam("dump_res_server") + fileName, "UTF-8");
			writer.println(outArray.toJSONString());
			writer.close();
			return fileName;
		} catch (Exception e) {
			System.out.println(e.toString());
			throw new IllegalStateException(e.toString());
		}
	}

}
