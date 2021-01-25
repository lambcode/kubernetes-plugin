package org.csanchez.jenkins.plugins.kubernetes.pipeline;

import hudson.model.Result;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.ServiceAccount;
import io.fabric8.kubernetes.client.KubernetesClient;
import org.csanchez.jenkins.plugins.kubernetes.KubernetesCloud;
import org.csanchez.jenkins.plugins.kubernetes.KubernetesTestUtil;
import org.jenkinsci.plugins.workflow.cps.CpsFlowDefinition;
import org.jenkinsci.plugins.workflow.job.WorkflowJob;
import org.jenkinsci.plugins.workflow.job.WorkflowRun;
import org.junit.*;
import org.junit.rules.TestName;
import org.jvnet.hudson.test.JenkinsRuleNonLocalhost;

import java.util.Collections;

import static org.csanchez.jenkins.plugins.kubernetes.KubernetesTestUtil.assumeKubernetes;
import static org.csanchez.jenkins.plugins.kubernetes.KubernetesTestUtil.setupHost;
import static org.junit.Assert.assertNotNull;

public class WithAllowedServiceAccountsStepExecutionTest {
    @Rule
    public JenkinsRuleNonLocalhost r = new JenkinsRuleNonLocalhost();

    @Rule
    public TestName name = new TestName();

    private KubernetesCloud cloud;

    @BeforeClass
    public static void isKubernetesConfigured() throws Exception {
        assumeKubernetes();
    }

    @Before
    public void configureCloud() throws Exception {
        cloud = KubernetesTestUtil.setupCloud(this, name);
        cloud.setDynamicServiceAccountSecurity(true);
        setupHost();
        r.jenkins.clouds.add(cloud);

        KubernetesClient client = cloud.connect();
        createServiceAccountForEnvironment(client, "dev");
        createServiceAccountForEnvironment(client, "prod");
    }

    @After
    public void deleteServiceAccounts() throws Exception {
        KubernetesClient client = cloud.connect();
        deleteServiceAccountForEnvironment(client, "dev");
        deleteServiceAccountForEnvironment(client, "prod");
    }

    private void createServiceAccountForEnvironment(KubernetesClient client, String environment) throws Exception {
        // Clean up any existing service account that may exist
        deleteServiceAccountForEnvironment(client, environment);

        ObjectMeta serviceAccountMeta = new ObjectMeta();
        serviceAccountMeta.setName(environment + "-" + name.getMethodName().toLowerCase());
        serviceAccountMeta.setNamespace(cloud.getNamespace());
        serviceAccountMeta.setLabels(Collections.singletonMap("environment", environment));
        ServiceAccount serviceAccount = new ServiceAccount();
        serviceAccount.setMetadata(serviceAccountMeta);
        client.serviceAccounts().create(serviceAccount);
    }

    private void deleteServiceAccountForEnvironment(KubernetesClient client, String environment) throws Exception {
        client.serviceAccounts().withName(environment + "-" + name.getMethodName().toLowerCase()).delete();
    }

    private String loadPipelineScript(String scriptName) {
        return KubernetesTestUtil.loadPipelineScript(getClass(), scriptName).replace("$TEST_METHOD_NAME", name.getMethodName().toLowerCase());
    }

    @Test
    public void testUnapprovedServiceAccount() throws Exception {
        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "unapproved_service_account");
        p.setDefinition(new CpsFlowDefinition(loadPipelineScript("withUnapprovedServiceAccount.groovy"), false)); // non-sandboxed to simulate pipeline library
        WorkflowRun b = p.scheduleBuild2(0).waitForStart();
        assertNotNull(b);
        r.assertBuildStatus(Result.FAILURE, r.waitForCompletion(b));
        r.assertLogContains("ERROR: Dynamic Service Account Security enabled and Service account", b);
    }

    @Test
    public void testApprovedServiceAccount() throws Exception {
        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "approved_service_account");
        p.setDefinition(new CpsFlowDefinition(loadPipelineScript("withApprovedServiceAccount.groovy"), false)); // non-sandboxed to simulate pipeline library
        WorkflowRun b = p.scheduleBuild2(0).waitForStart();
        assertNotNull(b);
        r.assertBuildStatusSuccess(r.waitForCompletion(b));
        r.assertLogContains("INFO: Handshaking", b);
        r.assertLogContains("INFO: Connected", b);
    }

    @Test
    public void testStepFailsInSandbox() throws Exception {
        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "with_service_account_step_fail");
        p.setDefinition(new CpsFlowDefinition(loadPipelineScript("withApprovedServiceAccount.groovy"), true));
        WorkflowRun b = p.scheduleBuild2(0).waitForStart();
        assertNotNull(b);
        r.assertBuildStatus(Result.FAILURE, r.waitForCompletion(b));
        r.assertLogContains("Scripts not permitted to use new org.csanchez.jenkins.plugins.kubernetes.pipeline.ServiceAccountSelectorFromLabels", b);
    }

    @Test
    public void testContextsAreNested() throws Exception {
        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "with_service_account_nested");
        p.setDefinition(new CpsFlowDefinition(loadPipelineScript("withApprovedServiceAccountNested.groovy"), false)); // non-sandboxed to simulate pipeline library
        WorkflowRun b = p.scheduleBuild2(0).waitForStart();
        assertNotNull(b);
        r.assertBuildStatusSuccess(r.waitForCompletion(b));
        r.assertLogContains("INFO: Handshaking", b);
        r.assertLogContains("INFO: Connected", b);
    }

    @Test
    public void testUnapprovedServiceAccountPassesWhenCloudOptionNotEnabled() throws Exception {
        cloud.setDynamicServiceAccountSecurity(false);
        WorkflowJob p = r.jenkins.createProject(WorkflowJob.class, "unapproved_service_account_passes");
        p.setDefinition(new CpsFlowDefinition(loadPipelineScript("withUnapprovedServiceAccount.groovy"), false)); // non-sandboxed to simulate pipeline library
        WorkflowRun b = p.scheduleBuild2(0).waitForStart();
        assertNotNull(b);
        r.assertBuildStatusSuccess(r.waitForCompletion(b));
        r.assertLogContains("INFO: Handshaking", b);
        r.assertLogContains("INFO: Connected", b);
    }
}
