/*
 * Copyright (c) 2002-2012, Mairie de Paris
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

import fr.paris.lutece.plugins.workflow.modules.createpdf.business.ITaskCreatePDFConfigDAO;
import fr.paris.lutece.plugins.workflow.modules.createpdf.business.TaskCreatePDFConfig;
import fr.paris.lutece.portal.service.plugin.PluginService;

import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;


/**
 *
 * TaskCreatePDFConfigService
 */
public class TaskCreatePDFConfigService implements ITaskCreatePDFConfigService
{
    public static final String BEAN_SERVICE = "workflow-createpdf.taskCreatePDFConfigService";
    @Inject
    private ITaskCreatePDFConfigDAO _taskCreatePDFConfigDAO;

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow-createpdf.transactionManager" )
    public void createTaskCreatePDFConfig( TaskCreatePDFConfig taskCreatePDFConfig )
    {
        _taskCreatePDFConfigDAO.createTaskCreatePDFConfig( PluginService.getPlugin( CreatePDFPlugin.PLUGIN_NAME ),
            taskCreatePDFConfig );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow-createpdf.transactionManager" )
    public void deleteTaskCreatePDFConfig( int nIdTask )
    {
        _taskCreatePDFConfigDAO.deleteTaskCreatePDFConfig( PluginService.getPlugin( CreatePDFPlugin.PLUGIN_NAME ),
            nIdTask );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TaskCreatePDFConfig loadTaskCreatePDFConfig( int nIdTask )
    {
        return _taskCreatePDFConfigDAO.loadTaskCreatePDFConfig( PluginService.getPlugin( CreatePDFPlugin.PLUGIN_NAME ),
            nIdTask );
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @Transactional( "workflow-createpdf.transactionManager" )
    public void updateTaskCreatePDFConfig( TaskCreatePDFConfig taskCreatePDFConfig )
    {
        _taskCreatePDFConfigDAO.updateTaskCreatePDFConfig( PluginService.getPlugin( CreatePDFPlugin.PLUGIN_NAME ),
            taskCreatePDFConfig );
    }
}
