package org.aksw.cubeqa;

import lombok.extern.log4j.Log4j;
import org.aksw.cubeqa.template.CubeTemplate;
import org.aksw.cubeqa.template.CubeTemplator;

/** Calls the templator which does the main work. */
@Log4j
public class Algorithm
{

	public CubeTemplate answer(String cubeName, String question)
	{
		CubeTemplate template = new CubeTemplator(Cube.getInstance(cubeName)).buildTemplate(question);
		return template;
	}
}