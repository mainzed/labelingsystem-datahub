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

@Path("/projects")
public class ProjectsResource {

	@GET
	@Produces({"application/json;charset=UTF-8", "application/xml;charset=UTF-8", "application/rdf+xml;charset=UTF-8", "text/turtle;charset=UTF-8", "text/n3;charset=UTF-8", "application/ld+json;charset=UTF-8", "application/rdf+json;charset=UTF-8"})
	public Response getProjects(
			@HeaderParam("Accept") String acceptHeader,
			@HeaderParam("Accept-Encoding") String acceptEncoding,
			@QueryParam("pretty") boolean pretty,
			@QueryParam("sort") String sort,
			@QueryParam("fields") String fields,
			@QueryParam("offset") String offset,
			@QueryParam("limit") String limit,
			@QueryParam("creator") String creator,
			@QueryParam("releaseType") String releaseType,
			@QueryParam("draft") String draft,
			@QueryParam("statistics") String statistics,
			@QueryParam("creatorInfo") String creatorInfo)
			throws IOException, JDOMException, ConfigException, ParserConfigurationException, TransformerException {
		try {
			String OUTSTRING = "";
			// QUERY STRING
			RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
			String query = rdf.getPREFIXSPARQL();
			query += "SELECT * WHERE { "
					+ "?s ?p ?o . "
					+ "?s a lsdh:Project . "
					+ "?s dcterms:identifier ?identifier . ";
			// FILTERING
			/*if (draft == null) {
				query += "?s ls:hasReleaseType ls:Public . ";
			}
			if (creator != null) {
				query += "FILTER(?creator=\"" + creator + "\") ";
			}
			if (releaseType != null) {
				query += "FILTER(?releaseType=<" + rdf.getPrefixItem("ls:" + releaseType) + ">) ";
			}*/
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
					JSONObject tmp = Transformer.project_GET(hh, h);
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
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.ProjectsResource"))
						.header("Content-Type", "application/json;charset=UTF-8").build();
			}
		}
	}
	
	@GET
	@Path("/{project}")
	@Produces({"application/json;charset=UTF-8", "application/xml;charset=UTF-8", "application/rdf+xml;charset=UTF-8", "text/turtle;charset=UTF-8", "text/n3;charset=UTF-8", "application/ld+json;charset=UTF-8", "application/rdf+json;charset=UTF-8"})
	public Response getProject(@PathParam("project") String itemid, @HeaderParam("Accept") String acceptHeader, @QueryParam("statistics") String statistics, @QueryParam("creatorInfo") String creatorInfo, @QueryParam("pretty") boolean pretty, @HeaderParam("Accept-Encoding") String acceptEncoding) throws IOException, JDOMException, ParserConfigurationException, TransformerException {
		try {
			String OUTSTRING = "";
			RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
			String item = "lsdh-p";
			String query = rdf.getPREFIXSPARQL() + "SELECT * WHERE { lsdh-p:" + itemid + " ?p ?o }";
			List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
			List<String> predicates = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
			List<String> objects = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
			if (result.size() < 1) {
				throw new ResourceNotAvailableException("resource " + itemid + " is not available");
			}
			for (int i = 0; i < predicates.size(); i++) {
				rdf.setModelTriple(item + ":" + itemid, predicates.get(i), objects.get(i));
			}
			if (acceptHeader.contains("application/json")) {
				String out = Transformer.project_GET(rdf.getModel("RDF/JSON"), itemid).toJSONString();
				if (pretty) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(out).getAsJsonObject();
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					return Response.ok(gson.toJson(json)).build();
				} else {
					OUTSTRING = out.toString();
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
				String out = Transformer.project_GET(rdf.getModel("RDF/JSON"), itemid).toJSONString();
				if (pretty) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(out).getAsJsonObject();
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					return Response.ok(gson.toJson(json)).header("Content-Type", "application/json;charset=UTF-8").build();
				} else {
					OUTSTRING = out.toString();
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
			} else {
				String out = Transformer.project_GET(rdf.getModel("RDF/JSON"), itemid).toJSONString();
				if (pretty) {
					JsonParser parser = new JsonParser();
					JsonObject json = parser.parse(out).getAsJsonObject();
					Gson gson = new GsonBuilder().setPrettyPrinting().create();
					return Response.ok(gson.toJson(json)).header("Content-Type", "application/json;charset=UTF-8").build();
				} else {
					OUTSTRING = out.toString();
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
				return Response.status(Response.Status.NOT_FOUND).entity(Logging.getMessageJSON(e, "v1.rest.ProjectsResource"))
						.header("Content-Type", "application/json;charset=UTF-8").build();
			} else {
				return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.ProjectsResource"))
						.header("Content-Type", "application/json;charset=UTF-8").build();
			}
		}
	}

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON + ";charset=UTF-8")
	public Response postProject(String json) throws IOException, JDOMException, ConfigException, ParserConfigurationException, TransformerException {
		try {
			// validate
			String token = ValidateJSONObject.validateProject(json);
			// set identifier
			String itemID = HashID.getHASHID();
			// create triples
			json = Transformer.project_POST(json, itemID);
			String triples = createProject(itemID, token);
			// input triples
			RDF4J_20.inputRDFfromRDFJSONString(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), json);
			RDF4J_20.SPARQLupdate(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), triples);
			// get result als json
			RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
			String query = rdf.getPREFIXSPARQL() + "SELECT * WHERE { lsdh-p:" + itemID + " ?p ?o }";
			List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
			List<String> predicates = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "p");
			List<String> objects = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "o");
			if (result.size() < 1) {
				throw new ResourceNotAvailableException("resource " + itemID + " is not available");
			}
			for (int i = 0; i < predicates.size(); i++) {
				rdf.setModelTriple("lsdh-p:" + itemID, predicates.get(i), objects.get(i));
			}
			String out = Transformer.project_GET(rdf.getModel("RDF/JSON"), itemID).toJSONString();
			return Response.status(Response.Status.CREATED).entity(out).build();
		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(Logging.getMessageJSON(e, "v1.rest.ProjectsResource"))
					.header("Content-Type", "application/json;charset=UTF-8").build();
		}
	}

	private static String createProject(String itemid, String token) throws ConfigException, IOException
	{
		RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
		String prefixes = rdf.getPREFIXSPARQL();
		String triples = prefixes + "INSERT DATA { ";
		triples += "lsdh-p:" + itemid + " a lsdh:Project . ";
		triples += "lsdh-p:" + itemid + " a void:Dataset . ";
		triples += "lsdh-p:" + itemid + " dcterms:identifier \"" + itemid + "\" . ";
		triples += "lsdh-p:" + itemid + " lsdh:token \"" + token + "\" . ";
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
