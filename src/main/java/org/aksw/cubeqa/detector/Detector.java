package org.aksw.cubeqa.detector;

import java.util.*;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.property.scorer.Scorers;
import org.aksw.cubeqa.template.Fragment;

/**Abstract class for a Detector, which is called before scorers and transforms certain keyphrases into additional query modifiers, such as aggregates.
 * A detector can find several or no matches in a phrase.
 * */
public interface Detector
{
	/** Detection is supposed to not overlap in phrases.*/
	public Set<Fragment> detect(Cube cube, String phrase);

	// TODO: generalize this,as per time detector always uses finland aid as of now
	public static final List<Detector> DETECTORS = Arrays.asList(HalfInfiniteIntervalDetector.INSTANCE,TopDetector.INSTANCE,PerTimeDetector.INSTANCE,AggregateDetector.INSTANCE);

	static final String PHRASE_REGEX = "([a-zA-Züöäéèô'-]+(\\s[a-zA-Züöäéèô,'-]+)*)";
	static final String WORD_REGEX = "([a-zA-Züöäéèô'-]+)";

	static public Set<ScoreResult> matchPart(Cube cube, String phrase)
	{
		Set<ScoreResult> partScores = new HashSet<>();
		String[] tokens = phrase.split("\\s");
		StringBuilder sb = new StringBuilder();
		for(int i=0;i<tokens.length;i++)
		{
			if(i>0) sb.append(" ");
			sb.append(tokens[i]);
			String part = sb.toString();
			Map<ComponentProperty,Double> nameRefs = Scorers.scorePhraseProperties(cube,part);
			nameRefs.entrySet().forEach(e->
			{
				partScores.add(new ScoreResult(e.getKey(), part, e.getValue()));//part.length()/phrase.length()
			});
		}
		return partScores;
	}

}