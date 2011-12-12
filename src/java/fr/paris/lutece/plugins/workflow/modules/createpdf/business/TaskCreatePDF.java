/*
 * Copyright (c) 2002-2011, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.createpdf.business;

import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.EntryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.business.Record;
import fr.paris.lutece.plugins.directory.business.RecordField;
import fr.paris.lutece.plugins.directory.business.RecordFieldFilter;
import fr.paris.lutece.plugins.directory.business.RecordFieldHome;
import fr.paris.lutece.plugins.directory.business.RecordHome;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.business.producerconfig.ConfigProducer;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.business.producerconfig.ConfigProducerHome;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.service.DirectoryPDFProducerPlugin;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.business.ResourceHistory;
import fr.paris.lutece.plugins.workflow.business.ResourceHistoryHome;
import fr.paris.lutece.plugins.workflow.business.task.Task;
import fr.paris.lutece.plugins.workflow.modules.createpdf.service.CreatePDFPlugin;
import fr.paris.lutece.plugins.workflow.modules.createpdf.service.RequestAuthenticatorService;
import fr.paris.lutece.plugins.workflow.modules.createpdf.utils.CreatePDFConstants;
import fr.paris.lutece.plugins.workflow.service.WorkflowPlugin;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.portal.service.util.AppPathService;
import fr.paris.lutece.portal.service.util.AppPropertiesService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;
import fr.paris.lutece.util.url.UrlItem;

import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;


/**
 * TaskCreatePDF
 *
 */
public class TaskCreatePDF extends Task
{
    // properties PDF configuration
    private static final String PROPERTY_TASK_TITLE = "workflow.createpdf.task.title";
    private static final String PARAM_SIGNATURE = "signature";
    private static final String PARAM_TIMESTAMP = "timestamp";
    private static final String PARAMETER_ID_CONFIG_PDF = "id_config_pdf";
    private static final String PARAMETER_ID_ENTRY_URL_PDF = "id_entry_url_pdf";
    private static final String MARKER_TASK_CREATEPDF_CONFIG = "task_config";
    private static final String TEMPLATE_TASK_CREATE_PDF = "admin/plugins/workflow/modules/createpdf/task_create_pdf_config.html";

    /**
     * {@inheritDoc}
     */
    public void doRemoveConfig( Plugin plugin )
    {
    }

    /**
    * {@inheritDoc}
    */
    public void doRemoveTaskInformation( int arg0, Plugin plugin )
    {
    }

    /**
    * {@inheritDoc}
    */
    public String doSaveConfig( HttpServletRequest request, Locale locale, Plugin plugin )
    {
        String strIdTask = request.getParameter( CreatePDFConstants.PARAMETER_ID_TASK );
        String strIdPDFConfig = request.getParameter( PARAMETER_ID_CONFIG_PDF );
        String strIdEntryUrlPDF = request.getParameter( PARAMETER_ID_ENTRY_URL_PDF );
        String strIdDirectory = request.getParameter( CreatePDFConstants.PARAMETER_ID_DIRECTORY );

        TaskCreatePDFConfig taskCreatePDFConfig = new TaskCreatePDFConfig(  );
        taskCreatePDFConfig.setIdTask( DirectoryUtils.convertStringToInt( strIdTask ) );
        taskCreatePDFConfig.setIdDirectory( DirectoryUtils.convertStringToInt( strIdDirectory ) );
        taskCreatePDFConfig.setIdConfig( DirectoryUtils.convertStringToInt( strIdPDFConfig ) );
        taskCreatePDFConfig.setIdEntryUrlPDF( ( DirectoryUtils.convertStringToInt( strIdEntryUrlPDF ) ) );

        if ( TaskCreatePDFConfigHome.loadTaskCreatePDFConfig( plugin, DirectoryUtils.convertStringToInt( strIdTask ) ) != null )
        {
            TaskCreatePDFConfigHome.updateTaskCreatePDFConfig( plugin, taskCreatePDFConfig );
        }
        else
        {
            TaskCreatePDFConfigHome.createTaskCreatePDFConfig( plugin, taskCreatePDFConfig );
        }

        return null;
    }

    /**
    * {@inheritDoc}
    */
    public String doValidateTask( int arg0, String arg1, HttpServletRequest arg2, Locale arg3, Plugin arg4 )
    {
        return null;
    }

