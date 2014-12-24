package org.aksw.autosparql.cube.property.scorer;

import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.tbsl.algorithm.util.Similarity;

public class StringScorer extends DatatypePropertyScorer
{
	private static final double	THRESHOLD	= 0.8;

	public StringScorer(ComponentProperty property)
	{
		super(property);
	}

	@Override public double unsafeScore(String value)
	{
		// TODO: fuzzy matching, wordnet,solr
		double cs = countScore(values.count(value));
		if(cs!=0) {return cs;}
		double score = 0;

		for(String s: values.elementSet())
		{
			double sim = Similarity.getSimilarity(value, s);
			if(sim<THRESHOLD) continue;
			score = Math.max(score, sim*countScore(values.count(s)));
		}
		return score;
//		values.elementSet().stream().map(s->Similarity.getSimilarity(value, s)).filter(sim->sim>THRESHOLD)
//		.map(sim->sim*countScore(values.count(s),maxCount));
	}
}