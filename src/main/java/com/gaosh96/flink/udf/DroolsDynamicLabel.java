package com.gaosh96.flink.udf;

import com.gaosh96.flink.udf.constants.UdfConstants;
import com.gaosh96.flink.udf.utils.DroolsUtils;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.ScalarFunction;
import org.apache.flink.util.concurrent.ExecutorThreadFactory;
import org.drools.core.common.DefaultFactHandle;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author gaosh
 * @version 1.0
 * @since 2024/4/24
 */
public class DroolsDynamicLabel extends ScalarFunction {

    private static final Logger LOG = LoggerFactory.getLogger(DroolsDynamicLabel.class);

    private String ruleName;
    private String validDrlConf;
    private KieBase kieBase;
    private StatelessKieSession kieSession;
    private final transient ScheduledExecutorService scheduledExecutorService = new ScheduledThreadPoolExecutor(1, new ExecutorThreadFactory("drools-drl-update"));

    @Override
    public void open(FunctionContext context) throws Exception {
        // set pipeline.global-job-parameters = 'ruleName:Customer'
        ruleName = context.getJobParameter(UdfConstants.RULE_NAME, null);

        if (ruleName == null) {
            throw new Exception("job parameter [ruleName] not found");
        }

        validDrlConf = DroolsUtils.getValidDrlConf(ruleName);

        kieBase = DroolsUtils.getKieBaseFromDrlContent(validDrlConf);
        kieSession = kieBase.newStatelessKieSession();

        scheduledExecutorService.scheduleWithFixedDelay(this::reloadKieSession, 1, 5, TimeUnit.MINUTES);
    }

    /**
     * Map[k1,v1,k2,v2]
     *
     * @param params
     * @return
     * @throws Exception
     */
    public String eval(Map<String, Object> params) throws Exception {
        // 可以把 ruleName 作为 drl 文件中声明的类名
        FactType factType = kieBase.getFactType(UdfConstants.CLASS_PACKAGE, ruleName);
        Object fact = factType.newInstance();
        factType.getFields().forEach(field -> {
            String fieldName = field.getName();
            factType.set(fact, fieldName, params.get(fieldName));
        });

        Object object = ((DefaultFactHandle) kieSession.execute(CommandFactory.newInsert(fact))).getObject();
        return (String) factType.get(object, UdfConstants.RESULT_FIELD_NAME);
    }

    /**
     * 重新加载数据库中的 drl 配置
     */
    private void reloadKieSession() {
        String latestValidDrlConf = DroolsUtils.getValidDrlConf(ruleName);
        if (latestValidDrlConf != null && !validDrlConf.equals(latestValidDrlConf)) {
            kieBase = DroolsUtils.getKieBaseFromDrlContent(validDrlConf);
            kieSession = kieBase.newStatelessKieSession();
            LOG.info(System.currentTimeMillis() / 1000 + " update drools drl config ...");
        }
    }

    @Override
    public void close() {
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
        }
    }
}
