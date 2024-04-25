package com.gaosh96.flink.udf;

import com.gaosh96.flink.udf.utils.DroolsUtils;
import org.apache.flink.table.functions.FunctionContext;
import org.apache.flink.table.functions.ScalarFunction;
import org.kie.api.KieBase;
import org.kie.api.runtime.StatelessKieSession;

/**
 * @author gaosh
 * @version 1.0
 * @since 2024/4/24
 */
public class DroolsDynamicLabel extends ScalarFunction {

    private StatelessKieSession kieSession;

    @Override
    public void open(FunctionContext context) throws Exception {
        // pipeline.global-job-parameters
        String ruleName = context.getJobParameter("ruleName", "");
        String validDrlConf = DroolsUtils.getValidDrlConf(ruleName);

        KieBase kieBase = DroolsUtils.getKieBaseFromDrlContent(validDrlConf);
        kieSession = kieBase.newStatelessKieSession();
    }


}
