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
package org.commonjava.maven.atlas.graph.spi.neo4j.traverse;

import static org.commonjava.maven.atlas.graph.spi.neo4j.io.Conversions.POM_LOCATION_URI;
import static org.commonjava.maven.atlas.graph.spi.neo4j.io.Conversions.SOURCE_URI;
import static org.commonjava.maven.atlas.graph.spi.neo4j.io.Conversions.getURIProperty;
import static org.commonjava.maven.atlas.graph.spi.neo4j.io.Conversions.getURISetProperty;
import static org.commonjava.maven.atlas.graph.spi.neo4j.io.Conversions.toProjectRelationship;
import static org.commonjava.maven.atlas.graph.util.RelationshipUtils.POM_ROOT_URI;
import static org.commonjava.maven.atlas.graph.util.RelationshipUtils.UNKNOWN_SOURCE_URI;

import java.net.URI;
import java.util.HashSet;
import java.util.Set;

import org.commonjava.maven.atlas.graph.filter.ProjectRelationshipFilter;
import org.commonjava.maven.atlas.graph.model.GraphView;
import org.commonjava.maven.atlas.graph.rel.ProjectRelationship;
import org.commonjava.maven.atlas.graph.rel.RelationshipType;
import org.commonjava.maven.atlas.graph.spi.neo4j.GraphRelType;
import org.commonjava.maven.atlas.graph.spi.neo4j.io.ConversionCache;
import org.commonjava.maven.atlas.graph.workspace.GraphWorkspace;
import org.neo4j.graphdb.Relationship;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TraversalUtils
{

    private static final Logger logger = LoggerFactory.getLogger( TraversalUtils.class );

    private TraversalUtils()
    {
    }

    //    public static boolean acceptedInView( final Path path, final GraphView view, final ConversionCache cache )
    //    {
    //        ProjectRelationshipFilter f = view.getFilter();
    //        final GraphWorkspace ws = view.getWorkspace();
    //
    //        for ( final Relationship r : path.relationships() )
    //        {
    //            if ( !accepted( r, f, ws, cache ) )
    //            {
    //                return false;
    //            }
    //
    //            if ( f != null )
    //            {
    //                final ProjectRelationship<?> rel = toProjectRelationship( r, cache );
    //                f = f.getChildFilter( rel );
    //            }
    //        }
    //
    //        debug( "ACCEPT: Path: {}", path );
    //        return true;
    //    }

    public static boolean acceptedInView( final Relationship r, final GraphView view, final ConversionCache cache )
    {
        return accepted( r, view, cache );
    }

    public static boolean accepted( final Relationship r, final GraphView view, final ConversionCache cache )
    {
        final ProjectRelationship<?> rel = toProjectRelationship( r, cache );

        debug( "Checking relationship for acceptance: {} ({})", r, rel );

        final GraphWorkspace workspace = view.getWorkspace();
        if ( workspace != null )
        {
            final Set<URI> sources = workspace.getActiveSources();
            if ( sources != null && !sources.isEmpty() )
            {
                final Set<URI> s = getURISetProperty( SOURCE_URI, r, UNKNOWN_SOURCE_URI );
                boolean found = false;
                for ( final URI uri : s )
                {
                    if ( sources == GraphWorkspace.DEFAULT_SOURCES || sources.contains( uri ) )
                    {
                        found = true;
                        break;
                    }
                }

                if ( !found )
                {
                    debug( "REJECTED: Found relationship in path with de-selected source-repository URI: {} (r={}, permissable sources: {})", s, r,
                           sources );
                    return false;
                }
            }

            final Set<URI> pomLocations = workspace.getActivePomLocations();
            if ( pomLocations != null && !pomLocations.isEmpty() )
            {
                final URI pomLocation = getURIProperty( POM_LOCATION_URI, r, POM_ROOT_URI );
                if ( !pomLocations.contains( pomLocation ) )
                {
                    debug( "REJECTED: Found relationship in path with de-selected pom-location URI: {}", r );
                    return false;
                }
            }
        }

        final ProjectRelationshipFilter filter = view.getFilter();
        if ( filter != null )
        {
            if ( !filter.accept( rel ) )
            {
                debug( "Filter: {} REJECTED relationship: {} ({})", filter, r, rel );
                return false;
            }
        }

        debug( "ACCEPT: {} ({})", r, rel );
        return true;
    }

    private static void debug( final String message, final Object... params )
    {
        logger.debug( message, params );
    }

    public static GraphRelType[] getGraphRelTypes( final ProjectRelationshipFilter filter )
    {
        if ( filter == null )
        {
            return GraphRelType.atlasRelationshipTypes();
        }

        final Set<GraphRelType> result = new HashSet<GraphRelType>();

        final Set<RelationshipType> types = filter.getAllowedTypes();
        for ( final RelationshipType rt : types )
        {
            if ( filter.includeManagedRelationships() )
            {
                final GraphRelType grt = GraphRelType.map( rt, true );
                if ( grt != null )
                {
                    result.add( grt );
                }
            }

            if ( filter.includeConcreteRelationships() )
            {
                final GraphRelType grt = GraphRelType.map( rt, false );
                if ( grt != null )
                {
                    result.add( grt );
                }
            }
        }

        return result.toArray( new GraphRelType[result.size()] );
    }

}
