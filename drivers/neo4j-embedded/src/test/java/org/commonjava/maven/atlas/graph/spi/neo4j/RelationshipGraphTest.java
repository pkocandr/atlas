/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc..
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 * 
 * Contributors:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.commonjava.maven.atlas.graph.spi.neo4j;

import org.commonjava.maven.atlas.graph.spi.RelationshipGraphConnectionFactory;
import org.commonjava.maven.atlas.graph.spi.neo4j.fixture.FileConnectionFixture;
import org.commonjava.maven.atlas.tck.graph.RelationshipGraphTCK;
import org.junit.Rule;

public class RelationshipGraphTest
    extends RelationshipGraphTCK
{
    @Rule
    public FileConnectionFixture fixture = new FileConnectionFixture();

    @Override
    protected synchronized RelationshipGraphConnectionFactory connectionFactory()
        throws Exception
    {
        return fixture.connectionFactory();
    }
}