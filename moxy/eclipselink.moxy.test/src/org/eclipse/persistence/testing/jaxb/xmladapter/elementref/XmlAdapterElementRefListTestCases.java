/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * dmccann - December 17/2010 - 2.2 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.xmladapter.elementref;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlAdapterElementRefListTestCases extends JAXBWithJSONTestCases {
	private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/transactionsadd.xml";
	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/transactionsadd.json";

    public XmlAdapterElementRefListTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);        
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = TransactionsAdd.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        TransactionsAdd ta = new TransactionsAdd();
        List<String> txns = new ArrayList<String>(3);
        txns.add("salesOrderAdd");
        txns.add("invoiceAdd");
        txns.add("salesOrderAdd");
        ta.txnType = txns;
        return ta;
    }
    

    protected Object getJSONReadControlObject() {
        TransactionsAdd ta = new TransactionsAdd();
        List<String> txns = new ArrayList<String>(3);
        txns.add("salesOrderAdd");
        txns.add("salesOrderAdd");
        txns.add("invoiceAdd");        
        ta.txnType = txns;
        return ta;
    }
}
