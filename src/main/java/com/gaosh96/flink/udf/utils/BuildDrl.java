package com.gaosh96.flink.udf.utils;


import com.gaosh96.flink.udf.entity.Customer;
import org.apache.commons.io.IOUtils;
import org.drools.core.common.DefaultFactHandle;
import org.kie.api.KieBase;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;
import org.kie.internal.utils.KieHelper;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

/**
 * @author gaosh
 * @version 1.0
 * @since 2024/4/25
 */
public class BuildDrl {

    public static void main(String[] args) throws Exception {

        StringWriter stringWriter = new StringWriter();
        IOUtils.copy(BuildDrl.class.getResourceAsStream("/rules/customer/customer.drl"), stringWriter, StandardCharsets.UTF_8);
        String drl = stringWriter.toString();

        System.out.println(drl);

        KieHelper kieHelper = new KieHelper();
        kieHelper.addContent(drl, ResourceType.DRL);
        KieBase kieBase = kieHelper.build();

        StatelessKieSession kieSession = kieBase.newStatelessKieSession();
        Customer customer = new Customer();
        customer.setUserAge(20);
        customer.setUserLevel("L1");
        customer.setAddress("深圳市坂田");

        Customer res = ((DefaultFactHandle) kieSession.execute(CommandFactory.newInsert(customer))).as(Customer.class);
        System.out.println(res.getLabel());


    }
}
