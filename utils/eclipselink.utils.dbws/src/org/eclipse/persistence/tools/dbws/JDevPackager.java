/*******************************************************************************
 * Copyright (c) 1998, 2009 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - Aug 2008, created DBWS JDev(Boxer) packager
 ******************************************************************************/

package org.eclipse.persistence.tools.dbws;

//javase imports
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

//EclipseLink imports
import org.eclipse.persistence.internal.dbws.ProviderHelper;
import org.eclipse.persistence.tools.dbws.DBWSBuilder;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OR_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_OX_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SCHEMA_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_SERVICE_XML;
import static org.eclipse.persistence.internal.xr.Util.DBWS_WSDL;
import static org.eclipse.persistence.internal.xr.Util.WEB_INF_DIR;
import static org.eclipse.persistence.internal.xr.Util.WSDL_DIR;
import static org.eclipse.persistence.tools.dbws.DBWSPackager.ArchiveUse.noArchive;
import static org.eclipse.persistence.tools.dbws.Util.DBWS_PROVIDER_SOURCE_FILE;
import static org.eclipse.persistence.tools.dbws.Util.SWAREF_FILENAME;
import static org.eclipse.persistence.tools.dbws.Util.UNDER_DBWS;
import static org.eclipse.persistence.tools.dbws.Util.WEB_XML_FILENAME;

/**
 * <p>
 * <b>PUBLIC:</b> JDevPackager extends {@link WeblogicPackager}. It is responsible for generating<br>
 * the source code of the DBWS Provider (instead of a <tt>.class</tt> file) and packaging in a<br>
 * JDev-friendly directory structure all the other DBWS files produced by its parent:
 * <pre>
 * \--- JDev <b>Projectnnn</b> root directory
 *    |   application.xml
 *    |   build.properties
 *    |   build.xml
 *    |   data-sources.xml
 *    |   dbws-builder.xml
 *    |   Projectnnn.jpr
 *    |
 *    +---<b>public_html</b>
 *    |   \---WEB-INF
 *    |       |   <b>web.xml</b>
 *    |       |
 *    |       \---wsdl
 *    |               <b><i>swaref.xsd</i></b>
 *    |               <b>eclipselink-dbws-schema.xsd</b>
 *    |               <b>eclipselink-dbws.wsdl</b>
 *    |
 *    \---<b>src</b>
 *        |   <b>eclipselink-dbws-or.xml</b>
 *        |   <b>eclipselink-dbws-ox.xml</b>
 *        |   <b>eclipselink-dbws-sessions.xml</b>
 *        |   <b>eclipselink-dbws.xml</b>
 *        |
 *        \---_dbws
 *                <b>DBWSProvider.java</b>  -- generated by this Packager
 * </pre>
 *
 * @author Mike Norman - michael.norman@oracle.com
 * @since EclipseLink 1.x
 */
public class JDevPackager extends WeblogicPackager {

    public static final String DBWS_PROVIDER_SOURCE_PREAMBLE =
        "package _dbws;\n" +
        "\n//javase imports\n" +
        "import java.lang.reflect.Method;\n" +
        "\n//Java extension libraries\n" +
        "import javax.annotation.PostConstruct;\n" +
        "import javax.annotation.PreDestroy;\n" +
        "import javax.annotation.Resource;\n" +
        "import javax.servlet.ServletContext;\n" +
        "import javax.xml.soap.SOAPMessage;\n" +
        "import javax.xml.ws.BindingType;\n" +
        "import javax.xml.ws.Provider;\n" +
        "import javax.xml.ws.ServiceMode;\n" +
        "import javax.xml.ws.WebServiceContext;\n" +
        "import javax.xml.ws.WebServiceProvider;\n" +
        "import javax.xml.ws.handler.MessageContext;\n" +
        "import javax.xml.ws.soap.SOAPBinding;\n" +
        "import static javax.xml.ws.Service.Mode.MESSAGE;\n" +
        "import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_BINDING;\n" +
        "import static javax.xml.ws.soap.SOAPBinding.SOAP11HTTP_MTOM_BINDING;\n" +
        "import static javax.xml.ws.soap.SOAPBinding.SOAP12HTTP_MTOM_BINDING;\n" +        
        "\n//EclipseLink imports\n" +
        "import " + ProviderHelper.class.getName() + ";\n" +
        "\n" +
        "@WebServiceProvider(\n" +
        "    wsdlLocation = \"WEB-INF/wsdl/eclipselink-dbws.wsdl\",\n" +
        "    serviceName = \"";
    public static final String DBWS_PROVIDER_SOURCE_PORT_NAME =
        "\",\n    portName = \"";
    public static final String DBWS_PROVIDER_SOURCE_TARGET_NAMESPACE =
        "\",\n    targetNamespace = \"";
    public static final String DBWS_PROVIDER_SOURCE_SUFFIX =
        "\"\n)\n@ServiceMode(MESSAGE)\n";
    
