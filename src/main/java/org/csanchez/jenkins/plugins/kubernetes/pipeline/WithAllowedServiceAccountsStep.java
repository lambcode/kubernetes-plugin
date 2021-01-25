package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import hudson.Extension;
import org.jenkinsci.plugins.workflow.steps.Step;
import org.jenkinsci.plugins.workflow.steps.StepContext;
import org.jenkinsci.plugins.workflow.steps.StepDescriptor;
import org.jenkinsci.plugins.workflow.steps.StepExecution;
import org.kohsuke.stapler.DataBoundConstructor;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;

public class WithAllowedServiceAccountsStep extends Step implements Serializable {

    private final ServiceAccountSelector serviceAccountSelector;

    @DataBoundConstructor
    public WithAllowedServiceAccountsStep(ServiceAccountSelector serviceAccountSelector) {
        this.serviceAccountSelector = serviceAccountSelector;
    }

    @Override
    public StepExecution start(StepContext context) throws Exception {
        return new WithAllowedServiceAccountsStepExecution(serviceAccountSelector, context);
    }

    @Extension
    public static class DescriptorImpl extends StepDescriptor {

        @Override
        public String getFunctionName() {
            return "withAllowedServiceAccounts";
        }

        @Override
        public String getDisplayName() {
            return "For use with dynamic service account security mode. Allows service accounts to be constrained on new pod templates.";
        }

        @Override
        public boolean takesImplicitBlockArgument() {
            return true;
        }

        @Override
        public Set<? extends Class<?>> getRequiredContext() {
            return Collections.emptySet();
        }
    }
}
