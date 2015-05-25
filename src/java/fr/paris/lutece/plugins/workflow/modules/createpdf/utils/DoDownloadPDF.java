/*
 * Copyright (c) 2002-2015, Mairie de Paris
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
package fr.paris.lutece.plugins.workflow.modules.createpdf.utils;

import fr.paris.lutece.plugins.directory.modules.pdfproducer.business.producerconfig.DefaultConfigProducer;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.business.producerconfig.IConfigProducer;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.service.ConfigProducerService;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.service.DirectoryPDFProducerPlugin;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.utils.PDFUtils;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.createpdf.business.TaskCreatePDFConfig;
import fr.paris.lutece.plugins.workflow.modules.createpdf.service.RequestAuthenticatorService;
import fr.paris.lutece.plugins.workflowcore.service.config.ITaskConfigService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.service.util.AppLogService;

import org.apache.commons.lang.StringUtils;

import org.springframework.beans.factory.BeanDefinitionStoreException;
import org.springframework.beans.factory.CannotLoadBeanClassException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 *
 * DoDownloadPDF
 *
 */
public class DoDownloadPDF
{
    /**
     * Do download pdf to Front User
     * @param request request
     * @param response response
     */
    public void doDownloadFile( HttpServletRequest request, HttpServletResponse response )
    {
        ITaskConfigService taskCreatePDFConfigService = SpringContextService.getBean( CreatePDFConstants.BEAN_CREATE_PDF_CONFIG_SERVICE );
        TaskCreatePDFConfig taskCreatePDFConfig = taskCreatePDFConfigService.findByPrimaryKey( DirectoryUtils.convertStringToInt( 
                    request.getParameter( CreatePDFConstants.PARAMETER_ID_TASK ) ) );
        String strIdConfig = Integer.toString( taskCreatePDFConfig.getIdConfig(  ) );
        int nIdRecord = DirectoryUtils.convertStringToInt( request.getParameter( 
                    CreatePDFConstants.PARAMETER_ID_DIRECTORY_RECORD ) );

        if ( RequestAuthenticatorService.getRequestAuthenticatorForUrl(  ).isRequestAuthenticated( request ) &&
                StringUtils.isNotBlank( strIdConfig ) )
        {
            ConfigProducerService manageConfigProducerService = null;

            try
            {
                manageConfigProducerService = SpringContextService.getBean( 
                        "directory-pdfproducer.manageConfigProducer" );
            }
            catch ( BeanDefinitionStoreException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( NoSuchBeanDefinitionException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }
            catch ( CannotLoadBeanClassException e )
            {
                AppLogService.error( e.getMessage(  ), e );
            }

            if ( manageConfigProducerService != null )
            {
                Plugin plugin = PluginService.getPlugin( DirectoryPDFProducerPlugin.PLUGIN_NAME );
                int nIdConfig = DirectoryUtils.convertStringToInt( strIdConfig );
                IConfigProducer config;

                if ( ( nIdConfig == -1 ) || ( nIdConfig == 0 ) )
                {
                    config = new DefaultConfigProducer(  );
                }
                else
                {
                    config = manageConfigProducerService.loadConfig( plugin, nIdConfig );
                }

                if ( config == null )
                {
                    config = new DefaultConfigProducer(  );
                }

                PDFUtils.doDownloadPDF( request, response, plugin, config,
                    manageConfigProducerService.loadListConfigEntry( plugin,
                        DirectoryUtils.convertStringToInt( strIdConfig ) ), request.getLocale(  ), nIdRecord );
            }
        }
    }
}
