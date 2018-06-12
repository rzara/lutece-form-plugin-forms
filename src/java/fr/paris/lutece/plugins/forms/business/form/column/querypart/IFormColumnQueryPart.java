/*
 * Copyright (c) 2002-2018, Mairie de Paris
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
package fr.paris.lutece.plugins.forms.business.form.column.querypart;

import java.util.List;

import fr.paris.lutece.plugins.forms.business.form.column.FormColumnCell;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.util.sql.DAOUtil;

/**
 * Global interface for all form column query part
 */
public interface IFormColumnQueryPart
{
    /**
     * Return the select query part for the FormColumn
     * 
     * @return the select query part for the FormColumn
     */
    String getFormColumnSelectQuery( );

    /**
     * Return the from query part for the FormColumn
     * 
     * @return the from query part for the FormColumn
     */
    String getFormColumnFromQuery( );

    /**
     * Return the list of join queries for the FormColumn
     * 
     * @return the list of join queries for the FormColumn
     */
    List<String> getFormColumnJoinQueries( );

    /**
     * Set the form column to the FormColumnQueryPart
     * 
     * @param formColumn
     *            The FormColumn to set to the FormColumnQueryPart
     */
    void setFormColumn( IFormColumn formColumn );

    /**
     * Return the FormColumn of the FormColumnQueryPart
     * 
     * @return the FormColumn of the FormColumnQueryPart
     */
    IFormColumn getFormColumn( );

    /**
     * Return the FormColumnCell of the FormColumnQueryPart
     * 
     * @param daoUtil
     *            The daoUtil to retrieve the values to retrieve to the form column
     * @return the FormColumnCell which contains all the values of the form column from the given daoUtil
     */
    FormColumnCell getFormColumnCell( DAOUtil daoUtil );
}
