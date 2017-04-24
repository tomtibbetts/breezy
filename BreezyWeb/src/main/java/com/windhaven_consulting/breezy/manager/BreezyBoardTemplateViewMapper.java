package com.windhaven_consulting.breezy.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.windhaven_consulting.breezy.manager.viewobject.BreezyBoardTemplate;
import com.windhaven_consulting.breezy.manager.viewobject.ComponentConfigurationTemplate;
import com.windhaven_consulting.breezy.manager.viewobject.ExtensionTemplate;
import com.windhaven_consulting.breezy.manager.viewobject.InputConfigurationTemplate;
import com.windhaven_consulting.breezy.manager.viewobject.OutputConfigurationTemplate;
import com.windhaven_consulting.breezy.persistence.domain.BreezyBoardTemplateDO;
import com.windhaven_consulting.breezy.persistence.domain.ComponentConfigurationTemplateDO;
import com.windhaven_consulting.breezy.persistence.domain.ExtensionTemplateDO;
import com.windhaven_consulting.breezy.persistence.domain.InputConfigurationTemplateDO;
import com.windhaven_consulting.breezy.persistence.domain.OutputConfigurationTemplateDO;

public class BreezyBoardTemplateViewMapper implements ViewDiskObjectMapper<BreezyBoardTemplate, BreezyBoardTemplateDO> {

	@Override
	public BreezyBoardTemplateDO getAsDiskObject(BreezyBoardTemplate breezyBoardTemplate) {
		Map<ExtensionTemplateDO, UUID> extensionTemplateDOToExtensionIdMap = new HashMap<ExtensionTemplateDO, UUID>();

		BreezyBoardTemplateDO breezyBoardTemplateDO = new BreezyBoardTemplateDO();
		
		breezyBoardTemplateDO.setDescription(breezyBoardTemplate.getDescription());
		breezyBoardTemplateDO.setId(breezyBoardTemplate.getId());
		breezyBoardTemplateDO.setName(breezyBoardTemplate.getName());
		breezyBoardTemplateDO.setInactive(breezyBoardTemplate.isInactive());
		
		for(ExtensionTemplate extensionTemplate : breezyBoardTemplate.getExtensionTemplates()) {
			ExtensionTemplateDO extensionTemplateDO = getExtensionTemplateDO(extensionTemplate);
			breezyBoardTemplateDO.getExtensionTemplateDOs().add(extensionTemplateDO);
			extensionTemplateDOToExtensionIdMap.put(extensionTemplateDO, extensionTemplateDO.getId());
		}
		
		for(InputConfigurationTemplate inputConfigurationTemplate : breezyBoardTemplate.getInputConfigurationTemplates()) {
			InputConfigurationTemplateDO inputConfigurationTemplateDO = getInputConfigurationTemplateDO(inputConfigurationTemplate);
			inputConfigurationTemplateDO.setExtensionTemplateId(inputConfigurationTemplate.getExtensionTemplate().getId());
			breezyBoardTemplateDO.getInputConfigurationTemplateDOs().add(inputConfigurationTemplateDO);
		}
		
		for(ComponentConfigurationTemplate componentConfigurationTemplate :	breezyBoardTemplate.getComponentConfigurationTemplates()) {
			ComponentConfigurationTemplateDO componentConfigurationTemplateDO = getComponentConfigurationTemplateDO(componentConfigurationTemplate, extensionTemplateDOToExtensionIdMap);
			breezyBoardTemplateDO.getComponentConfigurationTemplateDOs().add(componentConfigurationTemplateDO);
		}
		
		return breezyBoardTemplateDO;
	}

