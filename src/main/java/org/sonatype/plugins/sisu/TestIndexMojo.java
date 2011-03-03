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
import org.apache.maven.plugin.MojoExecutionException;

/**
 * Generates a qualified class index for test classes compiled by the current project.
 * 
 * @goal test-index
 * @phase process-test-classes
 */
public class TestIndexMojo
    extends AbstractMojo
{
    // ----------------------------------------------------------------------
    // Implementation fields
    // ----------------------------------------------------------------------

    /**
     * @parameter default-value="${project.build.testOutputDirectory}"
     * @required
     * @readonly
     */
    protected File outputDirectory;

    // ----------------------------------------------------------------------
    // Public methods
    // ----------------------------------------------------------------------

    public void execute()
        throws MojoExecutionException
    {
        final IndexMojo mojo = new IndexMojo();
        mojo.outputDirectory = outputDirectory;
        mojo.execute();
    }
}
