package org.aksw.autosparql.cube.detector;

import org.junit.Test;

public class TopDetectorTest
{

	@Test public void testDetect()
	{
		System.out.println(TopDetector.INSTANCE.detect("10 poorest countries"));
		System.out.println(TopDetector.INSTANCE.detect("top 5 beaches"));
		System.out.println(TopDetector.INSTANCE.detect("7 lowest prices"));
		System.out.println(TopDetector.INSTANCE.detect("Top 10 aid receivers"));
	}

}