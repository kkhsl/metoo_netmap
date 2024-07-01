package com.metoo.sqlite.manager;

import com.github.pagehelper.Page;
import com.metoo.sqlite.dto.OperationLogDTO;
import com.metoo.sqlite.dto.page.PageInfo;
import com.metoo.sqlite.entity.OperationLog;
import com.metoo.sqlite.service.IOperationLogService;
import com.metoo.sqlite.utils.ResponseUtil;
import com.metoo.sqlite.utils.net.Ipv4Utils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @author HKK
 * @version 1.0
 * @date 2023-11-07 16:32
 */
@RequestMapping("/admin/operation/log")
@RestController
public class OperationLogManagerController {

    @Autowired
    private IOperationLogService operationLogService;

    @PostMapping("/list")
    public Object list(@RequestBody OperationLogDTO dto){
        Page<OperationLog> page = this.operationLogService.selectObjConditionQuery(dto);
        if(page.getResult().size() > 0){
            return ResponseUtil.ok(new PageInfo<OperationLog>(page));
        }
        return ResponseUtil.ok();
    }

    @PostMapping("/save")
    public Object insert(HttpServletRequest request, @RequestBody OperationLog instance){
        instance.setType("1");
        instance.setIp(Ipv4Utils.getRealIP(request));
        int flag = this.operationLogService.insert(instance);
        if(flag > 0){
           return ResponseUtil.ok();
        }
        return ResponseUtil.error();
    }

    @DeleteMapping("/delete")
    public Object delete(@RequestParam String id){
        String[] dms = id.split(",");
        if(dms.length > 1){
            for (String s : dms) {
                int flag = this.operationLogService.delete(Integer.parseInt(id));
                if(flag > 0){
                    continue;
                }else{
                    OperationLog operationLog = this.operationLogService.selectObjById(Integer.parseInt(id));
                    if(operationLog != null){
                        return ResponseUtil.error(operationLog.getAccount() + "删除失败");
                    }
                    continue;
                }
            }
            return ResponseUtil.ok();
        }else{
            int flag = this.operationLogService.delete(Integer.parseInt(id));
            if(flag > 0){
                return ResponseUtil.ok();
            }
            return ResponseUtil.error();
        }
    }

}
