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
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertNull;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.ArrayUtils;
import org.hbird.core.commons.util.BytesUtility;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.embedder.DecoderEmbedder;
import org.jboss.netty.handler.codec.frame.FrameDecoder;
import org.junit.Before;
import org.junit.Test;

public class NettyKissFrameDecoderTest {
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

	private FrameDecoder decoder;

	DecoderEmbedder<byte[]> embedder;

	@Before
	public void setUp() throws Exception {
		decoder = new NettyKissFrameDecoder();
		embedder = new DecoderEmbedder<byte[]>(decoder);
	}

	private void offerAndAssert(byte[] expectedOutput, byte[]... testInputs) {
		byte[] testInput = null;
		for (byte[] testArray : testInputs) {
			testInput = ArrayUtils.addAll(testInput, testArray);
		}

		embedder.offer(ChannelBuffers.copiedBuffer(testInput));
		byte[] actual = embedder.poll();

		assertArrayEquals(expectedOutput, actual);
	}

	private void offerFragmentsAndAssert(byte[] expectedOutput, byte[]... fragments) {
		int count = 0;
		for (byte[] testFrag : fragments) {
			embedder.offer(ChannelBuffers.copiedBuffer(testFrag));
			byte[] output = embedder.poll();
			if (count++ != fragments.length - 1) {
				assertNull(output);
			}
			else {
				assertArrayEquals(expectedOutput, output);
			}
		}

	}

	private void offerAndAssert(List<byte[]> expectedOutputs, byte[]... testInputs) {
		byte[] testInput = null;
		for (byte[] testArray : testInputs) {
			testInput = ArrayUtils.addAll(testInput, testArray);
		}
		System.out.println("Input: " + BytesUtility.hexDump(testInput));

		embedder.offer(ChannelBuffers.copiedBuffer(testInput));

		for (byte[] expectedOutput : expectedOutputs) {
			byte[] actual = embedder.poll();
			assertArrayEquals(expectedOutput, actual);
		}
	}

