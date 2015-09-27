package com.fmse.absserver;

import java.io.InputStream;

import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.util.Validate;

/*
 * ACKNOWLEDGE: https://github.com/sir-muamua/ABSServer/blob/master/src/com/fmse/absserver/ResourceResolver.java
 */
public class ResourceResolver implements IResourceResolver
{
	private static final String NAME = "ABSResourceResolver";
	
	@Override
	public String getName() 
	{
		// TODO Auto-generated method stub
		return NAME;
	}

	@Override
	public InputStream getResourceAsStream(TemplateProcessingParameters templateProcessingParameter,
			String resourceName) 
	{
		
		Validate.notNull(resourceName, "Resource name cannot be null");
		String resourcePath = "/View/" + resourceName;
		return ResourceResolver.class.getResourceAsStream(resourcePath);
	}

}
