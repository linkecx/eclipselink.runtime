/*******************************************************************************
 * Copyright (c) 2012, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     08/30/2013-2.5.1 Chris Delahunt 
 *       - 413765: DeleteAll query on subclass with a parent that has secondary 
 *          tables causes statements to ignore selection criteria
 ******************************************************************************/ 
package org.eclipse.persistence.testing.models.jpa.advanced;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Table;

/*
 * This class uses the same table as Project, and should not have its own table or its immediate parent.
 * This allows HugeProject's table to act like a secondary table in tests for bug 413765
 */

@Entity
@Table(name="CMP3_PROJECT")
@DiscriminatorValue("Z")
public class SpecialHugeProject extends HugeProject {

    public SpecialHugeProject() {
    }

    public SpecialHugeProject(String name) {
        super(name);
    }

}
