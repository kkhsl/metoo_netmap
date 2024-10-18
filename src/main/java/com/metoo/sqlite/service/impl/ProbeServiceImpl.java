package com.metoo.sqlite.service.impl;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.mapper.ProbeMapper;
import com.metoo.sqlite.service.IProbeService;
import com.metoo.sqlite.utils.date.DateTools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-02-01 15:31
 */
@Service
@Transactional
public class ProbeServiceImpl implements IProbeService {

    @Autowired
    private ProbeMapper probeMapper;

    @Override
    public List<Probe> selectObjByMap(Map params) {
        List<Probe> probes = this.probeMapper.selectObjByMap(params);
        return probes;
    }

    @Override
    public List<Probe> selectDeduplicationByIp(Map params) {
        List<Probe> probes = this.probeMapper.selectObjByMap(params);
        return mergeProbes(probes);
    }

    public static List<Probe> mergeProbes(List<Probe> probes) {
        // Create a map to store the merged probes by ip_addr and ipv6
        Map<String, Probe> mergedProbes = new HashMap<>();

        for (Probe probe : probes) {
            // 合并v4和仅有v6 probe
            if(StringUtil.isNotEmpty(probe.getIp_addr())){
                mergeByIp(probe.getIp_addr(), probe, mergedProbes);
            }else{
                mergeByIp(probe.getIpv6(), probe, mergedProbes);
            }
        }

        // Return merged results as a list
        return new ArrayList<>(mergedProbes.values());
    }

    private static void mergeByIp(String ip, Probe probe, Map<String, Probe> mergedProbes) {
        if (StringUtil.isNotEmpty(ip)) {
            if (!mergedProbes.containsKey(ip)) {
                // Initialize TTLs to avoid null
                probe.setTtls(getTtlAsString(probe.getTtl()));
                mergedProbes.put(ip, probe);  // Add first occurrence
            } else {
                Probe existing = mergedProbes.get(ip);
                // Merge fields: vendor, os_gen, os_family, application_protocol, ttl
                existing.setVendor(mergeField(existing.getVendor(), probe.getVendor()));
                existing.setOs_gen(mergeField(existing.getOs_gen(), probe.getOs_gen()));
                existing.setOs_family(mergeField(existing.getOs_family(), probe.getOs_family()));
                existing.setApplication_protocol(mergeField(existing.getApplication_protocol(), probe.getApplication_protocol()));
                existing.setTtls(mergeField(getTtlAsString(existing.getTtl()), getTtlAsString(probe.getTtl())));
                existing.setPort_service_vendor(mergeField(existing.getPort_service_vendor(), probe.getPort_service_vendor()));
            }
        }
    }

    private static String getTtlAsString(Integer ttl) {
        return Optional.ofNullable(ttl).map(String::valueOf).orElse("");
    }

    private static String mergeField(String existingValue, String newValue) {
        if (existingValue == null || existingValue.isEmpty()) {
            if (newValue == null || newValue.isEmpty()) {
                return null;
            }
            return newValue;
        }
        if (newValue == null || newValue.isEmpty()) {
            return existingValue;
        }
        return existingValue + "," + newValue;
    }

    @Override
    public boolean insert(Probe instance) {
        try {
            instance.setCreateTime(DateTools.getCreateTime());
            this.probeMapper.insert(instance);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean update(Probe instance) {
        try {
            this.probeMapper.update(instance);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public int delete(Integer id) {
        try {
            return this.probeMapper.delete(id);
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteTable() {
        try {
            return this.probeMapper.deleteTable();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    public int deleteTableBack() {
        return this.probeMapper.deleteTableBack();
    }

    @Override
    public int copyToBck() {
        return this.probeMapper.copyToBck();
    }

    @Override
    public boolean deleteProbeByIp(String ipv4, String ipv6) {
        try {
            // 使用stream API和组合查询条件
            Map<String, Object> params = new HashMap<>();
            if (StringUtil.isNotEmpty(ipv4)) {
                params.put("ip_addr", ipv4);
            }
            if (StringUtil.isNotEmpty(ipv6)) {
                params.put("ipv6", ipv6);
            }

            List<Probe> deleteProbes = probeMapper.selectObjByMap(params);

            if (!deleteProbes.isEmpty()) {
                deleteProbes.forEach(probe -> {
                    try {
                        probeMapper.delete(probe.getId());
                    } catch (Exception e) {
                        e.printStackTrace();  // 可根据需要优化异常处理
                    }
                });
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

}
