package com.metoo.sqlite.gather.factory.gather.thread;

import com.metoo.sqlite.gather.pool.GatherDataThreadPool;
import com.metoo.sqlite.gather.strategy.Context;
import com.metoo.sqlite.gather.strategy.DataCollectionStrategy;
import com.metoo.sqlite.gather.strategy.DataCollector;
import com.metoo.sqlite.utils.Global;

public class ExecThread {

    public static void exec(DataCollectionStrategy dataCollectionStrategy, Context context){
        if(Global.isConcurrent){
            DataCollector dataCollector = new DataCollector(context, dataCollectionStrategy);
            GatherDataThreadPool.getInstance().addThread(dataCollector);
        }else{
            dataCollectionStrategy.collectData(context);
        }
    }

}
