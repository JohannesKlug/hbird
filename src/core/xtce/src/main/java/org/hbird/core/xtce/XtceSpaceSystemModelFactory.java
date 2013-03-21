package org.hbird.core.xtce;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.XMLContext;
import org.hbird.core.generatedcode.xtce.Argument;
import org.hbird.core.generatedcode.xtce.ArgumentAssignment;
import org.hbird.core.generatedcode.xtce.ArgumentList;
import org.hbird.core.generatedcode.xtce.ArgumentListItem;
import org.hbird.core.generatedcode.xtce.ArgumentTypeSetItem;
import org.hbird.core.generatedcode.xtce.BaseContainer;
import org.hbird.core.generatedcode.xtce.BaseDataTypeChoice;
import org.hbird.core.generatedcode.xtce.BaseMetaCommand;
import org.hbird.core.generatedcode.xtce.BinaryParameterType;
import org.hbird.core.generatedcode.xtce.CommandMetaData;
import org.hbird.core.generatedcode.xtce.Comparison;
import org.hbird.core.generatedcode.xtce.ComparisonList;
import org.hbird.core.generatedcode.xtce.ContainerSet;
import org.hbird.core.generatedcode.xtce.EntryList;
import org.hbird.core.generatedcode.xtce.FloatParameterType;
import org.hbird.core.generatedcode.xtce.IntegerArgumentType;
import org.hbird.core.generatedcode.xtce.IntegerParameterType;
import org.hbird.core.generatedcode.xtce.MetaCommand;
import org.hbird.core.generatedcode.xtce.MetaCommandSet;
import org.hbird.core.generatedcode.xtce.MetaCommandSetItem;
import org.hbird.core.generatedcode.xtce.ParameterProperties;
import org.hbird.core.generatedcode.xtce.ParameterRefEntry;
import org.hbird.core.generatedcode.xtce.ParameterSetTypeItem;
import org.hbird.core.generatedcode.xtce.ParameterTypeSet;
import org.hbird.core.generatedcode.xtce.ParameterTypeSetTypeItem;
import org.hbird.core.generatedcode.xtce.SequenceContainer;
import org.hbird.core.generatedcode.xtce.SizeRangeInCharacters;
import org.hbird.core.generatedcode.xtce.SpaceSystem;
import org.hbird.core.generatedcode.xtce.StringParameterType;
import org.hbird.core.generatedcode.xtce.TelemetryMetaData;
import org.hbird.core.generatedcode.xtce.types.FloatDataEncodingTypeEncodingType;
import org.hbird.core.generatedcode.xtce.types.IntegerDataEncodingTypeEncodingType;
import org.hbird.core.spacesystemmodel.SpaceSystemModel;
import org.hbird.core.spacesystemmodel.SpaceSystemModelFactory;
import org.hbird.core.spacesystemmodel.encoding.Encoding;
import org.hbird.core.spacesystemmodel.encoding.Encoding.BinaryRepresentation;
import org.hbird.core.spacesystemmodel.exceptions.InvalidSpaceSystemDefinitionException;
import org.hbird.core.spacesystemmodel.tmtc.CommandGroup;
import org.hbird.core.spacesystemmodel.tmtc.Parameter;
import org.hbird.core.spacesystemmodel.tmtc.ParameterGroup;
import org.hbird.core.spacesystemmodel.tmtc.TmTcGroup;
import org.hbird.core.spacesystemmodel.tmtc.provided.HummingbirdCommandGroup;
import org.hbird.core.spacesystemmodel.tmtc.provided.HummingbirdParameter;
import org.hbird.core.spacesystemmodel.tmtc.provided.HummingbirdParameterGroup;
import org.hbird.core.spacesystemmodel.tmtc.provided.ProtectedValueParameter;
import org.hbird.core.xtce.utils.XtceToJavaMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Ints;

public class XtceSpaceSystemModelFactory implements SpaceSystemModelFactory {

	private static final String TC_QUALIFIED_NAME_POSTFIX = ".tc.";

	public static final BinaryRepresentation DEFAULT_STRING_ENCODING = BinaryRepresentation.UTF8;

	private static final Logger LOG = LoggerFactory.getLogger(XtceSpaceSystemModelFactory.class);

	private String spaceSystemModelFilename;

	private SpaceSystem spaceSystem;

	private SpaceSystemModel model;

	private String modelName;

	private final Map<String, ParameterTypeSetTypeItem> xtceTmParameterTypes = new LinkedHashMap<String, ParameterTypeSetTypeItem>();

	private final Map<String, ParameterTypeSetTypeItem> xtceTcParameterTypes = new LinkedHashMap<String, ParameterTypeSetTypeItem>();

	private final Map<String, Parameter<Integer>> integerParameters = new LinkedHashMap<String, Parameter<Integer>>();

	private final Map<String, Parameter<Integer>> integerArguments = new LinkedHashMap<String, Parameter<Integer>>();

	private final Map<String, Parameter<Long>> longParameters = new LinkedHashMap<String, Parameter<Long>>();

	private final Map<String, Parameter<Long>> longArguments = new LinkedHashMap<String, Parameter<Long>>();

	private final Map<String, Parameter<Float>> floatParameters = new LinkedHashMap<String, Parameter<Float>>();

	private final Map<String, Parameter<Float>> floatArguments = new LinkedHashMap<String, Parameter<Float>>();

	private final Map<String, Parameter<Double>> doubleParameters = new LinkedHashMap<String, Parameter<Double>>();

	private final Map<String, Parameter<Double>> doubleArguments = new LinkedHashMap<String, Parameter<Double>>();

	private final Map<String, Parameter<BigDecimal>> bigDecimalParameters = new LinkedHashMap<String, Parameter<BigDecimal>>();

	private final Map<String, Parameter<BigDecimal>> bigDecimalArguments = new LinkedHashMap<String, Parameter<BigDecimal>>();

	private final Map<String, Parameter<String>> stringParameters = new LinkedHashMap<String, Parameter<String>>();

	private final Map<String, Parameter<String>> stringArguments = new LinkedHashMap<String, Parameter<String>>();

	private final Map<String, Parameter<Byte[]>> rawParameters = new LinkedHashMap<String, Parameter<Byte[]>>();

	private final Map<String, Parameter<Byte[]>> rawArguments = new LinkedHashMap<String, Parameter<Byte[]>>();

	/**
	 * This contains all the parameter groups (layouts if you like) defined in XTCE. It's contents are injected into the
	 * model this factory creates.
	 */
	private final Map<String, ParameterGroup> tmParameterGroups = new HashMap<String, ParameterGroup>();

	private final Map<String, CommandGroup> tcGroups = new HashMap<String, CommandGroup>();

	private final Map<String, List<Object>> restrictions = new HashMap<String, List<Object>>();

