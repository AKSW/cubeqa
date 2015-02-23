package org.aksw.cubeqa.property.scorer;

import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.scorer.NumericScorer;
import org.junit.Test;

public class NumericScorerTest
{

	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		NumericScorer scorer = new NumericScorer(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-amount"));
		System.out.println(scorer.unsafeScore("0"));
		System.out.println(scorer.unsafeScore("180000"));
		System.out.println(scorer.unsafeScore("4312"));
//		assertTrue(scorer.score("https://openspending.org/finland-aid/recipient-country/et")>0.6);
	}

}