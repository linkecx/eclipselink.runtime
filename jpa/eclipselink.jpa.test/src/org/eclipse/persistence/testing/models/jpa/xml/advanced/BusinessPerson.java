/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     tware
 ******************************************************************************/  
package org.eclipse.persistence.testing.models.jpa.xml.advanced;

/**
 * BusinessPerson is intended as a non-Entity, non-MappedSuperclass with a mappable attribute
 * @author tware
 *
 */
public class BusinessPerson {
    
    protected String businessId;

    public String getBusinessId() {
        return businessId;
    }
    
    public void setBusinessId(String businessId) {
        this.businessId = businessId;
    }
}
