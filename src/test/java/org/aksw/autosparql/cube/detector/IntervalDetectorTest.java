package org.aksw.autosparql.cube.detector;

import org.aksw.autosparql.cube.Cube;
import org.junit.Test;

public class IntervalDetectorTest
{

	@Test public void testDetect()
	{
		Cube cube = Cube.FINLAND_AID;
		System.out.println(IntervalDetector.INSTANCE.detect(cube,"extended amounts of > 1000000"));
//		System.out.println(TopDetector.INSTANCE.detect(cube,"top 5 beaches"));
//		System.out.println(TopDetector.INSTANCE.detect(cube,"7 lowest prices"));
//		System.out.println(TopDetector.INSTANCE.detect(cube,"Top 10 aid receivers"));
	}

}
