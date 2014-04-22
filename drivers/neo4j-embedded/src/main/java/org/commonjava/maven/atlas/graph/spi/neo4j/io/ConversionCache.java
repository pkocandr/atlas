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
package org.commonjava.maven.atlas.graph.spi.neo4j.io;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.codec.digest.DigestUtils;
import org.commonjava.maven.atlas.graph.rel.ProjectRelationship;
import org.commonjava.maven.atlas.ident.ref.ProjectVersionRef;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public class ConversionCache
{

    private Map<Long, WeakReference<ProjectRelationship<?>>> relationships;

    private Map<Long, WeakReference<ProjectVersionRef>> gavs;

    private Map<String, WeakReference<Object>> serializedObjects;

    public ProjectRelationship<?> getRelationship( final Relationship rel )
    {
        return getRelationship( rel.getId() );
    }

    public ProjectRelationship<?> getRelationship( final long rid )
    {
        if ( relationships == null )
        {
            return null;
        }

        final WeakReference<ProjectRelationship<?>> reference = relationships.get( rid );
        if ( reference == null )
        {
            return null;
        }

        return reference.get();
    }

    public void cache( final Relationship rel, final ProjectRelationship<?> r )
    {
        if ( relationships == null )
        {
            relationships = new HashMap<Long, WeakReference<ProjectRelationship<?>>>();
        }

        relationships.put( rel.getId(), new WeakReference<ProjectRelationship<?>>( r ) );
    }

    public ProjectVersionRef getProjectVersionRef( final Node node )
    {
        return getProjectVersionRef( node.getId() );
    }

    public ProjectVersionRef getProjectVersionRef( final long nid )
    {
        if ( gavs == null )
        {
            return null;
        }

        final WeakReference<ProjectVersionRef> reference = gavs.get( nid );
        if ( reference == null )
        {
            return null;
        }

        return reference.get();
    }

    public void cache( final Node node, final ProjectVersionRef ref )
    {
        if ( gavs == null )
        {
            gavs = new HashMap<Long, WeakReference<ProjectVersionRef>>();
        }

        gavs.put( node.getId(), new WeakReference<ProjectVersionRef>( ref ) );
    }

    public <T> T getSerializedObject( final byte[] data, final Class<T> type )
    {
        if ( serializedObjects != null )
        {
            final String key = DigestUtils.shaHex( data );
            final WeakReference<Object> reference = serializedObjects.get( key );
            if ( reference == null )
            {
                return null;
            }

            final Object value = reference.get();
            if ( value != null )
            {
                return type.cast( value );
            }
        }

        return null;
    }

    public void cache( final byte[] data, final Object value )
    {
        if ( serializedObjects == null )
        {
            serializedObjects = new HashMap<String, WeakReference<Object>>();
        }

        final String key = DigestUtils.shaHex( data );
        serializedObjects.put( key, new WeakReference<Object>( value ) );
    }
}
