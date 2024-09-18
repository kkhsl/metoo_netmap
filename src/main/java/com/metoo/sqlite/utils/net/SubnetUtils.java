package com.metoo.sqlite.utils.net;

import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-12 10:31
 */
@Component
public class SubnetUtils {

//    private ISubnetService subnetService;
//    @Autowired
//    private Ipv4DetailService ipv4DetailService;
//    @Autowired
//    private IAddressService addressService;
//
//    @PostConstruct
//    public void init() {
//        this.subnetService = (ISubnetService) ApplicationContextUtils.getBean("subnetServiceImpl");
//    }
//
//
//    public void cardingSubnetIp(){
//        // 同步子网ip
//        // 获取所有子网一级
//        List<Subnet> subnets = this.subnetService.selectSubnetByParentId(null);
//        List<Ipv4Detail> ipv4Details = this.ipv4DetailService.selectObjByMap(null);
//        if (subnets.size() > 0) {
//            GatherDataThreadPool.getInstance().addThread(new Runnable() {
//                @Override
//                public void run() {
//                    synchronized (this) {
//                        for (Ipv4Detail ipv4Detail : ipv4Details) {
//                            if (ipv4Detail.getIp() != null) {
//                                if (ipv4Detail.getIp().equals("0.0.0.0")) {
//                                    continue;
//                                }
//                                String ip = Ipv4Util.decConvertIp(Long.parseLong(ipv4Detail.getIp()));
//                                if (!Ipv4Util.verifyIp(ip)) {
//                                    continue;
//                                }
//                                // 判断ip地址是否属于子网
//                                for (Subnet subnet : subnets) {
//                                    genericNoSubnet(subnet, ipv4Detail);
//                                }
//                            }
//                        }
//                    }
//                }
//            });
//        }
//    }
//
//    public void genericNoSubnet(Subnet subnet, Ipv4Detail ipDetail) {
//        List<Subnet> childs = this.subnetService.selectSubnetByParentId(subnet.getId());
//        if (childs.size() > 0) {
//            for (Subnet child : childs) {
//                genericNoSubnet(child, ipDetail);
//            }
//        } else {
//            // 判断ip是否属于从属子网
//            boolean flag = Ipv4Util.ipIsInNet(Ipv4Util.decConvertIp(Long.parseLong(ipDetail.getIp())),
//                    subnet.getIp() + "/" + subnet.getMask());
//            if (flag) {
//                // 从属子网
//                Address obj = this.addressService.selectObjByIp(ipDetail.getIp());
//                if (obj != null) {
//                    obj.setSubnetId(subnet.getId());
//                    obj.setIp(null);
//                    int i = this.addressService.update(obj);
//                } else {
//                    Address address = new Address();
//                    System.out.println(Ipv4Util.decConvertIp(Long.parseLong(ipDetail.getIp())));
//                    address.setIp(ipDetail.getIp());
//                    address.setHostName(ipDetail.getDeviceName());
//                    address.setMac(ipDetail.getMac());
//                    address.setSubnetId(subnet.getId());
//                    int i = this.addressService.save(address);
//                }
//            }
//        }
//    }
}
