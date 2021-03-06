package org.hbird.core.commons.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.BitSet;

import org.hbird.core.commons.util.exceptions.BitSetOperationException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BitSetUtilityTest {
	private static final Logger LOG = LoggerFactory.getLogger(BitSetUtilityTest.class);

	private static final String BIT_STR_BE_123 = "01111011";
	private static BitSet BITSET_BE_123;
	private static final int BITSET_BE_123_DEC_BYTE_REP = 123;
	private static final byte[] BYTES_BE_123 = new byte[] { 123 };

	private static final String BIT_STR_LE_123 = "1101111";
	private static BitSet BITSET_LE_123;

	private static final String BIT_STR_BE_999 = "01111100111";
	private static BitSet BITSET_BE_999;

	private static final String BIT_STR_LE_999 = "11100111110";
	private static BitSet BITSET_LE_999;

	private static final String BIT_STR_BE_69BIT = "110000000000000000000000000000000000000000000000000000000000000000001";
	private static BitSet BITSET_BE_69BIT;

	private static final String BIT_STR_FLOAT32_NEG1658_035 = "11000100110011110100000100011111";
	private static BitSet BITSET_FLOAT32_NEG1658_035;

	private static final String BIT_STR_FLOAT64_89433_23532268 = "0100000011110101110101011001001111000011111000011011011011101010";

	private static BitSet BITSET_FLOAT64_89433_23532268;

	private final static String MULTI_BYTE_STRING = "00000000000100000000000000000000000000000000000000000000000000011110011101110100000000000000000000011110";
	private final static byte[] MULTI_BYTE_ARRAY = new byte[] { 0, 16, 0, 0, 0, 0, 0, 1, -25, 116, 0, 0, 30 };
	private static BitSet MULTI_BYTE_BITSET;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		LOG.info("############ Setting up before all tests #################");
		BITSET_BE_123 = new BitSet(BIT_STR_BE_123.length());
		BITSET_BE_123.set(1, 5);
		BITSET_BE_123.set(6, 8);
		String expected = BitSetUtility.bitSetToBinaryString(BITSET_BE_123, true);
		assertEquals(expected, BIT_STR_BE_123);

		BITSET_LE_123 = new BitSet(BIT_STR_LE_123.length());
		BITSET_LE_123.set(0, 2);
		BITSET_LE_123.set(3, 7);
		assertEquals(BIT_STR_LE_123, BitSetUtility.bitSetToBinaryString(BITSET_LE_123, true));

		BITSET_BE_999 = new BitSet(BIT_STR_BE_999.length());
		BITSET_BE_999.set(1, 6);
		BITSET_BE_999.set(8, 11);
		assertEquals(BIT_STR_BE_999, BitSetUtility.bitSetToBinaryString(BITSET_BE_999, true));

		BITSET_LE_999 = new BitSet(BIT_STR_LE_999.length());
		BITSET_LE_999.set(0, 3);
		BITSET_LE_999.set(5, 10);
		assertEquals(BIT_STR_LE_999, BitSetUtility.bitSetToBinaryString(BITSET_LE_999, BIT_STR_LE_999.length()));

		BITSET_BE_69BIT = new BitSet(BIT_STR_BE_69BIT.length());
		BITSET_BE_69BIT.set(0, 2);
		BITSET_BE_69BIT.set(68);
		assertEquals(BIT_STR_BE_69BIT, BitSetUtility.bitSetToBinaryString(BITSET_BE_69BIT, BIT_STR_BE_69BIT.length()));

		BITSET_FLOAT32_NEG1658_035 = new BitSet(BIT_STR_FLOAT32_NEG1658_035.length());
		BITSET_FLOAT32_NEG1658_035 = BitSetUtility.stringToBitSet(BIT_STR_FLOAT32_NEG1658_035, true, true);
		assertEquals(BIT_STR_FLOAT32_NEG1658_035, BitSetUtility.bitSetToBinaryString(BITSET_FLOAT32_NEG1658_035, Float.SIZE));

		BITSET_FLOAT64_89433_23532268 = new BitSet(BIT_STR_FLOAT64_89433_23532268.length());
		BITSET_FLOAT64_89433_23532268 = BitSetUtility.stringToBitSet(BIT_STR_FLOAT64_89433_23532268, true, true);
		assertEquals(BIT_STR_FLOAT64_89433_23532268, BitSetUtility.bitSetToBinaryString(BITSET_FLOAT64_89433_23532268, Double.SIZE));

		MULTI_BYTE_BITSET = new BitSet(MULTI_BYTE_STRING.length());
		MULTI_BYTE_BITSET = BitSetUtility.stringToBitSet(MULTI_BYTE_STRING, true, true);
		assertEquals(MULTI_BYTE_STRING, BitSetUtility.bitSetToBinaryString(MULTI_BYTE_BITSET, MULTI_BYTE_STRING.length()));
	}

	@SuppressWarnings("static-method")
	@Test
	public final void testStringToBitSetBE69BIT() throws BitSetOperationException {
		LOG.info("############ Starting test #################");
		final BitSet actual = BitSetUtility.stringToBitSet(BIT_STR_BE_69BIT, true, true);
		assertEquals(BITSET_BE_69BIT, actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void testStringToBitSetBE123() throws BitSetOperationException {
		final BitSet actual = BitSetUtility.stringToBitSet(BIT_STR_BE_123, true, true);
		assertEquals(BITSET_BE_123, actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void testStringToBitSetLE123() throws BitSetOperationException {
		final BitSet actual = BitSetUtility.stringToBitSet(BIT_STR_LE_123, false, false);
		assertEquals(BITSET_LE_123, actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void testStringToBitSetBE999() throws BitSetOperationException {
		LOG.debug("############ Starting test #################");
		final BitSet actual = BitSetUtility.stringToBitSet(BIT_STR_BE_999, true, true);
		assertEquals(BITSET_BE_999, actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void testStringToBitSetLE999() throws BitSetOperationException {
		LOG.debug("############ Starting test #################");
		final BitSet actual = BitSetUtility.stringToBitSet(BIT_STR_LE_999, false, false);
		assertEquals(BITSET_LE_999, actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void testBitSetToBinaryStringFixedSize() {
		LOG.debug("############ Starting test #################");
		String actual = BitSetUtility.bitSetToBinaryString(BITSET_BE_123, 1);
		assertEquals(BIT_STR_BE_123.subSequence(0, 1), actual);

		actual = BitSetUtility.bitSetToBinaryString(BITSET_BE_123, 5);
		assertEquals(BIT_STR_BE_123.subSequence(0, 5), actual);

		actual = BitSetUtility.bitSetToBinaryString(BITSET_BE_123, 8);
		assertEquals(BIT_STR_BE_123, actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void testPadStringFromTheBack() {
		LOG.debug("############ Starting test #################");
		final String actual = BitSetUtility.padStringFromTheBack(BIT_STR_BE_123, 25);
		assertEquals(25, actual.length());
		final StringBuilder expected = new StringBuilder();
		expected.append(BIT_STR_BE_123);
		expected.append("00000000000000000");
		assertEquals(expected.toString(), actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void testStringLittleToBigEndian() throws BitSetOperationException {
		LOG.debug("############ Starting test #################");
		final BitSet actual = BitSetUtility.stringToBitSet(BIT_STR_LE_999, false, true);
		assertEquals(BITSET_BE_999, actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void testStringBigToLittleEndian() throws BitSetOperationException {
		LOG.debug("############ Starting test #################");
		final BitSet actual = BitSetUtility.stringToBitSet(BIT_STR_BE_999, true, false);
		assertEquals(BITSET_LE_999, actual);
	}

	@SuppressWarnings("static-method")
	@Test(expected = BitSetOperationException.class)
	public final void testInvalidBitString() throws BitSetOperationException {
		LOG.debug("############ Starting test #################");
		BitSetUtility.stringToBitSet("16783287467823", false, true);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void testBitSetToBinaryStringNotLogicalSize() {
		LOG.debug("############ Starting test #################");
		final String actual = BitSetUtility.bitSetToBinaryString(BITSET_BE_123, false);
		final String expected = "0111101100000000000000000000000000000000000000000000000000000000";
		assertEquals(expected, actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void toFloat() {
		LOG.debug("############ Starting test #################");
		final float actual = BitSetUtility.toFloat(BITSET_FLOAT32_NEG1658_035);
		// TODO Does anybody know what an appropriate delta is?
		assertEquals(-1658.035f, actual, 0.000000000000000001);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void toDouble() {
		LOG.debug("############ Starting test #################");
		final double actual = BitSetUtility.toDouble(BITSET_FLOAT64_89433_23532268);
		// TODO Does anybody know what an appropriate delta is?
		assertEquals(89433.23532268d, actual, 0.000000000000000001);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void toByteArraySingleByteRequired() {
		LOG.debug("############ Starting test #################");
		final byte[] actual = BitSetUtility.toByteArray(BITSET_BE_123, 8);
		final byte[] expected = new byte[] { BITSET_BE_123_DEC_BYTE_REP };
		assertArrayEquals(expected, actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void toByteArrayTwoBytesRequired() {
		LOG.debug("############ Starting test #################");
		final byte[] actual = BitSetUtility.toByteArray(BITSET_BE_999, 11);
		final byte[] expected = new byte[] { 0x3, (byte) 0xE7 };
		assertArrayEquals(expected, actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void fromByteArray() {
		LOG.debug("############ Starting test #################");
		final BitSet actual = BitSetUtility.fromByteArray(BYTES_BE_123);
		assertEquals(BITSET_BE_123, actual);
	}

	@SuppressWarnings("static-method")
	@Test
	public final void fromByteArrayMultiByte() {
		LOG.debug("############ Starting test #################");
		final BitSet actual = BitSetUtility.fromByteArray(MULTI_BYTE_ARRAY);
		LOG.debug(BitSetUtility.binDump(actual));
		assertEquals(MULTI_BYTE_BITSET, actual);
	}

}
