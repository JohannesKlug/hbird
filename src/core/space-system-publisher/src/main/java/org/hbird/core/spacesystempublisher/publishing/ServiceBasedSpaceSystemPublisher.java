package org.hbird.core.spacesystempublisher.publishing;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.hbird.core.spacesystemmodel.SpaceSystemModel;
import org.hbird.core.spacesystemmodel.SpaceSystemModelFactory;
import org.hbird.core.spacesystemmodel.encoding.Encoding;
import org.hbird.core.spacesystemmodel.exceptions.InvalidParameterTypeException;
import org.hbird.core.spacesystemmodel.exceptions.InvalidSpaceSystemDefinitionException;
import org.hbird.core.spacesystemmodel.exceptions.UnknownParameterGroupException;
import org.hbird.core.spacesystemmodel.tmtc.CommandGroup;
import org.hbird.core.spacesystemmodel.tmtc.Parameter;
import org.hbird.core.spacesystemmodel.tmtc.ParameterGroup;
import org.hbird.core.spacesystempublisher.interfaces.SpaceSystemModelUpdate;
import org.hbird.core.spacesystempublisher.interfaces.SpaceSystemPublisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@link SpaceSystemPublisher} that uses a {@link SpaceSystemModelFactory} service interface to retrieve the model.
 * 
 * @author Mark Doyle
 * 
 */
public class ServiceBasedSpaceSystemPublisher implements SpaceSystemPublisher {
	private final static Logger LOG = LoggerFactory.getLogger(ServiceBasedSpaceSystemPublisher.class);

	/** {@link SpaceSystemModelFactory} used to retrieve the model */
	private SpaceSystemModelFactory factoryService;

	/** Cached Space system model */
	private SpaceSystemModel modelCache = null;

	/**
	 * Retrieves and caches the space system model from the space system model factory service.
	 */
	public void loadModel() {
		LOG.debug("Loading space system model from factory service...");

		if (factoryService != null) {
			LOG.debug("Factory service exists...");
			try {
				this.modelCache = factoryService.createSpaceSystemModel();
				LOG.debug("Model " + this.modelCache.getName() + " cached in publisher.");
			}
			catch (final InvalidParameterTypeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (final InvalidSpaceSystemDefinitionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			LOG.error("SpaceSystemModelFactoryService is null, cannot retrieve a space system model for loading and caching!");
		}
	}

	@Override
	public Map<String, ParameterGroup> getParameterGroups() {
		if (modelCache == null) {
			loadModel();
		}
		return modelCache.getParameterGroups();
	}

	@Override
	public List<ParameterGroup> getParameterGroupList() {
		return new ArrayList<ParameterGroup>(modelCache.getParameterGroupsCollection());
	}

	@Override
	public Map<String, CommandGroup> getCommands() {
		return modelCache.getCommands();
	}

	@Override
	public List<CommandGroup> getCommandList() {
		if (LOG.isTraceEnabled()) {
			LOG.trace("Returning " + modelCache.getCommands().values().size() + " command(s) from " + modelCache.getName());
		}
		return new ArrayList<CommandGroup>(modelCache.getCommands().values());
	}

	@Override
	public Map<String, Encoding> getEncodings() {
		return modelCache.getEncodings();
	}

	/**
	 * @return the model
	 */
	public SpaceSystemModel getModel() {
		return modelCache;
	}

	/**
	 * @param model
	 *            the model to set
	 */
	public void setModel(final SpaceSystemModel model) {
		this.modelCache = model;
	}

	@Override
	public Map<String, List<String>> getRestrictions() {
		return this.modelCache.getAllPayloadRestrictions();
	}

	@Override
	public void fireUpdate(final SpaceSystemModelUpdate update) {
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

	public SpaceSystemModelFactory getFactoryService() {
		return factoryService;
	}

	public void setFactoryService(final SpaceSystemModelFactory factoryService) {
		this.factoryService = factoryService;
	}

	@Override
	public void modelUpdated() {
		LOG.info("Publisher received notification that the factory space system model updated");
		modelCache = null;
		loadModel();
	}

	@Override
	public CommandGroup getCommand(final String qualifiedName) {
		return this.modelCache.getCommands().get(qualifiedName);
	}

	@Override
	public ParameterGroup getParameterGroup(final String qualifiedName) throws UnknownParameterGroupException {
		return this.modelCache.getParameterGroup(qualifiedName);
	}

	@Override
	public List<Parameter<?>> getAllParameters() {
		return new ArrayList<Parameter<?>>(this.modelCache.getAllPayloadParameters().values());
	}

}
