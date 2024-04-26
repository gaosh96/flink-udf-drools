package com.gaosh96.flink.udf.utils;

import org.drools.core.common.DefaultFactHandle;
import org.kie.api.KieBase;
import org.kie.api.definition.type.FactType;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

import java.util.HashMap;

/**
 * @author gaosh
 * @version 1.0
 * @since 2024/4/26
 */
public class DroolsUtilsTest {

    public static void main(String[] args) throws Exception {

        String drlContent = DroolsUtils.getValidDrlConf("customer");
        KieBase kieBase = DroolsUtils.getKieBaseFromDrlContent(drlContent);

        FactType customer = kieBase.getFactType("com.gaosh96.flink.udf.entity", "Customer");
        Object o = customer.newInstance();

        customer.getFields().forEach(field -> System.out.println(field.getName()));

        HashMap<String, Object> map = new HashMap<>();
        map.put("userAge", 20);
        map.put("userLevel", "L1");
        map.put("address", "上海市浦东新区");

        customer.setFromMap(o, map);

        StatelessKieSession kieSession = kieBase.newStatelessKieSession();
        Object object = ((DefaultFactHandle) kieSession.execute(CommandFactory.newInsert(o))).getObject();
        System.out.println(object);


    }

}
