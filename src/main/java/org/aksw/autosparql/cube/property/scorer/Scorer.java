package org.aksw.autosparql.cube.property.scorer;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.java.Log;
import org.aksw.autosparql.cube.Cube;
import org.aksw.autosparql.cube.CubeSparql;
import org.aksw.autosparql.cube.property.ComponentProperty;
import uk.ac.shef.wit.simmetrics.similaritymetrics.AbstractStringMetric;
import uk.ac.shef.wit.simmetrics.similaritymetrics.Jaro;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.sparql.function.library.leviathan.cube;
import de.konradhoeffner.commons.Pair;

/** Scorers match phrases or words to component property values.
 * Scorers are used when a phrase does not match to a component property label.
 * Scorers return a similarity value in [0,1].
 */
@Log
public abstract class Scorer implements Serializable
{
	private static final long	serialVersionUID	= 1L;
	final ComponentProperty property;
	protected static final AbstractStringMetric similarity = new Jaro();

	public Scorer(ComponentProperty property)
	{
		this.property=property;
	}

	abstract protected Optional<ScoreResult> unsafeScore(String value);

	/** @param phrase a word or phrase
	 * @return 	a pair similarity score in [0,1]*/
	public Optional<ScoreResult> score(String phrase)
	{
		try
		{
			return unsafeScore(phrase);
		}
		catch(Exception e)
		{
//			log.warning(e.getClass().getName()+": "+e.getMessage());
			return Optional.empty();
		}
	}

	protected ResultSet queryValues()
	{
		String query = "select ?value (count(?value) as ?cnt)"
				+ "{?obs a qb:Observation. ?obs <"+property.uri+"> ?value. } group by ?value";
		ResultSet rs = CubeSparql.linkedSpending(property.cube.name).select(query);
		return rs;
	}

	static protected float closestValue(float[] sorted, float key)
	{
		if(sorted.length==1) {return sorted[0];}	// trivial case
		if(key<sorted[0]) {return sorted[0];}		// lower boundary
		if(key>sorted[sorted.length-1]) {return sorted[sorted.length-1];} // upper boundary
		int pos = Arrays.binarySearch(sorted, key);
		if(pos>=0) {return sorted[pos];} // we found an exact match
		// we didn't find an exact match, now we have two candidates: insertion point and insertion point-1 (we excluded the trivial case before)
		// pos = -ip-1 | +ip -pos => ip = -pos-1
		int ip = -pos-1;
		//		if(ip>3) System.out.println(Arrays.toString(Arrays.copyOfRange(sorted,ip-2,ip+3)));
		float closest;
		if(sorted[ip]-key<key-sorted[ip-1])	{closest=sorted[ip];} // < can be <= if smaller value is preferred
		else							{closest=sorted[ip-1];}
		return closest;
	}

	static protected long closestValue(long[] sorted, long key)
	{
		if(sorted.length==1) {return sorted[0];}	// trivial case
		if(key<sorted[0]) {return sorted[0];}		// lower boundary
		if(key>sorted[sorted.length-1]) {return sorted[sorted.length-1];} // upper boundary
		int pos = Arrays.binarySearch(sorted, key);
		if(pos>=0) {return sorted[pos];} // we found an exact match
		// we didn't find an exact match, now we have two candidates: insertion point and insertion point-1 (we excluded the trivial case before)
		// pos = -ip-1 | +ip -pos => ip = -pos-1
		int ip = -pos-1;
		long closest;
		if(sorted[ip]-key<key-sorted[ip-1])	{closest=sorted[ip];} // < can be <= if smaller value is preferred
		else							{closest=sorted[ip-1];}
		return closest;
	}

}