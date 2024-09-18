package com.metoo.sqlite.gather.strategy;

import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.utils.date.DateTools;

/**
 * @author HKK
 * @version 1.0
 * @date 2024-06-23 16:02
 */
public class GatherDemo {

    public static void main(String[] args) {

        Context context = new Context();
        context.setCreateTime(DateTools.getCreateTime());

        //
//        PortCollectionStrategy portCollectionStrategy = new PortCollectionStrategy();
//        DataCollector dataCollector = new DataCollector(context, portCollectionStrategy);
//        dataCollector.run();


    }
}
