package com.gaosh96.flink.udf.utils;

import com.gaosh96.flink.udf.entity.Customer;
import org.drools.core.common.DefaultFactHandle;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;
import org.kie.internal.command.CommandFactory;

/**
 * @author gaosh
 * @version 1.0
 * @since 2024/4/25
 */
public class DroolsTest {

    public static void main(String[] args) {

        KieServices kieServices = KieServices.Factory.get();
        KieContainer kContainer = kieServices.getKieClasspathContainer();
        StatelessKieSession kieSession = kContainer.newStatelessKieSession("ks1");

        Customer customer = new Customer();
        customer.setUserAge(20);
        customer.setUserLevel("L1");
        customer.setAddress("深圳市坂田");

        Customer res = ((DefaultFactHandle) kieSession.execute(CommandFactory.newInsert(customer))).as(Customer.class);
        System.out.println(res.getLabel());
    }
}
