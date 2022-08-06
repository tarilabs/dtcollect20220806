
package org.example;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static utils.shim.Map.of;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.dmn.api.core.DMNContext;
import org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus;
import org.kie.dmn.api.core.DMNModel;
import org.kie.dmn.api.core.DMNResult;
import org.kie.dmn.api.core.DMNRuntime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DTCollectTest {
    static final Logger LOG = LoggerFactory.getLogger(DTCollectTest.class);

    private DMNRuntime dmnRuntime;
    private DMNModel dmnModelUT;
    
    @Before
    public void init() {
        KieServices kieServices = KieServices.Factory.get();
        KieContainer kieContainer = kieServices.getKieClasspathContainer();
        dmnRuntime = KieRuntimeFactory.of(kieContainer.getKieBase()).get(DMNRuntime.class);

        final String namespace = "https://kiegroup.org/dmn/_B48BE9F2-BB6C-4139-8DEB-1DB4CE18088B";
        final String modelName = "dtcollect";
        dmnModelUT = dmnRuntime.getModel(namespace, modelName);
    }
    
    @Test
    public void test() {
        DMNContext dmnContext = dmnRuntime.newContext();
        dmnContext.set("a", true);
        dmnContext.set("b", true);

        LOG.info("Evaluating DMN model");
        DMNResult dmnResult = dmnRuntime.evaluateAll(dmnModelUT, dmnContext);

        LOG.info("Checking results: {}", dmnResult);
        assertFalse(dmnResult.hasErrors());

        assertEquals(DecisionEvaluationStatus.SUCCEEDED, dmnResult.getDecisionResultByName("my decision").getEvaluationStatus());
        List<?> myDecisionResult = (List<?>) dmnResult.getDecisionResultByName("my decision").getResult();
        assertTrue(myDecisionResult.contains(of("name", "a true", "notes", "observing a true")));
        assertTrue(myDecisionResult.contains(of("name", "b true", "notes", "observing b true")));
        assertTrue(myDecisionResult.contains(of("name", "true", "notes", "observing true")));
    }
}