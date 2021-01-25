package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import io.fabric8.kubernetes.client.KubernetesClient;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class ServiceAccountSelectorFromNames implements ServiceAccountSelector {

    private final String namespace;
    private final List<String> serviceAccountNames;

    public ServiceAccountSelectorFromNames(List<String> serviceAccountNames) {
        this(null, serviceAccountNames);
    }

    /**
     * @param namespace Uses namespace defined in cloud config if {@code null}
     */
    public ServiceAccountSelectorFromNames(@Nullable String namespace, List<String> serviceAccountNames) {
        this.namespace = namespace;
        this.serviceAccountNames = serviceAccountNames;
    }

    @Override
    public List<NameAndNamespace> getServiceAccounts(KubernetesClient client) {
        return serviceAccountNames.stream()
                .map(name -> new NameAndNamespace(name,namespace != null ? namespace : client.getNamespace()))
                .collect(Collectors.toList());
    }
}
