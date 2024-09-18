package com.metoo.sqlite.model;

import com.github.pagehelper.util.StringUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-28 22:59
 */
public class TerminalModel {

    private String ip;
    private Set<String> port_numberS;
    private Set<String> os_familyS;
    private Set<String> port_service_productS;
    private String os;
    private String os_gen;
    private Float reliability;
    private Integer ttl;
    private String vendor;
    private boolean flag;

    public TerminalModel() {
    }

    public TerminalModel(String ip, String port_number, String os_family, String port_service_product,
                         String os, Float reliability, Integer ttl, String os_gen, String vendor, Boolean flag) {
        this.ip = ip;
        this.port_numberS = new HashSet<>();
        this.os_familyS = new HashSet<>();
        this.port_service_productS = new HashSet<>();

        if(StringUtil.isNotEmpty(port_number)){
            this.port_numberS.add(port_number);
        }
        if(StringUtil.isNotEmpty(os_family)){
            this.os_familyS.add(os_family);
        }
        if(StringUtil.isNotEmpty(port_service_product)){
            this.port_service_productS.add(port_service_product);
        }
        this.os = os;
        this.reliability = reliability;
        this.ttl = ttl;
        this.os_gen = os_gen;
        this.vendor = vendor;
        this.flag = flag;
    }

    public boolean containsPort_number(String port_number){
        return port_numberS.contains(port_number);
    }

    public boolean containsOs_family(String os_family){
        return os_familyS.contains(os_family);
    }

    public boolean containsPort_service_product(String port_service_product){
        return port_service_productS.contains(port_service_product);
    }

    public String getOs() {
        return os;
    }

    public void setOs(String os) {
        this.os = os;
    }

    public Float getReliability() {
        return reliability;
    }

    public void setReliability(Float reliability) {
        if(this.reliability != null){
            this.reliability = reliability;
        }
    }

    public Integer getTtl() {
        return ttl;
    }

    public void setTtl(Integer ttl) {
        this.ttl = ttl;
    }

    public String getOs_gen() {
        return os_gen;
    }

    public void setOs_gen(String os_gen) {
        this.os_gen = os_gen;
    }

    public String getVendor() {
        return vendor;
    }

    public void setVendor(String vendor) {
        this.vendor = vendor;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public Set<String> getPort_numberS(){
        return port_numberS;
    }

    public Set<String> getOs_familyS(){
        return os_familyS;
    }

    public Set<String> getPort_service_productS(){
        return port_service_productS;
    }

    public void addPort_number(String port_number){
        port_numberS.add(port_number);
    }

    public void addOs_family(String os_family){
        os_familyS.add(os_family);
    }

    public void addPort_service_product(String port_service_product){
        port_service_productS.add(port_service_product);
    }
}
