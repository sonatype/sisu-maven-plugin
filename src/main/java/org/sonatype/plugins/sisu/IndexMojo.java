/*******************************************************************************
 * Copyright (c) 2011 Sonatype, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Apache License v2.0 which accompanies this distribution.
 * The Eclipse Public License is available at
 *   http://www.eclipse.org/legal/epl-v10.html
 * The Apache License v2.0 is available at
 *   http://www.apache.org/licenses/LICENSE-2.0.html
 * You may elect to redistribute this code under either of these licenses.
 *******************************************************************************/
package org.sonatype.plugins.sisu;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.Mojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.sonatype.guice.bean.scanners.index.SisuIndex;

/**
 * Maven {@link Mojo} that generates a qualified class index for the current project and its dependencies.
 * 
 * @goal index
 * @phase test
 * @requiresDependencyResolution test
 */
public class IndexMojo
    extends AbstractMojo
{
    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter default-value="${project.build.outputDirectory}"
     * @required
     */
    private File outputDirectory;

    /**
     * @parameter default-value="${project.build.testOutputDirectory}"
     * @required
     */
    private File testOutputDirectory;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    @SuppressWarnings( "unchecked" )
    public void execute()
        throws MojoExecutionException
    {
        try
        {
            final List<String> mainElements = project.getCompileClasspathElements();
            final List<String> testElements = project.getTestClasspathElements();

            testElements.removeAll( mainElements );

            index( outputDirectory, mainElements );
            index( testOutputDirectory, testElements );
        }
        catch ( final DependencyResolutionRequiredException e )
        {
            throw new MojoExecutionException( "Missing dependency resolution", e );
        }
    }

    // ----------------------------------------------------------------------
    // Implementation methods
    // ----------------------------------------------------------------------

    private void index( final File targetDirectory, final List<String> elements )
    {
        final List<URL> classPath = new ArrayList<URL>( elements.size() );
        for ( final String path : elements )
        {
            try
            {
                classPath.add( new File( path ).toURI().toURL() );
            }
            catch ( final MalformedURLException e )
            {
                getLog().warn( e.getLocalizedMessage() );
            }
        }
        new SisuIndex( targetDirectory ).index( classPath );
    }
}
