package org.csanchez.jenkins.plugins.kubernetes.pipeline;


import org.jenkinsci.plugins.workflow.steps.BodyExecutionCallback;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepExecution;

class WithAllowedServiceAccountsStepExecution extends StepExecution {

    private final ServiceAccountSelector serviceAccountSelector;

    WithAllowedServiceAccountsStepExecution(ServiceAccountSelector serviceAccountSelector, StepContext context) {
        super(context);
        this.serviceAccountSelector = serviceAccountSelector;
    }

    @Override
    public boolean start() throws Exception {
        StepContext context = getContext();
        ServiceAccountSelector existingSelector = context.get(ServiceAccountSelector.class);
        ServiceAccountSelector newSelector = serviceAccountSelector;
        if (existingSelector != null) {
            newSelector = new ServiceAccountSelectorUnion(existingSelector, newSelector);
        }
        context.newBodyInvoker().withContext(newSelector).withCallback(BodyExecutionCallback.wrap(context)).start();
        return false;
    }
}
