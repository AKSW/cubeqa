package org.aksw.autosparql.cube.property;

import static de.konradhoeffner.commons.Streams.stream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.extern.log4j.Log4j;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.CubeSparql;
import org.aksw.autosparql.cube.property.scorer.NumericScorer;
import org.aksw.autosparql.cube.property.scorer.ObjectPropertyScorer;
import org.aksw.autosparql.cube.property.scorer.Scorer;
import org.aksw.autosparql.cube.property.scorer.StringScorer;
import org.aksw.autosparql.cube.property.scorer.old.DateScorer;
import org.aksw.autosparql.cube.property.scorer.old.TemporalScorers;
import org.aksw.linkedspending.tools.DataModel;
import org.aksw.linkedspending.tools.DataModel.Owl;
import org.apache.lucene.search.spell.NGramDistance;
import org.apache.lucene.search.spell.StringDistance;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.XSD;
import de.konradhoeffner.commons.Pair;

/** Represents a component property of a RDF Data Cube.
 * Implements the Multiton Pattern, with the key being the combination of cube name and uri, because information about values is safed.
 * Immutable except for the labels.*/
@Log4j
public class ComponentProperty implements Serializable
{
	private static final long	serialVersionUID	= 5L;
	private static final AtomicInteger id = new AtomicInteger(0);
	private static final Map<Pair<String,String>,ComponentProperty> instances = new HashMap<>();
	private static final boolean MATCH_RANGE = true;
	private static final double	RANGE_LABEL_MULTIPLIER	= 0.5; // range labels may be less specific then the property name and thus get a lower score

	protected static transient StringDistance similarity = new NGramDistance();

	public final String var;

	public final String range;
	public final Set<String> rangeLabels = new HashSet<>();

	public final Cube cube;
	public final String uri;
	//	public final Domain domain;

	public final Set<String> labels;
//	public final PropertyType type;

	@NonNull public final Scorer scorer;

	//	static Domain propertyDomain(String propertyUri)
	//	{
	//		try
	//		{
	//			return Domain.valueOf(propertyUri.toUpperCase());
	//		}
	//		catch(IllegalArgumentException e) {return Domain.OTHER;}
	//	}

	String guessRange()
	{
		// todo implement
		return null;
	}

	public double match(String label)
	{
		//		System.out.println(labels);
		//		for(AbstractStringMetric sim: similarities)
		//		for(String l: labels) {System.out.println(l+" "+sim+" "+sim.getSimilarity(l, label));}
		//		OptionalDouble d = similarities.stream().flatMapToDouble(
		//		System.out.println("matching "+label+" to "+labels);

		OptionalDouble dlo = labels.stream().mapToDouble(l->similarity.getDistance(l,label)).max();
		double dl = dlo.isPresent()?dlo.getAsDouble():0;
		OptionalDouble dro = MATCH_RANGE? rangeLabels.stream().mapToDouble(l->similarity.getDistance(l,label)).max():OptionalDouble.of(0);
		// we only want objectproperties, so exclude xsd
		double dr = (dro.isPresent()&&!range.startsWith(XSD.getURI()))?dro.getAsDouble()*RANGE_LABEL_MULTIPLIER:0;

		return Math.max(dl, dr);
	}

	public static enum PropertyType {DIMENSION,MEASURE,ATTRIBUTE}
	public final PropertyType propertyType;

