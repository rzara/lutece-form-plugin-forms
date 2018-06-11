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
package fr.paris.lutece.plugins.forms.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import fr.paris.lutece.plugins.forms.business.form.FormResponseItem;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItemComparator;
import fr.paris.lutece.plugins.forms.business.form.FormResponseItemComparatorConfig;
import fr.paris.lutece.plugins.forms.business.form.column.FormColumnFactory;
import fr.paris.lutece.plugins.forms.business.form.column.IFormColumn;
import fr.paris.lutece.plugins.forms.business.form.filter.FormFilterFactory;
import fr.paris.lutece.plugins.forms.business.form.filter.IFormFilter;
import fr.paris.lutece.plugins.forms.business.form.panel.FormPanelFactory;
import fr.paris.lutece.plugins.forms.business.form.panel.IFormPanel;
import fr.paris.lutece.plugins.forms.service.MultiviewFormService;
import fr.paris.lutece.plugins.forms.util.FormsConstants;
import fr.paris.lutece.plugins.forms.web.form.column.display.FormColumnDisplayFactory;
import fr.paris.lutece.plugins.forms.web.form.column.display.IFormColumnDisplay;
import fr.paris.lutece.plugins.forms.web.form.filter.display.FormFilterDisplayFactory;
import fr.paris.lutece.plugins.forms.web.form.filter.display.IFormFilterDisplay;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.FormListPositionComparator;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.FormListTemplateBuilder;
import fr.paris.lutece.plugins.forms.web.form.multiview.util.MultiviewFormUtil;
import fr.paris.lutece.plugins.forms.web.form.panel.display.IFormPanelDisplay;
import fr.paris.lutece.plugins.forms.web.form.panel.display.factory.FormPanelDisplayFactory;
import fr.paris.lutece.portal.service.spring.SpringContextService;
import fr.paris.lutece.portal.util.mvc.admin.annotations.Controller;
import fr.paris.lutece.portal.util.mvc.commons.annotations.View;
import fr.paris.lutece.util.html.Paginator;
import fr.paris.lutece.util.url.UrlItem;

/**
 * Controller which manage the multiview of responses of all Forms
 */
@Controller( controllerJsp = "MultiviewForms.jsp", controllerPath = "jsp/admin/plugins/forms/", right = "FORMS_MULTIVIEW" )
public class MultiviewFormsJspBean extends AbstractJspBean
{
    // Generated serial UID
    private static final long serialVersionUID = 2122079505317782087L;
    
    // Templates
    private static final String TEMPLATE_FORMS_MULTIVIEW = "admin/plugins/forms/multiview/forms_multiview.html";

    // Properties
    private static final String PROPERTY_FORMS_MULTIVIEW_PAGE_TITLE = "forms.multiviewForms.pageTitle";
    
    // JSP URL
    private static final String JSP_FORMS_MULTIVIEW = "jsp/admin/plugins/forms/MultiviewForms.jsp";
    
    // Constants
    private static final String BASE_SORT_URL_PATTERN = JSP_FORMS_MULTIVIEW + "?current_selected_panel=%s";
    
    // Views
    private static final String VIEW_MULTIVIEW_FORMS = "view_multiview_forms";
    
    // Parameters
    private static final String PARAMETER_PAGE_INDEX = "page_index";
    private static final String PARAMETER_SORT_COLUMN_POSITION = "column_position";
    private static final String PARAMETER_SORT_ATTRIBUTE_NAME = "sorted_attribute_name";
    private static final String PARAMETER_SORT_ASC_VALUE = "asc_sort";
    
    // Marks
    private static final String MARK_LOCALE = "locale";
    private static final String MARK_PAGINATOR = "paginator";
    private static final String MARK_FORM_PANEL_LIST = "form_panel_list";
    private static final String MARK_CURRENT_SELECTED_PANEL = "current_selected_panel";
    private static final String MARK_FORM_FILTER_LIST = "form_filter_list";
    private static final String MARK_TABLE_TEMPLATE = "table_template";

    // Session variables
    private String _strSelectedPanelTechnicalCode = StringUtils.EMPTY;
    private transient List<IFormColumn> _listFormColumn;
    private transient List<IFormFilterDisplay> _listFormFilterDisplay;
    private transient List<IFormColumnDisplay> _listFormColumnDisplay;
    private transient List<IFormPanelDisplay> _listFormPanelDisplay;
    private transient IFormPanelDisplay _formPanelDisplayActive;
    private transient FormResponseItemComparatorConfig _formResponseItemComparatorConfig;
    
