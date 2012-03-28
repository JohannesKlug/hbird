package org.hbird.core.spacesystemmodel.tmtcgroups;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.Map;

import org.hbird.core.commons.tmtc.Parameter;
import org.hbird.core.commons.tmtc.ParameterGroup;
import org.hbird.core.commons.tmtc.ParameterGroupReport;
import org.hbird.core.commons.tmtc.exceptions.UnknownParameterException;

/**
 * TELEMETRY
 *
 * @author Mark Doyle
 * @author Johannes Klug
 */
public class HummingbirdParameterGroup implements ParameterGroup {
	
	private static final long serialVersionUID = -6877917071118156741L;

	private final String qualifiedName;
	private final String name;
	private final String shortDescription;
	private final String longDescription;
	private long timeStamp = 0;

	private final Map<String, Parameter<?>> parameters = new LinkedHashMap<String, Parameter<?>>();
	private Map<String, Parameter<Integer>> integerParameters;
	private Map<String, Parameter<Long>> longParameters;
	private Map<String, Parameter<Float>> floatParameters;
	private Map<String, Parameter<Double>> doubleParameters;
	private Map<String, Parameter<BigDecimal>> bigDecimalParameters;
	private Map<String, Parameter<String>> stringParameters;
	private Map<String, Parameter<Byte[]>> rawParameters;

	private final ParameterGroupReport parameterReport = new ParameterGroupReport();

	/** List of Parameters belonging to this Group */

