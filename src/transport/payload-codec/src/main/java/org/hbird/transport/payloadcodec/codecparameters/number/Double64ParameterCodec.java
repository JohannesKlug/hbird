package org.hbird.transport.payloadcodec.codecparameters.number;

import java.nio.ByteBuffer;
import java.util.BitSet;

import org.hbird.core.commons.util.BitSetUtility;
import org.hbird.core.spacesystemmodel.encoding.Encoding;
import org.hbird.core.spacesystemmodel.tmtc.Parameter;
import org.hbird.transport.payloadcodec.codecparameters.CodecParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Parameter behaviour for an IEEE 754 64-bit precision Float.
 * 
 * @author Mark Doyle
 * @author Johannes Klug
 * 
 */
public class Double64ParameterCodec extends CodecParameter<Double> {
	private static final long serialVersionUID = 4281076707150170925L;

	private static final Logger LOG = LoggerFactory.getLogger(Double64ParameterCodec.class);

	public Double64ParameterCodec(final Parameter<Double> hostParameter, final Encoding encoding) {
		super(hostParameter, encoding);
	}

	@Override
	public void decode(final byte[] inBytes, final int offset) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Decoding parameter " + getQualifiedName() + " offset: " + offset / Byte.SIZE);
		}
		ByteBuffer buf = ByteBuffer.wrap(inBytes);
		double value = buf.getDouble(offset / Byte.SIZE);
		setValue(value);
	}

	@Override
	public void decode(final BitSet inBitset, final int offset) {
		BitSet value = inBitset.get(offset, offset + encoding.getSizeInBits() + 1);
		decode(BitSetUtility.toByteArray(value, encoding.getSizeInBits()), 0);
	}

	@Override
	public byte[] encodeToByteArray(final byte[] targetBytes, final int offset) {
		throw new UnsupportedOperationException();
	}

	@Override
	public BitSet encodeToBitSet(final BitSet targetBitSet, final int offset) {
		throw new UnsupportedOperationException();
	}
}
