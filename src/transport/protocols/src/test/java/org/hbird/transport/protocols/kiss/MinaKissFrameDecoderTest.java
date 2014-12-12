package org.hbird.transport.protocols.kiss;

import static org.hbird.transport.protocols.kiss.KissConstants.DATA_FRAME;
import static org.hbird.transport.protocols.kiss.KissConstants.FEND;
import static org.hbird.transport.protocols.kiss.KissConstants.FESC;
import static org.hbird.transport.protocols.kiss.KissConstants.FULL_DUPLEX;
import static org.hbird.transport.protocols.kiss.KissConstants.P;
import static org.hbird.transport.protocols.kiss.KissConstants.RETURN;
import static org.hbird.transport.protocols.kiss.KissConstants.SET_HARDWARE;
import static org.hbird.transport.protocols.kiss.KissConstants.SLOT_TIME;
import static org.hbird.transport.protocols.kiss.KissConstants.TFEND;
import static org.hbird.transport.protocols.kiss.KissConstants.TFESC;
import static org.hbird.transport.protocols.kiss.KissConstants.TX_DELAY;
import static org.hbird.transport.protocols.kiss.KissConstants.TX_TAIL;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.service.TransportMetadata;
import org.apache.mina.core.session.DummySession;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MinaKissFrameDecoderTest {

	private CumulativeProtocolDecoder decoder = new KissSyncerDecoder();

	@Mock
	private ProtocolDecoderOutput mockOut;

	@Mock
	private IoSession mockSession;

	@Mock
	private TransportMetadata mockTransportMetadata;

	private final static byte[] GOOD_DATA_FRAME = { FEND, DATA_FRAME, 0x08, 0x1F, FEND };

	private final static byte[] GOOD_DATA_FRAME_CONTENT = { 0x08, 0x1F };

	private final static byte[] GOOD_TX_DELAY_FRAME = { FEND, TX_DELAY, FEND };

	private final static byte[] GOOD_P_FRAME = { FEND, P, FEND };

	private final static byte[] GOOD_SLOT_TIME_FRAME = { FEND, SLOT_TIME, FEND };

	private final static byte[] GOOD_TX_TAIL_FRAME = { FEND, TX_TAIL, FEND };

	private final static byte[] GOOD_FULL_DUPLEX_FRAME = { FEND, FULL_DUPLEX, FEND };

	private final static byte[] GOOD_SET_HARDWARE_FRAME = { FEND, SET_HARDWARE, FEND };

	private final static byte[] GOOD_RETURN_FRAME = { FEND, RETURN, FEND };

	private final static byte[] CORRUPT_COMMAND_TYPE_FRAME = { FEND, 0x0C, FEND };

	@Before
	public void setUp() throws Exception {
		decoder = new KissSyncerDecoder();

		when(mockSession.getTransportMetadata()).thenReturn(mockTransportMetadata);
		when(mockTransportMetadata.hasFragmentation()).thenReturn(false);
	}

	@Test
	public void testKissFrameDecode() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F };

		decoder.decode(mockSession, IoBuffer.wrap(testInput), mockOut);

		verify(mockOut).write(expectedOutput);
	}

	@Test
	public void testKissFrameDecodeRubbishEitherSide() throws Exception {
		IoSession session = new DummySession();

		byte[] testInput = new byte[] { 0x45, 0x10, FEND, DATA_FRAME, 0x08, 0x1F, FEND, 0x0F };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F };

		decoder.decode(session, IoBuffer.wrap(testInput), mockOut);

		verify(mockOut).write(expectedOutput);
	}

	@Test
	public void testKissFrameDecodeDoubleFendAtStart() throws Exception {
		byte[] testInput = new byte[] { FEND, FEND, DATA_FRAME, 0x08, 0x1F, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F };

		decoder.decode(mockSession, IoBuffer.wrap(testInput), mockOut);

		verify(mockOut).write(expectedOutput);
	}

	@Test
	public void testKissFrameDecodeDoubleFendWithRubbish() throws Exception {
		byte[] testInput = new byte[] { 0x45, FEND, FEND, DATA_FRAME, 0x08, 0x1F, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F };

		decoder.decode(mockSession, IoBuffer.wrap(testInput), mockOut);

		verify(mockOut).write(expectedOutput);
	}

	@Test
	public void testKissFrameDecodeCorruptBeforeGoodWithRubbishAtTheStart() throws Exception {
		byte[] testInput = new byte[] { 0x45, FEND, DATA_FRAME, 0x56, 0x00, FEND, DATA_FRAME, 0x08, 0x1F, FEND };
		byte[] expectedOutput = new byte[] { 0x56, 0x00 };

		decoder.decode(mockSession, IoBuffer.wrap(testInput), mockOut);

		verify(mockOut).write(expectedOutput);
	}

	@Test
	public void testKissFrameDecodeEscapedFendValidFrame() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, FESC, TFEND, 0x08, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, (byte) 0xC0, 0x08 };

		decoder.decode(mockSession, IoBuffer.wrap(testInput), mockOut);

		verify(mockOut).write(expectedOutput);
	}

	/**
	 * C0 00 DB DC 80 85 09 02 66 9A 05 00 00 59 00 57 C0
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStrandFrame() throws Exception {
		IoSession session = new DummySession();

		byte[] testInput = new byte[] { (byte) 0xC0, 0x00, (byte) 0xDB, (byte) 0xDC, (byte) 0x80, (byte) 0x85, 0x09, 0x02, 0x66, (byte) 0x9A, 0x05, 0x00, 0x00,
				0x59, 0x00, 0x57, (byte) 0xC0 };
		byte[] expected = new byte[] { (byte) 0xC0, (byte) 0x80, (byte) 0x85, 0x09, 0x02, 0x66, (byte) 0x9A, 0x05, 0x00, 0x00, 0x59, 0x00, 0x57 };

		decoder.decode(session, IoBuffer.wrap(testInput), mockOut);

		verify(mockOut).write(expected);
	}

	@Test
	public void testKissFrameDecodeEscapedEscapeValidFrame() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, FESC, TFESC, 0x08, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, (byte) 0xDB, 0x08 };

		decoder.decode(mockSession, IoBuffer.wrap(testInput), mockOut);

		verify(mockOut).write(expectedOutput);
	}

	@Test
	public void testKissFrameDecodeEscapedEscapeNoisyFrame() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, FESC, 0x08, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, 0x08 };

		decoder.decode(mockSession, IoBuffer.wrap(testInput), mockOut);

		verify(mockOut).write(expectedOutput);
	}

	@Test
	public void testKissFrameDecodeEscapedFendNoisyFrame() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, FESC, (byte) 0xFF, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, (byte) 0xFF };

		decoder.decode(mockSession, IoBuffer.wrap(testInput), mockOut);

		verify(mockOut).write(expectedOutput);
	}

	@Test
	public void testKissFrameDecodeBackToBackFrames() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, (byte) 0xFF, FEND, FEND, DATA_FRAME, 0x08, 0x1F, (byte) 0x33, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, (byte) 0xFF };
		byte[] expectedOutput2 = new byte[] { 0x08, 0x1F, (byte) 0x33 };

		IoBuffer wrappedInput = IoBuffer.wrap(testInput);

		decoder.decode(mockSession, wrappedInput, mockOut);
		verify(mockOut).write(expectedOutput);

		decoder.decode(mockSession, wrappedInput, mockOut);
		verify(mockOut).write(expectedOutput2);
	}

	@Test
	public void testKissFrameDecodeSplitFrame() throws Exception {
		byte[] testInputPart1 = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F };
		byte[] testInputPart2 = new byte[] { 0x0F, 0x54, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, 0x0F, 0x54 };

		decoder.decode(mockSession, IoBuffer.wrap(testInputPart1), mockOut);
		decoder.decode(mockSession, IoBuffer.wrap(testInputPart2), mockOut);

		verify(mockOut).write(expectedOutput);
	}

	@Test
	public void testKissFrameDecodeSplitFrameRubbishEitherSide() throws Exception {
		byte[] testInputPart1 = new byte[] { 0x56, 0x7A, FEND, DATA_FRAME, 0x08, 0x1F };
		byte[] testInputPart2 = new byte[] { 0x0F, 0x54, FEND, 0x56, 0x7A };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, 0x0F, 0x54 };

		decoder.decode(mockSession, IoBuffer.wrap(testInputPart1), mockOut);
		decoder.decode(mockSession, IoBuffer.wrap(testInputPart2), mockOut);

		verify(mockOut).write(expectedOutput);
	}

	@Test
	public void testKissFrameDecodeMultipleGoodFrame() throws Exception {
		byte[] rubbish = new byte[] { 0x56, 0x7A };
		byte[] rubbishWithFend = new byte[] { 0x0F, 0x54, FEND };

		decoder.decode(mockSession, IoBuffer.wrap(rubbish), mockOut);
		decoder.decode(mockSession, IoBuffer.wrap(GOOD_DATA_FRAME), mockOut);
		decoder.decode(mockSession, IoBuffer.wrap(GOOD_DATA_FRAME), mockOut);
		decoder.decode(mockSession, IoBuffer.wrap(rubbishWithFend), mockOut);

		// the codec should have written out two good frames.
		verify(mockOut, times(2)).write(GOOD_DATA_FRAME_CONTENT);
	}

	@Test
	public void testKissFrameDecodeMultipleGoodFrameSplit() throws Exception {
		byte[] rubbish = new byte[] { 0x56, 0x7A };
		byte[] frameWithRubbish = ArrayUtils.addAll(new byte[] { 0x45 }, GOOD_DATA_FRAME);
		byte[] frameWithRubbish2 = ArrayUtils.addAll(new byte[] { 0x55 }, GOOD_DATA_FRAME);
		byte[] rubbishWithFend = new byte[] { 0x0F, 0x54, FEND };

		decoder.decode(mockSession, IoBuffer.wrap(rubbish), mockOut);
		decoder.decode(mockSession, IoBuffer.wrap(frameWithRubbish), mockOut);
		decoder.decode(mockSession, IoBuffer.wrap(frameWithRubbish2), mockOut);
		decoder.decode(mockSession, IoBuffer.wrap(rubbishWithFend), mockOut);

		// the codec should have written out two good frames.
		verify(mockOut, times(2)).write(GOOD_DATA_FRAME_CONTENT);
	}

	@Test
	public void testKissFrameDecodeDoubleFendNoRemainingInFirstBuffer() throws Exception {
		byte[] doubleFendTheEndBuffer = new byte[] { FEND, FEND };

		decoder.decode(mockSession, IoBuffer.wrap(doubleFendTheEndBuffer), mockOut);
		decoder.decode(mockSession, IoBuffer.wrap(GOOD_DATA_FRAME), mockOut);

		verify(mockOut).write(GOOD_DATA_FRAME_CONTENT);
	}

	@Test
	public void testKissFrameDecodeDataFrameEmptyBufferAfterDataCommand() throws Exception {
		byte[] uptoCommandType = ArrayUtils.subarray(GOOD_DATA_FRAME, 0, 2);
		byte[] restOfFrame = ArrayUtils.subarray(GOOD_DATA_FRAME, 2, GOOD_DATA_FRAME.length);

		decoder.decode(mockSession, IoBuffer.wrap(uptoCommandType), mockOut);
		decoder.decode(mockSession, IoBuffer.wrap(restOfFrame), mockOut);

		verify(mockOut).write(GOOD_DATA_FRAME_CONTENT);
	}

	@Test
	public void testFragmentationGoodFrame() throws Exception {
		IoSession session = new DummySession();

		IoBuffer firstFrag = IoBuffer.wrap(new byte[] { FEND, DATA_FRAME });
		IoBuffer secondFrag = IoBuffer.wrap(GOOD_DATA_FRAME_CONTENT.clone());
		IoBuffer thridFrag = IoBuffer.wrap(new byte[] { FEND });

		decoder.decode(session, firstFrag, mockOut);
		verify(mockOut, never()).write(anyObject());

		decoder.decode(session, secondFrag, mockOut);
		verify(mockOut, never()).write(anyObject());

		decoder.decode(session, thridFrag, mockOut);
		verify(mockOut).write(GOOD_DATA_FRAME_CONTENT);
	}

	@Test
	public void testKissFrameTxDelay() throws Exception {
		decoder.decode(mockSession, IoBuffer.wrap(GOOD_TX_DELAY_FRAME), mockOut);
		verifyZeroInteractions(mockOut);
	}

	@Test
	public void testKissFrameP() throws Exception {
		decoder.decode(mockSession, IoBuffer.wrap(GOOD_P_FRAME), mockOut);
		verifyZeroInteractions(mockOut);
	}

	@Test
	public void testKissFrameSlotTime() throws Exception {
		decoder.decode(mockSession, IoBuffer.wrap(GOOD_SLOT_TIME_FRAME), mockOut);
		verifyZeroInteractions(mockOut);
	}

	@Test
	public void testKissFrameTxTail() throws Exception {
		decoder.decode(mockSession, IoBuffer.wrap(GOOD_TX_TAIL_FRAME), mockOut);
		verifyZeroInteractions(mockOut);
	}

	@Test
	public void testKissFrameFullDuplex() throws Exception {
		decoder.decode(mockSession, IoBuffer.wrap(GOOD_FULL_DUPLEX_FRAME), mockOut);
		verifyZeroInteractions(mockOut);
	}

	@Test
	public void testKissFrameSetHardware() throws Exception {
		decoder.decode(mockSession, IoBuffer.wrap(GOOD_SET_HARDWARE_FRAME), mockOut);
		verifyZeroInteractions(mockOut);
	}

	@Test
	public void testKissFrameReturn() throws Exception {
		decoder.decode(mockSession, IoBuffer.wrap(GOOD_RETURN_FRAME), mockOut);
		verifyZeroInteractions(mockOut);
	}

	@Test
	public void testCorruptCommandTypeFrame() throws Exception {
		decoder.decode(mockSession, IoBuffer.wrap(CORRUPT_COMMAND_TYPE_FRAME), mockOut);
		verifyZeroInteractions(mockOut);
	}
}
