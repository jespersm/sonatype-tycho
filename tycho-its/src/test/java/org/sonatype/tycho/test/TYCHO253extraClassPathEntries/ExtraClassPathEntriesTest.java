package org.sonatype.tycho.test.TYCHO253extraClassPathEntries;

import java.util.Arrays;

import org.apache.maven.it.Verifier;
import org.junit.Test;
import org.sonatype.tycho.test.AbstractTychoIntegrationTest;

public class ExtraClassPathEntriesTest extends AbstractTychoIntegrationTest {
	@Test
	public void testJarsExtraClasspath() throws Exception {
		Verifier verifier = getVerifier("/TYCHO253extraClassPathEntries/org.sonatype.tycho.testExtraClasspathTest1");
        verifier.executeGoals(Arrays.asList("clean","install"));
		verifier.verifyErrorFreeLog();
	}
	
	@Test
	public void testExtraClasspath() throws Exception {
		Verifier verifier = getVerifier("/TYCHO253extraClassPathEntries/org.sonatype.tycho.testExtraClasspathTest2");
		verifier.executeGoals(Arrays.asList("clean","install"));
		verifier.verifyErrorFreeLog();
	}
	
	@Test
	public void testReferenceToInnerJar() throws Exception {
		Verifier verifier = getVerifier("/TYCHO253extraClassPathEntries");
        verifier.executeGoals(Arrays.asList("clean","install"));
		verifier.verifyErrorFreeLog();
	}
}
