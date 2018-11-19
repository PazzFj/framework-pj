package com.pazz.framework.spring.boot.autoconfigure.job;

import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.JobTypeConfiguration;
import com.dangdang.ddframe.job.config.dataflow.DataflowJobConfiguration;
import com.dangdang.ddframe.job.config.script.ScriptJobConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.event.rdb.JobEventRdbConfiguration;
import com.dangdang.ddframe.job.lite.api.listener.ElasticJobListener;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import com.pazz.framework.util.CollectionUtils;
import com.pazz.framework.util.string.StringUtil;
import lombok.Data;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.boot.bind.RelaxedPropertyResolver;
import org.springframework.context.EnvironmentAware;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author: 彭坚
 * @create: 2018/11/19 11:22
 * @description: JobScheduler 注册
 */
public class JobSchedulerRegistrar implements ImportBeanDefinitionRegistrar, EnvironmentAware, BeanClassLoaderAware {

    private final Log logger = LogFactory.getLog(getClass());

    private static final String PREFIX = "job.";

    private static final String ZK_PREFIX = "zookeeper.";

    private static final String JOBS_PREFIX = "jobs.";

    private static final String ZOOKEEPER_REGISTRY_CENTER_BEAN_NAME = "zookeeperRegistryCenter";

    private static final String JOB_EVENT_CONFIGURATION_BEAN_NAME = "jobEventConfiguration";

    private RelaxedPropertyResolver resolver;

    private ClassLoader classLoader;