	private final Map<String, Encoding> encodings = new HashMap<String, Encoding>();

	public XtceSpaceSystemModelFactory() {
		LOG.debug("Instantiating XtceSpaceSystemModelFactory with no space system file path");
	}

	public XtceSpaceSystemModelFactory(final String spaceSystemModelFilename) {
		LOG.debug("Instantiating XtceSpaceSystemModelFactory with filename " + spaceSystemModelFilename);
		this.spaceSystemModelFilename = spaceSystemModelFilename;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.hbird.core.xtce.SpaceSystemModelFactory#createSpaceSystemModel(java.lang.String)
	 */
	@Override
	public final SpaceSystemModel createSpaceSystemModel() throws InvalidSpaceSystemDefinitionException {

		if (spaceSystemModelFilename == null) {
			LOG.error("No path to xtce file set");
			throw new InvalidSpaceSystemDefinitionException("No path to xtce file set");
		}

		LOG.trace("Creating space system model using XTCE file = " + spaceSystemModelFilename);

		// Reset the previous model and collections used to construct the model
		model = new XtceSpaceSystemModel();
		tmParameterGroups.clear();
		tcGroups.clear();
		restrictions.clear();
		encodings.clear();

		try {
			spaceSystem = unmarshallXtceXmlSpaceSystem(spaceSystemModelFilename);
		}
		catch (final Exception e) {
			throw new InvalidSpaceSystemDefinitionException(e);
		}

		modelName = spaceSystem.getName();

		createTelemetryModel();
		createCommandModel();

		try {
			injectConstructsIntoModel();
		}
		catch (final IllegalArgumentException e) {
			LOG.error("Critical Error creating XTCE based Space System Model");
			// TODO - 27.03.2012 kimmell - replace with appropriate exception
		}
		catch (final IllegalAccessException e) {
			LOG.error("Critical Error creating XTCE based Space System Model");
			// TODO - 27.03.2012 kimmell - replace with appropriate exception
		}

		if (LOG.isTraceEnabled()) {
			LOG.trace("Space system model creation complete");
		}

		return model;
	}

	/**
	 * Creates the generated class model from the XML file.
	 * 
	 * @param spacesystemmodelFilename
	 * @return
	 * @throws FileNotFoundException
	 * @throws ValidationException
	 * @throws MarshalException
	 */
	// SpaceSystem is not a closeable
	@SuppressWarnings("resource")
	private static final SpaceSystem unmarshallXtceXmlSpaceSystem(String spacesystemmodelFilename) throws MarshalException, ValidationException,
			FileNotFoundException {
		SpaceSystem spaceSystem = null;
		final XMLContext context = new XMLContext();

		// Create a new Unmarshaller
		final Unmarshaller unmarshaller = context.createUnmarshaller();
		unmarshaller.setClass(SpaceSystem.class);

		// Unmarshall the space system object
		spaceSystem = (SpaceSystem) unmarshaller.unmarshal(new FileReader(spacesystemmodelFilename));
		return spaceSystem;
	}

	private void createTelemetryModel() throws InvalidSpaceSystemDefinitionException {
		createAllTmParameterTypes(spaceSystem.getTelemetryMetaData());
		createAllTmParameters();
		createAllParameterGroups();
		populateParameterGroups();
	}

	private void createCommandModel() throws InvalidSpaceSystemDefinitionException {
		final CommandMetaData commandMetaData = spaceSystem.getCommandMetaData();
		if (commandMetaData == null) {
			LOG.info("No command metadata defined");
			return;
		}
		createAllTcArgumentTypes(commandMetaData);
		createAllTcArguments();
		createAllCommandGroups();
		populateCommandGroups();
	}

	/**
	 * Bit nasty we have another method but we are working with XML. The generated code creates two separate classes
	 * TelemetryMetaData and CommandMetaData.
	 * 
	 * @param commandMetaData
	 * @throws InvalidSpaceSystemDefinitionException
	 */
	private final void createAllTcArgumentTypes(final CommandMetaData commandMetaData) throws InvalidSpaceSystemDefinitionException {
		final ParameterTypeSet parameterTypeSet = commandMetaData.getParameterTypeSet();

		if (parameterTypeSet == null) {
			LOG.error("Hbird only supports ParameterTypeSets in CommandMetaData. Check your XTCE file: " + spaceSystemModelFilename);
			throw new InvalidSpaceSystemDefinitionException("Hbird only supports ParameterTypeSets in CommandMetaData. Check your XTCE file: "
					+ spaceSystemModelFilename);
		}

		final int numberOfParameterTypes = parameterTypeSet.getParameterTypeSetTypeItemCount();

		for (int parameterTypeIndex = 0; parameterTypeIndex < numberOfParameterTypes; ++parameterTypeIndex) {
			final ParameterTypeSetTypeItem item = parameterTypeSet.getParameterTypeSetTypeItem(parameterTypeIndex);
			final String name = getParameterName(item);
			xtceTcParameterTypes.put(name, item);
		}
	}

	/**
	 * @param telemetryMetaData
	 * @return
	 * @throws InvalidSpaceSystemDefinitionException
	 */
	private final void createAllTmParameterTypes(final TelemetryMetaData telemetryMetaData) throws InvalidSpaceSystemDefinitionException {
		final int numberOfParameterTypes = telemetryMetaData.getParameterTypeSet().getParameterTypeSetTypeItemCount();

		for (int parameterTypeIndex = 0; parameterTypeIndex < numberOfParameterTypes; ++parameterTypeIndex) {
			final ParameterTypeSetTypeItem item = telemetryMetaData.getParameterTypeSet().getParameterTypeSetTypeItem(parameterTypeIndex);
			final String name = getParameterName(item);
			xtceTmParameterTypes.put(name, item);
		}
	}

	private final void createAllTmParameters() throws InvalidSpaceSystemDefinitionException {
		final TelemetryMetaData categoryMetaData = spaceSystem.getTelemetryMetaData();
		final int numberOfParameters = categoryMetaData.getParameterSet().getParameterSetTypeItemCount();

		// @formatter:off
		for (int i = 0; i < numberOfParameters; ++i) {
			final ParameterSetTypeItem xtceParameter = categoryMetaData.getParameterSet().getParameterSetTypeItem(i);
			final String parameterTypeRef = xtceParameter.getParameter().getParameterTypeRef();
			final ParameterTypeSetTypeItem xtceType = xtceTmParameterTypes.get(parameterTypeRef);

			if (xtceType == null) {
				throw new InvalidSpaceSystemDefinitionException("Unknown TM parameter type: " + parameterTypeRef
						+ ". A parameter references an undeclared TM parameter type in the XTCE space system definition file.");
			}

			final String qualifiedNamePrefix = spaceSystem.getName() + ".tm.";
			final String name = xtceParameter.getParameter().getName();
			final String qualifiedName = qualifiedNamePrefix + name;
			final String shortDescription = xtceParameter.getParameter().getShortDescription();
			final String longDescription = xtceParameter.getParameter().getLongDescription();

			// If it's an integer type ...
			if (xtceType.getIntegerParameterType() != null) {
				final IntegerParameterType type = xtceType.getIntegerParameterType();
				if (!XtceToJavaMapping.doesXtceIntRequireJavaLong(type)) {
					final Parameter<Integer> intParameter = new HummingbirdParameter<Integer>(qualifiedName, name, shortDescription, longDescription);
					LOG.debug("Adding Integer parameter {}", intParameter.getQualifiedName());
					integerParameters.put(intParameter.getQualifiedName(), intParameter);
					encodings.put(intParameter.getQualifiedName(), createXtceIntegerEncoding(type));
				}
				else {
					final Parameter<Long> longParameter = new HummingbirdParameter<Long>(qualifiedName, name, shortDescription, longDescription);
					LOG.debug("Adding Long parameter {}", longParameter.getQualifiedName());
					longParameters.put(longParameter.getQualifiedName(), longParameter);
					encodings.put(longParameter.getQualifiedName(), createXtceIntegerEncoding(type));
				}
			}

			// If it's a float type ...
			else if (xtceType.getFloatParameterType() != null) {
				final FloatParameterType type = xtceType.getFloatParameterType();
				switch (type.getSizeInBits()) {
					case VALUE_32:
						final Parameter<Float> floatParameter = new HummingbirdParameter<Float>(qualifiedName, name, shortDescription, longDescription);
						LOG.debug("Adding Float parameter {}", floatParameter.getQualifiedName());
						floatParameters.put(floatParameter.getQualifiedName(), floatParameter);
						// TODO - 27.03.2012 kimmell - add encoding
						break;
					case VALUE_64:
						final Parameter<Double> doubleParameter = new HummingbirdParameter<Double>(qualifiedName, name, shortDescription, longDescription);
						LOG.debug("Adding Double parameter {}", doubleParameter.getQualifiedName());
						doubleParameters.put(doubleParameter.getQualifiedName(), doubleParameter);
						// TODO - 27.03.2012 kimmell - add encoding
						break;
					case VALUE_128:
						final Parameter<BigDecimal> bigDecimalParameter = new HummingbirdParameter<BigDecimal>(qualifiedName, name, shortDescription, longDescription);
						LOG.debug("Adding BigDecimal parameter {}", bigDecimalParameter.getQualifiedName());
						bigDecimalParameters.put(bigDecimalParameter.getQualifiedName(), bigDecimalParameter);
						// TODO - 27.03.2012 kimmell - add encoding
						break;
					default:
						throw new InvalidSpaceSystemDefinitionException("Invalid bit size for float type " + type.getName());
				}
			}

			// If it's a string type ...
			else if (xtceType.getStringParameterType() != null) {
				final StringParameterType type = xtceType.getStringParameterType();
				SizeRangeInCharacters sizeRange = type.getSizeRangeInCharacters();
				if(sizeRange == null) {
					final String msg = "Hbird only supports String arguments that have a size range defined. The maximum range will correspond to the maximum string length. Type: " + type.getName() + " violates this.";
					LOG.error(msg);
					throw new InvalidSpaceSystemDefinitionException(msg);
				}
				
				final Parameter<String> stringParameter = new HummingbirdParameter<String>(qualifiedName, name, shortDescription, longDescription);
				LOG.debug("Adding String parameter {}", stringParameter.getQualifiedName());
				stringParameters.put(stringParameter.getQualifiedName(), stringParameter);
				encodings.put(stringParameter.getQualifiedName(), createXtceStringEncoding(type));
			}

			// If it's binary type ...
			else if (xtceType.getBinaryParameterType() != null) {
				final BinaryParameterType type = xtceType.getBinaryParameterType();
				final Parameter<Byte[]> rawParameter = new HummingbirdParameter<Byte[]>(qualifiedName, name, shortDescription, longDescription);
				LOG.debug("Adding raw parameter {}", rawParameter.getQualifiedName());
				rawParameters.put(rawParameter.getQualifiedName(), rawParameter);
				encodings.put(rawParameter.getQualifiedName(), createXtceBinaryEncoding(type));
			}

			else {
				throw new InvalidSpaceSystemDefinitionException("Unknown or unsupported parameter type: " + parameterTypeRef
						+ ". A parameter references an undeclared TM parameter type in the XTCE space system definition file.");
			}
			// @formatter:on
		}
	}

	/**
	 * @throws InvalidSpaceSystemDefinitionException
	 */
	private final void createAllTcArguments() throws InvalidSpaceSystemDefinitionException {
		final CommandMetaData commandMetaData = spaceSystem.getCommandMetaData();
		final int numberOfTcArguments = commandMetaData.getParameterSet().getParameterSetTypeItemCount();

		// @formatter:off
		for (int i = 0; i < numberOfTcArguments; ++i) {
			final ParameterSetTypeItem xtceArgument = commandMetaData.getParameterSet().getParameterSetTypeItem(i);
			final ParameterProperties paramProps = xtceArgument.getParameter().getParameterProperties();
			final String parameterTypeRef = xtceArgument.getParameter().getParameterTypeRef();
			final ParameterTypeSetTypeItem xtceType = xtceTcParameterTypes.get(parameterTypeRef);

			// If it's an integer type...
			if (xtceType == null) {
				final String msg = "Unknown TC argument (parameter) type: " + parameterTypeRef
						+ ". A parameter references an undeclared TC argument (parameter) type in the XTCE space system definition file.";
				LOG.error(msg);
				throw new InvalidSpaceSystemDefinitionException(msg);
			}

			final String qualifiedNamePrefix = spaceSystem.getName() + TC_QUALIFIED_NAME_POSTFIX;
			final String name = xtceArgument.getParameter().getName();
			final String qualifiedName = qualifiedNamePrefix + name;
			final String shortDescription = xtceArgument.getParameter().getShortDescription();
			final String longDescription = xtceArgument.getParameter().getLongDescription();

			if (xtceType.getIntegerParameterType() != null) {
				final IntegerParameterType type = xtceType.getIntegerParameterType();
				if (!XtceToJavaMapping.doesXtceIntRequireJavaLong(type)) {
					Parameter<Integer> intParameter;
					if(paramProps == null || !paramProps.isReadOnly()) {
						intParameter = new HummingbirdParameter<Integer>(qualifiedName, name, shortDescription, longDescription);
					}
					else {
						final int initialValue = (int) type.getInitialValue();
						// TODO integer in XTCE is of any size so init vlaue returns long. Should be check initial value is correct?
						intParameter = new ProtectedValueParameter<Integer>(qualifiedName, name, shortDescription, longDescription, initialValue);
						if (LOG.isTraceEnabled()) {
							LOG.trace("Adding ProtectedValueParameter Integer argument " + intParameter.getName());
						}
					}

					integerArguments.put(intParameter.getQualifiedName(), intParameter);
					encodings.put(intParameter.getQualifiedName(), createXtceIntegerEncoding(type));
				}
				else {
					final Parameter<Long> longParameter;
					if(paramProps != null && !paramProps.isReadOnly()) {
						longParameter = new HummingbirdParameter<Long>(qualifiedName, name, shortDescription, longDescription);
					}
					else{
						final long initialValue = type.getInitialValue();
						longParameter = new ProtectedValueParameter<Long>(qualifiedName, name, shortDescription, longDescription, initialValue);
						if (LOG.isTraceEnabled()) {
							LOG.trace("Adding ProtectedValueParameter Long argument " + longParameter.getName());
						}
					}

					longArguments.put(longParameter.getQualifiedName(), longParameter);
					encodings.put(longParameter.getQualifiedName(), createXtceIntegerEncoding(type));
				}
			}

			// If it's an float type...
			else if (xtceType.getFloatParameterType() != null) {
				final FloatParameterType type = xtceType.getFloatParameterType();
				switch (type.getSizeInBits()) {
					case VALUE_32:
						final Parameter<Float> floatParameter = new HummingbirdParameter<Float>(qualifiedName, name, shortDescription, longDescription);
						floatArguments.put(floatParameter.getQualifiedName(), floatParameter);
						if (LOG.isDebugEnabled()) {
							LOG.debug("Adding Float argument " + floatParameter.getName());
						}
						break;
					case VALUE_64:
						final Parameter<Double> doubleParameter = new HummingbirdParameter<Double>(qualifiedName, name, shortDescription, longDescription);
						doubleArguments.put(doubleParameter.getQualifiedName(), doubleParameter);
						if (LOG.isDebugEnabled()) {
							LOG.debug("Adding Double argument " + doubleParameter.getName());
						}
						break;
					case VALUE_128:
						final Parameter<BigDecimal> bigDecimalParameter = new HummingbirdParameter<BigDecimal>(qualifiedName, name, shortDescription, longDescription);
						bigDecimalArguments.put(bigDecimalParameter.getQualifiedName(), bigDecimalParameter);
						if (LOG.isDebugEnabled()) {
							LOG.debug("Adding BigDecimal argument " + bigDecimalParameter.getName());
						}
						break;
					default:
						throw new InvalidSpaceSystemDefinitionException("Could not add command argument " + type.getName() + " because it has an invalid bit size for float type.");
				}
			}

			// string types
			else if(xtceType.getStringParameterType() != null) {
				final StringParameterType type = xtceType.getStringParameterType();
				SizeRangeInCharacters sizeRange = type.getSizeRangeInCharacters();
				if(sizeRange == null) {
					final String msg = "Hbird only supports String arguments that have a size range defined. The maximum range will correspond to the maximum string length. Type: " + type.getName() + " violates this.";
					LOG.error(msg);
					throw new InvalidSpaceSystemDefinitionException(msg);
				}
				
				Parameter<String> stringParameter;
				if(paramProps.isReadOnly()) {
					final String initialValue = type.getInitialValue();
					if(initialValue != null) {
						LOG.trace("Creating readonly protected value parameter " + qualifiedName);
						stringParameter = new ProtectedValueParameter<String>(qualifiedName, name, shortDescription, longDescription, initialValue);
					}
					else {
						final String msg = "Hbird only supports read only parameter or argument types when an initial value is supplied";
						LOG.error(msg);
						throw new InvalidSpaceSystemDefinitionException(msg);
					}
				}
				else {
					stringParameter = new HummingbirdParameter<String>(qualifiedName, name, shortDescription, longDescription);
				}
				if (LOG.isDebugEnabled()) {
					LOG.debug("Adding String argument " + stringParameter.getName());
				}
				stringArguments.put(stringParameter.getQualifiedName(), stringParameter);
				encodings.put(stringParameter.getQualifiedName(), createXtceStringEncoding(type));
			}
			else {
				final String msg = "Unknown or unsupported TC argument (parameter) type: " + parameterTypeRef
						+ ". A parameter references an undeclared TC argument (parameter) type in the XTCE space system definition file.";
				LOG.error(msg);
				throw new InvalidSpaceSystemDefinitionException(msg);
			}
			// @formatter:on
		}
	}

	/**
	 * Create all ParameterGroups. In this iteration we create the parameter groups, but do not create the references
	 * between them as the referenced objects do not yet exit.
	 * 
	 * @throws InvalidSpaceSystemDefinitionException
	 * 
	 */
	private final void createAllParameterGroups() throws InvalidSpaceSystemDefinitionException {
		final String qualifiedNamePrefix = spaceSystem.getName() + ".tm.";
		final int numTmParameterGroups = spaceSystem.getTelemetryMetaData().getContainerSet().getContainerSetTypeItemCount();
		for (int containerIndex = 0; containerIndex < numTmParameterGroups; ++containerIndex) {
			final SequenceContainer xtceContainer = spaceSystem.getTelemetryMetaData().getContainerSet().getContainerSetTypeItem(containerIndex)
					.getSequenceContainer();

			// @formatter:off
			final ParameterGroup parameterGroup =
					new HummingbirdParameterGroup(qualifiedNamePrefix + xtceContainer.getName(), xtceContainer.getName(),
							xtceContainer.getShortDescription(), xtceContainer.getLongDescription());
			// @formatter:on

			tmParameterGroups.put(parameterGroup.getQualifiedName(), parameterGroup);
			populateParameterGroupRestrictions(parameterGroup.getQualifiedName(), xtceContainer);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Created ParameterGroup " + xtceContainer.getName());
			}
		}
	}

