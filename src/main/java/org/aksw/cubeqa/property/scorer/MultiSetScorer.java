package org.aksw.cubeqa.property.scorer;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;
import org.aksw.cubeqa.property.ComponentProperty;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.RDFNode;

@Slf4j
public abstract class MultiSetScorer extends Scorer
{
	final protected Multiset<String> values = HashMultiset.create();
//	final protected HashMap<String,RDFNode> valueToNode = new HashMap<>();
	final protected int maxCount;

	private static final long	serialVersionUID	= 1L;

	public MultiSetScorer(ComponentProperty property, Function<RDFNode,Set<String>> f)
	{
		super(property);
		ResultSet rs = queryValues();
		while(rs.hasNext())
		{
			QuerySolution qs = rs.next();
			RDFNode node = qs.get("value");
			f.apply(node).forEach(s->
			{
//				valueToNode.put(s, node);
				values.add(s, qs.get("cnt").asLiteral().getInt());
			});
		}

	Optional<Integer> max = values.elementSet().stream().map(s->values.count(s)).max(Integer::compare);
	if(!max.isPresent())
	{
		log.warn("no values for property "+property+": "+values);
		maxCount=0;
	}
	else
	{
		maxCount = max.get();
	}
	}


//	protected double countScore(int count)
//	{
//		// +1 to prevent div by 0 the nearer the score to the max, the higher the value, but don't fall of too steep so use log.
//		if(count==0) return 0;
//		return Math.sqrt(Math.log(count+1)/Math.log(maxCount+1)); // ad hoc, sqrt is to have a less steep falloff
//	}

}