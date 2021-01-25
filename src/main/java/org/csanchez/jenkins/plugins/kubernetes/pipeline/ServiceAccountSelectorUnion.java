package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import io.fabric8.kubernetes.client.KubernetesClient;

import java.util.ArrayList;
import java.util.List;

class ServiceAccountSelectorUnion implements ServiceAccountSelector {
    private final ServiceAccountSelector selector1;
    private final ServiceAccountSelector selector2;

    ServiceAccountSelectorUnion(ServiceAccountSelector selector1, ServiceAccountSelector selector2) {
        this.selector1 = selector1;
        this.selector2 = selector2;
    }

    @Override
    public List<NameAndNamespace> getServiceAccounts(KubernetesClient kubernetesClient) {
        List<NameAndNamespace> newList = new ArrayList<>();
        newList.addAll(selector1.getServiceAccounts(kubernetesClient));
        newList.addAll(selector2.getServiceAccounts(kubernetesClient));
        return newList;
    }
}