	@Override
	public BreezyBoardTemplate getAsViewObject(BreezyBoardTemplateDO breezyBoardTemplateDO) {
		Map<UUID, ExtensionTemplate> extensionIdToExtensionTemplateMap = new HashMap<UUID, ExtensionTemplate>();  
		
		BreezyBoardTemplate breezyBoardTemplate = new BreezyBoardTemplate();
		breezyBoardTemplate.setName(breezyBoardTemplateDO.getName());
		breezyBoardTemplate.setDescription(breezyBoardTemplateDO.getDescription());
		breezyBoardTemplate.setInactive(breezyBoardTemplateDO.isInactive());
		breezyBoardTemplate.setId(breezyBoardTemplateDO.getId());
		
		for(ExtensionTemplateDO extensionTemplateDO : breezyBoardTemplateDO.getExtensionTemplateDOs()) {
			ExtensionTemplate extensionTemplate = getExtensionTemplate(extensionTemplateDO);
			
			breezyBoardTemplate.getExtensionTemplates().add(extensionTemplate);
			extensionIdToExtensionTemplateMap.put(extensionTemplate.getId(), extensionTemplate);
		}
		
		for(InputConfigurationTemplateDO inputConfigurationTemplateDO : breezyBoardTemplateDO.getInputConfigurationTemplateDOs()) {
			InputConfigurationTemplate inputConfigurationTemplate = getInputConfigurationTemplate(inputConfigurationTemplateDO);
			
			if(extensionIdToExtensionTemplateMap.containsKey(inputConfigurationTemplateDO.getExtensionTemplateId())) {
				inputConfigurationTemplate.setExtensionTemplate(extensionIdToExtensionTemplateMap.get(inputConfigurationTemplateDO.getExtensionTemplateId()));
			}
			
			breezyBoardTemplate.getInputConfigurationTemplates().add(inputConfigurationTemplate);
		}
		
		for(ComponentConfigurationTemplateDO componentConfigurationTemplateDO : breezyBoardTemplateDO.getComponentConfigurationTemplateDOs()) {
			ComponentConfigurationTemplate componentConfigurationTemplate = getComponentConfigurationTemplate(componentConfigurationTemplateDO, extensionIdToExtensionTemplateMap);
			
			breezyBoardTemplate.getComponentConfigurationTemplates().add(componentConfigurationTemplate);
		}
		
		return breezyBoardTemplate;
	}

	@Override
	public List<BreezyBoardTemplateDO> getAsDiskObjects(List<BreezyBoardTemplate> sourceCollection) {
		List<BreezyBoardTemplateDO> results = new ArrayList<BreezyBoardTemplateDO>();
		
		for(BreezyBoardTemplate breezyBoardTemplate : sourceCollection) {
			BreezyBoardTemplateDO breezyBoardTemplateDO = getAsDiskObject(breezyBoardTemplate);
			results.add(breezyBoardTemplateDO);
		}
		
		return results;
	}

	@Override
	public List<BreezyBoardTemplate> getAsViewObjects(List<BreezyBoardTemplateDO> sourceCollection) {
		List<BreezyBoardTemplate> results = new ArrayList<BreezyBoardTemplate>();
		
		for(BreezyBoardTemplateDO breezyBoardTemplateDO : sourceCollection) {
			BreezyBoardTemplate breezyBoardTemplate = getAsViewObject(breezyBoardTemplateDO);
			results.add(breezyBoardTemplate);
		}
		
		return results;
	}

	private ExtensionTemplateDO getExtensionTemplateDO(ExtensionTemplate extensionTemplate) {
		if(extensionTemplate.getId() == null) {
			extensionTemplate.setId(UUID.randomUUID());
		}
		
		ExtensionTemplateDO extensionTemplateDO = new ExtensionTemplateDO();
		extensionTemplateDO.setId(extensionTemplate.getId());
		extensionTemplateDO.setName(extensionTemplate.getName());
		extensionTemplateDO.setExtensionType(extensionTemplate.getExtensionType());
		
		return extensionTemplateDO;
	}

	private InputConfigurationTemplateDO getInputConfigurationTemplateDO(InputConfigurationTemplate inputConfigurationTemplate) {
		if(inputConfigurationTemplate.getId() == null) {
			inputConfigurationTemplate.setId(UUID.randomUUID());
		}
		
		InputConfigurationTemplateDO inputConfigurationTemplateDO = new InputConfigurationTemplateDO();
		inputConfigurationTemplateDO.setId(inputConfigurationTemplate.getId());
		inputConfigurationTemplateDO.setName(inputConfigurationTemplate.getName());
		inputConfigurationTemplateDO.setMappedPin(inputConfigurationTemplate.getMappedPin());
		inputConfigurationTemplateDO.setPinPullResistance(inputConfigurationTemplate.getPinPullResistance());
		
		return inputConfigurationTemplateDO;
	}