    /**
     * Return the view with the responses of all the forms
     * 
     * @param request
     *          The request on which to retrieve informations
     * @return the view associated to the responses value of all forms
     */
    @View( value = VIEW_MULTIVIEW_FORMS, defaultView = true )
    public String getMultiviewFormsView( HttpServletRequest request )
    {
        // Retrieve the list of all filters, columns and panels if the pagination and the sort are not used
        boolean bIsSessionLost = isSessionLost( );
        if ( isPaginationAndSortNotUsed( request ) || bIsSessionLost )
        {
            initFormRelatedLists( request );
            manageSelectedPanel( );
        }

        // Build the Column for the Panel and save their values for the active panel
        buildFormPanelDisplayWithData( );

        // Sort the list of FormResponseItem of the FormPanel with the request information
        sortFormResponseItemList( request, _formPanelDisplayActive.getFormResponseItemList( ) );

        // Build the template of each form filter display
        if ( isPaginationAndSortNotUsed( request ) || bIsSessionLost )
        {
            _listFormFilterDisplay.stream( ).forEach( formFilterDisplay -> formFilterDisplay.buildTemplate( request ) );
            Collections.sort( _listFormFilterDisplay, new FormListPositionComparator( ) );
        }

        // Retrieve the list of all id of form response of the active FormPanelDisplay
        List<Integer> listIdFormResponse = MultiviewFormUtil.getListIdFormResponseOfFormPanel( _formPanelDisplayActive );

        // Build the model
        Map<String, Object> model = getPaginatedListModel( request, Paginator.PARAMETER_PAGE_INDEX, listIdFormResponse, buildPaginatorUrl( ) );
        model.put( MARK_PAGINATOR, getPaginator( ) );
        model.put( MARK_LOCALE, getLocale( ) );
        model.put( MARK_FORM_FILTER_LIST, _listFormFilterDisplay );

        // Add the template of column to the model
        String strSortUrl = String.format( BASE_SORT_URL_PATTERN, _strSelectedPanelTechnicalCode );
        List<FormResponseItem> listFormResponseItemToDisplay = buildFormResponseItemListToDisplay( );
        String strTableTemplate = FormListTemplateBuilder.buildTableTemplate( _listFormColumnDisplay, listFormResponseItemToDisplay, getLocale( ),
                strSortUrl );
        model.put( MARK_TABLE_TEMPLATE, strTableTemplate );

        // Add the list of all form panel
        model.put( MARK_FORM_PANEL_LIST, _listFormPanelDisplay );
        model.put( MARK_CURRENT_SELECTED_PANEL, _strSelectedPanelTechnicalCode );

        return getPage( PROPERTY_FORMS_MULTIVIEW_PAGE_TITLE, TEMPLATE_FORMS_MULTIVIEW, model );
    }
    
    /**
     * Return the boolean which tell if the session is lost
     * 
     * @return the boolean which tell if the session is lost
     */
    private boolean isSessionLost( )
    {
        return _listFormColumn == null || _listFormFilterDisplay == null || _listFormColumnDisplay == null || _listFormPanelDisplay == null
                || _formPanelDisplayActive == null;
    }
    
    /**
     * Return the boolean which tell if the pagination and the sort are not used
     * 
     * @param request
     *            The request to retrieve the information from
     * @return the boolean which tell if the pagination and the sort are not used
     */
    private boolean isPaginationAndSortNotUsed( HttpServletRequest request )
    {
        return request.getParameter( PARAMETER_PAGE_INDEX ) == null && request.getParameter( PARAMETER_SORT_COLUMN_POSITION ) == null;
    }
    
    /**
     * Build the list of all columns, filters and forms panels and all of their display equivalents
     * 
     * @param request
     *            The request used to build the list of the forms elements
     */
    private void initFormRelatedLists( HttpServletRequest request )
    {
        List<IFormFilter> listFormFilter = new FormFilterFactory( ).buildFormFilterList( );
        List<IFormPanel> listFormPanel = new FormPanelFactory( ).buildFormPanelList( );

        FormColumnFactory formColumnFactory = SpringContextService.getBean( FormColumnFactory.BEAN_NAME );
        _listFormColumn = formColumnFactory.buildFormColumnList( );

        _listFormFilterDisplay = new FormFilterDisplayFactory( ).createFormFilterDisplayList( request, listFormFilter );
        _listFormColumnDisplay = new FormColumnDisplayFactory( ).createFormColumnDisplayList( _listFormColumn );
        _listFormPanelDisplay = new FormPanelDisplayFactory( ).createFormPanelDisplayList( request, listFormPanel );
    }
    
    /**
     * Retrieve the technical code of the selected panel and change the value of the previous selected one if it wasn't the same and reset the pagination in
     * this case
     */
    private void manageSelectedPanel( )
    {
        IFormPanelDisplay formPanelDisplay = MultiviewFormService.getInstance( ).findActiveFormPanel( _listFormPanelDisplay );
        if ( formPanelDisplay != null )
        {
            String strSelectedPanelTechnicalCode = formPanelDisplay.getTechnicalCode( );
            if ( StringUtils.isNotBlank( strSelectedPanelTechnicalCode ) && !_strSelectedPanelTechnicalCode.equals( strSelectedPanelTechnicalCode ) )
            {
                _strSelectedPanelTechnicalCode = strSelectedPanelTechnicalCode;
                resetCurrentPaginatorPageIndex( );
            }
        }
    }
    
