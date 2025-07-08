package com.project.web.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.project.web.Entity.AddonTypeEntity;
import com.project.web.Repository.AddonTypeRepository;

@Service
public class AddonTypeService {

    @Autowired
    private AddonTypeRepository addonTypeRepository;

    public List<AddonTypeEntity> getAllAddonTypes() {
        return addonTypeRepository.findAll();
    }
}