    /**
     * 注册bean定义
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        //注册中心
        registerZookeeperRegistryCenter(registry);
        //注册事件配置
        registerJobEventConfiguration(registry);
        //注册所有job
        registerJobs(registry);
    }

    /**
     * 注册 zookeeper 注册中心
     */
    private void registerZookeeperRegistryCenter(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(ZOOKEEPER_REGISTRY_CENTER_BEAN_NAME)) {
            ZookeeperConfiguration zookeeperConfiguration = new ZookeeperConfiguration(resolver.getProperty(ZK_PREFIX + "server"), resolver.getProperty(ZK_PREFIX + "namespace"));
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ZookeeperRegistryCenter.class).setInitMethodName("init");
            builder.addConstructorArgValue(zookeeperConfiguration);
            registry.registerBeanDefinition(ZOOKEEPER_REGISTRY_CENTER_BEAN_NAME, builder.getBeanDefinition());
        }
    }

    /**
     * 注册job事件配置
     */
    private void registerJobEventConfiguration(BeanDefinitionRegistry registry) {
        if (!registry.containsBeanDefinition(JOB_EVENT_CONFIGURATION_BEAN_NAME)) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(JobEventRdbConfiguration.class);
            builder.addConstructorArgReference("dataSource");
            registry.registerBeanDefinition(JOB_EVENT_CONFIGURATION_BEAN_NAME, builder.getBeanDefinition());
        }
    }

    /**
     * 注册job
     */
    private void registerJobs(BeanDefinitionRegistry registry) {
        List<Job> jobs = getJobs(registry);
        jobs.forEach(job -> {
            try {
                Class<?> target = ClassUtils.forName(job.getJobClass(), this.classLoader);
                BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(target).setAutowireMode(GenericBeanDefinition.AUTOWIRE_BY_TYPE);
                registry.registerBeanDefinition(job.getJobName(), builder.getBeanDefinition());
                BeanDefinitionBuilder builderJob = BeanDefinitionBuilder.genericBeanDefinition(SpringJobScheduler.class).setInitMethodName("init");
                builderJob.addConstructorArgReference(job.getJobName());
                builderJob.addConstructorArgReference(ZOOKEEPER_REGISTRY_CENTER_BEAN_NAME);
                builderJob.addConstructorArgValue(getLiteJobConfiguration(job));
                builderJob.addConstructorArgReference(JOB_EVENT_CONFIGURATION_BEAN_NAME);
                builderJob.addConstructorArgValue(new ElasticJobListener[]{});
                registry.registerBeanDefinition(job.getJobName() + "SpringJobScheduler", builderJob.getBeanDefinition());
                logger.info("Register Job:" + job.getJobClass());
            } catch (ClassNotFoundException e) {
                logger.error("Job Class:" + job.getJobClass() + "not Found");
            }
        });
    }

    private Object getLiteJobConfiguration(Job job) {
        JobCoreConfiguration jobCoreConfiguration = getJobCoreConfiguration(job);
        final JobTypeConfiguration jobTypeConfiguration;
        switch (job.getType()) {
            case SIMPLE:
                jobTypeConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, job.getJobClass());
                break;
            case DATA_FLOW:
                jobTypeConfiguration = new DataflowJobConfiguration(jobCoreConfiguration, job.getJobClass(), true);
                break;
            case SCRIPTS:
                jobTypeConfiguration = new ScriptJobConfiguration(jobCoreConfiguration, job.getJobClass());
                break;
            default:
                jobTypeConfiguration = new SimpleJobConfiguration(jobCoreConfiguration, job.getJobClass());
        }
        return LiteJobConfiguration.newBuilder(jobTypeConfiguration)
                .overwrite(job.isOverwrite())
                .disabled(job.isDisabled())
                .jobShardingStrategyClass(job.getJobShardingStrategyClass())
                .maxTimeDiffSeconds(job.getMaxTimeDiffSeconds())
                .monitorExecution(job.isMonitorExecution())
                .monitorPort(job.getMonitorPort())
                .reconcileIntervalMinutes(job.getReconcileIntervalMinutes())
                .build();
    }

    private JobCoreConfiguration getJobCoreConfiguration(Job job) {
        return JobCoreConfiguration.newBuilder(job.getJobClass(), job.getCron(), job.getShardingTotalCount())
                .shardingItemParameters(job.getShardingItemParameters())
                .misfire(job.isMisfire())
                .failover(job.isFailover())
                .jobParameter(job.getJobParameter())
                .description(job.getDescription()).build();
    }

    private List<Job> getJobs(BeanDefinitionRegistry registry) {
        Map<String, Object> map = resolver.getSubProperties(JOBS_PREFIX);
        Map<String, Job> jobMap = new HashMap<>();
        map.keySet().forEach(key -> {
            String jobName = StringUtil.substring(key, 0, key.indexOf("."));
            if (!jobMap.containsKey(jobName)) {
                Job job = new Job();
                job.setJobName(jobName);
                job.setJobClass((String) map.get(jobName + ".jobClass"));
                job.setCron((String) map.get(jobName + ".cron"));
                if (map.containsKey(jobName + ".type")) {
                    job.setType(Enum.valueOf(JobType.class, (String) map.get(jobName + ".type")));
                }
                if (map.containsKey(jobName + ".shardingTotalCount")) {
                    job.setShardingTotalCount(Integer.valueOf((String) map.get(jobName + ".shardingTotalCount")));
                }
                if (map.containsKey(jobName + ".shardingItemParameters")) {
                    job.setShardingItemParameters((String) map.get(jobName + ".shardingItemParameters"));
                }
                if (map.containsKey(jobName + ".jobParameter")) {
                    job.setJobParameter((String) map.get(jobName + ".jobParameter"));
                }
                if (map.containsKey(jobName + ".failover")) {
                    job.setFailover(Boolean.valueOf((String) map.get(jobName + ".failover")));
                }
                if (map.containsKey(jobName + ".misfire")) {
                    job.setMisfire(Boolean.valueOf((String) map.get(jobName + ".misfire")));
                }
                if (map.containsKey(jobName + ".description")) {
                    job.setDescription((String) map.get(jobName + ".description"));
                }
                if (map.containsKey(jobName + ".monitorExecution")) {
                    job.setMonitorExecution(Boolean.valueOf((String) map.get(jobName + ".monitorExecution")));
                }
                if (map.containsKey(jobName + ".maxTimeDiffSeconds")) {
                    job.setMaxTimeDiffSeconds(Integer.valueOf((String) map.get(jobName + ".maxTimeDiffSeconds")));
                }
                if (map.containsKey(jobName + ".monitorPort")) {
                    job.setMonitorPort(Integer.valueOf((String) map.get(jobName + ".monitorPort")));
                }
                if (map.containsKey(jobName + ".jobShardingStrategyClass")) {
                    job.setJobShardingStrategyClass((String) map.get(jobName + ".jobShardingStrategyClass"));
                }
                if (map.containsKey(jobName + ".disabled")) {
                    job.setDisabled(Boolean.valueOf((String) map.get(jobName + ".disabled")));
                }
                if (map.containsKey(jobName + ".overwrite")) {
                    job.setOverwrite(Boolean.valueOf((String) map.get(jobName + ".overwrite")));
                }
                if (map.containsKey(jobName + ".reconcileIntervalMinutes")) {
                    job.setReconcileIntervalMinutes(Integer.valueOf((String) map.get(jobName + ".reconcileIntervalMinutes")));
                }
                jobMap.put(jobName, job);
            }
        });
        return CollectionUtils.asList(jobMap.values());
    }

    @Override
    public void setBeanClassLoader(ClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void setEnvironment(Environment environment) {
        resolver = new RelaxedPropertyResolver(environment, PREFIX);
    }

    @Data
    private static class Job {

        private String jobName;

        private String jobClass;

        private JobType type = JobType.SIMPLE;

        private String cron;

        private int shardingTotalCount = 1;

        private String shardingItemParameters = "";

        private String jobParameter = "";

        private boolean failover;

        private boolean misfire = true;

        private String description = "";

        private boolean monitorExecution = true;

        private int maxTimeDiffSeconds = -1;

        private int monitorPort = -1;

        private String jobShardingStrategyClass = "";

        private boolean disabled;

        private boolean overwrite = true;

        private int reconcileIntervalMinutes = 10;

    }
}