	/**
	 * Create all ParameterGroups. In this iteration we create the parameter groups, but do not create the references
	 * between them as the referenced objects do not yet exit.
	 * 
	 * @throws InvalidSpaceSystemDefinitionException
	 * 
	 */
	private final void createAllCommandGroups() throws InvalidSpaceSystemDefinitionException {
		final String qualifiedNamePrefix = spaceSystem.getName() + TC_QUALIFIED_NAME_POSTFIX;
		final int numTcParameterGroups = spaceSystem.getCommandMetaData().getMetaCommandSet().getMetaCommandSetItemCount();
		for (int containerIndex = 0; containerIndex < numTcParameterGroups; ++containerIndex) {
			final MetaCommand metaCommand = spaceSystem.getCommandMetaData().getMetaCommandSet().getMetaCommandSetItem(containerIndex).getMetaCommand();

			if (metaCommand.isAbstract()) {
				continue;
			}

			// @formatter:off
			final CommandGroup parameterGroup =
					new HummingbirdCommandGroup(qualifiedNamePrefix + metaCommand.getName(),
							metaCommand.getName(),
							metaCommand.getShortDescription(),
							metaCommand.getLongDescription());
			// @formatter:on

			tcGroups.put(parameterGroup.getQualifiedName(), parameterGroup);

			if (LOG.isDebugEnabled()) {
				LOG.debug("Created TC ParameterGroup " + metaCommand.getName());
			}
		}
	}

