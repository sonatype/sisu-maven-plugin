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

import org.apache.maven.artifact.Artifact;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.project.MavenProject;
import org.apache.maven.shared.artifact.filter.collection.ArtifactFilterException;
import org.apache.maven.shared.artifact.filter.collection.ArtifactIdFilter;
import org.apache.maven.shared.artifact.filter.collection.ClassifierFilter;
import org.apache.maven.shared.artifact.filter.collection.FilterArtifacts;
import org.apache.maven.shared.artifact.filter.collection.GroupIdFilter;
import org.apache.maven.shared.artifact.filter.collection.ProjectTransitivityFilter;
import org.apache.maven.shared.artifact.filter.collection.ScopeFilter;
import org.apache.maven.shared.artifact.filter.collection.TypeFilter;
import org.codehaus.plexus.util.StringUtils;
import org.sonatype.guice.bean.scanners.index.SisuIndex;

/**
 * Generates a qualified class index for the current project and its dependencies.
 * 
 * @goal index
 * @requiresDependencyResolution test
 */
public class IndexMojo
    extends AbstractMojo
{
    // ----------------------------------------------------------------------
    // Configurable parameters
    // ----------------------------------------------------------------------

    /**
     * @parameter expression="${outputDirectory}" default-value="${project.build.outputDirectory}"
     * @optional
     */
    protected File outputDirectory;

    /**
     * If we should include project dependencies when indexing.
     * 
     * @parameter expression="${includeDependencies}" default-value="true"
     * @optional
     */
    protected boolean includeDependencies;

    /**
     * Comma separated list of GroupIds to exclude when indexing.
     * 
     * @parameter expression="${excludeGroupIds}" default-value=""
     * @optional
     */
    protected String excludeGroupIds;

    /**
     * Comma separated list of GroupIds to include when indexing.
     * 
     * @parameter expression="${includeGroupIds}" default-value=""
     * @optional
     */
    protected String includeGroupIds;

    /**
     * Comma separated list of ArtifactIds to exclude when indexing.
     * 
     * @parameter expression="${excludeArtifactIds}" default-value=""
     * @optional
     */
    protected String excludeArtifactIds;

    /**
     * Comma separated list of ArtifactIds to include when indexing.
     * 
     * @parameter expression="${includeArtifactIds}" default-value=""
     * @optional
     */
    protected String includeArtifactIds;

    /**
     * Comma Separated list of Classifiers to exclude when indexing.
     * 
     * @parameter expression="${excludeClassifiers}" default-value=""
     * @optional
     */
    protected String excludeClassifiers;

    /**
     * Comma Separated list of Classifiers to include when indexing.
     * 
     * @parameter expression="${includeClassifiers}" default-value=""
     * @optional
     */
    protected String includeClassifiers;

    /**
     * Comma Separated list of Types to exclude when indexing.
     * 
     * @parameter expression="${excludeTypes}" default-value=""
     * @optional
     */
    protected String excludeTypes;

    /**
     * Comma Separated list of Types to include when indexing.
     * 
     * @parameter expression="${includeTypes}" default-value=""
     * @optional
     */
    protected String includeTypes;

    /**
     * Scope to exclude. Empty string indicates no scopes (default).
     * 
     * @parameter expression="${excludeScope}" default-value=""
     * @optional
     */
    protected String excludeScope;

    /**
     * Scope to include. Empty string indicates all scopes (default).
     * 
     * @parameter expression="${includeScope}" default-value=""
     * @optional
     */
    protected String includeScope;

    /**
     * If we should exclude transitive dependencies when indexing.
     * 
     * @parameter expression="${excludeTransitive}" default-value="false"
     * @optional
     */
    protected boolean excludeTransitive;

    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    /**
     * The Maven project to index.
     * 
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public void execute()
        throws MojoExecutionException
    {
        final List<URL> urls = new ArrayList<URL>();
        appendToClassPath( urls, outputDirectory );
        if ( includeDependencies )
        {
            final FilterArtifacts filter = new FilterArtifacts();

            filter.addFilter( new ProjectTransitivityFilter( project.getDependencyArtifacts(), excludeTransitive ) );
            filter.addFilter( new ScopeFilter( cleanList( includeScope ), cleanList( excludeScope ) ) );
            filter.addFilter( new TypeFilter( cleanList( includeTypes ), cleanList( excludeTypes ) ) );
            filter.addFilter( new ClassifierFilter( cleanList( includeClassifiers ), cleanList( excludeClassifiers ) ) );
            filter.addFilter( new GroupIdFilter( cleanList( includeGroupIds ), cleanList( excludeGroupIds ) ) );
            filter.addFilter( new ArtifactIdFilter( cleanList( includeArtifactIds ), cleanList( excludeArtifactIds ) ) );

            try
            {
                for ( final Object artifact : filter.filter( project.getArtifacts() ) )
                {
                    appendToClassPath( urls, ( (Artifact) artifact ).getFile() );
                }
            }
            catch ( final ArtifactFilterException e )
            {
                throw new MojoExecutionException( e.getMessage(), e );
            }
        }
        new SisuIndex( outputDirectory ).index( urls );
    }

    // ----------------------------------------------------------------------
    // Implementation methods
    // ----------------------------------------------------------------------

    private void appendToClassPath( final List<URL> urls, final File file )
    {
        if ( null != file )
        {
            try
            {
                urls.add( file.toURI().toURL() );
            }
            catch ( final MalformedURLException e )
            {
                getLog().warn( e.getLocalizedMessage() );
            }
        }
    }

    private static String cleanList( final String list )
    {
        return StringUtils.isEmpty( list ) ? "" : StringUtils.join( StringUtils.split( list ), "," );
    }
}
