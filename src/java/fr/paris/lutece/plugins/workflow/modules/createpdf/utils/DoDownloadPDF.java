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
package fr.paris.lutece.plugins.workflow.modules.createpdf.utils;

import fr.paris.lutece.plugins.directory.modules.pdfproducer.service.ConfigProducerService;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.service.DirectoryPDFProducerPlugin;
import fr.paris.lutece.plugins.directory.modules.pdfproducer.utils.PDFUtils;
import fr.paris.lutece.plugins.directory.utils.DirectoryUtils;
import fr.paris.lutece.plugins.workflow.modules.createpdf.business.TaskCreatePDFConfig;
import fr.paris.lutece.plugins.workflow.modules.createpdf.business.TaskCreatePDFConfigHome;
import fr.paris.lutece.plugins.workflow.modules.createpdf.service.CreatePDFPlugin;
import fr.paris.lutece.plugins.workflow.modules.createpdf.service.RequestAuthenticatorService;
import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.portal.service.plugin.PluginService;
import fr.paris.lutece.portal.service.spring.SpringContextService;

import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @author DoDownloadPDF
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
        TaskCreatePDFConfig taskCreatePDFConfig = TaskCreatePDFConfigHome.loadTaskCreatePDFConfig( PluginService.getPlugin( 
                    CreatePDFPlugin.PLUGIN_NAME ),
                DirectoryUtils.convertStringToInt( request.getParameter( CreatePDFConstants.PARAMETER_ID_TASK ) ) );
        String strIdConfig = Integer.toString( taskCreatePDFConfig.getIdConfig(  ) );

        if ( RequestAuthenticatorService.getRequestAuthenticatorForUrl(  ).isRequestAuthenticated( request ) &&
                StringUtils.isNotBlank( strIdConfig ) )
        {
            ConfigProducerService manageConfigProducerService = (ConfigProducerService) SpringContextService.getPluginBean( "directory-pdfproducer",
                    "directory-pdfproducer.manageConfigProducer" );
            Plugin plugin = PluginService.getPlugin( DirectoryPDFProducerPlugin.PLUGIN_NAME );
            PDFUtils.doDownloadPDF( request, response, plugin,
                manageConfigProducerService.loadConfig( plugin, DirectoryUtils.convertStringToInt( strIdConfig ) ),
                manageConfigProducerService.loadListConfigEntry( plugin,
                    DirectoryUtils.convertStringToInt( strIdConfig ) ), request.getLocale(  ) );
        }
    }
}
