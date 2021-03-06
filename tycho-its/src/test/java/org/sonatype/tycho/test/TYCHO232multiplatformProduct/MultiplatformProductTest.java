package org.sonatype.tycho.test.TYCHO232multiplatformProduct;

import java.io.File;

import org.apache.maven.it.Verifier;
import org.junit.Assert;
import org.junit.Test;
import org.sonatype.tycho.test.AbstractTychoIntegrationTest;

public class MultiplatformProductTest
    extends AbstractTychoIntegrationTest
{
    @Test
    public void exportProduct()
        throws Exception
    {
        Verifier verifier = getVerifier( "/TYCHO232multiplatformProduct" );
        verifier.executeGoal( "integration-test" );
        verifier.verifyErrorFreeLog();

        File targetdir = new File( verifier.getBasedir(), "tycho.demo.rcp/target" );

        // assert expanded product folders have proper swt fragments
        assertFileExists( targetdir, "linux.gtk.x86_64/eclipse/plugins/org.eclipse.swt.gtk.linux.x86_64_*.jar" );
        assertFileExists( targetdir, "macosx.carbon.x86/eclipse/plugins/org.eclipse.swt.carbon.macosx_*.jar" );
        assertFileExists( targetdir, "win32.win32.x86/eclipse/plugins/org.eclipse.swt.win32.win32.x86_*.jar" );

        // assert native launchers
        Assert.assertTrue( new File( targetdir, "linux.gtk.x86_64/eclipse/libcairo-swt.so" ).canRead() );
        Assert.assertTrue( new File( targetdir, "linux.gtk.x86_64/eclipse/tychodemo" ).canRead() );
        // TODO osx
        Assert.assertTrue( new File( targetdir, "win32.win32.x86/eclipse/tychodemo.exe" ).canRead() );

        // assert product zip was created for each target environment
        Assert.assertTrue( new File( targetdir, "tycho.demo.rcp-1.0.0-linux.gtk.x86_64.zip" ).canRead() );
        Assert.assertTrue( new File( targetdir, "tycho.demo.rcp-1.0.0-macosx.carbon.x86.zip" ).canRead() );
        Assert.assertTrue( new File( targetdir, "tycho.demo.rcp-1.0.0-win32.win32.x86.zip" ).canRead() );
    }

}
