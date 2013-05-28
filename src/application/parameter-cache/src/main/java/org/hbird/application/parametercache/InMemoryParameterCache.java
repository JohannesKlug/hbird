package org.hbird.application.parametercache;

import java.util.HashMap;
import java.util.Map;

import org.hbird.core.spacesystemmodel.tmtc.Parameter;
import org.hbird.core.spacesystemmodel.tmtc.ParameterGroup;

public class InMemoryParameterCache implements ParameterCache {

	private static final Map<String, Parameter<?>> parameterMap = new HashMap<String, Parameter<?>>();

	public void handleParameterUpdate(Parameter<?> parameter) {
		parameterMap.put(parameter.getQualifiedName(), parameter);
	}

	public void handleParameterUpdate(ParameterGroup parameterGroup) {
		for (Parameter<?> p : parameterGroup.getAllParameters().values()) {
			parameterMap.put(p.getQualifiedName(), p);
		}
	}

	/**
	 * Returns the requested {@link ParameterStatus} from the cache.
	 * 
	 * @param qualifiedName
	 * @return
	 */
	@Override
	public Parameter<?> getParameterStatus(String qualifiedName) {
		return parameterMap.get(qualifiedName);
	}

}