	private ComponentConfigurationTemplateDO getComponentConfigurationTemplateDO(ComponentConfigurationTemplate componentConfigurationTemplate, Map<ExtensionTemplateDO, UUID> extensionTemplateDOToExtensionIdMap) {
		if(componentConfigurationTemplate.getId() == null) {
			componentConfigurationTemplate.setId(UUID.randomUUID());
		}
		
		ComponentConfigurationTemplateDO componentConfigurationTemplateDO = new ComponentConfigurationTemplateDO();
		componentConfigurationTemplateDO.setComponentType(componentConfigurationTemplate.getComponentType());
		componentConfigurationTemplateDO.setId(componentConfigurationTemplate.getId());
		componentConfigurationTemplateDO.setName(componentConfigurationTemplate.getName());
		
		for(OutputConfigurationTemplate outputConfigurationTemplate : componentConfigurationTemplate.getOutputConfigurationTemplates()) {
			OutputConfigurationTemplateDO outputConfigurationTemplateDO = getOutputConfigurationTemplateDO(outputConfigurationTemplate);
			componentConfigurationTemplateDO.getOutputConfigurationTemplateDOs().add(outputConfigurationTemplateDO);
			outputConfigurationTemplateDO.setExtensionTemplateId(outputConfigurationTemplate.getExtensionTemplate().getId());
		}
		
		return componentConfigurationTemplateDO;
	}

	private OutputConfigurationTemplateDO getOutputConfigurationTemplateDO(OutputConfigurationTemplate outputConfigurationTemplate) {
		if(outputConfigurationTemplate.getId() == null) {
			outputConfigurationTemplate.setId(UUID.randomUUID());
		}
		
		OutputConfigurationTemplateDO outputConfigurationTemplateDO = new OutputConfigurationTemplateDO();
		outputConfigurationTemplateDO.setId(outputConfigurationTemplate.getId());
		outputConfigurationTemplateDO.setName(outputConfigurationTemplate.getName());
		outputConfigurationTemplateDO.setMappedPin(outputConfigurationTemplate.getMappedPin());
		
		return outputConfigurationTemplateDO;
	}


	private ExtensionTemplate getExtensionTemplate(ExtensionTemplateDO extensionTemplateDO) {
		ExtensionTemplate extensionTemplate = new ExtensionTemplate();
		extensionTemplate.setName(extensionTemplateDO.getName());
		extensionTemplate.setId(extensionTemplateDO.getId());
		extensionTemplate.setExtensionType(extensionTemplateDO.getExtensionType());
		
		return extensionTemplate;
	}

	private InputConfigurationTemplate getInputConfigurationTemplate(InputConfigurationTemplateDO inputConfigurationTemplateDO) {
		InputConfigurationTemplate inputConfigurationTemplate = new InputConfigurationTemplate();
		inputConfigurationTemplate.setName(inputConfigurationTemplateDO.getName());
		inputConfigurationTemplate.setId(inputConfigurationTemplateDO.getId());
		inputConfigurationTemplate.setMappedPin(inputConfigurationTemplateDO.getMappedPin());
		inputConfigurationTemplate.setPinPullResistance(inputConfigurationTemplateDO.getPinPullResistance());
		
		return inputConfigurationTemplate;
	}

	private ComponentConfigurationTemplate getComponentConfigurationTemplate(ComponentConfigurationTemplateDO componentConfigurationTemplateDO, Map<UUID, ExtensionTemplate> extensionTemplateMap) {
		ComponentConfigurationTemplate componentConfigurationTemplate = new ComponentConfigurationTemplate();
		componentConfigurationTemplate.setComponentType(componentConfigurationTemplateDO.getComponentType());
		componentConfigurationTemplate.setId(componentConfigurationTemplateDO.getId());
		componentConfigurationTemplate.setName(componentConfigurationTemplateDO.getName());
		
		for(OutputConfigurationTemplateDO outputConfigurationTemplateDO : componentConfigurationTemplateDO.getOutputConfigurationTemplateDOs()) {
			OutputConfigurationTemplate outputConfigurationTemplate = new OutputConfigurationTemplate();
			outputConfigurationTemplate.setId(outputConfigurationTemplateDO.getId());
			outputConfigurationTemplate.setName(outputConfigurationTemplateDO.getName());
			outputConfigurationTemplate.setMappedPin(outputConfigurationTemplateDO.getMappedPin());
			
			if(extensionTemplateMap.containsKey(outputConfigurationTemplateDO.getExtensionTemplateId())) {
				outputConfigurationTemplate.setExtensionTemplate(extensionTemplateMap.get(outputConfigurationTemplateDO.getExtensionTemplateId()));
			}
			
			componentConfigurationTemplate.getOutputConfigurationTemplates().add(outputConfigurationTemplate);
		}
		
		return componentConfigurationTemplate;
	}
	
}
