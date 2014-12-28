package org.aksw.autosparql.cube.property.scorer;

import java.util.Optional;
import org.aksw.autosparql.cube.property.ComponentProperty;
import com.google.common.collect.Range;
import com.hp.hpl.jena.query.QuerySolution;

/** tests if a number is included in the range. */
public class NumericScorer extends Scorer
{
	final Range<Double> range;

	public NumericScorer(ComponentProperty property)
	{
		super(property);
		String query = "select (min(xsd:double(?d)) as ?min) (max(xsd:double(?d)) as ?max) {?o a qb:Observation. ?o qb:dataSet <"+property.cube.uri+">."
				+ "?o <"+property.uri+"> ?d.}";
		QuerySolution qs = property.cube.sparql.select(query).next();
		range = Range.closed(qs.get("min").asLiteral().getDouble(), qs.get("max").asLiteral().getDouble());
	}

	@Override public Optional<ScoreResult> unsafeScore(String value)
	{
		throw new IllegalArgumentException("not implemented");
//		double d = Double.valueOf(value);
//		return range.contains(d)?1:0;
	}
}