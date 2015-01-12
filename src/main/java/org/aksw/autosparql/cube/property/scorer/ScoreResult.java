package org.aksw.autosparql.cube.property.scorer;

import java.io.Serializable;
import org.aksw.autosparql.cube.property.ComponentProperty;
import org.aksw.autosparql.cube.restriction.Restriction;
import org.aksw.autosparql.cube.restriction.UriRestriction;
import org.aksw.autosparql.cube.restriction.ValueRestriction;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class ScoreResult implements Serializable
{
	public final ComponentProperty property;
	public final String value;
	public final double score;

	public Restriction toRestriction()
	{
		if(property.scorer instanceof ObjectPropertyScorer)
		{
			return new UriRestriction(property, value);
		}
//		if(property.scorer instanceof DatatypePropertyScorer)
//		{
			return new ValueRestriction(property, value);
//		}

	}
}