    public static final String DBWS_PROVIDER_SOAP12_BINDING =
        "@BindingType(value=SOAP12HTTP_BINDING)\n";
    public static final String DBWS_PROVIDER_SOAP11_MTOM_BINDING =
        "@BindingType(value=SOAP11HTTP_MTOM_BINDING)\n";
    public static final String DBWS_PROVIDER_SOAP12_MTOM_BINDING =
        "@BindingType(value=SOAP12HTTP_MTOM_BINDING)\n";

    public static final String DBWS_PROVIDER_SOURCE_CLASSDEF =
        "public class DBWSProvider extends ProviderHelper implements Provider<SOAPMessage> {\n" +
        "\n" +
        "    // Container injects wsContext here\n" +
        "    @Resource\n" +
        "    protected WebServiceContext wsContext;\n" +
        "    public  DBWSProvider() {\n" +
        "        super();\n" +
        "    }\n" +
        "    private static final String CONTAINER_RESOLVER_CLASSNAME =\n" +
        "        \"com.sun.xml.ws.api.server.ContainerResolver\";\n" +
        "    @PostConstruct\n" +
        "    public void init() {\n" +
        "        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader();\n" +
        "        ServletContext sc = null;\n" +
        "        //ServletContext sc = \n" +
        "        //    ContainerResolver.getInstance().getContainer().getSPI(ServletContext.class);\n" +
        "        try {\n" +
        "            Class<?> containerResolverClass = parentClassLoader.loadClass(\n" +
        "                CONTAINER_RESOLVER_CLASSNAME);\n" +
        "            Method getInstanceMethod = containerResolverClass.getMethod(\"getInstance\");\n" +
        "            Object containerResolver = getInstanceMethod.invoke(null);\n" +
        "            Method getContainerMethod = containerResolver.getClass().getMethod(\"getContainer\");\n" +
        "            getContainerMethod.setAccessible(true);\n" +
        "            Object container = getContainerMethod.invoke(containerResolver);\n" +
        "            Method getSPIMethod = container.getClass().getMethod(\"getSPI\", Class.class);\n" +
        "            getSPIMethod.setAccessible(true);\n" +
        "            sc = (ServletContext)getSPIMethod.invoke(container, ServletContext.class);\n" +
        "        }\n" +
        "        catch (Exception e) {\n" +
        "            // if the above doesn't work, then maybe we are running in JavaSE 6 'containerless' mode\n" +
        "            // we can live with a null ServletContext (just use the parentClassLoader to load resources \n" +
        "        }\n" +
        "        boolean mtomEnabled = false;\n" +
        "        BindingType thisBindingType = this.getClass().getAnnotation(BindingType.class);\n" +
        "        if (thisBindingType != null) {\n" +
        "            if (thisBindingType.value().toLowerCase().contains(\"mtom=true\")) {\n" +
        "                mtomEnabled = true;\n" +
        "            }\n" +
        "        }\n" +
        "        super.init(parentClassLoader, sc, mtomEnabled);\n" +
        "    }\n" +
        "    @Override\n" +
        "    public SOAPMessage invoke(SOAPMessage request) {\n" +
        "        if (wsContext != null) {\n" +
        "            setMessageContext(wsContext.getMessageContext());\n" +
        "        }\n" +
        "        return super.invoke(request);\n" +
        "    }\n" +
        "    @Override\n" +
        "    @PreDestroy\n" +
        "    public void destroy() {\n" +
        "        super.destroy();\n" +
        "    }\n" +
        "};\n";

    public static final String SRC_DIR = "src";
    public static final String PUBLIC_HTML_DIR = "public_html";

    protected File srcDir;
    protected File publicHTMLDir;
    protected File webInfDir;
    protected File wsdlDir;
    protected File underDBWSDir;

    public JDevPackager() {
        this(null, "jdev", noArchive);
    }
    protected JDevPackager(Archiver archiver, String packagerLabel, ArchiveUse useJavaArchive) {
        super(archiver, packagerLabel, useJavaArchive);
    }

    @Override
    public String getArchiverLabel() {
        return "not supported";
    }
    @Override
    public String getAdditionalUsage() {
        return null;
    }

    @Override
    public Archiver buildDefaultArchiver() {
        return null;
    }
    
    // create streams according to JDev project layout (see above)

    @Override
    public OutputStream getSchemaStream() throws FileNotFoundException {
        buildWSDLDir();
        return new FileOutputStream(new File(wsdlDir, DBWS_SCHEMA_XML));
    }

