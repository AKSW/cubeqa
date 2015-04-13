package org.aksw.cubeqa.detector;

import static org.junit.Assert.*;
import java.util.regex.Pattern;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.detector.TopDetector;
import org.junit.Test;

public class TopDetectorTest
{
	/** uses CubeTemplateFragment.toString to test more easily, may break if CubeTemplateFragment.toString changes*/
	@Test public void testDetect()
	{
		Cube cube = Cube.FINLAND_AID;
		{
			String ds = TopDetector.INSTANCE.detect(cube,"top 5 amounts").toString();
			assertTrue(Pattern.matches("(?i).*order by DESC\\(\\?v[0-9]+\\) limit 5.*", ds));
			assertTrue(ds.contains("http://linkedspending.aksw.org/ontology/finland-aid-amount "));
		}
		{
			String ds = TopDetector.INSTANCE.detect(cube,"7 lowest extended amounts").toString();
			assertTrue(Pattern.matches("(?i).*order by ASC\\(\\?v[0-9]+\\) limit 7.*", ds));
			assertTrue(ds.contains("http://linkedspending.aksw.org/ontology/finland-aid-amounts-extended "));
		}
	}
}