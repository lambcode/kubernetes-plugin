package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import hudson.Extension;
import hudson.FilePath;
import hudson.model.Node;
import org.csanchez.jenkins.plugins.kubernetes.KubernetesCloud;
import org.csanchez.jenkins.plugins.kubernetes.KubernetesSlave;
import org.csanchez.jenkins.plugins.kubernetes.PodTemplate;
import org.jenkinsci.plugins.workflow.steps.DynamicContext;

import java.io.IOException;
import java.util.logging.Logger;

/**
 * When {@link KubernetesCloud#isDynamicServiceAccountSecurity()} is enabled, ensures files cannot be read from
 * protected pods unless their id is in the {@link ProtectedPodContext}
 */
@Extension
public final class FilePathDynamicContext extends DynamicContext.Typed<FilePath> {

    private static final Logger LOGGER = Logger.getLogger(FilePathDynamicContext.class.getName());

    @Override
    protected Class<FilePath> type() {
        return FilePath.class;
    }

    @Override
    protected FilePath get(DelegatedContext context) throws IOException, InterruptedException {

        FilePath current = context.get(FilePath.class);
        Node node = context.get(Node.class);
        KubernetesSlave slave = node instanceof KubernetesSlave ? (KubernetesSlave) node : null;
        if (slave == null || current == null) {
            return current;
        }

        ProtectedPodContext protectedPodContext = ProtectedPodContext.fromContext(context);
        PodTemplate template = slave.getTemplate();
        if (!template.isProtected() || protectedPodContext.contains(template.getId())) {
            return current;
        }

        throw new SecurityException("No executions allowed in a pod created from a protected template when template is not in pipeline context.");
    }
}