	/**
	 * @throws InvalidSpaceSystemDefinitionException
	 * 
	 */
	private final void populateParameterGroupRestrictions(final String qualifiedName, final SequenceContainer parameterGroupContainer)
			throws InvalidSpaceSystemDefinitionException {
		// If the group extends another, e.g. a payload that is linked to a header via a restriction
		// we need to create the restrictions.
		final BaseContainer baseContainer = parameterGroupContainer.getBaseContainer();
		if (baseContainer != null) {
			final List<Object> comparisons = new ArrayList<Object>();
			// In Hummingbird we do not model from the packet level, only the payload. In light of this we stipulate
			// that base containers representing parameter groups that are linked to another base container via a
			// restriction
			// (e.g. header) extend a base container whose name is defined as the
			// SpaceSystemModel.HUMMINGBIRD_PROCESSED_HEADER
			// constant
			if (StringUtils.equalsIgnoreCase(baseContainer.getContainerRef(), SpaceSystemModel.HUMMINGBIRD_PROCESSED_HEADER)) {
				// Check for lists of comparisons
				final ComparisonList comparisonList = baseContainer.getRestrictionCriteria().getComparisonList();
				if (comparisonList != null) {
					final Comparison[] restrictionCriteria = comparisonList.getComparison();
					for (final Comparison comparison : restrictionCriteria) {
						final String comparisonValue = comparison.getValue();
						if (comparisonValue.startsWith("0x")) {
							comparisons.add(Integer.decode(comparisonValue).toString());
						}
						else {
							comparisons.add(comparisonValue);
						}
						if (LOG.isDebugEnabled()) {
							LOG.debug("Added restriction " + comparisonValue + " to parameter group " + parameterGroupContainer.getName());
						}
					}
					restrictions.put(qualifiedName, comparisons);
				}
				// Check for a single comparison
				final Comparison singleComparison = baseContainer.getRestrictionCriteria().getComparison();
				if (singleComparison != null) {
					final String comparisonValue = singleComparison.getValue();
					if (comparisonValue.startsWith("0x")) {
						comparisons.add(Integer.decode(comparisonValue).toString());
					}
					else {
						comparisons.add(comparisonValue);
					}
					restrictions.put(qualifiedName, comparisons);
					if (LOG.isDebugEnabled()) {
						LOG.debug("Added restriction " + comparisonValue + " to parameter group " + parameterGroupContainer.getName());
					}
				}

				if (baseContainer.getRestrictionCriteria().getBooleanExpression() != null) {
					throw new InvalidSpaceSystemDefinitionException(
							"Hummingbird does not currently support Boolean Expression restrictions. Offending Container = "
									+ parameterGroupContainer.getName());
				}
				else if (baseContainer.getRestrictionCriteria().getChoiceValue() != null) {
					throw new InvalidSpaceSystemDefinitionException("Hummingbird does not currently support Choice Value restrictions. Offending Container = "
							+ parameterGroupContainer.getName());
				}
				else if (baseContainer.getRestrictionCriteria().getCustomAlgorithm() != null) {
					throw new InvalidSpaceSystemDefinitionException(
							"Hummingbird does not currently support Custom Algorithm restrictions. Offending Container = " + parameterGroupContainer.getName());
				}
				else if (baseContainer.getRestrictionCriteria().getNextContainer() != null) {
					throw new InvalidSpaceSystemDefinitionException(
							"Hummingbird does not currently support Next Container restrictions. Offending Container = " + parameterGroupContainer.getName());
				}
			}
			else {
				LOG.error("Hummingbird does not process hierarchical container models due to their incompatiablity with multi-packet spanning payloads and/or multi-frame spanning packets.");
				LOG.error("Specific error: ");
				LOG.error("ParameterGroup: " + parameterGroupContainer.getName() + " extends base container " + baseContainer.getContainerRef());
			}
		}
	}

