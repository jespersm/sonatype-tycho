package org.sonatype.tycho.p2.impl.publisher;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.equinox.internal.p2.metadata.InstallableUnit;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.metadata.IProvidedCapability;
import org.eclipse.equinox.p2.metadata.IRequirement;
import org.eclipse.equinox.p2.metadata.MetadataFactory;
import org.eclipse.equinox.p2.metadata.MetadataFactory.InstallableUnitDescription;
import org.eclipse.equinox.p2.metadata.Version;
import org.eclipse.equinox.p2.metadata.VersionRange;
import org.eclipse.equinox.p2.publisher.eclipse.Feature;
import org.eclipse.equinox.p2.publisher.eclipse.FeatureEntry;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.spi.p2.publisher.PublisherHelper;

@SuppressWarnings( "restriction" )
public class FeatureDependenciesAction
    extends AbstractDependenciesAction
{
    /**
     * Comma separated list of IInstallableUnit ids that are included (as opposed to required by) the feature.
     */
    public static final String INCLUDED_IUS = "org.sonatype.tycho.p2.includedIUs";

    private final Feature feature;

    public FeatureDependenciesAction( Feature feature )
    {
        this.feature = feature;
    }

    private String getInstallableUnitId( FeatureEntry entry )
    {
        if ( entry.isPlugin() )
        {
            return entry.getId();
        }
        else
        {
            return entry.getId() + FEATURE_GROUP_IU_SUFFIX;
        }
    }

    /**
     * Copy&Paste from 3.6M7 org.eclipse.equinox.p2.publisher.eclipse.FeaturesAction.getVersionRange(FeatureEntry)
     */
    private VersionRange getVersionRange( FeatureEntry entry )
    {
        String versionSpec = entry.getVersion();
        if ( versionSpec == null )
            return VersionRange.emptyRange;
        Version version = Version.parseVersion( versionSpec );
        if ( version.equals( Version.emptyVersion ) )
            return VersionRange.emptyRange;
        if ( !entry.isRequires() )
            return new VersionRange( version, true, version, true );
        String match = entry.getMatch();
        if ( match == null )
            // TODO should really be returning VersionRange.emptyRange here...
            return null;
        if ( match.equals( "perfect" ) ) //$NON-NLS-1$
            return new VersionRange( version, true, version, true );

        org.osgi.framework.Version osgiVersion = PublisherHelper.toOSGiVersion( version );
        if ( match.equals( "equivalent" ) ) { //$NON-NLS-1$
            Version upper = Version.createOSGi( osgiVersion.getMajor(), osgiVersion.getMinor() + 1, 0 );
            return new VersionRange( version, true, upper, false );
        }
        if ( match.equals( "compatible" ) ) { //$NON-NLS-1$
            Version upper = Version.createOSGi( osgiVersion.getMajor() + 1, 0, 0 );
            return new VersionRange( version, true, upper, false );
        }
        if ( match.equals( "greaterOrEqual" ) ) //$NON-NLS-1$
            return new VersionRange( version, true, new VersionRange( null ).getMaximum(), true );
        return null;
    }

    @Override
    protected Set<IRequirement> getRequiredCapabilities()
    {
        Set<IRequirement> required = new LinkedHashSet<IRequirement>();

        for ( FeatureEntry entry : feature.getEntries() )
        {
            VersionRange range;
            if ( entry.isRequires() )
            {
                range = getVersionRange( entry );
            }
            else
            {
                range = getVersionRange( createVersion( entry.getVersion() ) );
            }
            String id = getInstallableUnitId( entry );
            String filter = getFilter( entry.getFilter(), entry.getOS(), entry.getWS(), entry.getArch(), entry.getNL() );
            boolean optional = entry.isOptional();
            required.add( MetadataFactory.createRequirement( IInstallableUnit.NAMESPACE_IU_ID, id, range,
                                                             InstallableUnit.parseFilter( filter ), optional, false ) );
        }
        return required;
    }

    @Override
    protected Version getVersion()
    {
        return Version.create( feature.getVersion() );
    }

    @Override
    protected String getId()
    {
        return feature.getId() + FEATURE_GROUP_IU_SUFFIX;
    }

    @Override
    protected void addProvidedCapabilities( Set<IProvidedCapability> provided )
    {
        provided.add( MetadataFactory.createProvidedCapability( PublisherHelper.CAPABILITY_NS_UPDATE_FEATURE,
                                                                feature.getId(), getVersion() ) );
    }

    @Override
    protected void addProperties( InstallableUnitDescription iud )
    {
        iud.setProperty( QueryUtil.PROP_TYPE_GROUP, "true" );

        StringBuilder includedIUs = new StringBuilder();
        for ( FeatureEntry entry : feature.getEntries() )
        {
            String id = getInstallableUnitId( entry );
            if ( includedIUs.length() > 0 )
            {
                includedIUs.append( ',' );
            }
            includedIUs.append( id );
        }
        iud.setProperty( INCLUDED_IUS, includedIUs.toString() );
    }

    public static Set<String> getIncludedUIs( IInstallableUnit iu )
    {
        Set<String> includedIUs = new LinkedHashSet<String>();

        String prop = iu.getProperty( INCLUDED_IUS );
        if ( prop != null )
        {
            StringTokenizer st = new StringTokenizer( prop, "," );
            while ( st.hasMoreTokens() )
            {
                includedIUs.add( st.nextToken() );
            }
        }
        return includedIUs;
    }
}