    /**
    * {@inheritDoc}
    */
    public String getDisplayConfigForm( HttpServletRequest request, Plugin plugin, Locale locale )
    {
        Map<String, Object> model = new HashMap<String, Object>(  );
        String strIdTask = request.getParameter( CreatePDFConstants.PARAMETER_ID_TASK );

        int nIdDirectory;

        if ( StringUtils.isNotBlank( request.getParameter( CreatePDFConstants.PARAMETER_ID_DIRECTORY ) ) )
        {
            nIdDirectory = DirectoryUtils.convertStringToInt( request.getParameter( 
                        CreatePDFConstants.PARAMETER_ID_DIRECTORY ) );
        }
        else
        {
            nIdDirectory = -1;
        }

        if ( StringUtils.isNotBlank( strIdTask ) )
        {
            TaskCreatePDFConfig taskCreatePDFConfig = TaskCreatePDFConfigHome.loadTaskCreatePDFConfig( PluginService.getPlugin( 
                        CreatePDFPlugin.PLUGIN_NAME ), DirectoryUtils.convertStringToInt( strIdTask ) );

            if ( taskCreatePDFConfig != null )
            {
                model.put( MARKER_TASK_CREATEPDF_CONFIG, taskCreatePDFConfig );
                nIdDirectory = taskCreatePDFConfig.getIdDirectory(  );
            }
        }

        model.put( CreatePDFConstants.MARK_DIRECTORY_LIST, getListDirectories(  ) );
        model.put( CreatePDFConstants.MARK_LIST_ENTRIES_URL, getListEntriesUrl( nIdDirectory, request ) );
        model.put( CreatePDFConstants.MARK_LIST_CONFIG_PDF, getListConfigPdf( nIdDirectory ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CREATE_PDF, locale, model );

        return template.getHtml(  );
    }

    /**
    * {@inheritDoc}
    */
    public String getDisplayTaskForm( int arg0, String arg1, HttpServletRequest arg2, Plugin arg3, Locale arg4 )
    {
        return null;
    }

    /**
    * {@inheritDoc}
    */
    public String getDisplayTaskInformation( int arg0, HttpServletRequest arg1, Plugin arg2, Locale arg3 )
    {
        return null;
    }

    /**
    * {@inheritDoc}
    */
    public ReferenceList getTaskFormEntries( Plugin arg0, Locale arg1 )
    {
        return null;
    }

    /**
    * {@inheritDoc}
    */
    public String getTaskInformationXml( int arg0, HttpServletRequest request, Plugin arg2, Locale locale )
    {
        return null;
    }

    /**
    * {@inheritDoc}
    */
    public String getTitle( Plugin plugin, Locale locale )
    {
        return AppPropertiesService.getProperty( PROPERTY_TASK_TITLE );
    }

    /**
    * {@inheritDoc}
    */
    public void init(  )
    {
    }

    /**
    * {@inheritDoc}
    */
    public boolean isConfigRequire(  )
    {
        return true;
    }

    /**
    * {@inheritDoc}
    */
    public boolean isFormTaskRequire(  )
    {
        return false;
    }

    /**
    * {@inheritDoc}
    */
    public boolean isTaskForActionAutomatic(  )
    {
        return true;
    }

    /**
    * {@inheritDoc}
    */
    public void processTask( int nIdResourceHistory, HttpServletRequest request, Plugin plugin, Locale locale )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        ResourceHistory resourceHistory = ResourceHistoryHome.findByPrimaryKey( nIdResourceHistory,
                PluginService.getPlugin( WorkflowPlugin.PLUGIN_NAME ) );
        TaskCreatePDFConfig taskCreatePDFConfig = TaskCreatePDFConfigHome.loadTaskCreatePDFConfig( plugin, getId(  ) );
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
     * Get the list of directorise
     * @return a ReferenceList
     */
    private static ReferenceList getListDirectories(  )
    {
        Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
        ReferenceList listDirectories = DirectoryHome.getDirectoryList( pluginDirectory );
        ReferenceList refenreceListDirectories = new ReferenceList(  );
        refenreceListDirectories.addItem( DirectoryUtils.CONSTANT_ID_NULL, StringUtils.EMPTY );

        if ( listDirectories != null )
        {
            refenreceListDirectories.addAll( listDirectories );
        }

        return refenreceListDirectories;
    }

    /**
     * Method to get directory entries list
     * @param nIdDirectory id directory
     * @param request request
     * @return ReferenceList entries list
     */
    private static ReferenceList getListEntriesUrl( int nIdDirectory, HttpServletRequest request )
    {
        if ( nIdDirectory != -1 )
        {
            Plugin pluginDirectory = PluginService.getPlugin( DirectoryPlugin.PLUGIN_NAME );
            List<IEntry> listEntries = DirectoryUtils.getFormEntries( nIdDirectory, pluginDirectory,
                    AdminUserService.getAdminUser( request ) );
            ReferenceList referenceList = new ReferenceList(  );

            for ( IEntry entry : listEntries )
            {
                if ( entry.getEntryType(  ).getComment(  ) )
                {
                    continue;
                }

                if ( entry.getEntryType(  ).getGroup(  ) )
                {
                    if ( entry.getChildren(  ) != null )
                    {
                        for ( IEntry child : entry.getChildren(  ) )
                        {
                            if ( child.getEntryType(  ).getComment(  ) )
                            {
                                continue;
                            }

                            ReferenceItem referenceItem = new ReferenceItem(  );
                            referenceItem.setCode( String.valueOf( child.getIdEntry(  ) ) );
                            referenceItem.setName( child.getTitle(  ) );
                            referenceList.add( referenceItem );
                        }
                    }
                }
                else
                {
                    ReferenceItem referenceItem = new ReferenceItem(  );
                    referenceItem.setCode( String.valueOf( entry.getIdEntry(  ) ) );
                    referenceItem.setName( entry.getTitle(  ) );
                    referenceList.add( referenceItem );
                }
            }

            return referenceList;
        }
        else
        {
            return null;
        }
    }

    /**
     * Method to get list of config, by id directory
     * @param nIdDirectory id directory
     * @return ReferenceList list of config
     */
    private static ReferenceList getListConfigPdf( int nIdDirectory )
    {
        Plugin pluginDirectoryPDFProducer = PluginService.getPlugin( DirectoryPDFProducerPlugin.PLUGIN_NAME );
        List<ConfigProducer> listConfigProducer = ConfigProducerHome.loadListProducerConfig( pluginDirectoryPDFProducer,
                nIdDirectory, CreatePDFConstants.TYPE_CONFIG_PDF );

        ReferenceList referenceList = new ReferenceList(  );

        for ( ConfigProducer configProducer : listConfigProducer )
        {
            ReferenceItem referenceItem = new ReferenceItem(  );
            referenceItem.setCode( String.valueOf( configProducer.getIdProducerConfig(  ) ) );
            referenceItem.setName( configProducer.getName(  ) );
            referenceList.add( referenceItem );
        }

        return referenceList;
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