	private void populateParameterGroups() throws InvalidSpaceSystemDefinitionException {
		final String qualifiedNamePrefix = spaceSystem.getName() + ".tm.";
		final ContainerSet containers = spaceSystem.getTelemetryMetaData().getContainerSet();

		// For every defined container
		for (int i = 0; i < containers.getContainerSetTypeItemCount(); i++) {
			final SequenceContainer sequenceContainer = containers.getContainerSetTypeItem(i).getSequenceContainer();

			// Get the ParameterGroup we have created that corresponds to this SequenceContainer
			final ParameterGroup group = tmParameterGroups.get(qualifiedNamePrefix + sequenceContainer.getName());

			// grab it's entry list
			final EntryList parameterEntrys = sequenceContainer.getEntryList();

			for (int x = 0; x < parameterEntrys.getEntryListTypeItemCount(); x++) {
				final ParameterRefEntry parameterRefEntry = parameterEntrys.getEntryListTypeItem(x).getParameterRefEntry();
				if (parameterRefEntry == null) {
					throw new InvalidSpaceSystemDefinitionException("ParameterRefEntry for entry list type item " + x + " is null.");
				}
				final String parameterRef = parameterRefEntry.getParameterRef();

				addParameterToGroup(group, qualifiedNamePrefix + parameterRef);

				if (LOG.isDebugEnabled()) {
					LOG.debug("Added parameter " + qualifiedNamePrefix + parameterRef + " to group " + group.getName());
				}
			}
		}
	}

