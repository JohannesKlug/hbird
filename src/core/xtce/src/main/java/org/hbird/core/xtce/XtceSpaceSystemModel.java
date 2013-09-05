package org.hbird.core.xtce;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

import org.hbird.core.commons.tmtc.exceptions.UnknownParameterException;
import org.hbird.core.spacesystemmodel.SpaceSystemModel;
import org.hbird.core.spacesystemmodel.calibration.Calibrator;
import org.hbird.core.spacesystemmodel.encoding.Encoding;
import org.hbird.core.spacesystemmodel.exceptions.UnknownParameterGroupException;
import org.hbird.core.spacesystemmodel.tmtc.CommandGroup;
import org.hbird.core.spacesystemmodel.tmtc.Parameter;
import org.hbird.core.spacesystemmodel.tmtc.ParameterGroup;
import org.hbird.core.spacesystemmodel.tmtc.provided.TmTcGroups;

/**
 * 
 * @author Mark Doyle
 * @author Johannes Klug
 * 
 */
@XmlRootElement()
public class XtceSpaceSystemModel implements SpaceSystemModel {
	private static final long serialVersionUID = 2532805548202927668L;

	// private static final Logger LOG = LoggerFactory.getLogger(XtceSpaceSystemModel.class);

	private String name;

	private final Map<String, ParameterGroup> parameterGroups = new HashMap<String, ParameterGroup>();

	private final Map<String, CommandGroup> commands = new HashMap<String, CommandGroup>();

	private final Map<String, List<String>> restrictions = new HashMap<String, List<String>>();

	private final Map<String, Encoding> encodings = new HashMap<String, Encoding>();

	private final Map<String, Calibrator> calibrators = new HashMap<String, Calibrator>();

	private final Map<String, Map<String, String>> commandVerifiers = new HashMap<String, Map<String, String>>();

	private final Map<String, String> unitDescriptions = new HashMap<String, String>();

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
		final Map<String, Parameter<?>> allParameters = new HashMap<String, Parameter<?>>();
		for (final ParameterGroup pg : this.parameterGroups.values()) {
			for (final String parameterId : pg.getAllParameters().keySet()) {
				allParameters.put(parameterId, pg.getAllParameters().get(parameterId));
			}
		}
		return allParameters;
	}

	@Override
	public Parameter<?> getParameter(final String qualifiedName) throws UnknownParameterException {
		for (final ParameterGroup pg : this.parameterGroups.values()) {
			if (pg.getAllParameters().containsKey(qualifiedName)) {
				return pg.getParameter(qualifiedName);
			}
		}
		throw new UnknownParameterException(qualifiedName);
	}

	@Override
	public Parameter<Integer> getIntParameter(final String qualifiedName) throws UnknownParameterException {
		for (final ParameterGroup pg : this.parameterGroups.values()) {
			if (pg.getIntegerParameters().containsKey(qualifiedName)) {
				return pg.getIntegerParameter(qualifiedName);
			}
		}
		throw new UnknownParameterException(qualifiedName);
	}

	@Override
	public Parameter<Long> getLongParameter(final String qualifiedName) throws UnknownParameterException {
		for (final ParameterGroup pg : this.parameterGroups.values()) {
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
	public void replaceParameterInModel(final String qualifiedName, final Parameter<?> newParameter) {
		for (final ParameterGroup pg : this.parameterGroups.values()) {
			TmTcGroups.replaceParameterInGroup(pg, qualifiedName, newParameter);
		}
	}

	@Override
	public Map<String, Parameter<Integer>> getAllUniqueIntegerParameters() {
		final Map<String, Parameter<Integer>> allParameters = new HashMap<String, Parameter<Integer>>();
		for (final ParameterGroup pg : this.parameterGroups.values()) {
			final Map<String, Parameter<Integer>> integerParameters = pg.getIntegerParameters();
			if (integerParameters != null) {
				for (final String qualifiedName : integerParameters.keySet()) {
					allParameters.put(qualifiedName, integerParameters.get(qualifiedName));
				}
			}
		}
		return allParameters;
	}

	@Override
	public Map<String, Parameter<Long>> getAllUniqueLongParameters() {
		final Map<String, Parameter<Long>> allParameters = new HashMap<String, Parameter<Long>>();
		for (final ParameterGroup pg : this.parameterGroups.values()) {
			final Map<String, Parameter<Long>> longParameters = pg.getLongParameters();
			if (longParameters != null) {
				for (final String qualifiedName : longParameters.keySet()) {
					allParameters.put(qualifiedName, longParameters.get(qualifiedName));
				}
			}
		}
		return allParameters;
	}

	@Override
	public Map<String, Parameter<BigDecimal>> getAllUniqueBigDecimalParameters() {
		return null;
	}

	@Override
	public Map<String, Parameter<Float>> getAllUniqueFloatParameters() {
		return null;
	}

	@Override
	public Map<String, Parameter<Double>> getAllUniqueDoubleParameters() {
		return null;
	}

	@Override
	public Map<String, Parameter<String>> getAllUniqueStringParameters() {
		return null;
	}

	@Override
	public Map<String, Parameter<Byte[]>> getAllUniqueRawParameters() {
		return null;
	}

	@Override
	public Parameter<BigDecimal> getBigDecimalParameter(final String qualifiedName) {
		return null;
	}

	@Override
	public Parameter<String> getStringParameter(final String qualifiedName) {
		return null;
	}

	@Override
	public Parameter<Float> getFloatParameter(final String qualifiedName) {
		return null;
	}

	@Override
	public Parameter<Double> getDoubleParameter(final String qualifiedName) {
		return null;
	}

	@Override
	public Parameter<Byte[]> getRawParameter(final String qualifiedName) {
		return null;
	}

	@Override
	public Map<String, ParameterGroup> getParameterGroups() {
		return parameterGroups;
	}

	@Override
	public Map<String, CommandGroup> getCommands() {
		return commands;
	}

	@Override
	public Map<String, Encoding> getEncodings() {
		return encodings;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public Map<String, String> getCommandVerifiers(String qualifiedName) {
		return commandVerifiers.get(qualifiedName);
	}

	@Override
	public Calibrator getCalibrator(String qualifiedName) {
		return calibrators.get(qualifiedName);
	}

	@Override
	public Map<String, Calibrator> getAllCalibrators() {
		return calibrators;
	}

	@Override
	public Map<String, String> getAllUnitDescriptions() {
		return unitDescriptions;
	}

	@Override
	public String getUnitDescription(String qualifiedName) {
		return unitDescriptions.get(qualifiedName);
	}
}