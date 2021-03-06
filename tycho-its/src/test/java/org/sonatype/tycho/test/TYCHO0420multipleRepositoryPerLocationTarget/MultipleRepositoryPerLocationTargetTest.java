package org.sonatype.tycho.test.TYCHO0420multipleRepositoryPerLocationTarget;

import java.io.File;

import org.apache.maven.it.Verifier;
import org.codehaus.tycho.model.Target;
import org.codehaus.tycho.model.Target.Repository;
import org.junit.Test;
import org.sonatype.tycho.test.AbstractTychoIntegrationTest;

public class MultipleRepositoryPerLocationTargetTest extends AbstractTychoIntegrationTest
{
    @Test
    public void testDependenciesOfUnitResolvedFromTwoRepositories()
        throws Exception
    {
        Verifier verifier = getVerifier( "/TYCHO0420multipleRepositoryPerLocationTarget", false );

        File targetPlatformFile = new File( verifier.getBasedir(), "target-platform/tycho420.target" );
        makeRepositoryURLsAbsolute( targetPlatformFile );

        verifier.executeGoal( "integration-test" );
        verifier.verifyErrorFreeLog();
    }

    static void makeRepositoryURLsAbsolute( File dotTargetFile )
        throws Exception
    {
        Target target = Target.read( dotTargetFile );

        for ( Target.Location location : target.getLocations() )
        {
            for ( Repository repository : location.getRepositories() )
            {
                File file = new File( repository.getLocation() );
                repository.setLocation( file.getCanonicalFile().toURI().toASCIIString() );
            }
        }

        Target.write( target, dotTargetFile );
    }
}