	private void populateCommandGroups() throws InvalidSpaceSystemDefinitionException {
		final String qualifiedNamePrefix = spaceSystem.getName() + TC_QUALIFIED_NAME_POSTFIX;
		final MetaCommandSet commands = spaceSystem.getCommandMetaData().getMetaCommandSet();

		// For every defined command
		for (int i = 0; i < commands.getMetaCommandSetItemCount(); i++) {

			final MetaCommand command = commands.getMetaCommandSetItem(i).getMetaCommand();

			if (command.isAbstract()) {
				continue;
			}

			final CommandGroup commandGroup = tcGroups.get(qualifiedNamePrefix + command.getName());

			BaseMetaCommand baseMetaCommand = command.getBaseMetaCommand();
			// if current command has a base command we need to create a command instance of it's arguments.
			if (baseMetaCommand != null) {
				// find base command
				String baseCommandName = baseMetaCommand.getMetaCommandRef();
				for (MetaCommandSetItem commandSetItem : commands.getMetaCommandSetItem()) {
					if (StringUtils.equals(commandSetItem.getMetaCommand().getName(), baseCommandName)) {
						MetaCommand baseCommand = commandSetItem.getMetaCommand();

						ArgumentListItem[] baseCmdArgumentListItem = baseCommand.getArgumentList().getArgumentListItem();

						createCommandInstanceVersionsOfBaseArgs(baseCmdArgumentListItem, command.getName());

						for (ArgumentAssignment argumentAssignment : baseMetaCommand.getArgumentAssignmentList().getArgumentAssignment()) {
							performCommandArgumentAssignments(qualifiedNamePrefix, command.getName(), argumentAssignment);
						}

						// base command found, add all arguments
						addArgumentsToCommandGroup(qualifiedNamePrefix + command.getName() + ".", commandGroup, baseCmdArgumentListItem);

					}
				}

			}

			// cmd args non-base
			ArgumentList argumentList = command.getArgumentList();
			if (argumentList != null) {
				final ArgumentListItem[] parameterEntrys = argumentList.getArgumentListItem();
				addArgumentsToCommandGroup(qualifiedNamePrefix, commandGroup, parameterEntrys);
			}
		}
	}

	private void performCommandArgumentAssignments(String qualifiedNamePrefix, String commandName, ArgumentAssignment argumentAssignment) {
		Parameter<Integer> p = integerArguments.get(qualifiedNamePrefix + argumentAssignment.getArgumentName());
		if (p != null) {
			String qualifiedName = qualifiedNamePrefix + commandName + "." + p.getName();
			if (p.isReadOnly()) {
				p = new ProtectedValueParameter<Integer>(qualifiedName, p.getName(), p.getShortDescription(), p.getLongDescription(),
						Integer.decode(argumentAssignment.getArgumentValue()));
			}
			else {
				p = new HummingbirdParameter<Integer>(qualifiedName, p.getName(), p.getShortDescription(), p.getLongDescription());
				p.setValue(Integer.decode(argumentAssignment.getArgumentValue()));

			}
			LOG.debug("Adding base command integer argument" + p.getQualifiedName());
			integerArguments.put(p.getQualifiedName(), p);
			Encoding e = encodings.get(qualifiedNamePrefix + p.getName());
			encodings.put(qualifiedName, e);
		}
		else {
			Parameter<Long> lp;
			lp = longArguments.get(qualifiedNamePrefix + argumentAssignment.getArgumentName());
			if (lp != null) {
				String qualifiedName = qualifiedNamePrefix + commandName + "." + lp.getName();
				if (lp.isReadOnly()) {
					lp = new ProtectedValueParameter<Long>(qualifiedName, lp.getName(), lp.getShortDescription(), lp.getLongDescription(),
							Long.decode(argumentAssignment.getArgumentValue()));
				}
				else {
					lp = new HummingbirdParameter<Long>(qualifiedName, lp.getName(), lp.getShortDescription(), lp.getLongDescription());
					lp.setValue(Long.decode(argumentAssignment.getArgumentValue()));

				}
				LOG.debug("Adding base command long argument" + lp.getQualifiedName());
				longArguments.put(lp.getQualifiedName(), lp);
				Encoding e = encodings.get(qualifiedNamePrefix + lp.getName());
				encodings.put(qualifiedName, e);
			}
		}

	}

	private void createCommandInstanceVersionsOfBaseArgs(ArgumentListItem[] baseCmdArgumentListItem, String commandName) {
		String qualifiedNamePrefix = spaceSystem.getName() + TC_QUALIFIED_NAME_POSTFIX;
		String cmdQualifiedNamePrefix = qualifiedNamePrefix + commandName;
		for (ArgumentListItem argumentList : baseCmdArgumentListItem) {
			for (Argument arg : argumentList.getArgument()) {
				LOG.debug("Adding argument " + arg.getArgumentTypeRef());

				Parameter<Integer> baseArgument = integerArguments.get(qualifiedNamePrefix + arg.getArgumentTypeRef());
				if (baseArgument != null) {
					Parameter<Integer> p;
					if (baseArgument.isReadOnly()) {
						// create protected
						p = new ProtectedValueParameter<Integer>(cmdQualifiedNamePrefix + "." + arg.getArgumentTypeRef(), arg.getArgumentTypeRef(),
								baseArgument.getShortDescription(), baseArgument.getLongDescription(), baseArgument.getValue());
					}
					else {
						p = new HummingbirdParameter<Integer>(cmdQualifiedNamePrefix + "." + arg.getArgumentTypeRef(), arg.getArgumentTypeRef(),
								baseArgument.getShortDescription(), baseArgument.getLongDescription());
					}
					integerArguments.put(cmdQualifiedNamePrefix + "." + arg.getArgumentTypeRef(), p);
					continue;
				}
				Parameter<Long> longBaseArgument = longArguments.get(qualifiedNamePrefix + arg.getArgumentTypeRef());
				if (longBaseArgument != null) {
					Parameter<Long> p;
					if (longBaseArgument.isReadOnly()) {
						// create protected
						p = new ProtectedValueParameter<Long>(cmdQualifiedNamePrefix + "." + arg.getArgumentTypeRef(), arg.getArgumentTypeRef(),
								longBaseArgument.getShortDescription(), longBaseArgument.getLongDescription(), longBaseArgument.getValue());
					}
					else {
						p = new HummingbirdParameter<Long>(cmdQualifiedNamePrefix + "." + arg.getArgumentTypeRef(), arg.getArgumentTypeRef(),
								longBaseArgument.getShortDescription(), longBaseArgument.getLongDescription());
					}
					longArguments.put(cmdQualifiedNamePrefix + "." + arg.getArgumentTypeRef(), p);
					continue;
				}
			}
		}

	}

	private void addArgumentsToCommandGroup(final String qualifiedNamePrefix, final CommandGroup commandGroup, final ArgumentListItem[] parameterEntrys)
			throws InvalidSpaceSystemDefinitionException {
		for (int x = 0; x < parameterEntrys.length; x++) {
			final ArgumentListItem argumentListEntry = parameterEntrys[x];
			final Argument[] arguments = argumentListEntry.getArgument();
			for (int y = 0; y < arguments.length; y++) {
				final Argument argument = arguments[y];
				final String argumentTypeRef = argument.getArgumentTypeRef();

				addArgumentParameterToGroup(commandGroup, qualifiedNamePrefix + argumentTypeRef);

				if (LOG.isDebugEnabled()) {
					LOG.debug("Added TC argument " + qualifiedNamePrefix + argumentTypeRef + " to command group " + commandGroup.getName());
				}
			}
		}
	}