	private ComponentProperty(Cube cube, String uri)//, PropertyType type)
	{
		var = "v"+id.getAndIncrement();
		this.cube = cube;
		this.uri = uri;

		Set<String> labels = new HashSet<>();
		{
			labels.add(CubeSparql.suffix(uri));
			labels.addAll(stream(cube.sparql.select("select distinct(?l) {<"+uri+"> rdfs:label ?l}"))
					.map(qs->qs.get("l").asLiteral().getLexicalForm()).collect(Collectors.toSet()));
		}

		String propertyTypeQuery = "select ?p {?spec ?p <"+uri+">. filter(contains(str(?p),\"http://purl.org/linked-data/cube#\"))} limit 1";
//		System.out.println(propertyTypeQuery);
		String pt = cube.sparql.select(propertyTypeQuery).next().get("?p").asResource().getURI();
		switch(pt)
		{
			case "http://purl.org/linked-data/cube#measure": this.propertyType=PropertyType.MEASURE;break;
			case "http://purl.org/linked-data/cube#attribute": this.propertyType=PropertyType.ATTRIBUTE;break;
			case "http://purl.org/linked-data/cube#dimension": this.propertyType=PropertyType.DIMENSION;break;
			default:throw new RuntimeException("property type '"+pt+"' not recognized for property "+uri);
		}

		Set<String> types = new HashSet<>();
		{
			String typeQuery = "select distinct(?t) {<"+uri+"> a ?t."
					+ "FILTER (?type != <"+RDF.Property.getURI()+"> && ?type != <"+DataModel.DataCube.ComponentProperty.getURI()+">)}";
			types.addAll(stream(cube.sparql.select(typeQuery))
					.map(qs->qs.get("t").asResource().getURI()).collect(Collectors.toSet()));
			// easiest and fastest way to determine type of values, but isn't always modelled
			String rangeQuery = "select ?range ?label {<"+uri+"> rdfs:range ?range. OPTIONAL {?range rdfs:label ?label}}";
			ResultSet rs = cube.sparql.select(rangeQuery);
			Set<String> ranges = new HashSet<>();
			// very bad code TODO take multiple ranges into account, maybe create helper class "labelled uri"
			stream(rs).forEach(qs->
			{
				ranges.add(qs.get("range").asResource().getURI());
				if(qs.get("label")!=null) {rangeLabels.add(qs.get("label").asLiteral().getLexicalForm());}

			});
			this.range=ranges.isEmpty()?null:ranges.iterator().next();
			//			else {range = guessRange();}
			this.scorer = scorer(types);
		}

		this.labels=Collections.unmodifiableSet(labels);
		//		String query = "select distinct(?v) {?o a qb:Observation. ?o qb:dataSet <"+uri+">."
		//				+ "?o <"+uri+"> ?v. } limit 1000";
		//		CubeSparql.LINKED_SPENDING.select(query);
		//		this.domain=propertyDomain(propertyUri);
		//		this.type=type;
	}

	private Scorer scorer(Set<String> types)
	{
		for(String type: types)
		{
			switch(type)
			{
				case Owl.OBJECT_PROPERTY_URI:return new ObjectPropertyScorer(this);
				case Owl.DATATYPE_PROPERTY_URI:
				default:
			}
		}

		if(range!=null)
		{
			if(range.startsWith(XSD.getURI()))
			{
				Set<String> xsdNumeric = Arrays.asList(XSD.decimal,XSD.xbyte,XSD.xshort,XSD.xint,XSD.xlong,XSD.xfloat,XSD.xdouble)
						.stream().map(Resource::getURI).collect(Collectors.toSet());
				//				Set<String> xsdTemporal = Arrays.asList(XSD.date,XSD.dateTime,XSD.gDay,XSD.gMonth,XSD.gMonthDay,XSD.gYear,XSD.gYearMonth)
				//						.stream().map(Resource::getURI).collect(Collectors.toSet());
				// TODO: ensure right parsing of all xsd temporal types
				if(range.endsWith("Integer")||xsdNumeric.contains(range)) {return new NumericScorer(this);}
				if(range.equals(XSD.xstring.getURI())) {return new StringScorer(this);}
				//					if(r.equals(XSD.xboolean.getURI())) {return new BooleanScorer(this);} // TODO investigate do we need a boolean scorer?
				if(range.equals(XSD.gYear.getURI())) {return TemporalScorers.createYearScorer(this);}
				if(range.equals(XSD.date.getURI())||range.equals(XSD.dateTime.getURI())) {return new DateScorer(this);}

			} else
			{
				log.info("range "+range+": creating object property for "+this.uri);
				return new ObjectPropertyScorer(this);
			}
		}
		throw new RuntimeException("no scorer found for component "+this+" with range "+range);
		//		return scorer;
	}

	public static synchronized ComponentProperty getInstance(Cube cubeUri, String uri)//, String type)
	{
		Pair<String,String> key = new Pair<String,String>(cubeUri.uri, uri);
		ComponentProperty instance = instances.get(key);
		if(instance==null)
		{
			instance = new ComponentProperty(cubeUri, uri);//, PropertyType.ofRdfType(type));
		}
		return instance;
	}

	@Override public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cube == null) ? 0 : cube.hashCode());
		result = prime * result + ((uri == null) ? 0 : uri.hashCode());
		return result;
	}

	@Override public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		ComponentProperty other = (ComponentProperty) obj;
		if (cube == null)
		{
			if (other.cube != null) return false;
		}
		else if (!cube.equals(other.cube)) return false;
		if (uri == null)
		{
			if (other.uri != null) return false;
		}
		else if (!uri.equals(other.uri)) return false;
		return true;
	}

	@Override public String toString()
	{
		return uri;
	}

	public String shortName()
	{
		return cube.name+'-'+uri.substring(Math.max(uri.lastIndexOf('/'),uri.lastIndexOf('#'))+1);
	}

}