    /**
     * Build all the form panels by building their template and retrieve the data of their columns for the given list of filter and the specified text to
     * search
     */
    private void buildFormPanelDisplayWithData( )
    {
        // Retrieve the list of all FormFilter
        List<IFormFilter> listFormFilter = _listFormFilterDisplay.stream( ).map( IFormFilterDisplay::getFormFilter ).collect( Collectors.toList( ) );

        for ( IFormPanelDisplay formPanelDisplay : _listFormPanelDisplay )
        {
            // Retrieve the FormPanel from the FormPanelDisplay
            IFormPanel formPanel = formPanelDisplay.getFormPanel( );

            // Populate the FormColumns from the information of the list of FormResponseItem of the given FormPanel
            MultiviewFormService.getInstance( ).populateFormColumns( formPanel, _listFormColumn, listFormFilter );

            // Associate for each FormColumnDisplay its FormColumnValues if the panel is active
            if ( formPanelDisplay.isActive( ) )
            {
                _formPanelDisplayActive = formPanelDisplay;
            }

            // Build the template of the form list panel
            formPanelDisplay.buildTemplate( getLocale( ) );
        }
    }
    
    /**
     * Sort the given list of FormResponseItem from the values contains in the request
     * 
     * @param request
     *            The request to retrieve the values used for the sort
     * @param listFormResponseItem
     *            The list of FormResponseItem to sort
     */
    private void sortFormResponseItemList( HttpServletRequest request, List<FormResponseItem> listFormResponseItem )
    {
        if ( request.getParameter( PARAMETER_SORT_COLUMN_POSITION ) != null )
        {
            buildFormResponseItemComparatorConfiguration( request );
        }

        if ( listFormResponseItem != null && !listFormResponseItem.isEmpty( ) )
        {
            FormResponseItemComparator formResponseItemComparator = new FormResponseItemComparator( _formResponseItemComparatorConfig );
            Collections.sort( listFormResponseItem, formResponseItemComparator );
        }
    }
    
    /**
     * Build the configuration to use for sort the FormResponseItem with the information from the request
     * 
     * @param request
     *            The request to retrieve the values for the sort from
     */
    private void buildFormResponseItemComparatorConfiguration( HttpServletRequest request )
    {
        String strColumnToSortPosition = request.getParameter( PARAMETER_SORT_COLUMN_POSITION );
        int nColumnToSortPosition = NumberUtils.toInt( strColumnToSortPosition, NumberUtils.INTEGER_MINUS_ONE );

        String strSortKey = request.getParameter( PARAMETER_SORT_ATTRIBUTE_NAME );

        String strAscSort = request.getParameter( PARAMETER_SORT_ASC_VALUE );
        boolean bAscSort = Boolean.parseBoolean( strAscSort );

        _formResponseItemComparatorConfig = new FormResponseItemComparatorConfig( nColumnToSortPosition, strSortKey, bAscSort );
    }
    
    /**
     * Build the url to use for the pagination
     * 
     * @return the url to use for the pagination
     */
    private String buildPaginatorUrl( )
    {
        UrlItem url = new UrlItem( JSP_FORMS_MULTIVIEW );
        url.addParameter( FormsConstants.PARAMETER_CURRENT_SELECTED_PANEL, _strSelectedPanelTechnicalCode );
        
        return url.getUrl( );
    }
    
    /**
     * Build the list of FormResponseItem to display for the active FormPanelDisplay based on the number of items of the current paginator
     * 
     * @return list of FormResponseItem to display for the active FormPanelDisplay
     */
    private List<FormResponseItem> buildFormResponseItemListToDisplay( )
    {
        List<FormResponseItem> listFormResponseItemToDisplay = new ArrayList<>( );

        List<Integer> listIdFormResponsePaginated = getPaginator( ).getPageItems( );
        List<FormResponseItem> listFormResponseItem = _formPanelDisplayActive.getFormResponseItemList( );
        if ( !CollectionUtils.isEmpty( listIdFormResponsePaginated ) && !CollectionUtils.isEmpty( listFormResponseItem ) )
        {
            for ( FormResponseItem formResponseItem : listFormResponseItem )
            {
                Integer nIdFormResponse = formResponseItem.getIdFormResponse( );
                if ( listIdFormResponsePaginated.contains( nIdFormResponse ) )
                {
                    listFormResponseItemToDisplay.add( formResponseItem );
                }
            }
        }

        return listFormResponseItemToDisplay;
    }
}
