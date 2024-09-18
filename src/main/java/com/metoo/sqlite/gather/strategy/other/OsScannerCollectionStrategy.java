package com.metoo.sqlite.gather.strategy.other;

import com.github.pagehelper.util.StringUtil;
import com.metoo.sqlite.entity.Probe;
import com.metoo.sqlite.entity.Subnet;
import com.metoo.sqlite.gather.common.PyCommandBuilder;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.utils.PyExecUtils;
import com.metoo.sqlite.utils.Global;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 14:49
 * <p>
 */
@Slf4j
@Component
public class OsScannerCollectionStrategy implements DataCollectionStrategy {

    private final PyExecUtils pyExecUtils;

    @Autowired
    public OsScannerCollectionStrategy(PyExecUtils pyExecUtils) {
        this.pyExecUtils = pyExecUtils;
    }

    @Override
    public void collectData(Context context) {
        try {
            Probe obj = (Probe) context.getEntity();
            if (obj != null) {
                PyCommandBuilder pyCommand = new PyCommandBuilder();
                pyCommand.setPrefix("");
                pyCommand.setVersion("");
//                pyCommand.setPath(Global.os_scanner);
                pyCommand.setPath(context.getPath());
                pyCommand.setName(Global.os_scanner_name);
                pyCommand.setParams(new String[]{
                        "-i",
                        obj.getIp_addr(),
                        "-o",
                        obj.getPort_num(),
                        "-c",
                        "1"
                });
                log.info("执行开始 os-scanner" + " ip: "  + obj.getIp_addr() + " port " + obj.getPort_num());
                this.pyExecUtils.exec(pyCommand);
                log.info("执行结束 os-scanner");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
