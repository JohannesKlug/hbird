package org.hbird.core.spacesystemmodel.tmtcgroups;

import java.math.BigDecimal;

import javax.xml.bind.annotation.XmlRootElement;

import org.hbird.core.commons.tmtc.Parameter;
import org.hbird.core.commons.tmtc.TmTcGroup;
import org.hbird.core.commons.tmtc.exceptions.UnknownParameterException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Various ParameterGroup utility methods.
 *
 * TODO Still not sure where these belong. They are used by the payload codec but that doesn't mean it
 * won't be required elsewhere as changing and copying parameters is a pretty generic task.
 *
 * @author Mark Doyle
 * @author kimmell
 *
 */
@XmlRootElement(name = "TmTcGroups")
public class TmTcGroups {

	private static final Logger LOG = LoggerFactory.getLogger(TmTcGroups.class);

	// Cast suppress reasoning: Parameter names must be unique so if a Param is found in a specific type collection
	// it is safe to cast.
	@SuppressWarnings("unchecked")
	public static void replaceParameterInGroup(final TmTcGroup group, final String qualifiedName, final Parameter<?> parameter) {
		final String pname = parameter.getQualifiedName();

		if (group.getAllParameters().containsKey(pname)) {
			group.getAllParameters().put(qualifiedName, parameter);
		}

		if (group.getIntegerParameters() != null && group.getIntegerParameters().containsKey(pname)) {
			group.getIntegerParameters().put(qualifiedName, (Parameter<Integer>) parameter);
		}
		else if (group.getLongParameters() != null && group.getLongParameters().containsKey(pname)) {
			group.getLongParameters().put(qualifiedName, (Parameter<Long>) parameter);
		}
		else if (group.getFloatParameters() != null && group.getFloatParameters().containsKey(pname)) {
			group.getFloatParameters().put(qualifiedName, (Parameter<Float>) parameter);
		}
		else if (group.getDoubleParameters() != null && group.getDoubleParameters().containsKey(pname)) {
			group.getDoubleParameters().put(qualifiedName, (Parameter<Double>) parameter);
		}
		else if (group.getBigDecimalParameters() != null && group.getBigDecimalParameters().containsKey(pname)) {
			group.getBigDecimalParameters().put(qualifiedName, (Parameter<BigDecimal>) parameter);
		}
		else if (group.getStringParameters() != null && group.getStringParameters().containsKey(pname)) {
			group.getStringParameters().put(qualifiedName, (Parameter<String>) parameter);
		}
		else if (group.getRawParameters() != null && group.getRawParameters().containsKey(pname)) {
			group.getRawParameters().put(qualifiedName, (Parameter<Byte[]>) parameter);
		}
	}

	public static TmTcGroup copyAllParameterValues(final TmTcGroup sourceGroup, final TmTcGroup targetGroup) {
		try {
			// Ints
			if (targetGroup.getIntegerParameters() != null) {
				for (final String qualifiedName : targetGroup.getIntegerParameters().keySet()) {
					targetGroup.getIntegerParameter(qualifiedName).setValue(sourceGroup.getIntegerParameter(qualifiedName).getValue());
					targetGroup.getIntegerParameter(qualifiedName).setReceivedTime(sourceGroup.getIntegerParameter(qualifiedName).getReceivedTime());
				}
			}
			if (targetGroup.getLongParameters() != null) {
				for (final String qualifiedName : targetGroup.getLongParameters().keySet()) {
					targetGroup.getLongParameter(qualifiedName).setValue(sourceGroup.getLongParameter(qualifiedName).getValue());
					targetGroup.getLongParameter(qualifiedName).setReceivedTime(sourceGroup.getLongParameter(qualifiedName).getReceivedTime());
				}
			}
			if (targetGroup.getFloatParameters() != null) {
				for (final String qualifiedName : targetGroup.getFloatParameters().keySet()) {
					targetGroup.getFloatParameter(qualifiedName).setValue(sourceGroup.getFloatParameter(qualifiedName).getValue());
					targetGroup.getFloatParameter(qualifiedName).setReceivedTime(sourceGroup.getFloatParameter(qualifiedName).getReceivedTime());
				}
			}
			if (targetGroup.getDoubleParameters() != null) {
				for (final String qualifiedName : targetGroup.getDoubleParameters().keySet()) {
					targetGroup.getDoubleParameter(qualifiedName).setValue(sourceGroup.getDoubleParameter(qualifiedName).getValue());
					targetGroup.getDoubleParameter(qualifiedName).setReceivedTime(sourceGroup.getDoubleParameter(qualifiedName).getReceivedTime());
				}
			}
			if (targetGroup.getBigDecimalParameters() != null) {
				for (final String qualifiedName : targetGroup.getBigDecimalParameters().keySet()) {
					targetGroup.getBigDecimalParameter(qualifiedName).setValue(sourceGroup.getBigDecimalParameter(qualifiedName).getValue());
					targetGroup.getBigDecimalParameter(qualifiedName).setReceivedTime(sourceGroup.getBigDecimalParameter(qualifiedName).getReceivedTime());
				}
			}
			if (targetGroup.getStringParameters() != null) {
				for (final String qualifiedName : targetGroup.getStringParameters().keySet()) {
					targetGroup.getStringParameter(qualifiedName).setValue(sourceGroup.getStringParameter(qualifiedName).getValue());
					targetGroup.getStringParameter(qualifiedName).setReceivedTime(sourceGroup.getStringParameter(qualifiedName).getReceivedTime());
				}
			}
			if (targetGroup.getRawParameters() != null) {
				for (final String qualifiedName : targetGroup.getRawParameters().keySet()) {
					targetGroup.getRawParameter(qualifiedName).setValue(sourceGroup.getRawParameter(qualifiedName).getValue());
					targetGroup.getRawParameter(qualifiedName).setReceivedTime(sourceGroup.getRawParameter(qualifiedName).getReceivedTime());
				}
			}
		}
		catch (final UnknownParameterException e) {
			// TODO - 27.03.2012 kimmell - unit test
			LOG.error("Unknown parameter when copying parameter values. This is is a serious internal error and must indicate a corruption "
					+ "in memory, a system bug, or a seriosu misuse of the API (copying parameters to a different space system"
					+ "model which has a different structure.  The system must shut down as integrity cannot be guaranteed.");
			// TODO - 27.03.2012 kimmell - maybe there is better exception to throw at this point?
			throw new RuntimeException("Unknown parameter when copying parameter values");
		}

		return targetGroup;
	}
}
