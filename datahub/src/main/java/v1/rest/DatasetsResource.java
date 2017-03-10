package v1.rest;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import rdf.RDF;
import rdf.RDF4J_20;
import v1.utils.randomid.HashID;
import exceptions.ConfigException;
import exceptions.Logging;
import exceptions.ResourceNotAvailableException;
import v1.utils.transformer.Transformer;
import v1.utils.config.ConfigProperties;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.zip.GZIPOutputStream;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import org.jdom.JDOMException;
import org.json.simple.JSONObject;
import org.eclipse.rdf4j.query.BindingSet;
import org.json.simple.JSONArray;
import org.json.simple.parser.JSONParser;
import v1.utils.validatejson.ValidateJSONObject;

@Path("/datasets")
public class DatasetsResource {

	@GET
	@Produces({"application/json;charset=UTF-8", "application/xml;charset=UTF-8", "application/rdf+xml;charset=UTF-8", "text/turtle;charset=UTF-8", "text/n3;charset=UTF-8", "application/ld+json;charset=UTF-8", "application/rdf+json;charset=UTF-8"})
	public Response getDatasets(
			@HeaderParam("Accept") String acceptHeader,
			@HeaderParam("Accept-Encoding") String acceptEncoding,
			@QueryParam("pretty") boolean pretty,
			@QueryParam("sort") String sort,
			@QueryParam("fields") String fields,
			@QueryParam("offset") String offset,
			@QueryParam("limit") String limit)
			throws IOException, JDOMException, ConfigException, ParserConfigurationException, TransformerException {
		try {
			String OUTSTRING = "";
			// QUERY STRING
			RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
			String query = rdf.getPREFIXSPARQL();
			query += "SELECT * WHERE { "
					+ "?s ?p ?o . "
					+ "?s a lsdh:Dataset . "
					+ "?s dcterms:identifier ?identifier . ";
			query += " } ";
			// SORTING
			List<String> sortList = new ArrayList<String>();
			if (sort != null) {
				String sortArray[] = sort.split(",");
				for (String element : sortArray) {
					if (sort != null) {
						String sortDirection = element.substring(0, 1);
						if (sortDirection.equals("+")) {
							sortDirection = "ASC";
						} else {
							sortDirection = "DESC";
						}
						element = element.substring(1);
						sortList.add(sortDirection + "(?" + element + ") ");
					}
				}
				query += "ORDER BY ";
				for (String element : sortList) {
					query += element;
				}
			}
			// PAGING
			if (limit != null && offset != null) {
				query += "LIMIT " + limit + " OFFSET " + offset;
			}
			List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
			List<String> s = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "s");
			List<String> p = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
			List<String> o = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
			if (result.size() < 1) {
				throw new ResourceNotAvailableException("resource is not available");
			}
			for (int i = 0; i < s.size(); i++) {
				rdf.setModelTriple(s.get(i), p.get(i), o.get(i));
			}
			if (acceptHeader.contains("application/rdf+json")
					|| acceptHeader.contains("application/xml")
					|| acceptHeader.contains("application/rdf+xml")
					|| acceptHeader.contains("text/turtle")
					|| acceptHeader.contains("text/n3")
					|| acceptHeader.contains("application/ld+json")) {
				for (int i = 0; i < s.size(); i++) {
					if (p.get(i).contains("hasTarget")) {
						query = rdf.getPREFIXSPARQL() + "SELECT * WHERE { <" + o.get(i) + "> ?p ?o }";
						result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
						List<String> predicates = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
						List<String> objects = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
						if (result.size() < 1) {
							throw new ResourceNotAvailableException("resource " + o.get(i) + " is not available");
						}
						for (int ii = 0; ii < predicates.size(); ii++) {
							rdf.setModelTriple(o.get(i), predicates.get(ii), objects.get(ii));
						}
					}
				}
			}
			JSONArray outArray = new JSONArray();
			if (acceptHeader.contains("application/json") || acceptHeader.contains("text/html") || acceptHeader.contains("*/*")) {
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
			if (acceptHeader.contains("application/json")) {
				if (pretty) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(outArray.toString()).getAsJsonObject();
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					return Response.ok(gson.toJson(json)).build();
				} else {
					OUTSTRING = outArray.toString();
					if (acceptEncoding.contains("gzip")) {
						// set outputstream
						final String OUTSTRING_FINAL = OUTSTRING;
						StreamingOutput stream;
						stream = new StreamingOutput() {
							@Override
							public void write(OutputStream output) throws IOException, WebApplicationException {
								try {
									output = GZIP(OUTSTRING_FINAL, output);
								} catch (Exception e) {
									System.out.println(e.toString());
								}
							}
						};
						return Response.ok(stream).header("Content-Type", "application/json;charset=UTF-8").header("Content-Encoding", "gzip").build();
					} else {
						return Response.ok(OUTSTRING).header("Content-Type", "application/json;charset=UTF-8").build();
					}
				}
			} else if (acceptHeader.contains("application/rdf+json")) {
				return Response.ok(rdf.getModel("RDF/JSON")).header("Content-Type", "application/json;charset=UTF-8").build();
			} else if (acceptHeader.contains("text/html")) {
				if (pretty) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(outArray.toString()).getAsJsonObject();
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					return Response.ok(gson.toJson(json)).header("Content-Type", "application/json;charset=UTF-8").build();
				} else {
					OUTSTRING = outArray.toString();
					if (acceptEncoding.contains("gzip")) {
						// set outputstream
						final String OUTSTRING_FINAL = OUTSTRING;
						StreamingOutput stream;
						stream = new StreamingOutput() {
							@Override
							public void write(OutputStream output) throws IOException, WebApplicationException {
								try {
									output = GZIP(OUTSTRING_FINAL, output);
								} catch (Exception e) {
									System.out.println(e.toString());
								}
							}
						};
						return Response.ok(stream).header("Content-Type", "application/json;charset=UTF-8").header("Content-Encoding", "gzip").build();
					} else {
						return Response.ok(OUTSTRING).header("Content-Type", "application/json;charset=UTF-8").build();
					}
				}
			} else if (acceptHeader.contains("application/xml")) {
				return Response.ok(rdf.getModel("RDF/XML")).build();
			} else if (acceptHeader.contains("application/rdf+xml")) {
				return Response.ok(rdf.getModel("RDF/XML")).build();
			} else if (acceptHeader.contains("text/turtle")) {
				return Response.ok(rdf.getModel("Turtle")).build();
			} else if (acceptHeader.contains("text/n3")) {
				return Response.ok(rdf.getModel("N-Triples")).build();
			} else if (acceptHeader.contains("application/ld+json")) {
				return Response.ok(rdf.getModel("JSON-LD")).build();
			} else if (pretty) {
				JsonParser parser = new JsonParser();
				JsonObject json = parser.parse(outArray.toString()).getAsJsonObject();
				Gson gson = new GsonBuilder().setPrettyPrinting().create();
				return Response.ok(gson.toJson(json)).header("Content-Type", "application/json;charset=UTF-8").build();
			} else {
				OUTSTRING = outArray.toString();
				if (acceptEncoding.contains("gzip")) {
					// set outputstream
					final String OUTSTRING_FINAL = OUTSTRING;
					StreamingOutput stream;
					stream = new StreamingOutput() {
						@Override
						public void write(OutputStream output) throws IOException, WebApplicationException {
							try {
								output = GZIP(OUTSTRING_FINAL, output);
							} catch (Exception e) {
								System.out.println(e.toString());
							}
						}
					};
					return Response.ok(stream).header("Content-Type", "application/json;charset=UTF-8").header("Content-Encoding", "gzip").build();
				} else {
					return Response.ok(OUTSTRING).header("Content-Type", "application/json;charset=UTF-8").build();
				}
			}
		} catch (Exception e) {
			if (e.toString().contains("ResourceNotAvailableException")) {
				JSONArray outArray = new JSONArray();
				return Response.ok(outArray).header("Content-Type", "application/json;charset=UTF-8").build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.DatasetsResource"))
						.header("Content-Type", "application/json;charset=UTF-8").build();
			}
		}
	}

	@GET
	@Path("/{dataset}")
	@Produces({"application/json;charset=UTF-8", "application/xml;charset=UTF-8", "application/rdf+xml;charset=UTF-8", "text/turtle;charset=UTF-8", "text/n3;charset=UTF-8", "application/ld+json;charset=UTF-8", "application/rdf+json;charset=UTF-8"})
	public Response getDataset(@PathParam("dataset") String itemid, @HeaderParam("Accept") String acceptHeader, @QueryParam("statistics") String statistics, @QueryParam("creatorInfo") String creatorInfo, @QueryParam("pretty") boolean pretty, @HeaderParam("Accept-Encoding") String acceptEncoding) throws IOException, JDOMException, ParserConfigurationException, TransformerException {
		try {
			String OUTSTRING = "";
			RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
			String query = rdf.getPREFIXSPARQL() + "SELECT * WHERE { lsdh-d:" + itemid + " ?p ?o }";
			List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
			List<String> predicates = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
			List<String> objects = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
			if (result.size() < 1) {
				throw new ResourceNotAvailableException("resource " + itemid + " is not available");
			}
			for (int i = 0; i < predicates.size(); i++) {
				rdf.setModelTriple("lsdh-d:" + itemid, predicates.get(i), objects.get(i));
			}
			// get dataset uri
			String datasetBody = "";
			for (int i = 0; i < predicates.size(); i++) {
				if (predicates.get(i).contains("hasTarget")) {
					datasetBody = objects.get(i);
				}
			}
			if (acceptHeader.contains("application/json")) {
				String out = Transformer.dataset_GET(rdf.getModel("RDF/JSON"), itemid).toJSONString();
				JSONObject outObject = (JSONObject) new JSONParser().parse(out);
				RDF rdf2 = new RDF(ConfigProperties.getPropertyParam("host"));
				String query2 = rdf2.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
				List<BindingSet> result2 = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query2);
				List<String> predicates2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "p");
				List<String> objects2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "o");
				if (result2.size() < 1) {
					throw new ResourceNotAvailableException("resource " + itemid + " is not available");
				}
				for (int i = 0; i < predicates2.size(); i++) {
					rdf2.setModelTriple(datasetBody, predicates2.get(i), objects2.get(i));
				}
				String out2 = Transformer.datasetBody_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
				JSONObject out2Object = (JSONObject) new JSONParser().parse(out2);
				outObject.put("title", out2Object.get("title"));
				if (out2Object.get("description") != null) {
					outObject.put("description", out2Object.get("description"));
				}
				if (out2Object.get("depiction") != null) {
					outObject.put("depiction", out2Object.get("depiction"));
				}
				if (out2Object.get("coverage") != null) {
					outObject.put("coverage", out2Object.get("coverage"));
				}
				if (out2Object.get("temporal") != null) {
					outObject.put("temporal", out2Object.get("temporal"));
				}
				if (pretty) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(outObject.toString()).getAsJsonObject();
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					return Response.ok(gson.toJson(json)).build();
				} else {
					OUTSTRING = outObject.toString();
					if (acceptEncoding.contains("gzip")) {
						// set outputstream
						final String OUTSTRING_FINAL = OUTSTRING;
						StreamingOutput stream;
						stream = new StreamingOutput() {
							@Override
							public void write(OutputStream output) throws IOException, WebApplicationException {
								try {
									output = GZIP(OUTSTRING_FINAL, output);
								} catch (Exception e) {
									System.out.println(e.toString());
								}
							}
						};
						return Response.ok(stream).header("Content-Type", "application/json;charset=UTF-8").header("Content-Encoding", "gzip").build();
					} else {
						return Response.ok(OUTSTRING).header("Content-Type", "application/json;charset=UTF-8").build();
					}
				}
			} else if (acceptHeader.contains("application/rdf+json")) {
				query = rdf.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
				result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
				predicates = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
				objects = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
				if (result.size() < 1) {
					throw new ResourceNotAvailableException("resource " + itemid + " is not available");
				}
				for (int i = 0; i < predicates.size(); i++) {
					rdf.setModelTriple(datasetBody, predicates.get(i), objects.get(i));
				}
				return Response.ok(rdf.getModel("RDF/JSON")).header("Content-Type", "application/json;charset=UTF-8").build();
			} else if (acceptHeader.contains("text/html")) {
				String out = Transformer.dataset_GET(rdf.getModel("RDF/JSON"), itemid).toJSONString();
				JSONObject outObject = (JSONObject) new JSONParser().parse(out);
				RDF rdf2 = new RDF(ConfigProperties.getPropertyParam("host"));
				String query2 = rdf2.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
				List<BindingSet> result2 = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query2);
				List<String> predicates2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "p");
				List<String> objects2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "o");
				if (result2.size() < 1) {
					throw new ResourceNotAvailableException("resource " + itemid + " is not available");
				}
				for (int i = 0; i < predicates2.size(); i++) {
					rdf2.setModelTriple(datasetBody, predicates2.get(i), objects2.get(i));
				}
				String out2 = Transformer.datasetBody_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
				JSONObject out2Object = (JSONObject) new JSONParser().parse(out2);
				outObject.put("title", out2Object.get("title"));
				if (out2Object.get("description") != null) {
					outObject.put("description", out2Object.get("description"));
				}
				if (out2Object.get("depiction") != null) {
					outObject.put("depiction", out2Object.get("depiction"));
				}
				if (out2Object.get("coverage") != null) {
					outObject.put("coverage", out2Object.get("coverage"));
				}
				if (out2Object.get("temporal") != null) {
					outObject.put("temporal", out2Object.get("temporal"));
				}
				if (pretty) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(outObject.toString()).getAsJsonObject();
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					return Response.ok(gson.toJson(json)).header("Content-Type", "application/json;charset=UTF-8").build();
				} else {
					OUTSTRING = outObject.toString();
					if (acceptEncoding.contains("gzip")) {
						// set outputstream
						final String OUTSTRING_FINAL = OUTSTRING;
						StreamingOutput stream;
						stream = new StreamingOutput() {
							@Override
							public void write(OutputStream output) throws IOException, WebApplicationException {
								try {
									output = GZIP(OUTSTRING_FINAL, output);
								} catch (Exception e) {
									System.out.println(e.toString());
								}
							}
						};
						return Response.ok(stream).header("Content-Type", "application/json;charset=UTF-8").header("Content-Encoding", "gzip").build();
					} else {
						return Response.ok(OUTSTRING).header("Content-Type", "application/json;charset=UTF-8").build();
					}
				}
			} else if (acceptHeader.contains("application/xml")) {
				query = rdf.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
				result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
				predicates = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
				objects = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
				if (result.size() < 1) {
					throw new ResourceNotAvailableException("resource " + itemid + " is not available");
				}
				for (int i = 0; i < predicates.size(); i++) {
					rdf.setModelTriple(datasetBody, predicates.get(i), objects.get(i));
				}
				return Response.ok(rdf.getModel("RDF/XML")).build();
			} else if (acceptHeader.contains("application/rdf+xml")) {
				query = rdf.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
				result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
				predicates = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
				objects = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
				if (result.size() < 1) {
					throw new ResourceNotAvailableException("resource " + itemid + " is not available");
				}
				for (int i = 0; i < predicates.size(); i++) {
					rdf.setModelTriple(datasetBody, predicates.get(i), objects.get(i));
				}
				return Response.ok(rdf.getModel("RDF/XML")).build();
			} else if (acceptHeader.contains("text/turtle")) {
				query = rdf.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
				result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
				predicates = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
				objects = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
				if (result.size() < 1) {
					throw new ResourceNotAvailableException("resource " + itemid + " is not available");
				}
				for (int i = 0; i < predicates.size(); i++) {
					rdf.setModelTriple(datasetBody, predicates.get(i), objects.get(i));
				}
				return Response.ok(rdf.getModel("Turtle")).build();
			} else if (acceptHeader.contains("text/n3")) {
				query = rdf.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
				result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
				predicates = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
				objects = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
				if (result.size() < 1) {
					throw new ResourceNotAvailableException("resource " + itemid + " is not available");
				}
				for (int i = 0; i < predicates.size(); i++) {
					rdf.setModelTriple(datasetBody, predicates.get(i), objects.get(i));
				}
				return Response.ok(rdf.getModel("N-Triples")).build();
			} else if (acceptHeader.contains("application/ld+json")) {
				query = rdf.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
				result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
				predicates = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
				objects = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
				if (result.size() < 1) {
					throw new ResourceNotAvailableException("resource " + itemid + " is not available");
				}
				for (int i = 0; i < predicates.size(); i++) {
					rdf.setModelTriple(datasetBody, predicates.get(i), objects.get(i));
				}
				return Response.ok(rdf.getModel("JSON-LD")).build();
			} else {
				String out = Transformer.dataset_GET(rdf.getModel("RDF/JSON"), itemid).toJSONString();
				JSONObject outObject = (JSONObject) new JSONParser().parse(out);
				RDF rdf2 = new RDF(ConfigProperties.getPropertyParam("host"));
				String query2 = rdf2.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
				List<BindingSet> result2 = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query2);
				List<String> predicates2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "p");
				List<String> objects2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "o");
				if (result2.size() < 1) {
					throw new ResourceNotAvailableException("resource " + itemid + " is not available");
				}
				for (int i = 0; i < predicates2.size(); i++) {
					rdf2.setModelTriple(datasetBody, predicates2.get(i), objects2.get(i));
				}
				String out2 = Transformer.datasetBody_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
				JSONObject out2Object = (JSONObject) new JSONParser().parse(out2);
				outObject.put("title", out2Object.get("title"));
				if (out2Object.get("description") != null) {
					outObject.put("description", out2Object.get("description"));
				}
				if (out2Object.get("depiction") != null) {
					outObject.put("depiction", out2Object.get("depiction"));
				}
				if (out2Object.get("coverage") != null) {
					outObject.put("coverage", out2Object.get("coverage"));
				}
				if (out2Object.get("temporal") != null) {
					outObject.put("temporal", out2Object.get("temporal"));
				}
				if (pretty) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(outObject.toString()).getAsJsonObject();
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					return Response.ok(gson.toJson(json)).header("Content-Type", "application/json;charset=UTF-8").build();
				} else {
					OUTSTRING = outObject.toString();
					if (acceptEncoding.contains("gzip")) {
						// set outputstream
						final String OUTSTRING_FINAL = OUTSTRING;
						StreamingOutput stream;
						stream = new StreamingOutput() {
							@Override
							public void write(OutputStream output) throws IOException, WebApplicationException {
								try {
									output = GZIP(OUTSTRING_FINAL, output);
								} catch (Exception e) {
									System.out.println(e.toString());
								}
							}
						};
						return Response.ok(stream).header("Content-Type", "application/json;charset=UTF-8").header("Content-Encoding", "gzip").build();
					} else {
						return Response.ok(OUTSTRING).header("Content-Type", "application/json;charset=UTF-8").build();
					}
				}
			}
		} catch (Exception e) {
			if (e.toString().contains("ResourceNotAvailableException")) {
				return Response.status(Response.Status.NOT_FOUND).entity(Logging.getMessageJSON(e, "v1.rest.DatasetsResource"))
						.header("Content-Type", "application/json;charset=UTF-8").build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.DatasetsResource"))
						.header("Content-Type", "application/json;charset=UTF-8").build();
			}
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response postDataset(String json) throws IOException, JDOMException, ConfigException, ParserConfigurationException, TransformerException {
		try {
			// get dataset
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
			String datasetBody = (String) jsonObject.get("dataset");
			// validate
			String token = ValidateJSONObject.validateDataset(json);
			// set identifier
			String itemID = HashID.getHASHID();
			// create triples
			json = Transformer.dataset_POST(json, itemID);
			String triples = createDataset(itemID, token);
			// input triples
			RDF4J_20.inputRDFfromRDFJSONString(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), json);
			RDF4J_20.SPARQLupdate(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), triples);
			// get result als json
			RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
			String query = rdf.getPREFIXSPARQL() + "SELECT * WHERE { lsdh-d:" + itemID + " ?p ?o }";
			List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
			List<String> predicates = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
			List<String> objects = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
			if (result.size() < 1) {
				throw new ResourceNotAvailableException("resource " + itemID + " is not available");
			}
			for (int i = 0; i < predicates.size(); i++) {
				rdf.setModelTriple("lsdh-d:" + itemID, predicates.get(i), objects.get(i));
			}
			String out = Transformer.dataset_GET(rdf.getModel("RDF/JSON"), itemID).toJSONString();
			JSONObject outObject = (JSONObject) new JSONParser().parse(out);
			RDF rdf2 = new RDF(ConfigProperties.getPropertyParam("host"));
			String query2 = rdf2.getPREFIXSPARQL() + "SELECT * WHERE { <" + datasetBody + "> ?p ?o }";
			List<BindingSet> result2 = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query2);
			List<String> predicates2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "p");
			List<String> objects2 = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result2, "o");
			if (result2.size() < 1) {
				throw new ResourceNotAvailableException("resource " + itemID + " is not available");
			}
			for (int i = 0; i < predicates2.size(); i++) {
				rdf2.setModelTriple(datasetBody, predicates2.get(i), objects2.get(i));
			}
			String out2 = Transformer.datasetBody_GET(rdf2.getModel("RDF/JSON"), datasetBody).toJSONString();
			JSONObject out2Object = (JSONObject) new JSONParser().parse(out2);
			outObject.put("title", out2Object.get("title"));
			if (out2Object.get("description") != null) {
				outObject.put("description", out2Object.get("description"));
			}
			if (out2Object.get("depiction") != null) {
				outObject.put("depiction", out2Object.get("depiction"));
			}
			if (out2Object.get("coverage") != null) {
				outObject.put("coverage", out2Object.get("coverage"));
			}
			if (out2Object.get("temporal") != null) {
				outObject.put("temporal", out2Object.get("temporal"));
			}
			return Response.status(Response.Status.CREATED).entity(outObject.toJSONString()).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.DatasetsResource"))
					.header("Content-Type", "application/json;charset=UTF-8").build();
		}
	}

	private static String createDataset(String itemid, String token) throws ConfigException, IOException {
		RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
		String prefixes = rdf.getPREFIXSPARQL();
		String triples = prefixes + "INSERT DATA { ";
		triples += "lsdh-d:" + itemid + " a lsdh:Dataset . ";
		triples += "lsdh-d:" + itemid + " a oa:Annotation . ";
		triples += "lsdh-d:" + itemid + " dcterms:identifier \"" + itemid + "\" . ";
		triples += "lsdh-d:" + itemid + " lsdh:token \"" + token + "\" . ";
		triples += " }";
		return triples;
	}

	private static OutputStream GZIP(String input, OutputStream baos) throws IOException {
		try (GZIPOutputStream gzos = new GZIPOutputStream(baos)) {
			gzos.write(input.getBytes("UTF-8"));
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return baos;
	}

}
