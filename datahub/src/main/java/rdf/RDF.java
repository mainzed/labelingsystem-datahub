package rdf;

import com.github.jsonldjava.jena.JenaJSONLD;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import exceptions.RdfException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

/**
 * CLASS for set up a RDF graph and export it
 *
 * @author Florian Thiery M.Sc.
 * @author i3mainz - Institute for Spatial Information and Surveying Technology
 * @version 27.06.2015
 */
public class RDF {

	private Model model = null;
	private final String PREFIX_LSDH = "http://labeling.link/docs/ls-dh/core#";
	private final String PREFIX_LSDH_PROJECT = "http://143.93.114.135/datahub/projects/";
	private final String PREFIX_LSDH_DATASET = "http://143.93.114.135/datahub/datasets/";
	private final String PREFIX_VOID = "http://rdfs.org/ns/void#";
	private final String PREFIX_DCTERMS = "http://purl.org/dc/terms/";
	private final String PREFIX_DCAT = "http://www.w3.org/ns/dcat#";
	private final String PREFIX_OA = "http://www.w3.org/ns/oa#";
	private final String PREFIX_RDF = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	private final String PREFIX_FOAF = "http://xmlns.com/foaf/0.1/";
	private String PREFIXSPARQL = ""
			+ "PREFIX lsdh: <" + PREFIX_LSDH + "> "
			+ "PREFIX lsdh-p: <" + PREFIX_LSDH_PROJECT + "> "
			+ "PREFIX lsdh-d: <" + PREFIX_LSDH_DATASET + "> "
			+ "PREFIX void: <" + PREFIX_VOID + "> "
			+ "PREFIX dcterms: <" + PREFIX_DCTERMS + "> "
			+ "PREFIX dcat: <" + PREFIX_DCAT + "> "
			+ "PREFIX oa: <" + PREFIX_OA + "> "
			+ "PREFIX rdf: <" + PREFIX_RDF + "> "
			+ "PREFIX foaf: <" + PREFIX_FOAF + "> ";

	public RDF(String HOST) throws IOException {
		model = ModelFactory.createDefaultModel();
		model.setNsPrefix("lsdh", PREFIX_LSDH);
		model.setNsPrefix("lsdh-p", PREFIX_LSDH_PROJECT);
		model.setNsPrefix("lsdh-d", PREFIX_LSDH_DATASET);
		model.setNsPrefix("void", PREFIX_VOID);
		model.setNsPrefix("dcterms", PREFIX_DCTERMS);
		model.setNsPrefix("dcat", PREFIX_DCAT);
		model.setNsPrefix("oa", PREFIX_OA);
		model.setNsPrefix("rdf", PREFIX_RDF);
		model.setNsPrefix("foaf", PREFIX_FOAF);
	}

	public Model getModelObject() {
		return model;
	}

	public String getPrefixItem(String shortDesc) {
		if (shortDesc.startsWith("lsdh:")) {
			return shortDesc.replace("lsdh:", PREFIX_LSDH);
		} else if (shortDesc.startsWith("lsdh-p:")) {
			return shortDesc.replace("lsdh-p:", PREFIX_LSDH_PROJECT);
		} else if (shortDesc.startsWith("lsdh-d:")) {
			return shortDesc.replace("lsdh-d:", PREFIX_LSDH_DATASET);
		} else if (shortDesc.startsWith("void:")) {
			return shortDesc.replace("void:", PREFIX_VOID);
		} else if (shortDesc.startsWith("dcterms:")) {
			return shortDesc.replace("dcterms:", PREFIX_DCTERMS);
		} else if (shortDesc.startsWith("dcat:")) {
			return shortDesc.replace("dcat:", PREFIX_DCAT);
		} else if (shortDesc.startsWith("oa:")) {
			return shortDesc.replace("oa:", PREFIX_OA);
		} else if (shortDesc.startsWith("rdf:")) {
			return shortDesc.replace("rdf:", PREFIX_RDF);
		} else if (shortDesc.startsWith("foaf:")) {
			return shortDesc.replace("foaf:", PREFIX_FOAF);
		} else {
			return shortDesc;
		}
	}

	public String getPREFIXSPARQL() {
		return PREFIXSPARQL;
	}

	public void setModelLiteral(String subject, String predicate, String object) throws RdfException {
		try {
			Resource s = model.createResource(getPrefixItem(subject));
			Property p = model.createProperty(getPrefixItem(predicate));
			Literal o = model.createLiteral(object);
			model.add(s, p, o);
		} catch (Exception e) {
			throw new RdfException(e.getMessage());
		}
	}

	public void setModelLiteralLanguage(String subject, String predicate, String object, String lang) throws RdfException {
		try {
			Resource s = model.createResource(getPrefixItem(subject));
			Property p = model.createProperty(getPrefixItem(predicate));
			Literal o = model.createLiteral(object, lang);
			model.add(s, p, o);
		} catch (Exception e) {
			throw new RdfException(e.getMessage());
		}
	}

	public void setModelURI(String subject, String predicate, String object) throws RdfException {
		try {
			Resource s = model.createResource(getPrefixItem(subject));
			Property p = model.createProperty(getPrefixItem(predicate));
			Resource o = model.createResource(getPrefixItem(object));
			model.add(s, p, o);
		} catch (Exception e) {
			throw new RdfException(e.getMessage());
		}
	}

	public void setModelTriple(String subject, String predicate, String object) throws RdfException {
		try {
			if (object.startsWith("http://") || object.contains("mailto")) {
				setModelURI(subject, predicate, object);
			} else if (object.contains("@")) {
				String literalLanguage[] = object.split("@");
				setModelLiteralLanguage(subject, predicate, literalLanguage[0].replaceAll("\"", ""), literalLanguage[1]);
			} else {
				setModelLiteral(subject, predicate, object.replaceAll("\"", ""));
			}
		} catch (Exception e) {
			throw new RdfException(e.getMessage());
		}
	}

	public String getModel() throws RdfException {
		try {
			JenaJSONLD.init();
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			model.write(o, "RDF/XML");
			model.removeAll();
			return o.toString("UTF-8");
		} catch (Exception e) {
			throw new RdfException(e.getMessage());
		}
	}

	public String getModel(String format) throws UnsupportedEncodingException, RdfException {
		// https://jena.apache.org/documentation/io/rdf-output.html#jena_model_write_formats
		try {
			JenaJSONLD.init();
			ByteArrayOutputStream o = new ByteArrayOutputStream();
			model.write(o, format);
			model.removeAll();
			return o.toString("UTF-8");
		} catch (Exception e) {
			throw new RdfException(e.getMessage());
		}
	}

}
