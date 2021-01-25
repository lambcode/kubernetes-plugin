package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import io.fabric8.kubernetes.client.KubernetesClient;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ServiceAccountSelectorFromLabels implements ServiceAccountSelector {

    private final String namespace;
    private final Map<String, String> labels;

    public ServiceAccountSelectorFromLabels(Map<String, String> labels) {
        this(null, labels);
    }

    /**
     * @param namespace Uses namespace defined in cloud config if {@code null}
     * @param labels set of kubernetes labels to match
     */
    public ServiceAccountSelectorFromLabels(@Nullable String namespace, Map<String, String> labels) {
        this.namespace = namespace;
        this.labels = labels;
    }

    @Override
    public List<NameAndNamespace> getServiceAccounts(KubernetesClient client) {
        return client.serviceAccounts()
                .inNamespace(namespace != null ? namespace : client.getNamespace())
                .withLabels(labels).list().getItems().stream()
                .map(x -> new NameAndNamespace(x.getMetadata().getName(), x.getMetadata().getNamespace()))
                .collect(Collectors.toList());
    }
}
