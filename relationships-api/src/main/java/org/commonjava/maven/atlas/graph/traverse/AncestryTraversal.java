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
package org.commonjava.maven.atlas.graph.traverse;

import java.util.List;

import org.commonjava.maven.atlas.graph.filter.ParentFilter;
import org.commonjava.maven.atlas.ident.ref.ProjectVersionRef;

public class AncestryTraversal
    extends FilteringTraversal
{

    public AncestryTraversal()
    {
        super( ParentFilter.EXCLUDE_TERMINAL_PARENTS, true );
    }

    public List<ProjectVersionRef> getAncestry()
    {
        return getCapturedProjects( true );
    }

}