	/**
	 * Constructor of the ParameterGroup class.
	 *
	 * @param name
	 *            The name of the container.
	 * @param shortDescription
	 *            A one line description of the container, used for tooltip type information.
	 * @param longDescription
	 *            A detailed description of the container.
	 *
	 */
	public HummingbirdParameterGroup(final String qualifiedName, final String name, final String shortDescription, final String longDescription) {
		this.name = name;
		this.qualifiedName = qualifiedName;
		this.shortDescription = shortDescription;
		this.longDescription = longDescription;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public String getShortDescription() {
		return this.shortDescription;
	}

	@Override
	public String getLongDescription() {
		return this.longDescription;
	}
	
	@Override
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	@Override
	public long getTimeStamp() {
		return this.timeStamp;
	}

	@Override
	public Map<String, Parameter<?>> getAllParameters() {
		return parameters;
	}

	@Override
	public Map<String, Parameter<Integer>> getIntegerParameters() {
		return integerParameters;
	}

	@Override
	public Map<String, Parameter<Long>> getLongParameters() {
		return longParameters;
	}

	@Override
	public Map<String, Parameter<Float>> getFloatParameters() {
		return floatParameters;
	}

	@Override
	public Map<String, Parameter<Double>> getDoubleParameters() {
		return doubleParameters;
	}

	@Override
	public Map<String, Parameter<BigDecimal>> getBigDecimalParameters() {
		return bigDecimalParameters;
	}

	@Override
	public Map<String, Parameter<String>> getStringParameters() {
		return stringParameters;
	}

	@Override
	public Map<String, Parameter<Byte[]>> getRawParameters() {
		return rawParameters;
	}

	@Override
	public void addIntegerParameter(final String qualifiedName, final Parameter<Integer> parameter) {
		if (this.integerParameters == null) {
			this.integerParameters = new LinkedHashMap<String, Parameter<Integer>>();
		}
		this.integerParameters.put(qualifiedName, parameter);
		this.parameters.put(qualifiedName, parameter);
		this.parameterReport.incrementIntCount();
	}

	@Override
	public void addLongParameter(final String qualifiedName, final Parameter<Long> parameter) {
		if (this.longParameters == null) {
			this.longParameters = new LinkedHashMap<String, Parameter<Long>>();
		}
		this.longParameters.put(qualifiedName, parameter);
		this.parameters.put(qualifiedName, parameter);
		this.parameterReport.incrementLongCount();
	}

	@Override
	public void addBigDecimalParameter(final String qualifiedName, final Parameter<BigDecimal> parameter) {
		if (this.bigDecimalParameters == null) {
			this.bigDecimalParameters = new LinkedHashMap<String, Parameter<BigDecimal>>();
		}
		this.bigDecimalParameters.put(qualifiedName, parameter);
		this.parameters.put(qualifiedName, parameter);
		this.parameterReport.incrementBigDecimalCount();
	}

	@Override
	public void addFloatParameter(final String qualifiedName, final Parameter<Float> parameter) {
		if (this.floatParameters == null) {
			this.floatParameters = new LinkedHashMap<String, Parameter<Float>>();
		}
		this.floatParameters.put(qualifiedName, parameter);
		this.parameters.put(qualifiedName, parameter);
		this.parameterReport.incrementFloatCount();
	}

	@Override
	public void addDoubleParameter(final String qualifiedName, final Parameter<Double> parameter) {
		if (this.doubleParameters == null) {
			this.doubleParameters = new LinkedHashMap<String, Parameter<Double>>();
		}
		this.doubleParameters.put(qualifiedName, parameter);
		this.parameters.put(qualifiedName, parameter);
		this.parameterReport.incrementDoubleCount();
	}

	@Override
	public void addStringParameter(final String qualifiedName, final Parameter<String> parameter) {
		if (this.stringParameters == null) {
			this.stringParameters = new LinkedHashMap<String, Parameter<String>>();
		}
		this.stringParameters.put(qualifiedName, parameter);
		this.parameters.put(qualifiedName, parameter);
		this.parameterReport.incrementStringCount();
	}

	@Override
	public void addRawParameter(final String qualifiedName, final Parameter<Byte[]> parameter) {
		if (this.rawParameters == null) {
			this.rawParameters = new LinkedHashMap<String, Parameter<Byte[]>>();
		}
		this.rawParameters.put(qualifiedName, parameter);
		this.parameters.put(qualifiedName, parameter);
		this.parameterReport.incrementRawCount();
	}

	@Override
	public ParameterGroupReport getParameterReport() {
		return this.parameterReport;
	}

	@Override
	public Parameter<Integer> getIntegerParameter(final String qualifiedName) throws UnknownParameterException {
		Parameter<Integer> p = integerParameters == null ? null : integerParameters.get(qualifiedName);
		validateParameterNotNull(p, qualifiedName);
		return p;
	}

	@Override
	public Parameter<Long> getLongParameter(final String qualifiedName) throws UnknownParameterException {
		Parameter<Long> p = longParameters == null ? null : longParameters.get(qualifiedName);
		validateParameterNotNull(p, qualifiedName);
		return p;
	}

	@Override
	public Parameter<?> getParameter(final String qualifiedName) throws UnknownParameterException {
		Parameter<?> p = parameters.get(qualifiedName);
		if (p == null) {
			throw new UnknownParameterException(qualifiedName);
		}
		return p;
	}

	@Override
	public String getQualifiedName() {
		return this.qualifiedName;
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
	public Parameter<BigDecimal> getBigDecimalParameter(final String qualifiedName) throws UnknownParameterException {
		throw new UnsupportedOperationException();
	}

	@Override
	public Parameter<String> getStringParameter(final String qualifiedName) throws UnknownParameterException {
		Parameter<String> p = stringParameters == null ? null : stringParameters.get(qualifiedName);
		validateParameterNotNull(p, qualifiedName);
		return p;
	}
	
	void validateParameterNotNull(Parameter<?> p, String qualifiedName) throws UnknownParameterException {
		if (p == null) {
			throw new UnknownParameterException(qualifiedName);
		}
	}

	@Override
	public Parameter<Byte[]> getRawParameter(final String qualifiedName) throws UnknownParameterException {
		throw new UnsupportedOperationException();
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("HummingbirdParameterGroup [qualifiedName=");
		builder.append(qualifiedName);
		builder.append(", name=");
		builder.append(name);
		builder.append(", shortDescription=");
		builder.append(shortDescription);
		builder.append(", longDescription=");
		builder.append(longDescription);
		builder.append(", integerParameters=");
		builder.append(integerParameters);
		builder.append(", longParameters=");
		builder.append(longParameters);
		builder.append(", floatParameters=");
		builder.append(floatParameters);
		builder.append(", doubleParameters=");
		builder.append(doubleParameters);
		builder.append(", bigDecimalParameters=");
		builder.append(bigDecimalParameters);
		builder.append(", stringParameters=");
		builder.append(stringParameters);
		builder.append(", rawParameters=");
		builder.append(rawParameters);
		builder.append("]");
		return builder.toString();
	}
}
