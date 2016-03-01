package org.aksw.cubeqa;

import static org.junit.Assert.*;
import org.aksw.cubeqa.template.Template;
import org.junit.Test;
import com.hp.hpl.jena.query.ResultSet;

public class AlgorithmTest
{
	final String[] questions =
	{
			"How much money was given to strengthen civil society in Yemen?",
//		"What was the average aid to environment per month in year 2010?"
//		,"How much wood would a wood chuck chuck?"
	};

	// TODO: find out why the sector is not found in AlgorithmTest even when boostString is set to 0.1 (in ObjectPropertyScorerTest it works)
	@Test public void testAnswer()
	{
		for(String question: questions)
		{
			Template t = new Algorithm().template("finland-aid",question);
			ResultSet rs = t.cube.sparql.select(t.sparqlQuery());
			assertTrue(rs.hasNext());	
			System.out.println(t.sparqlQuery());
			System.out.println(rs.getResultVars().get(0));
			System.out.println(rs.next().get(rs.getResultVars().get(0)));
			assertEquals(rs.next().get(rs.getResultVars().get(0)).asLiteral().getInt(),180000);
		}
	}

}