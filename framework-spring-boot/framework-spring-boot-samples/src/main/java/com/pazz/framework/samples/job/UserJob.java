package com.pazz.framework.samples.job;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.pazz.framework.util.DateUtils;

import java.util.Date;

/**
 * @author: 彭坚
 * @create: 2018/11/16 14:37
 * @description: User job
 */
public class UserJob implements SimpleJob {

    @Override
    public void execute(ShardingContext shardingContext) {
        System.out.println(DateUtils.convert(new Date()) + ":" + shardingContext.getShardingParameter());
    }

}
