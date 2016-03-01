package org.aksw.cubeqa.benchmark;

import static org.junit.Assert.*;
import java.io.File;
import java.io.IOException;
import org.aksw.cubeqa.Algorithm;
import org.aksw.cubeqa.CubeSparql;
import org.junit.Test;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.vocabulary.RDF;

public class BenchmarkTest
{
	@Test public void testCompleteQuestion()
	{
		Question q = Benchmark.completeQuestion(CubeSparql.finlandAid(), "some string","ask {?s ?p ?o.}");
		assertTrue(q.answers.size()==1);
		assertTrue(q.dataTypes.get("")==DataType.BOOLEAN);
		assertTrue(q.answers.iterator().next().get("").equals("true"));
	}

	@Test public void testEvaluate()
	{
		// TODO: choose a faster example, 10 s is too long for a test
		Benchmark.fromQald("qald6t3-train").evaluate(new Algorithm(),6);
	}

	@Test public void testFromCsv() throws IOException
	{
		assertEquals(Benchmark.fromCsv("qald6t3-train").questions.get(0).string,"How much was spent on public works and utilities by the Town of Cary in 2011?");
	}

	@Test public void testNodeString()
	{
		System.out.println(Benchmark.nodeString(RDF.type));
		assertEquals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type",Benchmark.nodeString(RDF.type));
		assertEquals("hello world",Benchmark.nodeString(ResourceFactory.createLangLiteral("hello world", "en")));
	}

	@Test public void testFromQald()
	{
		Benchmark b = Benchmark.fromQald("finland-aid");
		assertTrue(b.questions.size()==100);
		assertEquals("What was the average aid committed per month in year 2010?",b.questions.get(0).string);
		assertEquals(DataType.NUMBER,b.questions.get(0).dataTypes.get(""));
		assertTrue(b.questions.get(0).answers.iterator().next().get("").toString().startsWith("134145226.83"));
	}

	@Test public void testSaveAndLoadQald() throws IOException
	{
		Benchmark b = Benchmark.fromQald("finland-aid");
		b.saveAsQald(new File(new File("benchmark"),"test.xml"));
		Benchmark c = Benchmark.fromQald("test");
		for(int i=0;i<100;i++)
		{
			Question q = b.questions.get(i);
			Question r = c.questions.get(i);
			// to get more targeted debug output in case of inequalities
			assertEquals(q.string,r.string);
			assertEquals(q.query,r.query);
			assertEquals(q.dataTypes,r.dataTypes);
			assertEquals(q.answers,r.answers);
			assertEquals(q,r);
		}
	}

}