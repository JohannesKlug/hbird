package org.hbird.business.calibration;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hbird.transport.spacesystemmodel.parameters.HummingbirdParameter;
import org.hbird.transport.spacesystemmodel.parameters.Parameter;
import org.junit.Test;

public class PolynomialCalibratorTest {
	
	@Test
	public void testCalibration() {
		Map<String, List<Double>> calibrationsMap = new HashMap<String, List<Double>>();
		
		List<Double> polynomials = new ArrayList<Double>();
		// y = +9 +3x +2x^2 +1x^3
		polynomials.add(9d);
		polynomials.add(3d);
		polynomials.add(2d);
		polynomials.add(1d);
		
		calibrationsMap.put("Test.TestParam", polynomials);
		
		PolynomialCalibrator calibrator = new PolynomialCalibrator(calibrationsMap);
		
		Parameter<Integer> testParam = new HummingbirdParameter<Integer>("Test.TestParam", "TestParam", "", "");
		testParam.setValue(3);
		
		Parameter<Double> calibratedParameter = calibrator.calibrateParameter(testParam);
		
		// y = +9 +3x +2x^2 +1x^3
		// x = 3
		// y = 63
		
		double expected = (Double) calibratedParameter.getValue();
		
		assertEquals(63d, expected, 0.00001d);
		
	}

}
