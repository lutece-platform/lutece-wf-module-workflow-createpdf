/*
 * Copyright (c) 2002-2013, Mairie de Paris
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *  1. Redistributions of source code must retain the above copyright notice
 *     and the following disclaimer.
 *
 *  2. Redistributions in binary form must reproduce the above copyright notice
 *     and the following disclaimer in the documentation and/or other materials
 *     provided with the distribution.
 *
 *  3. Neither the name of 'Mairie de Paris' nor 'Lutece' nor the names of its
 *     contributors may be used to endorse or promote products derived from
 *     this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * License 1.0
 */
package fr.paris.lutece.plugins.workflow.modules.createpdf.service;

import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.createpdf.business.TaskCreatePDFConfig;
import fr.paris.lutece.plugins.workflow.modules.createpdf.utils.CreatePDFConstants;
import fr.paris.lutece.plugins.workflowcore.business.resource.ResourceHistory;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.resource.IResourceHistoryService;
import fr.paris.lutece.plugins.workflowcore.service.task.SimpleTask;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * TaskCreatePDF
 *
 */
public class TaskCreatePDF extends SimpleTask
{
    // PROPERTIES
    private static final String PROPERTY_TASK_TITLE = "workflow.createpdf.task.title";

    // PARAMETERS
    private static final String PARAM_SIGNATURE = "signature";
    private static final String PARAM_TIMESTAMP = "timestamp";

    // SERVICES
    @Inject
    @Named( CreatePDFConstants.BEAN_CREATE_PDF_CONFIG_SERVICE )
    private ITaskConfigService _taskCreatePDFConfigService;
    @Inject
    private IResourceHistoryService _resourceHistoryService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle( Locale locale )
    {
        return AppPropertiesService.getProperty( PROPERTY_TASK_TITLE );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Locale locale )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        ResourceHistory resourceHistory = _resourceHistoryService.findByPrimaryKey( nIdResourceHistory );
        TaskCreatePDFConfig taskCreatePDFConfig = _taskCreatePDFConfigService.findByPrimaryKey( getId(  ) );
        String strIdEntryUrlPDF = String.valueOf( taskCreatePDFConfig.getIdEntryUrlPDF(  ) );

        IEntry entry = EntryHome.findByPrimaryKey( DirectoryUtils.convertStringToInt( strIdEntryUrlPDF ),
                pluginDirectory );
        Record record = RecordHome.findByPrimaryKey( resourceHistory.getIdResource(  ), pluginDirectory );

        if ( ( entry != null ) && ( record != null ) )
        {
            List<String> listElements = new ArrayList<String>(  );
            listElements.add( Integer.toString( record.getIdRecord(  ) ) );

            String strTime = Long.toString( new Date(  ).getTime(  ) );

            String strSignature = RequestAuthenticatorService.getRequestAuthenticatorForUrl(  )
                                                             .buildSignature( listElements, strTime );

            StringBuilder sbUrl = new StringBuilder( getBaseUrl( request ) );

            if ( !sbUrl.toString(  ).endsWith( CreatePDFConstants.SLASH ) )
            {
                sbUrl.append( CreatePDFConstants.SLASH );
            }

            UrlItem url = new UrlItem( sbUrl.toString(  ) + CreatePDFConstants.URL_DOWNLOAD_PDF );
            url.addParameter( PARAM_SIGNATURE, strSignature );
            url.addParameter( PARAM_TIMESTAMP, strTime );
            url.addParameter( CreatePDFConstants.PARAMETER_ID_DIRECTORY_RECORD, record.getIdRecord(  ) );
            url.addParameter( CreatePDFConstants.PARAMETER_ID_TASK, taskCreatePDFConfig.getIdTask(  ) );

            RecordFieldFilter filter = new RecordFieldFilter(  );
            filter.setIdRecord( record.getIdRecord(  ) );
            filter.setIdEntry( DirectoryUtils.convertStringToInt( strIdEntryUrlPDF ) );

            // Delete record field
            RecordFieldHome.removeByFilter( filter, pluginDirectory );

            // Insert the new record Field
            RecordField recordField = new RecordField(  );
            recordField.setRecord( record );
            recordField.setEntry( entry );
            recordField.setValue( url.getUrl(  ) );
            RecordFieldHome.create( recordField, pluginDirectory );
        }
    }

    /**
     * Get the base url
     * @param request the HTTP request
     * @return the base url
     */
    private String getBaseUrl( HttpServletRequest request )
    {
        String strBaseUrl = StringUtils.EMPTY;

        if ( request != null )
        {
            strBaseUrl = AppPathService.getBaseUrl( request );
        }
        else
        {
            strBaseUrl = AppPropertiesService.getProperty( CreatePDFConstants.PROPERTY_LUTECE_BASE_URL );

            if ( StringUtils.isBlank( strBaseUrl ) )
            {
                strBaseUrl = AppPropertiesService.getProperty( CreatePDFConstants.PROPERTY_LUTECE_PROD_URL );
            }
        }

        return strBaseUrl;
    }
}
