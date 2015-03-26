package org.aksw.cubeqa.property.scorer;

import static org.junit.Assert.*;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.scorer.StringScorer;
import org.junit.Test;

public class StringScorerTest
{

	@Test public void testScore()
	{
		Cube cube = Cube.getInstance("finland-aid");
		{
			ObjectPropertyScorer scorer = new ObjectPropertyScorer(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-recipient-country"));
			assertFalse(scorer.score("per").isPresent()); // "per" shouln't be mapped to Peru
			assertEquals(scorer.unsafeScore("Afganistan").get().value,"https://openspending.org/finland-aid/recipient-country/af");
		}
		{
			StringScorer scorer= new StringScorer(cube.properties.get("http://linkedspending.aksw.org/ontology/finland-aid-channel-of-delivery-name"));
			System.out.println(scorer.unsafeScore("Finnfund"));
			System.out.println(scorer.unsafeScore("Fida International"));
			System.out.println(scorer.unsafeScore("Fida"));
		}
	}

}
