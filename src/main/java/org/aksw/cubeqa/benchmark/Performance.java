package org.aksw.cubeqa.benchmark;

import java.util.*;
import lombok.*;
import lombok.extern.log4j.Log4j;

/** Represents and calculates precision, recall and f-score. */
@RequiredArgsConstructor
@EqualsAndHashCode
@Log4j
@Getter
public class Performance
{
	final double precision;
	final double recall;
	final boolean empty;
	public String query = null;

	public static final Performance performance(Set correct, Set found)
	{
		return performance(correct,found,false);
	}

	public static final Performance performance(Set<Map<String,Object>> correct, Set<Map<String,Object>> found, boolean alreadyNormalized)
	{
		if(correct.isEmpty()) {
			throw new IllegalArgumentException("correct answer is empty");
		}
		if(found.isEmpty()) {
			return new Performance(0,0,true);
		}
		// align maps so that the keys are named the same
		// TODO: improve this, instanceofs are messy
		Map<String,Object> firstCorrect = correct.iterator().next();

		if(!alreadyNormalized&&!firstCorrect.containsKey(""))
		{
			Map<String,Object> firstFound = found.iterator().next();
			if(firstFound.containsKey("")) {
				return new Performance(0,0,true);
			}

			// TODO this may fail on optionals
			if(firstCorrect.size()!=firstFound.size()) {return new Performance(0,0,true);}// unequal dimension count
			if(firstCorrect.size()>2) {
				throw new RuntimeException("more than 2 answer dimensions not supported");
			}
			// TODO this is so ugly but it's late at night and deadline in 12 hours, improve later
			Iterator it = firstCorrect.keySet().iterator();
			String key1 = (String)it.next();
			String key2 = (String)it.next();
			Iterator foundIt = firstFound.keySet().iterator();
			String found1 = (String)foundIt.next();
			String found2 = (String)foundIt.next();

			Set<Map<String,Object>> normalizedFoundMap1 = new HashSet<Map<String,Object>>();
			Set<Map<String,Object>> normalizedFoundMap2 = new HashSet<Map<String,Object>>();
			for(Object o: correct)
			{
				Map<String,Object> m = (Map<String,Object>)o;
				Map<String,Object> nm1 = new HashMap<>();
				Map<String,Object> nm2 = new HashMap<>();
				// this is so ugly, fix problem at an earlier stage then bandaid here
				nm1.put(key1,m.get(key1));
				nm1.put(key2,m.get(key2));
				nm2.put(key1,m.get(key2));
				nm2.put(key2,m.get(key1));
				normalizedFoundMap1.add(nm1);
				normalizedFoundMap2.add(nm2);
			}
			Performance p1 = performance(correct, normalizedFoundMap1, true);
			Performance p2 = performance(correct, normalizedFoundMap2, true);
			return p1.fscore()>p2.fscore()?p1:p2;

//			throw new RuntimeException("its a map");
		}
		Set correctFound = new HashSet(found);
		System.out.println(correct);
		System.out.println(found);
		correctFound.retainAll(correct);
		return performance(correct.size(),found.size(),correctFound.size());
	}

	public static final Performance performance(int correct, int found, int correctFound)
	{
		if(correct==0)
			{
//			throw new IllegalArgumentException("correct==0");
			log.fatal("no correct answer");
			return new Performance(0, 0,true);
			}
		if(found==0) {
			return new Performance(0,0,true);
		}
		return new Performance((double)correctFound/found,(double)correctFound/correct,false);
	}

	double fscore() {return fscore(1);}

	double fscore(double beta)
	{
		if(precision+recall==0) {
			return 0;
		}
		return (1+beta*beta)*(precision*recall)/(beta*beta*precision+recall);
	}

	@Override public String toString()
	{
	return "Performance(precision="+precision+", recall="+recall+", f-score="+fscore()+")";
	}
}