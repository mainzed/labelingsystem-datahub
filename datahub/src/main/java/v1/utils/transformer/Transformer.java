package v1.utils.transformer;

import v1.utils.config.ConfigProperties;
import rdf.RDF;
import exceptions.TransformRdfToApiJsonException;
import java.io.IOException;
import java.util.List;
import org.eclipse.rdf4j.query.BindingSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import rdf.RDF4J_20;
import v1.utils.trigger.Triggers;

public class Transformer {

	public static String project_POST(String json, String id) throws IOException, ParseException {
		//init
		RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
		String item = "";
		// parse json
		JSONObject rdfObject = new JSONObject();
		JSONObject object = (JSONObject) new JSONParser().parse(json);
		// change items
		item = (String) object.get("title");
		if (item != null) {
			object.remove("title");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "literal");
			itemObject.put("value", item);
			newArray.add(itemObject);
			object.put(rdf.getPrefixItem("dcterms:title"), newArray);
		}
		item = (String) object.get("publisher");
		if (item != null) {
			object.remove("publisher");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", item);
			newArray.add(itemObject);
			object.put(rdf.getPrefixItem("dcterms:publisher"), newArray);
		}
		item = (String) object.get("creator");
		if (item != null) {
			object.remove("creator");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "literal");
			itemObject.put("value", item);
			newArray.add(itemObject);
			object.put(rdf.getPrefixItem("dcelements:creator"), newArray);
		}
		item = (String) object.get("description");
		if (item != null) {
			object.remove("description");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "literal");
			itemObject.put("value", item);
			newArray.add(itemObject);
			object.put(rdf.getPrefixItem("dcterms:description"), newArray);
		}
		item = (String) object.get("license");
		if (item != null) {
			object.remove("license");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", item);
			newArray.add(itemObject);
			object.put(rdf.getPrefixItem("dcterms:license"), newArray);
		}
		item = (String) object.get("dump");
		if (item != null) {
			object.remove("dump");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", item);
			newArray.add(itemObject);
			object.put(rdf.getPrefixItem("void:dataDump"), newArray);
		}
		item = (String) object.get("sparql");
		if (item != null) {
			object.remove("sparql");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", item);
			newArray.add(itemObject);
			object.put(rdf.getPrefixItem("dcat:accessURL"), newArray);
		}
		// delete items
		object.remove("title");
		object.remove("publisher");
		object.remove("creator");
		object.remove("description");
		object.remove("license");
		object.remove("dump");
		object.remove("sparql");
		object.remove("token");
		object.remove("id");
		object.remove("datasets");
		// add object
		rdfObject.put(rdf.getPrefixItem("lsdh-p" + ":" + id), object);
		return rdfObject.toJSONString();
	}

	public static JSONObject project_GET(String json, String id) throws IOException, ParseException, TransformRdfToApiJsonException {
		JSONObject object = null;
		try {
			//init
			RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
			JSONArray array;
			// parse json
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
			jsonObject.put("project", jsonObject.remove(rdf.getPrefixItem("lsdh-p:" + id)));
			// get items
			object = (JSONObject) jsonObject.get(rdf.getPrefixItem("project"));
			// change dcterms:identifier
			array = (JSONArray) object.get(rdf.getPrefixItem("dcterms:identifier"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcterms:identifier"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("id", value);
				}
			}
			// change dcterms:title
			array = (JSONArray) object.get(rdf.getPrefixItem("dcterms:title"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcterms:title"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("title", value);
				}
			}
			// change dcterms:publisher
			array = (JSONArray) object.get(rdf.getPrefixItem("dcterms:publisher"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcterms:publisher"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("publisher", value);
				}
			}
			// change dcelements:creator
			array = (JSONArray) object.get(rdf.getPrefixItem("dcelements:creator"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcelements:creator"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("creator", value);
				}
			}
			// change dcterms:description
			array = (JSONArray) object.get(rdf.getPrefixItem("dcterms:description"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcterms:description"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("description", value);
				}
			}
			// change dcterms:license
			array = (JSONArray) object.get(rdf.getPrefixItem("dcterms:license"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcterms:license"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("license", value);
				}
			}
			// change void:dataDump
			array = (JSONArray) object.get(rdf.getPrefixItem("void:dataDump"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("void:dataDump"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("dump", value);
				}
			}
			// change void:dataDump
			array = (JSONArray) object.get(rdf.getPrefixItem("dcat:accessURL"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcat:accessURL"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("sparql", value);
				}
			}
			// get dataset count
			String query = rdf.getPREFIXSPARQL() + "SELECT (count(?d) as ?count) WHERE { ?d lsdh:project ?p. FILTER (?p = lsdh-p:" + id + ") }";
			List<BindingSet> result = RDF4J_20.SPARQLquery(ConfigProperties.getPropertyParam("repository"), ConfigProperties.getPropertyParam("ts_server"), query);
			List<String> count = RDF4J_20.getValuesFromBindingSet_ORDEREDLIST(result, "count");
			object.put("datasets", Integer.parseInt(count.get(0)));
			// delete items
			object.remove(rdf.getPrefixItem("rdf:type"));
			object.remove(rdf.getPrefixItem("dcterms:title"));
			object.remove(rdf.getPrefixItem("dcterms:publisher"));
			object.remove(rdf.getPrefixItem("dcelements:creator"));
			object.remove(rdf.getPrefixItem("dcterms:description"));
			object.remove(rdf.getPrefixItem("dcterms:license"));
			object.remove(rdf.getPrefixItem("dcterms:identifier"));
			object.remove(rdf.getPrefixItem("void:dataDump"));
			object.remove(rdf.getPrefixItem("dcat:accessURL"));
			object.remove(rdf.getPrefixItem("lsdh:token"));
		} catch (Exception e) {
			int errorLine = -1;
			for (StackTraceElement element : e.getStackTrace()) {
				errorLine = element.getLineNumber();
				if (element.getClassName().equals(Transformer.class.getName())) {
					break;
				}
			}
			throw new TransformRdfToApiJsonException(e.toString() + " in line " + String.valueOf(errorLine));
		}
		// return
		return object;
	}

	public static String dataset_POST(String json, String id) throws IOException, ParseException {
		//init
		RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
		String item = "";
		// parse json
		JSONObject rdfObject = new JSONObject();
		JSONObject object = (JSONObject) new JSONParser().parse(json);
		JSONObject objectDataset = new JSONObject();
		// change items
		item = (String) object.get("project");
		if (item != null) {
			object.remove("project");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", rdf.getPrefixItem("lsdh-p:" + item));
			newArray.add(itemObject);
			object.put(rdf.getPrefixItem("lsdh:project"), newArray);
		}
		item = (String) object.get("relation");
		if (item != null) {
			object.remove("relation");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", item);
			newArray.add(itemObject);
			object.put(rdf.getPrefixItem("lsdh:relation"), newArray);
		}
		item = (String) object.get("label");
		if (item != null) {
			object.remove("label");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", item);
			newArray.add(itemObject);
			object.put(rdf.getPrefixItem("oa:hasBody"), newArray);
		}
		item = (String) object.get("dataset");
		String targetString = item;
		if (item != null) {
			object.remove("dataset");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", item);
			newArray.add(itemObject);
			object.put(rdf.getPrefixItem("oa:hasTarget"), newArray);
			// set target class
			newArray = new JSONArray();
			itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", rdf.getPrefixItem("lsdh:Target"));
			newArray.add(itemObject);
			objectDataset.put(rdf.getPrefixItem("rdf:type"), newArray);
			rdfObject.put(targetString, objectDataset);
		}
		// target properties
		item = (String) object.get("title");
		if (item != null) {
			object.remove("title");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "literal");
			itemObject.put("value", item);
			newArray.add(itemObject);
			objectDataset.put(rdf.getPrefixItem("dcterms:title"), newArray);
			rdfObject.put(targetString, objectDataset);
		}
		item = (String) object.get("description");
		if (item != null) {
			object.remove("description");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "literal");
			itemObject.put("value", item);
			newArray.add(itemObject);
			objectDataset.put(rdf.getPrefixItem("dcterms:description"), newArray);
			rdfObject.put(targetString, objectDataset);
		}
		item = (String) object.get("depiction");
		if (item != null) {
			object.remove("depiction");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", item);
			newArray.add(itemObject);
			objectDataset.put(rdf.getPrefixItem("foaf:depiction"), newArray);
			rdfObject.put(targetString, objectDataset);
		}
		item = (String) object.get("type");
		if (item != null) {
			object.remove("type");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", rdf.getPrefixItem(item));
			newArray.add(itemObject);
			objectDataset.put(rdf.getPrefixItem("lsdh:type"), newArray);
			rdfObject.put(targetString, objectDataset);
		}
		item = (String) object.get("coverage");
		if (item != null) {
			object.remove("coverage");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", item);
			newArray.add(itemObject);
			objectDataset.put(rdf.getPrefixItem("dcterms:coverage"), newArray);
			rdfObject.put(targetString, objectDataset);
		}
		// trigger for lat and long
		if (item != null) {
			if (item.contains("geonames")) {
				JSONObject latlng = Triggers.getLatLngFromGeoNames(item);
				if (latlng.size() > 0) {
					JSONArray newArray = new JSONArray();
					JSONObject itemObject = new JSONObject();
					itemObject.put("type", "literal");
					itemObject.put("value", latlng.get("lat"));
					newArray.add(itemObject);
					objectDataset.put(rdf.getPrefixItem("geo:lat"), newArray);
					rdfObject.put(targetString, objectDataset);
					newArray = new JSONArray();
					itemObject = new JSONObject();
					itemObject.put("type", "literal");
					itemObject.put("value", latlng.get("lng"));
					newArray.add(itemObject);
					objectDataset.put(rdf.getPrefixItem("geo:long"), newArray);
					rdfObject.put(targetString, objectDataset);
				}
			}
		}
		item = (String) object.get("temporal");
		if (item != null) {
			object.remove("temporal");
			JSONArray newArray = new JSONArray();
			JSONObject itemObject = new JSONObject();
			itemObject.put("type", "uri");
			itemObject.put("value", item);
			newArray.add(itemObject);
			objectDataset.put(rdf.getPrefixItem("dcterms:temporal"), newArray);
			rdfObject.put(targetString, objectDataset);
		}
		// trigger for start and end
		if (item != null) {
			if (item.contains("chronontology")) {
				JSONObject beginend = Triggers.getBeginEndFromChronOntology(item);
				if (beginend.size() > 0) {
					JSONArray newArray = new JSONArray();
					JSONObject itemObject = new JSONObject();
					itemObject.put("type", "literal");
					itemObject.put("value", beginend.get("begin"));
					newArray.add(itemObject);
					objectDataset.put(rdf.getPrefixItem("prov:startedAtTime"), newArray);
					rdfObject.put(targetString, objectDataset);
					newArray = new JSONArray();
					itemObject = new JSONObject();
					itemObject.put("type", "literal");
					itemObject.put("value", beginend.get("end"));
					newArray.add(itemObject);
					objectDataset.put(rdf.getPrefixItem("prov:endedAtTime"), newArray);
					rdfObject.put(targetString, objectDataset);
				}
			}
		}
		if (item == null) {
			String begin = (String) object.get("begin");
			String end = (String) object.get("end");
			if (begin != null && end != null) {
				object.remove("begin");
				JSONArray newArray = new JSONArray();
				JSONObject itemObject = new JSONObject();
				itemObject.put("type", "literal");
				itemObject.put("value", begin);
				newArray.add(itemObject);
				objectDataset.put(rdf.getPrefixItem("prov:startedAtTime"), newArray);
				rdfObject.put(targetString, objectDataset);
				object.remove("end");
				newArray = new JSONArray();
				itemObject = new JSONObject();
				itemObject.put("type", "literal");
				itemObject.put("value", end);
				newArray.add(itemObject);
				objectDataset.put(rdf.getPrefixItem("prov:endedAtTime"), newArray);
				rdfObject.put(targetString, objectDataset);
			}
		}
		// delete items
		object.remove("project");
		object.remove("relation");
		object.remove("dataset");
		object.remove("label");
		object.remove("title");
		object.remove("description");
		object.remove("depiction");
		object.remove("coverage");
		object.remove("temporal");
		object.remove("begin");
		object.remove("end");
		object.remove("token");
		object.remove("id");
		object.remove("type");
		// add object
		rdfObject.put(rdf.getPrefixItem("lsdh-d:" + id), object);
		return rdfObject.toJSONString();
	}

	public static JSONObject dataset_GET(String json, String id) throws IOException, ParseException, TransformRdfToApiJsonException {
		JSONObject object = null;
		try {
			//init
			RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
			JSONArray array;
			// parse json
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
			jsonObject.put("dataset", jsonObject.remove(rdf.getPrefixItem("lsdh-d:" + id)));
			// get items
			object = (JSONObject) jsonObject.get(rdf.getPrefixItem("dataset"));
			// change dcterms:identifier
			array = (JSONArray) object.get(rdf.getPrefixItem("dcterms:identifier"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcterms:identifier"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("id", value);
				}
			}
			// change lsdh:project
			array = (JSONArray) object.get(rdf.getPrefixItem("lsdh:project"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("lsdh:project"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("project", value.replace(rdf.getPrefixItem("lsdh-p:"), ""));
				}
			}
			// change lsdh:relation
			array = (JSONArray) object.get(rdf.getPrefixItem("lsdh:relation"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("lsdh:relation"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("relation", value);
				}
			}
			// change oa:hasTarget
			array = (JSONArray) object.get(rdf.getPrefixItem("oa:hasTarget"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("oa:hasTarget"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("dataset", value);
				}
			}
			// change oa:hasBody
			array = (JSONArray) object.get(rdf.getPrefixItem("oa:hasBody"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("oa:hasBody"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("label", value);
				}
			}
			// delete items
			object.remove(rdf.getPrefixItem("rdf:type"));
			object.remove(rdf.getPrefixItem("dcterms:identifier"));
			object.remove(rdf.getPrefixItem("lsdh:project"));
			object.remove(rdf.getPrefixItem("lsdh:relation"));
			object.remove(rdf.getPrefixItem("oa:hasTarget"));
			object.remove(rdf.getPrefixItem("oa:hasBody"));
			object.remove(rdf.getPrefixItem("lsdh:token"));
		} catch (Exception e) {
			int errorLine = -1;
			for (StackTraceElement element : e.getStackTrace()) {
				errorLine = element.getLineNumber();
				if (element.getClassName().equals(Transformer.class.getName())) {
					break;
				}
			}
			throw new TransformRdfToApiJsonException(e.toString() + " in line " + String.valueOf(errorLine));
		}
		// return
		return object;
	}

	public static JSONObject target_GET(String json, String datasetBody) throws IOException, ParseException, TransformRdfToApiJsonException {
		JSONObject object = null;
		try {
			//init
			RDF rdf = new RDF(ConfigProperties.getPropertyParam("host"));
			JSONArray array;
			// parse json
			JSONObject jsonObject = (JSONObject) new JSONParser().parse(json);
			jsonObject.put("datasetBody", jsonObject.remove(datasetBody));
			// get items
			object = (JSONObject) jsonObject.get(rdf.getPrefixItem("datasetBody"));
			// change dcterms:title
			array = (JSONArray) object.get(rdf.getPrefixItem("dcterms:title"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcterms:title"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("title", value);
				}
			}
			// change dcterms:description
			array = (JSONArray) object.get(rdf.getPrefixItem("dcterms:description"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcterms:description"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("description", value);
				}
			}
			// change foaf:depiction
			array = (JSONArray) object.get(rdf.getPrefixItem("foaf:depiction"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("foaf:depiction"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("depiction", value);
				}
			}
			// change lsdh:type
			array = (JSONArray) object.get(rdf.getPrefixItem("lsdh:type"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("lsdh:type"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("type", value.replace(rdf.getPrefixItem("lsdh:"), "lsdh:"));
				}
			}
			// change dcterms:coverage
			array = (JSONArray) object.get(rdf.getPrefixItem("dcterms:coverage"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcterms:coverage"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("coverage", value);
				}
			}
			// change geo:lat
			array = (JSONArray) object.get(rdf.getPrefixItem("geo:lat"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("geo:lat"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("lat", value);
				}
			}
			// change geo:lat
			array = (JSONArray) object.get(rdf.getPrefixItem("geo:long"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("geo:long"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("lng", value);
				}
			}
			// change dcterms:temporal
			array = (JSONArray) object.get(rdf.getPrefixItem("dcterms:temporal"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("dcterms:temporal"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("temporal", value);
				}
			}
			// change prov:startedAtTime
			array = (JSONArray) object.get(rdf.getPrefixItem("prov:startedAtTime"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("prov:startedAtTime"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("begin", value);
				}
			}
			// change prov:endedAtTime 
			array = (JSONArray) object.get(rdf.getPrefixItem("prov:endedAtTime"));
			if (array != null && !array.isEmpty()) {
				for (Object element : array) {
					object.remove(rdf.getPrefixItem("prov:endedAtTime"));
					JSONObject obj = (JSONObject) element;
					String value = (String) obj.get("value");
					object.put("end", value);
				}
			}
			// delete items
			object.remove(rdf.getPrefixItem("rdf:type"));
			object.remove(rdf.getPrefixItem("dcterms:title"));
			object.remove(rdf.getPrefixItem("dcterms:description"));
			object.remove(rdf.getPrefixItem("foaf:depiction"));
			object.remove(rdf.getPrefixItem("dcterms:coverage"));
			object.remove(rdf.getPrefixItem("geo:lat"));
			object.remove(rdf.getPrefixItem("geo:long"));
			object.remove(rdf.getPrefixItem("dcterms:temporal"));
			object.remove(rdf.getPrefixItem("prov:startedAtTime"));
			object.remove(rdf.getPrefixItem("prov:endedAtTime"));
			object.remove(rdf.getPrefixItem("lsdh:type"));
		} catch (Exception e) {
			int errorLine = -1;
			for (StackTraceElement element : e.getStackTrace()) {
				errorLine = element.getLineNumber();
				if (element.getClassName().equals(Transformer.class.getName())) {
					break;
				}
			}
			throw new TransformRdfToApiJsonException(e.toString() + " in line " + String.valueOf(errorLine));
		}
		// return
		return object;
	}

}
