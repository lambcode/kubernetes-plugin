package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import org.jenkinsci.plugins.workflow.steps.DynamicContext;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A context object containing a list of all pod template ids that are in scope at a particular point in the pipeline
 * script.
 */
class ProtectedPodContext implements Serializable {

    private List<String> allowedTemplateIds;

    public ProtectedPodContext(@Nonnull List<String> allowedTemplateIds) {
        this.allowedTemplateIds = allowedTemplateIds;
    }

    public ProtectedPodContext append(String templateId) {
        ArrayList<String> newList = new ArrayList<>(allowedTemplateIds.size() + 1);
        newList.addAll(allowedTemplateIds);
        newList.add(templateId);
        return new ProtectedPodContext(newList);
    }

    public boolean contains(String templateId) {
        return allowedTemplateIds.contains(templateId);
    }

    public static ProtectedPodContext fromContext(StepContext context) throws IOException, InterruptedException {
        ProtectedPodContext existing = context.get(ProtectedPodContext.class);
        return existing != null ? existing : new ProtectedPodContext(Collections.emptyList());
    }

    public static ProtectedPodContext fromContext(DynamicContext.DelegatedContext context) throws IOException, InterruptedException {
        ProtectedPodContext existing = context.get(ProtectedPodContext.class);
        return existing != null ? existing : new ProtectedPodContext(Collections.emptyList());
    }
}
