/**
 * Copyright (c) 2005-2007 Intalio inc.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Intalio inc. - initial API and implementation
 */
package org.intalio.tempo.deployment.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.intalio.tempo.web.SysPropApplicationContextLoader;

/**
 * Provides a REST web interface for deployment
 */
public class DeployServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Logger LOG = Logger.getLogger(DeployServlet.class);

    private static final String DEFAULT_CONFIG_FILE = "file:${org.intalio.tempo.configDirectory}/tempo-deploy.properties";

	DeployService _deploy;
	
    @Override
    public void init() throws ServletException {
        SysPropApplicationContextLoader loader;
        String configFile = getServletConfig().getInitParameter("contextConfigLocation");
        if (configFile == null) {
            configFile = DEFAULT_CONFIG_FILE;
        }
        LOG.debug("Loading configuration: "+configFile);
        try {
            loader = new SysPropApplicationContextLoader(configFile);
        } catch (IOException except) {
            throw new ServletException(except);
        }
        _deploy = loader.getBean("deployService");
        LOG.debug("Servlet initialized.");
    }

    /**
     * Get relative resource URI.
     * <p/>
     * For example, "GET /servlet/123", this method will return "123"
     */
    private static String getResourceUri(HttpServletRequest request) {
        int base = request.getContextPath().length();
        String relativeUri = request.getRequestURI().substring(base);
        // remove any leading slashes
        while (relativeUri.startsWith("/")) {
            relativeUri = relativeUri.substring(1);
        }
        return relativeUri;
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {
        if (LOG.isDebugEnabled()) LOG.debug("doDelete request="+request);
        String resourceUri = getResourceUri(request);
/*
        WDSService service = null;
        try {
            LOG.debug("Deleting item: '" + resourceUri + "'");
            service = _serviceFactory.getWDSService();
            if (Boolean.valueOf(request.getHeader("Delete-PIPA-Tasks"))) {
                String formUrl = request.getRequestURL().toString();
                String wdsEndPoint = _serviceFactory.getWdsEndpoint();
                if (formUrl.startsWith(wdsEndPoint)) {
                    formUrl = formUrl.substring(wdsEndPoint.length());
                }
                PipaTask pipa = newPipa(request, formUrl);
                service.deletePIPA(participantToken, pipa);
            } else {
                service.deleteItem(resourceUri, participantToken);
                service.commit();
            }
            LOG.debug("Item '" + resourceUri + "' deleted OK.");
        } catch (AuthenticationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            warn("Authentication error", e);
        } catch (UnavailableItemException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            warn("Item not found: '" + resourceUri + "'", e);
        } finally {
            if (service != null) service.close();
        }
        */
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        LOG.debug("doGet request="+request);
/*
        String resourceUri = getResourceUri(request);
        if ("".equals(resourceUri)) {
            response.getWriter().println(getServletContext().getServletContextName());
        } else {
            String participantToken = getParticipantToken(request);
            InputStream dataStream = null;
            WDSService service = null;
            try {
                service = _serviceFactory.getWDSService();

                LOG.debug("Retrieving the item.");
                Item item = service.retrieveItem(resourceUri, participantToken);
                dataStream = new ByteArrayInputStream(item.getPayload());
                OutputStream outputStream = response.getOutputStream();
                response.setContentLength(item.getContentLength());
                response.setContentType(item.getContentType());

                LOG.debug("Sending the data..");
                IOUtils.copy(dataStream, outputStream);
                outputStream.flush();

                service.commit();
                LOG.debug("Item retrieved & sent OK.");
            } catch (AuthenticationException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                LOG.warn("Authentication error", e);
            } catch (UnavailableItemException e) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                LOG.warn("Item not found: '" + resourceUri + "'", e);
            } finally {
                if (service != null) service.close();
                if (dataStream != null) dataStream.close();
            }
        }
        */
    }

    /**
     * Splits comma-delimited values into array
     */
    private String[] split(String source) {
        if (source == null) return new String[0];
        List<String> list = new ArrayList<String>();
        StringTokenizer tok = new StringTokenizer(source, ",");
        while (tok.hasMoreTokens()) {
            String id = tok.nextToken();
            list.add(id);
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) 
        throws ServletException, IOException
    {
        if (LOG.isDebugEnabled()) {
            LOG.debug("doPut request="+request+" contentType="+request.getContentType()+
                      " contentLength="+request.getContentLength());
        }
        /*
        String resourceUri = getResourceUri(request);
        String contentType = request.getContentType();
        if (contentType == null) {
            contentType = "application/octet-stream";
        }
        String participantToken = getParticipantToken(request);
        InputStream payloadStream = request.getInputStream();
        WDSService service = null;
        try {
            service = _serviceFactory.getWDSService();

            if (request.getContentLength() != 0) {
            	// added by atoulme 03-28
            	// we send a zip file, containing all the elements
            	// our objectives are :
            	// 1. Delete the previous version if present (version with same name)
            	// 2. Install all files in the zip as items
            	// as for now 1# is TODO.
            	// for more info on what is sent here, please refer to the 
            	// WorkflowDeploymentJob in XForms builder
            	if ("application/zip".equals(contentType)) {
            		if (!resourceUri.endsWith("/")) {
            			resourceUri += "/";
            		}
            		ZipInputStream zstream = new ZipInputStream(payloadStream);
            		try {
            			ZipEntry entry = zstream.getNextEntry(); 
                        while (entry != null) {
                            if (entry.getName().endsWith(".pipa")) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug("Pipa descriptor "+ resourceUri + entry.getName());
                                }
                                Properties prop = new Properties();
                                prop.load(zstream);
                                PipaTask task = newPipa(prop);
                                if (!task.isValid()) {
                                    throw new InvalidRequestFormatException("Invalid PIPA task:\n" + task);
                                }
                                service.storePipaTask(task, participantToken);
                                LOG.debug("Pipa descriptor '" + resourceUri + "' stored OK.");
                            } else if (entry.getName().endsWith(".xform")){
                                LOG.debug("Processing xform '" + resourceUri + "'");
                                try {
                                    ByteArrayOutputStream output = new ByteArrayOutputStream();
                                    copy(zstream, output);
                                    ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
                                    Item item = XFormsProcessor.processXForm(entry.getName(), input);
                                    service.storeItem(item, participantToken);
                                    LOG.debug("Storing the item: '" + resourceUri + entry.getName());
                                } catch (Exception e) {
                                   LOG.error(e.getMessage(), e);
                                   throw new RuntimeException(e);
                                }
                            } else {
                                ByteArrayOutputStream output = new ByteArrayOutputStream();
                                copy(zstream, output);
                                Item item = new Item(entry.getName(), "application/xml", output.toByteArray());
                                service.storeItem(item, participantToken);
                                LOG.debug("Storing the item: '" + resourceUri + entry.getName());
                            }
                            entry = zstream.getNextEntry(); 
            			}
            		} finally {
            			zstream.close();
            		}
            	} else {
            		Item item;
            		if ("True".equals(request.getHeader("Is-XForm"))) {
            			try {
            				item = XFormsProcessor.processXForm(resourceUri, payloadStream);
            			} catch (Exception e) {
            				throw new InvalidRequestFormatException("Invalid XForm XML.", e);
            			}
            		} else {
            			byte[] payload = IOUtils.toByteArray(payloadStream);
            			item = new Item(resourceUri, contentType, payload);
            		}
                    LOG.debug("Storing the item: '" + resourceUri + "'.");
                    try {
                        service.deleteItem(item.getURI(), participantToken);
                    } catch (UnavailableItemException except) {
                        // ignore
                    }
            		service.storeItem(item, participantToken);
            		LOG.debug("Item '" + resourceUri + "' stored OK.");
            	}
            	
            } else if ("True".equals(request.getHeader("Create-PIPA-Task"))) {
                PipaTask pipaTask = parsePipaTaskFromHeaders(request);
                if (LOG.isDebugEnabled()) LOG.debug("Storing pipa task:\n" + pipaTask);
                service.storePipaTask(pipaTask, participantToken);
                if (LOG.isDebugEnabled()) LOG.debug("Pipa task stored OK.");
            }
            service.commit();
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (InvalidRequestFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write(e.getMessage());
            LOG.warn("Invalid request message format", e);
        } catch (AuthenticationException e) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write(e.getMessage());
            LOG.warn("Authentication error", e);
        } catch (UnavailableItemException e) {
            response.sendError(HttpServletResponse.SC_CONFLICT);
            response.getWriter().write(e.getMessage());
            LOG.warn("URI already taken", e);
        } finally {
            if (service != null) {
                service.close();
            }
            if (payloadStream != null) {
            	payloadStream.close();
            }
        }
        */
    }

    /**
     * Copies the contents of the <code>InputStream</code> into the 
     * <code>OutputStream</code>.
     * <p/>
     * Note: when the copy is successful the <code>OutputStream</code> is 
     * closed.
     */
    private void copy(InputStream input, OutputStream output) { 
        try {
            byte[] bytes = new byte[4096];
            int bytesRead = 0;
            while ((bytesRead = input.read(bytes)) >= 0) {
                output.write(bytes, 0, bytesRead);
            }
            output.flush();
            output.close();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }
    
    private void warn(String message, Throwable t) {
        if (LOG.isDebugEnabled()) {
            LOG.warn(message, t);
        } else {
            LOG.warn(message + ": " + t.getMessage());
        }
    }
}