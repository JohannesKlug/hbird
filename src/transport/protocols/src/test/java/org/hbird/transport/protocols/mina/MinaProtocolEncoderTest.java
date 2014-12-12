package org.hbird.transport.protocols.mina;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MinaProtocolEncoderTest {
	
	@Mock
	private ProtocolEncoderOutput mockOut;

	@Test
	public void test() {
		RawMinaCodec customCodec = new RawMinaCodec();
		assertTrue(org.apache.mina.filter.codec.ProtocolCodecFactory.class.isAssignableFrom(customCodec.getClass()));
	}
	
	@Test
	public void testRawBytesEncoding() {
		RawMinaEncoder encoder = new RawMinaEncoder();
		byte[] input = new byte[] {0x00, 0x08, 0x1F, 0x10};
		encoder.encode(null, input, mockOut);
		verify(mockOut).write(IoBuffer.wrap(input));
	}

}
