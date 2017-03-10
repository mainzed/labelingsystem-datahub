package v1.rest;

import exceptions.Logging;
import exceptions.ResourceNotAvailableException;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.eclipse.rdf4j.query.BindingSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import rdf.RDF;
import rdf.RDF4J_20;
import v1.utils.config.ConfigProperties;
import v1.utils.transformer.Transformer;

@Path("/search")
public class SearchResource {

	@GET
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response getDatasetsBySearch(
			@QueryParam("project") String project,
			@QueryParam("publisher") String publisher,
			@QueryParam("concept") String concept) {
		try {
			JSONArray outArray = new JSONArray();
			// get datasets for a project
			if (project != null) {
				RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
				String query = rdf.getPREFIXSPARQL();
				query += "SELECT ?s ?p ?o WHERE { "
						+ "?s ?p ?o . "
						+ "?s a lsdh:Dataset . "
						+ "?s lsdh:project lsdh-p:" + project + " . "
						+ " } ";
				List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
				List<String> s = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "s");
				List<String> p = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
				List<String> o = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
				for (int i = 0; i < s.size(); i++) {
					rdf.setModelTriple(s.get(i), p.get(i), o.get(i));
				}
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(rdf.getModel("RDF/JSON"));
				Set keys = jsonObject.keySet();
				Iterator a = keys.iterator();
				while (a.hasNext()) {
					String key = (String) a.next();
					JSONObject tmpObject = (JSONObject) jsonObject.get(key);
					JSONArray idArray = (JSONArray) tmpObject.get(rdf.getPrefixItem("dcterms:identifier"));
					JSONObject idObject = (JSONObject) idArray.get(0);
					String h = (String) idObject.get("value");
					JSONObject tmpObject2 = new JSONObject();
					tmpObject2.put(key, tmpObject);
					String hh = tmpObject2.toString();
					JSONObject tmp = Transformer.dataset_GET(hh, h);
					String datasetBody = (String) tmp.get("dataset");
					RDF rdf2 = new RDF(ConfigProperties.getPropertyParam("host"));
					String query2 = rdf2.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
					List<BindingSet> result2 = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query2);
					List<String> predicates2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "p");
					List<String> objects2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "o");
					if (result2.size() < 1) {
						throw new ResourceNotAvailableException("resource " + h + " is not available");
					}
					for (int i = 0; i < predicates2.size(); i++) {
						rdf2.setModelTriple(datasetBody, predicates2.get(i), objects2.get(i));
					}
					String out2 = Transformer.datasetBody_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
					JSONObject out2Object = (JSONObject) new JSONParser().parse(out2);
					tmp.put("title", out2Object.get("title"));
					if (out2Object.get("description") != null) {
						tmp.put("description", out2Object.get("description"));
					}
					if (out2Object.get("depiction") != null) {
						tmp.put("depiction", out2Object.get("depiction"));
					}
					if (out2Object.get("coverage") != null) {
						tmp.put("coverage", out2Object.get("coverage"));
					}
					if (out2Object.get("temporal") != null) {
						tmp.put("temporal", out2Object.get("temporal"));
					}
					outArray.add(tmp);
				}
			}
			// get datasets for a publisher
			if (publisher != null) {
				RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
				String query = rdf.getPREFIXSPARQL();
				query += "SELECT ?s ?p ?o WHERE { "
						+ "?s ?p ?o . "
						+ "?s a lsdh:Dataset . "
						+ "?s lsdh:project ?pro . "
						+ "?pro dcterms:publisher <" + publisher + "> . "
						+ " } ";
				List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
				List<String> s = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "s");
				List<String> p = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
				List<String> o = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
				for (int i = 0; i < s.size(); i++) {
					rdf.setModelTriple(s.get(i), p.get(i), o.get(i));
				}
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(rdf.getModel("RDF/JSON"));
				Set keys = jsonObject.keySet();
				Iterator a = keys.iterator();
				while (a.hasNext()) {
					String key = (String) a.next();
					JSONObject tmpObject = (JSONObject) jsonObject.get(key);
					JSONArray idArray = (JSONArray) tmpObject.get(rdf.getPrefixItem("dcterms:identifier"));
					JSONObject idObject = (JSONObject) idArray.get(0);
					String h = (String) idObject.get("value");
					JSONObject tmpObject2 = new JSONObject();
					tmpObject2.put(key, tmpObject);
					String hh = tmpObject2.toString();
					JSONObject tmp = Transformer.dataset_GET(hh, h);
					String datasetBody = (String) tmp.get("dataset");
					RDF rdf2 = new RDF(ConfigProperties.getPropertyParam("host"));
					String query2 = rdf2.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
					List<BindingSet> result2 = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query2);
					List<String> predicates2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "p");
					List<String> objects2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "o");
					if (result2.size() < 1) {
						throw new ResourceNotAvailableException("resource " + h + " is not available");
					}
					for (int i = 0; i < predicates2.size(); i++) {
						rdf2.setModelTriple(datasetBody, predicates2.get(i), objects2.get(i));
					}
					String out2 = Transformer.datasetBody_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
					JSONObject out2Object = (JSONObject) new JSONParser().parse(out2);
					tmp.put("title", out2Object.get("title"));
					if (out2Object.get("description") != null) {
						tmp.put("description", out2Object.get("description"));
					}
					if (out2Object.get("depiction") != null) {
						tmp.put("depiction", out2Object.get("depiction"));
					}
					if (out2Object.get("coverage") != null) {
						tmp.put("coverage", out2Object.get("coverage"));
					}
					if (out2Object.get("temporal") != null) {
						tmp.put("temporal", out2Object.get("temporal"));
					}
					outArray.add(tmp);
				}
			}
			// get datasets for a specific concept
			if (concept != null) {
				RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
				String query = rdf.getPREFIXSPARQL();
				query += "SELECT ?s ?p ?o WHERE { "
						+ "?s ?p ?o . "
						+ "?s a lsdh:Dataset . "
						+ "?s oa:hasBody <" + concept + "> . "
						+ " } ";
				List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
				List<String> s = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "s");
				List<String> p = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
				List<String> o = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
				for (int i = 0; i < s.size(); i++) {
					rdf.setModelTriple(s.get(i), p.get(i), o.get(i));
				}
				JSONObject jsonObject = (JSONObject) new JSONParser().parse(rdf.getModel("RDF/JSON"));
				Set keys = jsonObject.keySet();
				Iterator a = keys.iterator();
				while (a.hasNext()) {
					String key = (String) a.next();
					JSONObject tmpObject = (JSONObject) jsonObject.get(key);
					JSONArray idArray = (JSONArray) tmpObject.get(rdf.getPrefixItem("dcterms:identifier"));
					JSONObject idObject = (JSONObject) idArray.get(0);
					String h = (String) idObject.get("value");
					JSONObject tmpObject2 = new JSONObject();
					tmpObject2.put(key, tmpObject);
					String hh = tmpObject2.toString();
					JSONObject tmp = Transformer.dataset_GET(hh, h);
					String datasetBody = (String) tmp.get("dataset");
					RDF rdf2 = new RDF(ConfigProperties.getPropertyParam("host"));
					String query2 = rdf2.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
					List<BindingSet> result2 = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query2);
					List<String> predicates2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "p");
					List<String> objects2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "o");
					if (result2.size() < 1) {
						throw new ResourceNotAvailableException("resource " + h + " is not available");
					}
					for (int i = 0; i < predicates2.size(); i++) {
						rdf2.setModelTriple(datasetBody, predicates2.get(i), objects2.get(i));
					}
					String out2 = Transformer.datasetBody_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
					JSONObject out2Object = (JSONObject) new JSONParser().parse(out2);
					tmp.put("title", out2Object.get("title"));
					if (out2Object.get("description") != null) {
						tmp.put("description", out2Object.get("description"));
					}
					if (out2Object.get("depiction") != null) {
						tmp.put("depiction", out2Object.get("depiction"));
					}
					if (out2Object.get("coverage") != null) {
						tmp.put("coverage", out2Object.get("coverage"));
					}
					if (out2Object.get("temporal") != null) {
						tmp.put("temporal", out2Object.get("temporal"));
					}
					outArray.add(tmp);
				}
			}
			return Response.ok(outArray).header("Content-Type", "application/json;charset=UTF-8").build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.SearchResource"))
					.header("Content-Type", "application/json;charset=UTF-8").build();
		}
	}

}
