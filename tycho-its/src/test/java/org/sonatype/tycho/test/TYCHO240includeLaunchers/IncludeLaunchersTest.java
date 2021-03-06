package org.sonatype.tycho.test.TYCHO240includeLaunchers;

import java.io.File;

import org.apache.maven.it.Verifier;
import org.junit.Assert;
import org.junit.Test;
import org.sonatype.tycho.test.AbstractTychoIntegrationTest;

public class IncludeLaunchersTest
    extends AbstractTychoIntegrationTest
{

    @Test
    public void includeLaunchers()
        throws Exception
    {
        Verifier verifier = getVerifier( "/TYCHO240includeLaunchers/includeLaunchers" );
        verifier.executeGoal( "integration-test" );
        verifier.verifyErrorFreeLog();

        File targetdir = new File( verifier.getBasedir(), "target" );

        // assert product zip was created for each target environment
        Assert.assertTrue( new File( targetdir, "linux.gtk.x86_64/eclipse/libcairo-swt.so" ).canRead() );
        Assert.assertTrue( new File( targetdir, "linux.gtk.x86_64/eclipse/includedLauncher" ).canRead() );
        
    }

    @Test
    public void noIncludeLaunchers()
        throws Exception
    {
        Verifier verifier = getVerifier( "/TYCHO240includeLaunchers/noIncludeLaunchers" );
        verifier.executeGoal( "integration-test" );
        verifier.verifyErrorFreeLog();

        File targetdir = new File( verifier.getBasedir(), "target" );

        // assert product zip was created for each target environment
        Assert.assertFalse( new File( targetdir, "linux.gtk.x86_64/eclipse/libcairo-swt.so" ).canRead() );
        Assert.assertFalse( new File( targetdir, "linux.gtk.x86_64/eclipse/includedLauncher" ).canRead() );
    }

}
