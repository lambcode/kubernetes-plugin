package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import org.csanchez.jenkins.plugins.kubernetes.PodTemplate;
import org.jenkinsci.plugins.workflow.steps.StepContext;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
}
