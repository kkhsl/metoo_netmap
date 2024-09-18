package com.metoo.sqlite.utils.license;

import com.alibaba.fastjson.JSONObject;
import com.metoo.sqlite.entity.License;
import com.metoo.sqlite.utils.encryption.AesEncryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LicenseTools {

    @Autowired
    private AesEncryptUtils aesEncryptUtils;

    /**
     * 验证License合法性
     * @return
     */
    public boolean verify(String systemSN, String code){
        try {
            String decrypt = this.aesEncryptUtils.decrypt(code);
            System.out.println("&&&decrypt" + decrypt);
            License license = JSONObject.parseObject(decrypt, License.class);
            System.out.println("&&&decrypt" + JSONObject.toJSONString(license));
            String sn = license.getSystemSN();
            if(sn.equals(systemSN)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
