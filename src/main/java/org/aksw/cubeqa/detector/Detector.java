package org.aksw.cubeqa.detector;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import org.aksw.cubeqa.Cube;
import org.aksw.cubeqa.property.ComponentProperty;
import org.aksw.cubeqa.property.scorer.ScoreResult;
import org.aksw.cubeqa.property.scorer.Scorers;
import org.aksw.cubeqa.restriction.RestrictionWithPhrase;

/**Abstract class for a Dectector, which is called before scorers and transforms certain keyphrases into additional query modifiers, such as aggregates. */
public abstract class Detector
{
	public abstract Optional<RestrictionWithPhrase> detect(Cube cube, String phrase);

	public static final List<Detector> DETECTORS = Arrays.asList(IntervalDetector.INSTANCE,TopDetector.INSTANCE);

	static final protected String PHRASE_REGEX = "([\\w'-]+(\\s[\\w,'-]+)*)";

	static private final Set<String> stopwords =
			new HashSet<>(Arrays.asList(
			   "a", "an", "and", "are", "as", "at", "be", "but", "by",
			      "for", "if", "in", "into", "is", "it",
			      "no", "not", "of", "on", "or", "such",
			      "that", "the", "their", "then", "there", "these",
			      "they", "this", "to", "was", "will", "with"));

	protected String removeStopwords(String s)
	{
		for(String stopword: stopwords)
		{
			s=s.replace(" "+stopword+" "," ");
			s=s.replace("^"+stopword+" ","");
			s=s.replace(" "+stopword+"$","");
		}
		return s;
//		return s.replaceAll("\\s+"," ");
	}

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