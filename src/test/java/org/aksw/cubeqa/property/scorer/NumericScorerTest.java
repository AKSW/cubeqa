package org.aksw.cubeqa.property.scorer;

import static org.junit.Assert.*;
import org.aksw.cubeqa.Cube;
import org.junit.Test;

public class NumericScorerTest
{

	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		NumericScorer scorer = new NumericScorer(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-amount"));
		assertEquals(scorer.score("0").get().score,1,0);
		assertEquals(scorer.score("180000").get().score,1,0);
		assertEquals(scorer.score("4312").get().score,1,0);
		assertEquals(scorer.score("123456789").get().score,0,0);
	}

}