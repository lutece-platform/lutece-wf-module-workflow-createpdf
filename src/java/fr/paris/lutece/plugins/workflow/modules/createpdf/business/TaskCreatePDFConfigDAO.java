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

import fr.paris.lutece.portal.service.plugin.Plugin;
import fr.paris.lutece.util.sql.DAOUtil;


/**
 * TaskCreatePDFConfigDAO
 *
 */
public class TaskCreatePDFConfigDAO implements ITaskCreatePDFConfigDAO
{
    private static final String SQL_QUERY_SELECT = "SELECT id_task, id_directory, id_entry_url_pdf, id_config FROM task_create_pdf_cf WHERE id_task = ? ;";
    private static final String SQL_QUERY_INSERT = "INSERT INTO task_create_pdf_cf ( id_task, id_directory, id_entry_url_pdf, id_config ) VALUES ( ? , ? , ? , ? );";
    private static final String SQL_QUERY_DELETE = "DELETE FROM task_create_pdf_cf WHERE id_task = ? ;";
    private static final String SQL_QUERY_UPDATE = "UPDATE task_create_pdf_cf SET id_entry_url_pdf = ? , id_directory = ? , id_config = ? WHERE id_task = ? ;";

    /**
    * {@inheritDoc}
    */
    public void createTaskCreatePDFConfig( Plugin plugin, TaskCreatePDFConfig taskCreatePDFConfig )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_INSERT, plugin );
        daoUtil.setInt( 1, taskCreatePDFConfig.getIdTask(  ) );
        daoUtil.setInt( 2, taskCreatePDFConfig.getIdDirectory(  ) );
        daoUtil.setInt( 3, taskCreatePDFConfig.getIdEntryUrlPDF(  ) );
        daoUtil.setInt( 4, taskCreatePDFConfig.getIdConfig(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }

    /**
    * {@inheritDoc}
    */
    public void deleteTaskCreatePDFConfig( Plugin plugin, int nIdTask )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_DELETE, plugin );
        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeQuery(  );
        daoUtil.free(  );
    }

    /**
    * {@inheritDoc}
    */
    public TaskCreatePDFConfig loadTaskCreatePDFConfig( Plugin plugin, int nIdTask )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_SELECT, plugin );
        daoUtil.setInt( 1, nIdTask );
        daoUtil.executeQuery(  );

        if ( daoUtil.next(  ) )
        {
            TaskCreatePDFConfig taskCreatePDFConfig = new TaskCreatePDFConfig(  );
            taskCreatePDFConfig.setIdTask( daoUtil.getInt( 1 ) );
            taskCreatePDFConfig.setIdDirectory( daoUtil.getInt( 2 ) );
            taskCreatePDFConfig.setIdEntryUrlPDF( daoUtil.getInt( 3 ) );
            taskCreatePDFConfig.setIdConfig( daoUtil.getInt( 4 ) );
            daoUtil.free(  );
            return taskCreatePDFConfig;
        }
        else
        {
        	daoUtil.free(  );	
        	return null;
        }
    }

    /**
    * {@inheritDoc}
    */
    public void updateTaskCreatePDFConfig( Plugin plugin, TaskCreatePDFConfig taskCreatePDFConfig )
    {
        DAOUtil daoUtil = new DAOUtil( SQL_QUERY_UPDATE, plugin );
        daoUtil.setInt( 1, taskCreatePDFConfig.getIdEntryUrlPDF(  ) );
        daoUtil.setInt( 2, taskCreatePDFConfig.getIdDirectory(  ) );
        daoUtil.setInt( 3, taskCreatePDFConfig.getIdConfig(  ) );
        daoUtil.setInt( 4, taskCreatePDFConfig.getIdTask(  ) );
        daoUtil.executeUpdate(  );
        daoUtil.free(  );
    }
}
