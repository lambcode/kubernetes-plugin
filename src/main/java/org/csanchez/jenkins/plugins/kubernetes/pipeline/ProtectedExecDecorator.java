package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import hudson.Extension;
import hudson.Launcher;
import hudson.LauncherDecorator;
import hudson.Proc;
import hudson.model.Node;
import org.csanchez.jenkins.plugins.kubernetes.KubernetesCloud;
import org.csanchez.jenkins.plugins.kubernetes.KubernetesSlave;
import org.csanchez.jenkins.plugins.kubernetes.PodTemplate;

import java.io.IOException;
import java.io.Serializable;
import java.util.Collections;

/**
 * When {@link KubernetesCloud#isDynamicServiceAccountSecurity()} is enabled, ensures files cannot be read from
 * protected pods unless their id is in the {@link ProtectedPodContext}
 * <p>
 * This LauncherDecorator is installed globally, in steps that change the execution context.
 * <p>
 * It needs to be installed globally because steps like the 'sh' step can be run on a kubernetes node without
 * calling any other step from this plugin. Having it installed prevents those executions on protected pod templates.
 * <p>
 * Due to more permissive launchers being layered on top of less permissive ones, we only want to have the outer most
 * launcher actually check the permissions. A env variable is added once the check has been done on the outer most
 * launcher and subsequent launchers in the chain will be a pass-through.
 */
@Extension
public class ProtectedExecDecorator extends LauncherDecorator implements Serializable {

    private static final String CHECKED_FLAG = "ProtectedExecDecoratorCheckedFlag=";
    private final ProtectedPodContext protectedPodContext;

    public ProtectedExecDecorator() {
        this(new ProtectedPodContext(Collections.emptyList()));
    }

    public ProtectedExecDecorator(ProtectedPodContext protectedPodContext) {
        this.protectedPodContext = protectedPodContext;
    }

    @Override
    public Launcher decorate(Launcher launcher, Node node) {
        if (node instanceof KubernetesSlave) {
            return new ProtectedLauncher(launcher, (KubernetesSlave) node);
        }
        return launcher;
    }

    private class ProtectedLauncher extends Launcher.DecoratedLauncher {

        private final KubernetesSlave slave;

        public ProtectedLauncher(Launcher delegate, KubernetesSlave slave) {
            super(delegate);
            this.slave = slave;
        }

        @Override
        public Proc launch(ProcStarter starter) throws IOException {
            PodTemplate template = slave.getTemplate();

            String[] currentEnvs = starter.envs();
            for (String env : currentEnvs) {
                if (env.startsWith(CHECKED_FLAG)) {
                    String alreadyCheckedTemplateId = env.split("=")[1];
                    if (alreadyCheckedTemplateId.equals(template.getId())) {
                        return super.launch(starter);
                    }
                }
            }

            if (template.isProtected() && !protectedPodContext.contains(template.getId())) {
                throw new SecurityException("No executions allowed in a pod created from a protected template when template is not in pipeline context.");
            }

            String[] newEnvs = new String[currentEnvs.length + 1];
            System.arraycopy(currentEnvs, 0, newEnvs, 1, currentEnvs.length);
            // Put the new env at the start to optimize looking through the list in inner launchers
            newEnvs[0] = CHECKED_FLAG + template.getId();
            ProcStarter newStarter = starter.envs(newEnvs);
            return super.launch(newStarter);
        }
    }
}
