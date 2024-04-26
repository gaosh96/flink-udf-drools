package com.gaosh96.flink.udf.utils;


import com.gaosh96.flink.udf.constants.UdfConstants;
import org.apache.commons.io.IOUtils;
import org.drools.core.common.DefaultFactHandle;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.utils.KieHelper;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

/**
 * @author gaosh
 * @version 1.0
 * @since 2024/4/25
 */
public class BuildDrlTest {

    public static void main(String[] args) throws Exception {

        StringWriter stringWriter = new StringWriter();
        IOUtils.copy(BuildDrlTest.class.getResourceAsStream("/rules/customer/customer.drl"), stringWriter, StandardCharsets.UTF_8);
        String drl = stringWriter.toString();

        System.out.println(drl);

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);
        KieBase kieBase = kieHelper.build();

        final FactType factType = kieBase.getFactType(UdfConstants.CLASS_PACKAGE, "Customer");
        final Object o = factType.newInstance();
        factType.set(o, "userAge", 20);
        factType.set(o, "userLevel", "L1");
        factType.set(o, "address", "上海市浦东新区");

        StatelessKieSession kieSession = kieBase.newStatelessKieSession();

        final Object object = ((DefaultFactHandle) kieSession.execute(CommandFactory.newInsert(o))).getObject();
        final Object $res = factType.get(object, "$res");
        System.out.println($res);


    }
}