	private void addParameterToGroup(final ParameterGroup group, final String qualifiedName) throws InvalidSpaceSystemDefinitionException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Adding " + qualifiedName + " to ParameterGroup " + group.getQualifiedName());
		}
		if (integerParameters.containsKey(qualifiedName)) {
			group.addIntegerParameter(integerParameters.get(qualifiedName));
		}
		else if (longParameters.containsKey(qualifiedName)) {
			group.addLongParameter(longParameters.get(qualifiedName));
		}
		else if (stringParameters.containsKey(qualifiedName)) {
			group.addStringParameter(stringParameters.get(qualifiedName));
		}
		else if (rawParameters.containsKey(qualifiedName)) {
			group.addRawParameter(rawParameters.get(qualifiedName));
		}
		else {
			// TODO Finish unsupported parameter types
			throw new InvalidSpaceSystemDefinitionException("Hummingbird currently only supports integer, long string & binary parameters");
		}
	}

	private void addArgumentParameterToGroup(final TmTcGroup group, final String qualifiedName) throws InvalidSpaceSystemDefinitionException {
		if (LOG.isDebugEnabled()) {
			LOG.debug("Adding " + qualifiedName + " to ParameterGroup " + group.getQualifiedName());
		}
		if (integerArguments.containsKey(qualifiedName)) {
			group.addIntegerParameter(integerArguments.get(qualifiedName));
		}
		else if (longArguments.containsKey(qualifiedName)) {
			group.addLongParameter(longArguments.get(qualifiedName));
		}
		else if (stringArguments.containsKey(qualifiedName)) {
			group.addStringParameter(stringArguments.get(qualifiedName));
		}
		else if (rawArguments.containsKey(qualifiedName)) {
			group.addRawParameter(rawArguments.get(qualifiedName));
		}
		else {
			// TODO Finish unsupported parameter types
			final String msg = "Could not find parameter "
					+ qualifiedName
					+ ". Check the type ref for the parameter in the model definition file, you may be referencing an undelcared type ot simply have a typo. The other possibility is that you are referencing an unsupported type. Hummingbird currently only supports integer, long, string & binary parameters. ";
			LOG.error(msg);
			throw new InvalidSpaceSystemDefinitionException(msg);
		}
	}

	/**
	 * Injects the data into the model using reflection. This means we don't have to pollute the Space System Model
	 * interface with lots of setters.
	 * 
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	private void injectConstructsIntoModel() throws IllegalArgumentException, IllegalAccessException {
		final Field[] fields = model.getClass().getDeclaredFields();
		LOG.trace("Injecting the following fields into the " + modelName + " model:");
		LOG.trace(tmParameterGroups.size() + " TM parameter groups");
		LOG.trace(tcGroups.size() + " TC groups");
		LOG.trace(restrictions.size() + " restrictions");
		LOG.trace(encodings.size() + " encodings");
		// TODO Switch on String when jdk 7 works with camel! Much nicer!
		for (final Field field : fields) {
			field.setAccessible(true);
			final String name = field.getName();
			if (StringUtils.equals(name, "parameterGroups")) {
				field.set(model, tmParameterGroups);
			}
			else if (StringUtils.equals(name, "commands")) {
				field.set(model, tcGroups);
			}
			else if (StringUtils.equals(name, "restrictions")) {
				field.set(model, restrictions);
			}
			else if (StringUtils.equals(name, "encodings")) {
				field.set(model, encodings);
			}
			else if (StringUtils.equals(name, "name")) {
				field.set(model, modelName);
			}
			else {
				LOG.debug("Not interested in field : " + name);
			}
		}
	}

	/**
	 * Checks the parameter and returns the name if valid.
	 * 
	 * TODO This is for future support of argument types.
	 * 
	 * @param item
	 * @return
	 * @throws InvalidSpaceSystemDefinitionException
	 */
	private static String checkArgumentType(final ArgumentTypeSetItem item) throws InvalidSpaceSystemDefinitionException {
		String name = null;

		// If it's an integer parameter..
		final IntegerArgumentType integerArgumentType = item.getIntegerArgumentType();
		if (integerArgumentType != null) {
			name = integerArgumentType.getName();
			if (name == null) {
				throw new InvalidSpaceSystemDefinitionException("IntegerParameter has a null name; cannot add to parameterTypes");
			}
		}
		// If it is a float parameter...
		else if (item.getFloatArgumentType() != null) {
			name = item.getFloatArgumentType().getName();
			if (name == null) {
				throw new InvalidSpaceSystemDefinitionException("FloatParameter has a null name; cannot add to parameterTypes");
			}
		}
		// If it is a string parameter...
		else if (item.getStringArgumentType() != null) {
			name = item.getStringArgumentType().getName();
			if (name == null) {
				throw new InvalidSpaceSystemDefinitionException("StringParameter has a null name; cannot add to parameterTypes");
			}
		}
		// If it is a boolean parameter...
		else if (item.getBooleanArgumentType() != null) {
			name = item.getBooleanArgumentType().getName();
			if (name == null) {
				throw new InvalidSpaceSystemDefinitionException("BooleanParameter has a null name; cannot add to parameterTypes");
			}
		}
		// If it is a binary parameter...
		else if (item.getBinaryArgumentType() != null) {
			name = item.getBinaryArgumentType().getName();
			if (name == null) {
				throw new InvalidSpaceSystemDefinitionException("BinaryParameter has a null name; cannot add to parameterTypes");
			}
		}
		else {
			throw new InvalidSpaceSystemDefinitionException("Unknown/unsupported parameter type: " + item);
		}

		return name;
	}

	/**
	 * Checks the parameter and returns the name if valid.
	 * 
	 * @param item
	 * @return
	 * @throws InvalidSpaceSystemDefinitionException
	 */
	private static String getParameterName(final ParameterTypeSetTypeItem item) throws InvalidSpaceSystemDefinitionException {
		String name = null;

		// If it's an integer parameter..
		final IntegerParameterType integerParameterType = item.getIntegerParameterType();
		if (integerParameterType != null) {
			name = integerParameterType.getName();
			if (name == null) {
				throw new InvalidSpaceSystemDefinitionException("IntegerParameter has a null name; cannot add to parameterTypes");
			}
		}
		// If it is a float parameter...
		else if (item.getFloatParameterType() != null) {
			name = item.getFloatParameterType().getName();
			if (name == null) {
				throw new InvalidSpaceSystemDefinitionException("FloatParameter has a null name; cannot add to parameterTypes");
			}
		}
		// If it is a string parameter...
		else if (item.getStringParameterType() != null) {
			name = item.getStringParameterType().getName();
			if (name == null) {
				throw new InvalidSpaceSystemDefinitionException("StringParameter has a null name; cannot add to parameterTypes");
			}
		}
		// If it is a boolean parameter...
		else if (item.getBooleanParameterType() != null) {
			name = item.getBooleanParameterType().getName();
			if (name == null) {
				throw new InvalidSpaceSystemDefinitionException("BooleanParameter has a null name; cannot add to parameterTypes");
			}
		}
		// If it is a binary parameter...
		else if (item.getBinaryParameterType() != null) {
			name = item.getBinaryParameterType().getName();
			if (name == null) {
				throw new InvalidSpaceSystemDefinitionException("BinaryParameter has a null name; cannot add to parameterTypes");
			}
		}
		else {
			throw new InvalidSpaceSystemDefinitionException("Unknown/unsupported parameter type: " + item);
		}

		return name;
	}

	/**
	 * Covers Java Integers and Longs
	 * 
	 * @param intParamType
	 * @return
	 * @throws InvalidSpaceSystemDefinitionException
	 */
	private static final Encoding createXtceIntegerEncoding(final IntegerParameterType intParamType) throws InvalidSpaceSystemDefinitionException {
		final Encoding encoding = new Encoding();

		switch (intParamType.getByteOrder()) {
			case BIGENDIAN:
				encoding.setByteOrder(ByteOrder.BIG_ENDIAN);
				break;
			case LITTLEENDIAN:
				encoding.setByteOrder(ByteOrder.LITTLE_ENDIAN);
				break;
			default:
				LOG.error("SCHEISSE MIT GREP");
				break;
		}

		int sizeInBits = 0;
		try {
			sizeInBits = Ints.checkedCast(intParamType.getSizeInBits());
		}
		catch (final IllegalArgumentException e) {
			throw new InvalidSpaceSystemDefinitionException("Illegal value (" + intParamType.getSizeInBits() + ") defined as size in bits for parameter type "
					+ intParamType.getName() + ". Hummingbird suppports sizes up to " + Integer.MAX_VALUE + ".");
		}

		encoding.setSizeInBits(sizeInBits);

		final BaseDataTypeChoice baseDataTypeChoice = intParamType.getBaseDataTypeChoice();
		if (baseDataTypeChoice == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Base data type does not have a base data type choice, assuming default of unsigned integer encoding");
			}
			encoding.setBinaryRepresentation(BinaryRepresentation.unsigned);
			return encoding;
		}

		final IntegerDataEncodingTypeEncodingType xtceEncoding = baseDataTypeChoice.getIntegerDataEncoding().getEncoding();
		switch (xtceEncoding) {
			case UNSIGNED:
				encoding.setBinaryRepresentation(BinaryRepresentation.unsigned);
				break;
			case TWOSCOMPLIMENT:
				encoding.setBinaryRepresentation(BinaryRepresentation.twosComplement);
				break;
			case BCD:
				encoding.setBinaryRepresentation(BinaryRepresentation.binaryCodedDecimal);
				break;
			case ONESCOMPLIMENT:
				encoding.setBinaryRepresentation(BinaryRepresentation.onesComplement);
				break;
			case SIGNMAGNITUDE:
				encoding.setBinaryRepresentation(BinaryRepresentation.signMagnitude);
				break;
			case PACKEDBCD:
				encoding.setBinaryRepresentation(BinaryRepresentation.packedBinaryCodedDecimal);
				break;
			default:
				throw new InvalidSpaceSystemDefinitionException("Invalid integer encoding in type " + intParamType);
		}

		return encoding;
	}

	/**
	 * Covers Java Floats and Doubles.
	 * 
	 * @param type
	 * @return
	 * @throws InvalidSpaceSystemDefinitionException
	 */
	private final static Encoding getFloatEncoding(final FloatParameterType type) throws InvalidSpaceSystemDefinitionException {
		final BaseDataTypeChoice baseDataTypeChoice = type.getBaseDataTypeChoice();

		final Encoding encoding = new Encoding();
		encoding.setSizeInBits(Integer.parseInt(type.getSizeInBits().value()));

		if (baseDataTypeChoice == null) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Base data type does not have a base data type choice, assuming default of IEEE754_1985 float encoding");
			}
			encoding.setBinaryRepresentation(BinaryRepresentation.IEEE754_1985);
		}
		final FloatDataEncodingTypeEncodingType xtceEncoding = baseDataTypeChoice.getFloatDataEncoding().getEncoding();

		switch (xtceEncoding) {
			case IEEE754_1985:
				encoding.setBinaryRepresentation(BinaryRepresentation.IEEE754_1985);
				break;
			case MILSTD_1750A:
				encoding.setBinaryRepresentation(BinaryRepresentation.MILSTD_1750A);
				break;
			default:
				throw new InvalidSpaceSystemDefinitionException("Invalid float encoding in float type " + type.getName());
		}
		return encoding;
	}

	final static Encoding createXtceStringEncoding(final StringParameterType type) throws InvalidSpaceSystemDefinitionException {
		final int uft8CharBitLength = 8;
		final int utf16CharBitLength = 16;

		final Encoding encoding = new Encoding();
		if (type.getCharacterWidth() == null) {
			// fall back to default encoding
			encoding.setBinaryRepresentation(DEFAULT_STRING_ENCODING);
			encoding.setSizeInBits((int) type.getSizeRangeInCharacters().getMax() * uft8CharBitLength);
		}
		else {
			switch (type.getCharacterWidth()) {
				case VALUE_8:
					encoding.setBinaryRepresentation(BinaryRepresentation.UTF8);
					// FIXME Possible loss of data in int cast. Why the hell anybody would have a long length string in
					// their TC or TM is anybodies guess!
					encoding.setSizeInBits((int) type.getSizeRangeInCharacters().getMax() * uft8CharBitLength);
					LOG.trace("Size in bits for UTF8 parameter " + type.getName() + " set to " + encoding.getSizeInBits());
					break;
				case VALUE_16:
					encoding.setBinaryRepresentation(BinaryRepresentation.UTF16);
					// FIXME Possible loss of data in int cast. Why the hell anybody would have a long length string in
					// their TC or TM is anybodies guess!
					encoding.setSizeInBits((int) type.getSizeRangeInCharacters().getMax() * utf16CharBitLength);
					break;
				default:
					// encoding.setBinaryRepresentation(BinaryRepresentation.ASCII); TODO valid?
					throw new InvalidSpaceSystemDefinitionException("Invalid string character width for String type: " + type.getName());
			}
		}

		return encoding;
	}

	private static final Encoding createXtceBinaryEncoding(final BinaryParameterType type) {
		final Encoding encoding = new Encoding();
		// TODO - 29.03.2012 kimmell - implement
		return encoding;
	}

	public String getSpaceSystemModelFilename() {
		return spaceSystemModelFilename;
	}

	public void setSpaceSystemModelFilename(final String spaceSystemModelFilename) {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Changing SpaceSystemModelFilename to: " + spaceSystemModelFilename);
		}
		this.spaceSystemModelFilename = spaceSystemModelFilename;
	}
}
