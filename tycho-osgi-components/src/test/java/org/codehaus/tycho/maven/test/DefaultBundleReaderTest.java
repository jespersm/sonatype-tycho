package org.codehaus.tycho.maven.test;

import java.io.File;

import org.codehaus.plexus.util.FileUtils;
import org.codehaus.tycho.osgitools.BundleReader;
import org.codehaus.tycho.osgitools.DefaultBundleReader;
import org.codehaus.tycho.testing.AbstractTychoMojoTestCase;

public class DefaultBundleReaderTest
    extends AbstractTychoMojoTestCase
{

    private File cacheDir;

    private DefaultBundleReader bundleReader;

    @Override
    protected void setUp()
        throws Exception
    {
        cacheDir = File.createTempFile( "cache", "" );
        cacheDir.delete();
        cacheDir.mkdirs();
        bundleReader = (DefaultBundleReader) lookup( BundleReader.class );
        bundleReader.setLocationRepository( cacheDir );
    }

    @Override
    protected void tearDown()
        throws Exception
    {
        FileUtils.deleteDirectory( cacheDir );
    }

    public void testExtractDirClasspathEntries()
        throws Exception
    {
        File bundleWithNestedDirClasspath = getTestJar();
        File libDirectory = bundleReader.getEntry( bundleWithNestedDirClasspath, "lib/" );
        assertTrue( "directory classpath entry lib/ not extracted", libDirectory.isDirectory() );
        assertTrue( new File( libDirectory, "log4j.properties" ).isFile() );
        assertTrue( new File( libDirectory, "subdir/test.txt" ).isFile() );
    }

    public void testEntryMissingTrailingSlash()
        throws Exception
    {
        File bundleWithNestedDirClasspath = getTestJar();
        File libDirectory = bundleReader.getEntry( bundleWithNestedDirClasspath, "lib" );
        assertTrue( "directory classpath entry lib/ not extracted", libDirectory.isDirectory() );
        assertTrue( new File( libDirectory, "log4j.properties" ).isFile() );
        assertTrue( new File( libDirectory, "subdir/test.txt" ).isFile() );
    }

    public void testExtractSingleFile()
        throws Exception
    {
        File bundleWithNestedDirClasspath = getTestJar();
        File singleFile = bundleReader.getEntry( bundleWithNestedDirClasspath, "lib/log4j.properties" );
        assertTrue( singleFile.isFile() );
        assertFalse( new File( singleFile.getParentFile(), "subdir" ).exists() );
        assertFalse( new File( singleFile.getParentFile().getParentFile(), "META-INF" ).exists() );
    }

    public void testNonExistingEntry()
        throws Exception
    {
        File bundleWithNestedDirClasspath = getTestJar();
        File nonExistingFile = bundleReader.getEntry( bundleWithNestedDirClasspath, "foo/bar.txt" );
        assertNull( nonExistingFile );
    }

    private File getTestJar()
    {
        return new File( getBasedir(), "src/test/resources/bundlereader/testNestedDirClasspath_1.0.0.201007261122.jar" );
    }
}