	@Test
	public void testKissFrameDecode() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F };

		offerAndAssert(expectedOutput, testInput);
	}

	@Test
	public void testKissFrameDecodeRubbishEitherSide() throws Exception {
		byte[] testInput = new byte[] { 0x45, 0x10, FEND, DATA_FRAME, 0x08, 0x1F, FEND, 0x0F };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F };

		offerAndAssert(expectedOutput, testInput);
	}

	@Test
	public void testKissFrameDecodeDoubleFendAtStart() throws Exception {
		byte[] testInput = new byte[] { FEND, FEND, DATA_FRAME, 0x08, 0x1F, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F };

		offerAndAssert(expectedOutput, testInput);
	}

	@Test
	public void testKissFrameDecodeDoubleFendWithRubbish() throws Exception {
		byte[] testInput = new byte[] { 0x45, FEND, FEND, DATA_FRAME, 0x08, 0x1F, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F };

		offerAndAssert(expectedOutput, testInput);
	}

	@Test
	public void testKissFrameDecodeCorruptBeforeGoodWithRubbishAtTheStart() throws Exception {
		byte[] testInput = new byte[] { 0x45, FEND, DATA_FRAME, 0x56, 0x00, FEND, DATA_FRAME, 0x08, 0x1F, FEND };
		byte[] expectedOutput = new byte[] { 0x56, 0x00 };

		offerAndAssert(expectedOutput, testInput);
	}

	@Test
	public void testKissFrameDecodeEscapedFendValidFrame() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, FESC, TFEND, 0x08, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, (byte) 0xC0, 0x08 };

		offerAndAssert(expectedOutput, testInput);
	}

	/**
	 * C0 00 DB DC 80 85 09 02 66 9A 05 00 00 59 00 57 C0
	 * 
	 * @throws Exception
	 */
	@Test
	public void testStrandFrame() throws Exception {
		byte[] testInput = new byte[] { (byte) 0xC0, 0x00, (byte) 0xDB, (byte) 0xDC, (byte) 0x80, (byte) 0x85, 0x09, 0x02, 0x66, (byte) 0x9A, 0x05, 0x00, 0x00,
				0x59, 0x00, 0x57, (byte) 0xC0 };
		byte[] expectedOutput = new byte[] { (byte) 0xC0, (byte) 0x80, (byte) 0x85, 0x09, 0x02, 0x66, (byte) 0x9A, 0x05, 0x00, 0x00, 0x59, 0x00, 0x57 };

		offerAndAssert(expectedOutput, testInput);
	}

	@Test
	public void testKissFrameDecodeEscapedEscapeValidFrame() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, FESC, TFESC, 0x08, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, (byte) 0xDB, 0x08 };

		offerAndAssert(expectedOutput, testInput);
	}

	@Test
	public void testKissFrameDecodeEscapedEscapeNoisyFrame() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, FESC, 0x08, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, 0x08 };

		offerAndAssert(expectedOutput, testInput);
	}

	@Test
	public void testKissFrameDecodeEscapedFendNoisyFrame() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, FESC, (byte) 0xFF, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, (byte) 0xFF };

		offerAndAssert(expectedOutput, testInput);
	}

	@Test
	public void testKissFrameDecodeBackToBackFrames() throws Exception {
		byte[] testInput = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F, (byte) 0xFF, FEND, FEND, DATA_FRAME, 0x11, 0x11, 0x11, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, (byte) 0xFF };
		byte[] expectedOutput2 = new byte[] { 0x11, 0x11, 0x11 };

		List<byte[]> expecteds = new ArrayList<byte[]>(2);
		expecteds.add(expectedOutput);
		expecteds.add(expectedOutput2);
		offerAndAssert(expecteds, testInput);
	}

	@Test
	public void testKissFrameDecodeSplitFrame() throws Exception {
		byte[] testInputPart1 = new byte[] { FEND, DATA_FRAME, 0x08, 0x1F };
		byte[] testInputPart2 = new byte[] { 0x0F, 0x54, FEND };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, 0x0F, 0x54 };

		offerAndAssert(expectedOutput, testInputPart1, testInputPart2);
	}

	@Test
	public void testKissFrameDecodeSplitFrameRubbishEitherSide() throws Exception {
		byte[] testInputPart1 = new byte[] { 0x56, 0x7A, FEND, DATA_FRAME, 0x08, 0x1F };
		byte[] testInputPart2 = new byte[] { 0x0F, 0x54, FEND, 0x56, 0x7A };
		byte[] expectedOutput = new byte[] { 0x08, 0x1F, 0x0F, 0x54 };

		offerAndAssert(expectedOutput, testInputPart1, testInputPart2);
	}

	@Test
	public void testKissFrameDecodeMultipleGoodFrame() throws Exception {
		final int numGoodFrames = 2;

		byte[] rubbish = new byte[] { 0x56, 0x7A };
		byte[] rubbishWithFend = new byte[] { 0x0F, 0x54, FEND };

		List<byte[]> expectedOutputs = new ArrayList<byte[]>(numGoodFrames);
		expectedOutputs.add(GOOD_DATA_FRAME_CONTENT);
		expectedOutputs.add(GOOD_DATA_FRAME_CONTENT);

		offerAndAssert(expectedOutputs, rubbish, GOOD_DATA_FRAME, GOOD_DATA_FRAME, rubbishWithFend);
	}

	@Test
	public void testKissFrameDecodeMultipleGoodFrameSplit() throws Exception {
		final int numGoodFrames = 2;

		byte[] rubbish = new byte[] { 0x56, 0x7A };
		byte[] frameWithRubbish = ArrayUtils.addAll(new byte[] { 0x45 }, GOOD_DATA_FRAME);
		byte[] frameWithRubbish2 = ArrayUtils.addAll(new byte[] { 0x55 }, GOOD_DATA_FRAME);
		byte[] rubbishWithFend = new byte[] { 0x0F, 0x54, FEND };

		List<byte[]> expectedOutputs = new ArrayList<byte[]>(numGoodFrames);
		expectedOutputs.add(GOOD_DATA_FRAME_CONTENT);
		expectedOutputs.add(GOOD_DATA_FRAME_CONTENT);

		offerAndAssert(expectedOutputs, rubbish, frameWithRubbish, frameWithRubbish2, rubbishWithFend);
	}

	@Test
	public void testKissFrameDecodeDoubleFendStart() throws Exception {
		byte[] doubleFendTheEndBuffer = new byte[] { FEND, FEND };

		offerAndAssert(GOOD_DATA_FRAME_CONTENT, doubleFendTheEndBuffer, GOOD_DATA_FRAME);
	}

	@Test
	public void testKissFrameDecodeTripleFendStart() throws Exception {
		byte[] tripleFendTheEndBuffer = new byte[] { FEND, FEND, FEND };

		offerAndAssert(GOOD_DATA_FRAME_CONTENT, tripleFendTheEndBuffer, GOOD_DATA_FRAME);
	}

	@Test
	public void testKissFrameDecodeTripleFendFlush() throws Exception {
		final int numGoodFrames = 2;

		byte[] tripleFendTheEndBuffer = new byte[] { FEND, FEND, FEND };

		List<byte[]> expectedOutputs = new ArrayList<byte[]>(numGoodFrames);
		expectedOutputs.add(GOOD_DATA_FRAME_CONTENT);
		expectedOutputs.add(GOOD_DATA_FRAME_CONTENT);

		offerAndAssert(expectedOutputs, GOOD_DATA_FRAME, tripleFendTheEndBuffer, GOOD_DATA_FRAME);
	}

	@Test
	public void testKissFrameDecodeDataFrameEmptyBufferAfterDataCommand() throws Exception {
		byte[] uptoCommandType = ArrayUtils.subarray(GOOD_DATA_FRAME, 0, 2);
		byte[] restOfFrame = ArrayUtils.subarray(GOOD_DATA_FRAME, 2, GOOD_DATA_FRAME.length);

		offerAndAssert(GOOD_DATA_FRAME_CONTENT, uptoCommandType, restOfFrame);
	}

	@Test
	public void testFragmentationGoodFrame() throws Exception {
		byte[] firstFrag = new byte[] { FEND, DATA_FRAME };
		byte[] secondFrag = GOOD_DATA_FRAME_CONTENT.clone();
		byte[] thridFrag = new byte[] { FEND };

		offerFragmentsAndAssert(GOOD_DATA_FRAME_CONTENT, firstFrag, secondFrag, thridFrag);
	}

	@Test
	public void testKissFrameTxDelay() throws Exception {
		byte[] expected = null;
		offerAndAssert(expected, GOOD_TX_DELAY_FRAME);
	}

	@Test
	public void testKissFrameP() throws Exception {
		byte[] expected = null;
		offerAndAssert(expected, GOOD_P_FRAME);
	}

	@Test
	public void testKissFrameSlotTime() throws Exception {
		byte[] expected = null;
		offerAndAssert(expected, GOOD_SLOT_TIME_FRAME);
	}

	@Test
	public void testKissFrameTxTail() throws Exception {
		byte[] expected = null;
		offerAndAssert(expected, GOOD_TX_TAIL_FRAME);
	}

	@Test
	public void testKissFrameFullDuplex() throws Exception {
		byte[] expected = null;
		offerAndAssert(expected, GOOD_FULL_DUPLEX_FRAME);
	}

	@Test
	public void testKissFrameSetHardware() throws Exception {
		byte[] expected = null;
		offerAndAssert(expected, GOOD_SET_HARDWARE_FRAME);
	}

	@Test
	public void testKissFrameReturn() throws Exception {
		byte[] expected = null;
		offerAndAssert(expected, GOOD_RETURN_FRAME);
	}

	@Test
	public void testCorruptCommandTypeFrame() throws Exception {
		byte[] expected = null;
		offerAndAssert(expected, CORRUPT_COMMAND_TYPE_FRAME);
	}
}
