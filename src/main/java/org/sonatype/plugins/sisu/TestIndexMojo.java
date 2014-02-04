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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.project.MavenProject;

/**
 * Generates a qualified class index for test classes compiled by the current project.
 * 
 * @goal test-index
 * @phase process-test-classes
 * @requiresDependencyResolution test
 * @threadSafe
 */
public class TestIndexMojo
    extends AbstractMojo
{
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
    {
        final IndexMojo mojo = new IndexMojo();
        mojo.setLog( getLog() );
        mojo.setProject( project );
        mojo.setOutputDirectory( new File( project.getBuild().getTestOutputDirectory() ) );
        mojo.execute();
    }
}
