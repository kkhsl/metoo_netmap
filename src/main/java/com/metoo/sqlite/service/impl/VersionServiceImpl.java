package com.metoo.sqlite.service.impl;

import com.metoo.sqlite.entity.Version;
import com.metoo.sqlite.mapper.VersionMapper;
import com.metoo.sqlite.service.IVersionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional
public class VersionServiceImpl implements IVersionService {

    @Resource
    private VersionMapper versionMapper;

    @Override
    public Version selectObjByOne() {
        return this.versionMapper.selectObjByOne();
    }

    @Override
    public int save(Version instance) {
        if(instance.getId() == null){
            try {
                return this.versionMapper.save(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }else{
            try {
                return this.versionMapper.update(instance);
            } catch (Exception e) {
                e.printStackTrace();
                return 0;
            }
        }
    }

    @Override
    public int update(Version instance) {
        try {
            return this.versionMapper.update(instance);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}
