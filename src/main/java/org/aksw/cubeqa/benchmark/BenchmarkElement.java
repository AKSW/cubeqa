package org.aksw.cubeqa.benchmark;

import lombok.RequiredArgsConstructor;
import org.aksw.cubeqa.template.CubeTemplate;

/**A single benchmark question along with its correct template.*/
@RequiredArgsConstructor
class BenchmarkElement {
	public final String question;
	public final boolean doable;
	public final CubeTemplate template;
}