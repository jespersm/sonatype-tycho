package org.sonatype.tycho.versions.manipulation;

import java.io.File;
import java.io.IOException;
import java.util.Set;

import org.codehaus.plexus.component.annotations.Component;
import org.sonatype.tycho.versions.bundle.MutableBundleManifest;
import org.sonatype.tycho.versions.engine.VersionChange;
import org.sonatype.tycho.versions.engine.MetadataManipulator;
import org.sonatype.tycho.versions.engine.ProjectMetadata;

@Component( role = MetadataManipulator.class, hint = "bundle-manifest" )
public class BundleManifestManipulator
    extends AbstractMetadataManipulator
{

    public void applyChange( ProjectMetadata project, VersionChange change, Set<VersionChange> allChanges )
    {
        if ( isBundle( project ) )
        {
            // only update bundle version for now
            if ( isBundleVersionChange( change ) && isBundleIdEquals( project, change ) )
            {
                MutableBundleManifest mf = getBundleManifest( project );

                logger.info( "  META-INF/MANIFEST.MF//Bundle-Version: " + change.getVersion() + " => "
                    + change.getNewVersion() );

                mf.setVersion( change.getNewVersion() );
            }
        }
    }

    private boolean isBundleIdEquals( ProjectMetadata project, VersionChange change )
    {
        MutableBundleManifest mf = getBundleManifest( project );
        return change.getArtifactId().equals( mf.getSymbolicName() ) && change.getVersion().equals( mf.getVersion() );
    }

    private MutableBundleManifest getBundleManifest( ProjectMetadata project )
    {
        MutableBundleManifest mf = project.getMetadata( MutableBundleManifest.class );
        if ( mf == null )
        {
            File file = new File( project.getBasedir(), "META-INF/MANIFEST.MF" );
            try
            {
                mf = MutableBundleManifest.read( file );
            }
            catch ( IOException e )
            {
                throw new IllegalArgumentException( "Could not parse bundle manifest " + file, e );
            }
            project.putMetadata( mf );
        }
        return mf;
    }

    private boolean isBundleVersionChange( VersionChange change )
    {
        return isBundle( change.getProject() );
    }

    public void writeMetadata( ProjectMetadata project )
        throws IOException
    {
        MutableBundleManifest mf = project.getMetadata( MutableBundleManifest.class );
        if ( mf != null )
        {
            MutableBundleManifest.write( mf, new File( project.getBasedir(), "META-INF/MANIFEST.MF" ) );
        }
    }
}
