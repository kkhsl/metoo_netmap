//package com.metoo.sqlite.core.config.tomcat;
//
//import org.apache.catalina.Context;
//import org.apache.catalina.session.FileStore;
//import org.apache.catalina.session.PersistentManager;
//import org.springframework.boot.web.embedded.tomcat.TomcatContextCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class TomcatConfig {
//
//    @Bean
//    public TomcatContextCustomizer tomcatContextCustomizer() {
//        return new TomcatContextCustomizer() {
//            @Override
//            public void customize(Context context) {
//                PersistentManager manager = new PersistentManager();
//                manager.setSaveOnRestart(true);
//                manager.setMaxActiveSessions(1000);
//                manager.setMinIdleSwap(5);
//                manager.setMaxIdleSwap(10);
//                manager.setMaxIdleBackup(30);
//
//                FileStore store = new FileStore();
//                store.setDirectory("sessions");
//
//                manager.setStore(store);
//                context.setManager(manager);
//            }
//        };
//    }
//}