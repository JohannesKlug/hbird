package org.hbird.core.xtce;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hbird.core.commons.tmtc.Parameter;
import org.hbird.core.commons.tmtc.ParameterGroup;
import org.hbird.core.commons.tmtc.exceptions.UnknownParameterException;
import org.hbird.core.spacesystemmodel.SpaceSystemModel;
import org.hbird.core.spacesystemmodel.encoding.Encoding;
import org.hbird.core.spacesystemmodel.exceptions.ParameterNotInModelException;
import org.hbird.core.spacesystemmodel.exceptions.UnknownParameterGroupException;
import org.hbird.core.spacesystemmodel.tmtcgroups.TmTcGroups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Mark Doyle
 * @author Johannes Klug
 *
 */
public class XtceSpaceSystemModel implements SpaceSystemModel {
	private static final long serialVersionUID = 2532805548202927668L;
	 private static final Logger LOG = LoggerFactory.getLogger(XtceSpaceSystemModel.class);

	private String name;

	private final Map<String, ParameterGroup> parameterGroups = new HashMap<String, ParameterGroup>();

	private final Map<String, List<String>> restrictions = new HashMap<String, List<String>>();

	private final Map<String, Encoding> encodings = new HashMap<String, Encoding>();

	public XtceSpaceSystemModel() {
	}

	@Override
	public Collection<ParameterGroup> getParameterGroupsCollection() {
		return parameterGroups.values();
	}

	@Override
	public ParameterGroup getParameterGroup(final String qualifiedName) throws UnknownParameterGroupException {
		final ParameterGroup container = parameterGroups.get(qualifiedName);

		if (container == null) {
			throw new UnknownParameterGroupException("Your container lookup for '" + qualifiedName
					+ "' did not return any containers. Check your SpaceSystem configuration.");
		}

		return container;
	}

	/**
	 * Iterates over all the parameter groups and creates a list of all their parameters.
	 *
	 * @throws UnknownParameterException
	 */
	@Override
	public Map<String, Parameter<?>> getAllPayloadParameters() {
		Map<String, Parameter<?>> allParameters = new HashMap<String, Parameter<?>>();
		for (ParameterGroup pg : this.parameterGroups.values()) {
			for (String parameterId : pg.getAllParameters().keySet()) {
				allParameters.put(parameterId, pg.getAllParameters().get(parameterId));
			}
		}
		return allParameters;
	}

	@Override
	public Parameter<?> getParameter(final String qualifiedName) throws UnknownParameterException {
		for (ParameterGroup pg : this.parameterGroups.values()) {
			if (pg.getAllParameters().containsKey(qualifiedName)) {
				return pg.getParameter(qualifiedName);
			}
		}
		throw new UnknownParameterException(qualifiedName);
	}

	@Override
	public Parameter<Integer> getIntParameter(final String qualifiedName) throws UnknownParameterException {
		for (ParameterGroup pg : this.parameterGroups.values()) {
			if (pg.getIntegerParameters().containsKey(qualifiedName)) {
				return pg.getIntegerParameter(qualifiedName);
			}
		}
		throw new UnknownParameterException(qualifiedName);
	}

	@Override
	public Parameter<Long> getLongParameter(final String qualifiedName) throws UnknownParameterException {
		for (ParameterGroup pg : this.parameterGroups.values()) {
			if (pg.getLongParameters().containsKey(qualifiedName)) {
				return pg.getLongParameter(qualifiedName);
			}
		}
		throw new UnknownParameterException(qualifiedName);
	}

	@Override
	public Map<String, List<String>> getAllPayloadRestrictions() {
		return restrictions;
	}

	@Override
	public void replaceParameterInModel(final String qualifiedName, final Parameter<?> newParameter) throws ParameterNotInModelException {
		for (ParameterGroup pg : this.parameterGroups.values()) {
			TmTcGroups.replaceParameterInGroup(pg, qualifiedName, newParameter);
		}
	}

	@Override
	public Map<String, Parameter<Integer>> getAllUniqueIntegerParameters() {
		Map<String, Parameter<Integer>> allParameters = new HashMap<String, Parameter<Integer>>();
		for (ParameterGroup pg : this.parameterGroups.values()) {
			Map<String, Parameter<Integer>> integerParameters = pg.getIntegerParameters();
			if (integerParameters != null) {
				for (String qualifiedName : integerParameters.keySet()) {
					allParameters.put(qualifiedName, integerParameters.get(qualifiedName));
				}
			}
		}
		return allParameters;
	}

	@Override
	public Map<String, Parameter<Long>> getAllUniqueLongParameters() {
		Map<String, Parameter<Long>> allParameters = new HashMap<String, Parameter<Long>>();
		for (ParameterGroup pg : this.parameterGroups.values()) {
			Map<String, Parameter<Long>> longParameters = pg.getLongParameters();
			if (longParameters != null) {
				for (String qualifiedName : longParameters.keySet()) {
					allParameters.put(qualifiedName, longParameters.get(qualifiedName));
				}
			}
		}
		return allParameters;
	}

	@Override
	public Map<String, Parameter<BigDecimal>> getAllUniqueBigDecimalParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Parameter<Float>> getAllUniqueFloatParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Parameter<Double>> getAllUniqueDoubleParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Parameter<String>> getAllUniqueStringParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, Parameter<Byte[]>> getAllUniqueRawParameters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Parameter<BigDecimal> getBigDecimalParameter(final String qualifiedName) throws UnknownParameterException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Parameter<String> getStringParameter(final String qualifiedName) throws UnknownParameterException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Parameter<Float> getFloatParameter(final String qualifiedName) throws UnknownParameterException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Parameter<Double> getDoubleParameter(final String qualifiedName) throws UnknownParameterException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Parameter<Byte[]> getRawParameter(final String qualifiedName) throws UnknownParameterException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Map<String, ParameterGroup> getParameterGroups() {
		return parameterGroups;
	}

	@Override
	public Map<String, Encoding> getEncodings() {
		return encodings;
	}

	@Override
	public String getName() {
		return name;
	}

}