    @Override
    public OutputStream getSessionsStream(String sessionsFileName) throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, sessionsFileName));
    }

    @Override
    public OutputStream getServiceStream() throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, DBWS_SERVICE_XML));
    }

    @Override
    public OutputStream getOrStream() throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, DBWS_OR_XML));
    }

    @Override
    public OutputStream getOxStream() throws FileNotFoundException {
        buildSrcDir();
        return new FileOutputStream(new File(srcDir, DBWS_OX_XML));
    }

    @Override
    public OutputStream getWSDLStream() throws FileNotFoundException {
        buildWSDLDir();
        return new FileOutputStream(new File(wsdlDir, DBWS_WSDL));
    }

    @Override
    public OutputStream getSWARefStream() throws FileNotFoundException {
        if (hasAttachments) {
            buildWSDLDir();
            return new FileOutputStream(new File(wsdlDir, SWAREF_FILENAME));
        }
        else {
            return XRPackager.__nullStream;
        }
    }

    @Override
    public OutputStream getWebXmlStream() throws FileNotFoundException {
        buildWebInfDir();
        return new FileOutputStream(new File(webInfDir, WEB_XML_FILENAME));
    }

    // do nothing - we doesn't need the ASM-generated .class file:
    // JDev is an IDE, just compile the source!
    @Override
    public OutputStream getProviderClassStream() throws FileNotFoundException {
        return XRPackager.__nullStream;
    }
    @Override
    public void writeProviderClass(OutputStream codeGenProviderStream, DBWSBuilder builder) {
    }

    protected void buildSrcDir() throws FileNotFoundException {
        srcDir = new File(stageDir, SRC_DIR);
        if (!srcDir.exists()) {
            boolean worked = srcDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    SRC_DIR + " under " + stageDir);
            }
        }
    }

    protected void buildUnderDBWS() throws FileNotFoundException {
        buildSrcDir();
        underDBWSDir = new File(srcDir, UNDER_DBWS);
        if (!underDBWSDir.exists()) {
            boolean worked = underDBWSDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " + SRC_DIR + "/" + UNDER_DBWS +
                    " dir under " + stageDir);
            }
        }
    }

    protected void buildPublicHTMLDir() throws FileNotFoundException {
        publicHTMLDir = new File(stageDir, PUBLIC_HTML_DIR);
        if (!publicHTMLDir.exists()) {
            boolean worked = publicHTMLDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    PUBLIC_HTML_DIR + " under " + stageDir);
            }
        }
    }

    protected void buildWebInfDir() throws FileNotFoundException {
        buildPublicHTMLDir();
        webInfDir = new File(publicHTMLDir, WEB_INF_DIR);
        if (!webInfDir.exists()) {
            boolean worked = webInfDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WEB_INF_DIR + " under " + PUBLIC_HTML_DIR);
            }
        }
    }

    protected void buildWSDLDir() throws FileNotFoundException {
        buildWebInfDir();
        wsdlDir = new File(webInfDir, WSDL_DIR);
        if (!wsdlDir.exists()) {
            boolean worked = wsdlDir.mkdir();
            if (!worked) {
                throw new FileNotFoundException("cannot create " +
                    WSDL_DIR + " under " + WEB_INF_DIR);
            }
        }
    }

    @Override
    public OutputStream getProviderSourceStream() throws FileNotFoundException {
        buildUnderDBWS();
        return new FileOutputStream(new File(underDBWSDir, DBWS_PROVIDER_SOURCE_FILE));
    }
    @Override
    public void writeProviderSource(OutputStream sourceProviderStream, DBWSBuilder builder) {
        StringBuilder sb = new StringBuilder(DBWS_PROVIDER_SOURCE_PREAMBLE);
        String serviceName = builder.getWSDLGenerator().getServiceName();
        sb.append(serviceName);
        sb.append(DBWS_PROVIDER_SOURCE_PORT_NAME);
        sb.append(serviceName + "Port");
        sb.append(DBWS_PROVIDER_SOURCE_TARGET_NAMESPACE);
        sb.append(builder.getWSDLGenerator().getServiceNameSpace());
        sb.append(DBWS_PROVIDER_SOURCE_SUFFIX);
        if (builder.usesSOAP12()) {
            if (builder.mtomEnabled()) {
                sb.append(DBWS_PROVIDER_SOAP12_MTOM_BINDING);
            }
            else {
                sb.append(DBWS_PROVIDER_SOAP12_BINDING);
            }
        }
        else {
            if (builder.mtomEnabled()) {
                sb.append(DBWS_PROVIDER_SOAP11_MTOM_BINDING);
            }
            // else the default BindingType, don't have to explicitly set it
        }
        sb.append(DBWS_PROVIDER_SOURCE_CLASSDEF);
        OutputStreamWriter osw =
            new OutputStreamWriter(new BufferedOutputStream(sourceProviderStream));
        try {
            osw.write(sb.toString());
            osw.flush();
        }
        catch (IOException e) {/* ignore */}
    }
}
