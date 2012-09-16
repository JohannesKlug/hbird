package org.hbird.transport.payloadcodec.codecdecorators;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.hbird.core.commons.tmtc.Parameter;
import org.hbird.core.commons.tmtc.ParameterGroup;
import org.hbird.transport.payloadcodec.codecdecorators.number.IntegerCodecFactory;
import org.hbird.transport.payloadcodec.codecdecorators.number.LongCodecFactory;
import org.hbird.transport.payloadcodec.codecdecorators.string.StringCodecFactory;
import org.hbird.transport.payloadcodec.exceptions.NoEncodingException;
import org.hbird.transport.payloadcodec.exceptions.UnexpectedParameterTypeException;
import org.hbird.transport.payloadcodec.exceptions.UnknownParameterEncodingException;
import org.hbird.transport.payloadcodec.exceptions.UnsupportedParameterEncodingException;
import org.hbird.core.spacesystemmodel.encoding.Encoding;
import org.hbird.core.spacesystemmodel.tmtcgroups.TmTcGroups;

import com.rits.cloning.Cloner;

public final class ParameterGroupCodecDecorator {

	private final Map<String, Encoding> encodings;

	public ParameterGroupCodecDecorator(final Map<String, Encoding> encodings) {
		this.encodings = encodings;
	}


	/**
	 * Remember this takes place at initialisation or reconfiguration, not in the parameter processing chain. It's a static
	 * configuration.
	 *
	 * @param parameterGroups
	 * @return
	 * @throws NoEncodingException
	 * @throws UnsupportedParameterEncodingException
	 * @throws UnknownParameterEncodingException
	 * @throws UnexpectedParameterTypeException
	 */
	public Map<String, ParameterGroup> decorateParameterGroups(final Map<String, ParameterGroup> parameterGroups) throws NoEncodingException,
	UnsupportedParameterEncodingException, UnknownParameterEncodingException, UnexpectedParameterTypeException {

		// Create a new cloned Map with the existing parameters in. This list will be decorated and returned.
		Cloner cloner = new Cloner();
		Map<String, ParameterGroup> codecAwareParameterGroups = cloner.deepClone(parameterGroups);

		// the list contains all the parameters acquired from the space system model since it was created from it!
		for (ParameterGroup pg : codecAwareParameterGroups.values()) {
			// Iterate over the parameter group integer parameters, decorating each one with codec functionality
			// and replacing the original Parameter in the new list.

			// First up, the Integer parameters..
			Map<String, Parameter<Integer>> integerParameters = pg.getIntegerParameters();
			if (integerParameters != null) {
				Iterator<Entry<String, Parameter<Integer>>> it = integerParameters.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Parameter<Integer>> entry = it.next();

					Encoding enc = findEncoding(entry.getValue().getQualifiedName());
					Parameter<Integer> codecAwareIntParameter = IntegerCodecFactory.decorateParameterWithCodec(entry.getValue(), enc);
					TmTcGroups.replaceParameterInGroup(pg, codecAwareIntParameter.getQualifiedName(), codecAwareIntParameter);
				}
			}

			// next, the Long parameters..
			Map<String, Parameter<Long>> longParameters = pg.getLongParameters();
			if (longParameters != null) {
				Iterator<Entry<String, Parameter<Long>>> it = longParameters.entrySet().iterator();
				while (it.hasNext()) {
					Entry<String, Parameter<Long>> entry = it.next();

					Encoding enc = findEncoding(entry.getValue().getQualifiedName());
					Parameter<Long> codecAwareLongParameter = LongCodecFactory.decorateParameterWithCodec(entry.getValue(), enc);
					TmTcGroups.replaceParameterInGroup(pg, codecAwareLongParameter.getQualifiedName(), codecAwareLongParameter);
				}
			}

			// next, the string parameters..
			Map<String, Parameter<String>> stringParameters = pg.getStringParameters();
			if(stringParameters != null) {
				Iterator<Entry<String, Parameter<String>>> it = stringParameters.entrySet().iterator();
				while(it.hasNext()) {
					Entry<String, Parameter<String>> entry = it.next();
					Encoding enc = findEncoding(entry.getValue().getQualifiedName());
					Parameter<String> codecAwareStringParameter = StringCodecFactory.decorateParameterWithCodec(entry.getValue(), enc);
					TmTcGroups.replaceParameterInGroup(pg, codecAwareStringParameter.getQualifiedName(), codecAwareStringParameter);
				}
			}

		}

		// FIXME BigDecimal, Float, Double, Binary

		return codecAwareParameterGroups;
	}

	private Encoding findEncoding(final String qualifiedName) throws NoEncodingException {
		if (encodings.containsKey(qualifiedName)) {
			return encodings.get(qualifiedName);
		}
		else {
			throw new NoEncodingException(qualifiedName);
		}
	}
}
