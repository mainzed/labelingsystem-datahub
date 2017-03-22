package v1.rest;

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.OperatorWithin;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;
import exceptions.Logging;
import exceptions.ResourceNotAvailableException;
import exceptions.SparqlParseException;
import exceptions.SparqlQueryException;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
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
import org.eclipse.rdf4j.query.MalformedQueryException;
import org.eclipse.rdf4j.query.QueryEvaluationException;
import org.eclipse.rdf4j.repository.RepositoryException;
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
			@QueryParam("labels") boolean labels,
			@QueryParam("lang") String lang,
			@QueryParam("project") String project,
			@QueryParam("creator") String creator,
			@QueryParam("concept") String concept,
			@QueryParam("resource") String resource,
			@QueryParam("start") String start,
			@QueryParam("end") String end,
			@QueryParam("lat_min") String lat_min,
			@QueryParam("lng_min") String lng_min,
			@QueryParam("lat_max") String lat_max,
			@QueryParam("lng_max") String lng_max,
			@QueryParam("languages") boolean languages,
			@QueryParam("geojson") boolean geojson) {
		try {
			JSONArray outArray = new JSONArray();
			JSONObject geoJSON = new JSONObject();
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
					String out2 = Transformer.target_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
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
					if (out2Object.get("lat") != null) {
						tmp.put("lat", out2Object.get("lat"));
					}
					if (out2Object.get("lng") != null) {
						tmp.put("lng", out2Object.get("lng"));
					}
					if (out2Object.get("temporal") != null) {
						tmp.put("temporal", out2Object.get("temporal"));
					}
					if (out2Object.get("begin") != null) {
						tmp.put("begin", out2Object.get("begin"));
					}
					if (out2Object.get("end") != null) {
						tmp.put("end", out2Object.get("end"));
					}
					outArray.add(tmp);
				}
				// labels output if requested
				outArray = getLabels(labels, lang, outArray);
			} else if (creator != null) { // get datasets for a publisher
				RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
				String query = rdf.getPREFIXSPARQL();
				query += "SELECT ?s ?p ?o WHERE { "
						+ "?s ?p ?o . "
						+ "?s a lsdh:Dataset . "
						+ "?s lsdh:project ?pro . "
						+ "?pro dcelements:creator \"" + creator + "\" . "
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
					String out2 = Transformer.target_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
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
					if (out2Object.get("lat") != null) {
						tmp.put("lat", out2Object.get("lat"));
					}
					if (out2Object.get("lng") != null) {
						tmp.put("lng", out2Object.get("lng"));
					}
					if (out2Object.get("temporal") != null) {
						tmp.put("temporal", out2Object.get("temporal"));
					}
					if (out2Object.get("begin") != null) {
						tmp.put("begin", out2Object.get("begin"));
					}
					if (out2Object.get("end") != null) {
						tmp.put("end", out2Object.get("end"));
					}
					outArray.add(tmp);
				}
				// labels output if requested
				outArray = getLabels(labels, lang, outArray);
			} else if (concept != null) { // get datasets for a specific concept
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
					String out2 = Transformer.target_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
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
					if (out2Object.get("lat") != null) {
						tmp.put("lat", out2Object.get("lat"));
					}
					if (out2Object.get("lng") != null) {
						tmp.put("lng", out2Object.get("lng"));
					}
					if (out2Object.get("temporal") != null) {
						tmp.put("temporal", out2Object.get("temporal"));
					}
					if (out2Object.get("begin") != null) {
						tmp.put("begin", out2Object.get("begin"));
					}
					if (out2Object.get("end") != null) {
						tmp.put("end", out2Object.get("end"));
					}
					outArray.add(tmp);
				}
				// labels output if requested
				outArray = getLabels(labels, lang, outArray);
			} else if (resource != null) { // get datasets for a specific resource related to a concept
				RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
				// query labeling system of concepts related to this resource
				String query = rdf.getPREFIXSPARQL() + "SELECT ?concept WHERE { ?concept ?p <" + resource + "> }";
				List<BindingSet> result = RDF4J_20.SPARQLquery("labelingsystem", ConfigProperties.getPropertyParam("ts_server"), query);
				List<String> conceptsForResource = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "concept");
				// query for datasets with this concepts
				query = rdf.getPREFIXSPARQL();
				query += "SELECT ?s ?p ?o WHERE { "
						+ "?s ?p ?o . "
						+ "?s a lsdh:Dataset . "
						+ "?s oa:hasBody ?uri . "
						+ "FILTER ( ";
				for (String item : conceptsForResource) {
					query += "?uri=<" + item + "> || ";
				}
				query = query.substring(0, query.length() - 3);
				query += ") ";
				query += "}";
				result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
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
					String out2 = Transformer.target_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
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
					if (out2Object.get("lat") != null) {
						tmp.put("lat", out2Object.get("lat"));
					}
					if (out2Object.get("lng") != null) {
						tmp.put("lng", out2Object.get("lng"));
					}
					if (out2Object.get("temporal") != null) {
						tmp.put("temporal", out2Object.get("temporal"));
					}
					if (out2Object.get("begin") != null) {
						tmp.put("begin", out2Object.get("begin"));
					}
					if (out2Object.get("end") != null) {
						tmp.put("end", out2Object.get("end"));
					}
					outArray.add(tmp);
				}
				// labels output if requested
				outArray = getLabels(labels, lang, outArray);
			} else if (start != null && end != null) { // get datasets for a timespan
				RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
				String query = rdf.getPREFIXSPARQL();
				query += "SELECT ?s ?p ?o WHERE { "
						+ "?s ?p ?o . "
						+ "?s a lsdh:Dataset . "
						+ "?s oa:hasTarget ?t . "
						+ "?t prov:startedAtTime ?start . "
						+ "?t prov:endedAtTime ?end . "
						+ "FILTER (xsd:integer(?start) > " + start + " && xsd:integer(?end) < " + end + ") "
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
					String out2 = Transformer.target_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
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
					if (out2Object.get("lat") != null) {
						tmp.put("lat", out2Object.get("lat"));
					}
					if (out2Object.get("lng") != null) {
						tmp.put("lng", out2Object.get("lng"));
					}
					if (out2Object.get("temporal") != null) {
						tmp.put("temporal", out2Object.get("temporal"));
					}
					if (out2Object.get("begin") != null) {
						tmp.put("begin", out2Object.get("begin"));
					}
					if (out2Object.get("end") != null) {
						tmp.put("end", out2Object.get("end"));
					}
					outArray.add(tmp);
				}
				// labels output if requested
				outArray = getLabels(labels, lang, outArray);
			} else if (lat_min != null && lng_min != null && lat_max != null && lng_max != null) { // get datasets for envelope
				RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
				String query = rdf.getPREFIXSPARQL();
				query += "SELECT ?s ?p ?o WHERE { "
						+ "?s ?p ?o . "
						+ "?s a lsdh:Dataset . "
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
					String out2 = Transformer.target_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
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
					if (out2Object.get("lat") != null) {
						tmp.put("lat", out2Object.get("lat"));
					}
					if (out2Object.get("lng") != null) {
						tmp.put("lng", out2Object.get("lng"));
					}
					if (out2Object.get("temporal") != null) {
						tmp.put("temporal", out2Object.get("temporal"));
					}
					if (out2Object.get("begin") != null) {
						tmp.put("begin", out2Object.get("begin"));
					}
					if (out2Object.get("end") != null) {
						tmp.put("end", out2Object.get("end"));
					}
					outArray.add(tmp);
				}
				JSONArray outArray2 = new JSONArray();
				for (Object item : outArray) {
					JSONObject tmp = (JSONObject) item;
					// https://github.com/Esri/geometry-api-java/wiki
					String lat = (String) tmp.get("lat");
					String lng = (String) tmp.get("lng");
					if (lat != null && lng != null) {
						SpatialReference spatialRef = SpatialReference.create(4326);
						Point center = new Point(Double.parseDouble(lat), Double.parseDouble(lng));
						// envelope
						/* Another way to create an envelope is to specify the X and Y extents.
						*  xmin: Minimum X-coordinate, ymin: Minimum Y-coordinate,
						*  xmax: Maximum X-coordinate and ymax: Maximum Y-coordinate
						 */
						//Envelope env1 = new Envelope(xmin, ymin, xmax, ymax);
						// x = lat - y = lng
						//Envelope env1 = new Envelope(0, 0, 30.5, 30.5);
						// http://esri.github.io/geometry-api-java/doc/Envelope.html
						Envelope env1 = new Envelope(Double.parseDouble(lat_min), Double.parseDouble(lng_min), Double.parseDouble(lat_max), Double.parseDouble(lng_max));
						boolean isWithin = OperatorWithin.local().execute(center, env1, spatialRef, null);
						if (isWithin) {
							outArray2.add(tmp);
						}
					}
				}
				// labels output if requested
				outArray = getLabels(labels, lang, outArray2);
			} else if (languages) {
				RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
				String query = rdf.getPREFIXSPARQL();
				query += "SELECT ?s ?p ?o WHERE { "
						+ "?s ?p ?o . "
						+ "?s a lsdh:Dataset . "
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
					String out2 = Transformer.target_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
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
					if (out2Object.get("lat") != null) {
						tmp.put("lat", out2Object.get("lat"));
					}
					if (out2Object.get("lng") != null) {
						tmp.put("lng", out2Object.get("lng"));
					}
					if (out2Object.get("temporal") != null) {
						tmp.put("temporal", out2Object.get("temporal"));
					}
					if (out2Object.get("begin") != null) {
						tmp.put("begin", out2Object.get("begin"));
					}
					if (out2Object.get("end") != null) {
						tmp.put("end", out2Object.get("end"));
					}
					outArray.add(tmp);
				}
				// labels output if requested
				outArray = getLabels(true, null, outArray);
				// get set with used languages
				HashSet<String> uniqueLanguages = new HashSet();
				for (Object item : outArray) {
					JSONObject tmp = (JSONObject) item;
					uniqueLanguages.add((String) tmp.get("lang"));
				}
				// load languages json
				BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(SearchResource.class.getClassLoader().getResource("languages.json").getFile()), "UTF8"));
				String inputLine;
				StringBuilder response = new StringBuilder();
				while ((inputLine = br.readLine()) != null) {
					response.append(inputLine);
				}
				br.close();
				JSONArray languagesArray = (JSONArray) new JSONParser().parse(response.toString());
				// get long version for language
				outArray.clear();
				for (String tmpLang : uniqueLanguages) {
					for (Object tmpLangObj : languagesArray) {
						JSONObject tmp = (JSONObject) tmpLangObj;
						if (tmpLang.equals(tmp.get("value"))) {
							outArray.add(tmpLangObj);
						}
					}
				}
				// sort array
				JSONArray sortedJsonArray = new JSONArray();
				List<JSONObject> jsonValues = new ArrayList();
				for (int i = 0; i < outArray.size(); i++) {
					jsonValues.add((JSONObject) outArray.get(i));
				}
				Collections.sort(jsonValues, new Comparator<JSONObject>() {
					private static final String KEY_NAME = "name";

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
			} else if (geojson) {
				RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
				String query = rdf.getPREFIXSPARQL();
				query += "SELECT DISTINCT ?lat ?lng WHERE { "
						+ "?s ?p ?o . "
						+ "?s a lsdh:Dataset . "
						+ "?s oa:hasTarget ?t . "
						+ "?t geo:lat ?lat . "
						+ "?t geo:long ?lng . "
						+ " } ";
				List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
				List<String> lat = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "lat");
				List<String> lng = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "lng");
				// cretae geojson
				geoJSON.put("type", "FeatureCollection");
				JSONArray features = new JSONArray();
				for (int i=0; i<lat.size(); i++) {
					JSONObject feature = new JSONObject();
					feature.put("type", "Feature");
					JSONObject geometry = new JSONObject();
					geometry.put("type", "Point");
					JSONArray point = new JSONArray();
					Double latDbl = Double.parseDouble(lat.get(i));
					Double lngDbl = Double.parseDouble(lng.get(i));
					point.add(latDbl);
					point.add(lngDbl);
					geometry.put("coordinates", point);
					feature.put("geometry", geometry);
					JSONObject properties = new JSONObject();
					feature.put("properties", properties);
					features.add(feature);
				}
				geoJSON.put("features", features);
			} else {
				RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
				String query = rdf.getPREFIXSPARQL();
				query += "SELECT ?s ?p ?o WHERE { "
						+ "?s ?p ?o . "
						+ "?s a lsdh:Dataset . "
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
					String out2 = Transformer.target_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
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
					if (out2Object.get("lat") != null) {
						tmp.put("lat", out2Object.get("lat"));
					}
					if (out2Object.get("lng") != null) {
						tmp.put("lng", out2Object.get("lng"));
					}
					if (out2Object.get("temporal") != null) {
						tmp.put("temporal", out2Object.get("temporal"));
					}
					if (out2Object.get("begin") != null) {
						tmp.put("begin", out2Object.get("begin"));
					}
					if (out2Object.get("end") != null) {
						tmp.put("end", out2Object.get("end"));
					}
					outArray.add(tmp);
				}
				// labels output if requested
				outArray = getLabels(labels, lang, outArray);
			}
			if (!geoJSON.isEmpty()) {
				return Response.ok(geoJSON).header("Content-Type", "application/json;charset=UTF-8").build();
			} else {
				return Response.ok(outArray).header("Content-Type", "application/json;charset=UTF-8").build();
			}
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.SearchResource"))
					.header("Content-Type", "application/json;charset=UTF-8").build();
		}
	}

	private static JSONArray getLabels(boolean labels, String lang, JSONArray outArray) throws IOException, RepositoryException, MalformedQueryException, QueryEvaluationException, SparqlQueryException, SparqlParseException {
		if (labels && !outArray.isEmpty()) {
			List<String> concepts = new ArrayList();
			for (Object tmp : outArray) {
				JSONObject outObj = (JSONObject) tmp;
				concepts.add((String) outObj.get("label"));
			}
			// get labels from labeling system
			outArray.clear();
			String sparql = "PREFIX skos: <http://www.w3.org/2004/02/skos/core#> "
					+ "SELECT DISTINCT ?uri ?pl WHERE { "
					+ "?uri skos:prefLabel ?pl . "
					+ "FILTER ( ";
			for (String item : concepts) {
				sparql += " ?uri=<" + item + "> || ";
			}
			sparql = sparql.substring(0, sparql.length() - 3);
			sparql += ") ";
			if (lang != null) {
				sparql += "FILTER(LANGMATCHES(LANG(?pl), \"" + lang + "\")) ";
			}
			sparql += "}";
			List<BindingSet> result = RDF4J_20.SPARQLquery("labelingsystem", ConfigProperties.getPropertyParam("ts_server"), sparql);
			List<String> uris = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "uri");
			List<String> pls = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "pl");
			for (int i = 0; i < uris.size(); i++) {
				JSONObject labelObj = new JSONObject();
				labelObj.put("uri", uris.get(i));
				labelObj.put("value", pls.get(i).split("@")[0].replace("\"", ""));
				labelObj.put("lang", pls.get(i).split("@")[1]);
				// count appearance of uri in datahub
				int count = 0;
				for (String conceptURI : concepts) {
					int z = 0;
					if (conceptURI.equals(uris.get(i))) {
						count++;
					}
				}
				labelObj.put("datasets", count);
				outArray.add(labelObj);
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
		}
		return outArray;
	}

}
