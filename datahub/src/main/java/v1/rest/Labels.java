package v1.rest;

import exceptions.Logging;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.rdf4j.query.BindingSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import rdf.RDF4J_20;
import v1.utils.config.ConfigProperties;
import v1.utils.randomid.HashID;

@Path("/labels")
public class Labels {

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response getLabels() {
		JSONArray outArray = new JSONArray();
		try {
			String query = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> PREFIX ls: <http://labeling.link/docs/ls/core#> SELECT ?uri ?pl WHERE { ?uri skos:prefLabel ?pl . ?uri skos:inScheme ?voc . ?voc ls:hasReleaseType ls:Public . }";
			List<BindingSet> result = RDF4J_20.SPARQLquery("labelingsystem", ConfigProperties.getPropertyParam("ts_server"), query);
			List<String> uris = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "uri");
			List<String> preflabels = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "pl");
			for (int i = 0; i < uris.size(); i++) {
				JSONObject tmp = new JSONObject();
				tmp.put("id", HashID.getHASHID());
				tmp.put("uri", uris.get(i));
				tmp.put("value", preflabels.get(i).split("@")[0].replace("\"", ""));
				tmp.put("lang", preflabels.get(i).split("@")[1]);
				outArray.add(tmp);
			}
			// sort array
			JSONArray sortedJsonArray = new JSONArray();
			List<JSONObject> jsonValues = new ArrayList();
			for (int i = 0; i < outArray.size(); i++) {
				jsonValues.add((JSONObject) outArray.get(i));
			}
			Collections.sort(jsonValues, new Comparator<JSONObject>() {
				private static final String KEY_NAME = "value";

				@Override
				public int compare(JSONObject a, JSONObject b) {
					String valA = new String();
					String valB = new String();
					valA = (String) a.get(KEY_NAME);
					valB = (String) b.get(KEY_NAME);
					return valA.compareTo(valB);
				}
			});
			for (int i = 0; i < outArray.size(); i++) {
				sortedJsonArray.add(jsonValues.get(i));
			}
			outArray = sortedJsonArray;
			return Response.ok(outArray.toJSONString()).header("Content-Type", "application/json;charset=UTF-8").build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.Labels"))
					.header("Content-Type", "application/json;charset=UTF-8").build();
		}
	}

}
