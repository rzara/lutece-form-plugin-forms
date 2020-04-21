

<#include "all_entry_commons.ftl" />

<@breadforms title=entry.entryType.title />
<@box color='primary'>
	<@boxHeader title='#i18n{forms.createEntry.titleQuestion} : "${form.title}"' />
	<@boxBody>
		<@tform action='jsp/admin/plugins/forms/ManageQuestions.jsp'>
			<fieldset>
				<legend class="sr-only">#i18n{forms.createEntry.titleQuestion} : '${form.title}'</legend>
				<input name="id_type" value="${entry.entryType.idType}" type="hidden" />
				<#if entry.fieldDepend?exists>
					<input name="id_field" value="${entry.fieldDepend.idField}" type="hidden" />
				</#if>
				<input name="id_parent" value="${id_parent!'0'}" type="hidden">
				<input name="id_step" value="${step.id!'0'}" type="hidden">
				<@formGroup labelFor='type' labelKey='#i18n{forms.createEntry.labelType}'>
				<@input type='text' name='type' id='type' readonly=true disabled=true value='${entry.entryType.title}' />
				</@formGroup>
				<@formGroup labelFor='title' labelKey='#i18n{forms.createEntry.labelTitle}' helpKey='#i18n{forms.createEntry.labelTitleComment}' mandatory=true>
					<@input type='text' name='title' id='title' value='' maxlength=255 />
				</@formGroup>
                                <@formGroup labelFor='entry_code' labelKey='#i18n{forms.createEntry.labelCode}' helpKey='#i18n{forms.createEntry.labelCodeComment}'>
                                    <@input type='text' name='entry_code' id='entry_code' value='' maxlength=100 />
                                </@formGroup>
				<@formGroup labelFor='help_message' labelKey='#i18n{forms.createEntry.labelHelpMessage}' helpKey='#i18n{forms.createEntry.labelHelpMessageComment}'>
					<@input type='textarea' name='help_message' id='help_message' rows=2></@input>
				</@formGroup>
				<@formGroup labelFor='comment' labelKey='#i18n{forms.createEntry.labelComment}'>
					<@input type='textarea' name='comment' id='comment' rows=2></@input>
				</@formGroup>
				<@formGroup labelFor='mandatory' labelKey='#i18n{forms.createEntry.labelMandatory}'>
					<@checkBox name='mandatory' id='mandatory' value='1' checked=getChecked('mandatory',list_param_default_values) />
				</@formGroup>
				<@formGroup labelFor='file_max_size' labelKey='#i18n{forms.createEntry.labelFileMaxSize}' mandatory=true>
					<@input type='text' name='file_max_size' id='file_max_size' value=getName('file_max_size',list_param_default_values) />
				</@formGroup>
				<@formGroup labelFor='max_files' labelKey='#i18n{forms.createEntry.labelMaxFiles}' mandatory=true>
					<@input type='text' name='max_files' id='max_files' value=getName('max_files',list_param_default_values) maxlength=2 />
				</@formGroup>
				<@formGroup labelFor='export_binary' labelKey='#i18n{forms.createEntry.labelExportBinary}'>
					<@checkBox labelFor='export_binary' labelKey='#i18n{forms.createEntry.labelExportBinaryComment}' name='export_binary' id='export_binary' value='1' />
					<@tag color='warning'>#i18n{forms.createEntry.labelExportBinaryWarning}</@tag>
				</@formGroup>
				<@formGroup labelFor='only_display_in_back' labelKey='#i18n{forms.createEntry.labelOnlyDisplayBack}'>
					<@checkBox name='only_display_in_back' id='only_display_in_back' value='1' />
				</@formGroup>
				<@formGroup labelFor='editable_back' labelKey='#i18n{forms.createEntry.labelEditableInBO}'>
					<@checkBox name='editable_back' id='editable_back' value='1' />
				</@formGroup>
				<@formGroup labelFor='used_in_correct_form_response' labelKey='#i18n{forms.createEntry.labelResponsesCorrection}' helpKey='#i18n{forms.createEntry.labelResponsesCorrectionComment}'>
					<@checkBox name='used_in_correct_form_response' id='used_in_correct_form_response' value='1' checked=false />
				</@formGroup>
				<@formGroup labelFor='used_in_complete_form_response' labelKey='#i18n{forms.createEntry.labelResponsesComplete}' helpKey='#i18n{forms.createEntry.labelResponsesCompleteComment}'>
					<@checkBox name='used_in_complete_form_response' id='used_in_complete_form_response' value='1' checked=false />
				</@formGroup>
				<@formGroup labelFor='exportable' labelKey='#i18n{forms.createEntry.labelResponsesExportable}' helpKey='#i18n{forms.createEntry.labelResponsesExportableComment}'>
					<@checkBox name='exportable' id='exportable' value='1' checked=false />
				</@formGroup>
				<@formGroup labelFor='css_class' labelKey='#i18n{forms.createEntry.labelCSSClass}' helpKey='#i18n{forms.createEntry.labelCSSClassComment}'>
					<@input type='text' name='css_class' id='css_class' value='${entry.CSSClass!}' maxlength=255 />
				</@formGroup>
				<@formGroup>
					<@button type='submit' name='action_createQuestion' buttonIcon='save' title='#i18n{forms.modifyEntry.buttonSave}' showTitleXs=false size='' />
					<@button type='submit' name='action_createQuestionAndManageEntries' buttonIcon='check' title='#i18n{forms.creatyEntry.buttonApply}' showTitleXs=false size='' />
					<@aButton href='jsp/admin/plugins/forms/ManageQuestions.jsp?id_step=${step.id}' buttonIcon='close' title='#i18n{forms.createEntry.buttonCancel}' showTitleXs=false size='' color='btn-secondary' />
				</@formGroup>
			</fieldset>
		</@tform>
	</@boxBody>
</@box>

<#include "/admin/util/editor/editor.ftl" />
<@initEditor />

 