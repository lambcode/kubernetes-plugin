package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import io.fabric8.kubernetes.client.KubernetesClient;

import java.io.Serializable;
import java.util.List;

public interface ServiceAccountSelector extends Serializable {
    List<NameAndNamespace> getServiceAccounts(KubernetesClient kubernetesClient);

    class NameAndNamespace {
        private final String name;
        private final String namespace;

        NameAndNamespace(String name, String namespace) {
            this.name = name;
            this.namespace = namespace;
        }

        String getName() {
            return name;
        }

        String getNamespace() {
            return namespace;
        }
    }
}
