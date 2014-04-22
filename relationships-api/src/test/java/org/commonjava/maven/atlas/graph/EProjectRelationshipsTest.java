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
package org.commonjava.maven.atlas.graph;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Set;

import org.commonjava.maven.atlas.graph.model.EProjectDirectRelationships;
import org.commonjava.maven.atlas.graph.rel.DependencyRelationship;
import org.commonjava.maven.atlas.graph.rel.ExtensionRelationship;
import org.commonjava.maven.atlas.graph.rel.ParentRelationship;
import org.commonjava.maven.atlas.graph.rel.PluginRelationship;
import org.commonjava.maven.atlas.graph.rel.ProjectRelationship;
import org.commonjava.maven.atlas.ident.DependencyScope;
import org.commonjava.maven.atlas.ident.ref.ArtifactRef;
import org.commonjava.maven.atlas.ident.ref.ProjectVersionRef;
import org.commonjava.maven.atlas.ident.version.InvalidVersionSpecificationException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class EProjectRelationshipsTest
{

    @Rule
    public TestName naming = new TestName();

    private URI testURI()
        throws URISyntaxException
    {
        return new URI( "test:repo:" + naming.getMethodName() );
    }

    @Test
    public void builderWith2Dependencies2PluginsAParentAndAnExtension()
        throws InvalidVersionSpecificationException, URISyntaxException
    {
        final ProjectVersionRef p = new ProjectVersionRef( "org.apache.maven", "maven-core", "3.0.3" );
        final URI source = testURI();

        final EProjectDirectRelationships.Builder prb = new EProjectDirectRelationships.Builder( source, p );

        final ProjectVersionRef parent = new ProjectVersionRef( "org.apache.maven", "maven", "3.0.3" );
        final ParentRelationship parentRel = new ParentRelationship( source, p, parent );

        int idx = 0;
        int pidx = 0;
        final DependencyRelationship papi =
            new DependencyRelationship( source, p, new ArtifactRef( "org.apache.maven", "maven-plugin-api", "3.0.3",
                                                                    null, null, false ), DependencyScope.compile,
                                        idx++, false );
        final DependencyRelationship art =
            new DependencyRelationship( source, p, new ArtifactRef( "org.apache.maven", "maven-artifact", "3.0.3",
                                                                    null, null, false ), DependencyScope.compile,
                                        idx++, false );
        final PluginRelationship jarp =
            new PluginRelationship( source, p, new ProjectVersionRef( "org.apache.maven.plugins", "maven-jar-plugin",
                                                                      "2.2" ), pidx++, false );
        final PluginRelationship comp =
            new PluginRelationship( source, p, new ProjectVersionRef( "org.apache.maven.plugins",
                                                                      "maven-compiler-plugin", "2.3.2" ), pidx++, false );
        final ExtensionRelationship wag =
            new ExtensionRelationship( source, p, new ProjectVersionRef( "org.apache.maven.wagon",
                                                                         "wagon-provider-webdav", "1.0" ), 0 );

        prb.withParent( parentRel );
        prb.withDependencies( papi, art );
        prb.withPlugins( jarp, comp );
        prb.withExtensions( wag );

        final EProjectDirectRelationships rels = prb.build();

        final Set<ProjectRelationship<?>> all = rels.getAllRelationships();

        assertThat( all.size(), equalTo( 6 ) );

        assertThat( all.contains( parentRel ), equalTo( true ) );
        assertThat( all.contains( papi ), equalTo( true ) );
        assertThat( all.contains( art ), equalTo( true ) );
        assertThat( all.contains( jarp ), equalTo( true ) );
        assertThat( all.contains( comp ), equalTo( true ) );
        assertThat( all.contains( wag ), equalTo( true ) );
    }

}
