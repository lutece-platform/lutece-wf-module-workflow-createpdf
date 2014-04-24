/*
 * Copyright (c) 2002-2014, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.createpdf.web;

import fr.paris.lutece.plugins.directory.business.DirectoryHome;
import fr.paris.lutece.plugins.directory.business.IEntry;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.business.producerconfig.ConfigProducer;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.business.producerconfig.ConfigProducerHome;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.service.DirectoryPDFProducerPlugin;
import fr.paris.lutece.plugins.directory.service.DirectoryPlugin;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.createpdf.business.TaskCreatePDFConfig;
import fr.paris.lutece.plugins.workflow.modules.createpdf.utils.CreatePDFConstants;
import fr.paris.lutece.plugins.workflow.utils.WorkflowUtils;
import fr.paris.lutece.plugins.workflow.web.task.NoFormTaskComponent;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.plugins.workflowcore.service.task.ITask;
import fr.paris.lutece.portal.service.admin.AdminUserService;
import fr.paris.lutece.portal.service.i18n.I18nService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.template.AppTemplateService;
import fr.paris.lutece.util.ReferenceItem;
import fr.paris.lutece.util.ReferenceList;
import fr.paris.lutece.util.html.HtmlTemplate;

import org.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import javax.servlet.http.HttpServletRequest;


/**
 *
 * CreatePDFTaskComponent
 *
 */
public class CreatePDFTaskComponent extends NoFormTaskComponent
{
    // MARKS
    private static final String MARKER_TASK_CREATEPDF_CONFIG = "task_config";

    // TEMPLATES
    private static final String TEMPLATE_TASK_CREATE_PDF = "admin/plugins/workflow/modules/createpdf/task_create_pdf_config.html";

    // PROPERTIES
    private static final String PROPERTY_LABEL_DEFAULT = "module.workflow.createpdf.task_create_pdf_config.label.default";

    // SERVICES
    @Inject
    @Named( CreatePDFConstants.BEAN_CREATE_PDF_CONFIG_SERVICE )
    private ITaskConfigService _taskCreatePDFConfigService;

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayConfigForm( HttpServletRequest request, Locale locale, ITask task )
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
            TaskCreatePDFConfig taskCreatePDFConfig = _taskCreatePDFConfigService.findByPrimaryKey( DirectoryUtils.convertStringToInt( 
                        strIdTask ) );

            if ( taskCreatePDFConfig != null )
            {
                model.put( MARKER_TASK_CREATEPDF_CONFIG, taskCreatePDFConfig );
                nIdDirectory = taskCreatePDFConfig.getIdDirectory(  );
            }
        }

        model.put( CreatePDFConstants.MARK_DIRECTORY_LIST, getListDirectories(  ) );
        model.put( CreatePDFConstants.MARK_LIST_ENTRIES_URL, getListEntriesUrl( nIdDirectory, request ) );
        model.put( CreatePDFConstants.MARK_LIST_CONFIG_PDF, getListConfigPdf( nIdDirectory, locale ) );

        HtmlTemplate template = AppTemplateService.getTemplate( TEMPLATE_TASK_CREATE_PDF, locale, model );

        return template.getHtml(  );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDisplayTaskInformation( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTaskInformationXml( int nIdHistory, HttpServletRequest request, Locale locale, ITask task )
    {
        return null;
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
     * @param locale the locale
     * @return ReferenceList list of config
     */
    private static ReferenceList getListConfigPdf( int nIdDirectory, Locale locale )
    {
        Plugin pluginDirectoryPDFProducer = PluginService.getPlugin( DirectoryPDFProducerPlugin.PLUGIN_NAME );
        List<ConfigProducer> listConfigProducer = ConfigProducerHome.loadListProducerConfig( pluginDirectoryPDFProducer,
                nIdDirectory, CreatePDFConstants.TYPE_CONFIG_PDF );

        ReferenceList referenceList = new ReferenceList(  );
        referenceList.addItem( WorkflowUtils.CONSTANT_ID_NULL,
            I18nService.getLocalizedString( PROPERTY_LABEL_DEFAULT, locale ) );

        for ( ConfigProducer configProducer : listConfigProducer )
        {
            ReferenceItem referenceItem = new ReferenceItem(  );
            referenceItem.setCode( String.valueOf( configProducer.getIdProducerConfig(  ) ) );
            referenceItem.setName( configProducer.getName(  ) );
            referenceList.add( referenceItem );
        }

        return referenceList;
    }
}
