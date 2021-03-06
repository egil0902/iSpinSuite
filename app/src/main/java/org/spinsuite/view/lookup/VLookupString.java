/*************************************************************************************
 * Product: Spin-Suite (Making your Business Spin)                                   *
 * This program is free software; you can redistribute it and/or modify it           *
 * under the terms version 2 of the GNU General Public License as published          *
 * by the Free Software Foundation. This program is distributed in the hope          *
 * that it will be useful, but WITHOUT ANY WARRANTY; without even the implied        *
 * warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                  *
 * See the GNU General Public License for more details.                              *
 * You should have received a copy of the GNU General Public License along           *
 * with this program; if not, write to the Free Software Foundation, Inc.,           *
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA.                            *
 * For the text or an alternative of this public license, you may reach us           *
 * Copyright (C) 2012-2014 E.R.P. Consultores y Asociados, S.A. All Rights Reserved. *
 * Contributor(s): Yamel Senih www.erpcya.com                                        *
 *************************************************************************************/
package org.spinsuite.view.lookup;


import org.spinsuite.base.R;
import org.spinsuite.util.DisplayType;
import org.spinsuite.util.TabParameter;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;

/**
 * 
 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com Feb 27, 2015, 11:44:15 PM
 *	<li> Selected All on focus
 * 	@see https://adempiere.atlassian.net/browse/SPIN-2
 */
public class VLookupString extends GridField {

	/**
	 * 
	 * *** Constructor ***
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @param context
	 */
	public VLookupString(Context context) {
		super(context);
		init();
	}

	/**
	 * 
	 * *** Constructor ***
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @param context
	 * @param attrs
	 */
	public VLookupString(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	/**
	 * *** Constructor ***
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @param context
	 * @param attrs
	 * @param defStyle
	 */
	public VLookupString(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	/**
	 * With Tab Parameter
	 * *** Constructor ***
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @param context
	 * @param m_field
	 */
	public VLookupString(Context context, InfoField m_field) {
		this(context, m_field, null);
	}
	
	/**
	 * With Tab Parameter
	 * *** Constructor ***
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @param context
	 * @param m_field
	 * @param tabParam
	 */
	public VLookupString(Context context, InfoField m_field, TabParameter tabParam) {
		super(context, m_field, tabParam);
		init();
	}
	
	/**	String Edit Text	*/
	private EditText 		v_String = null;
	/**	Old Value			*/
	private String 			m_OldValue = null;
	

	@Override
	protected void init() {
		v_String = new EditText(getContext());
		v_String.setTextAppearance(getContext(), R.style.TextDynamicTabEditText);
		v_String.setOnFocusChangeListener(new OnFocusChangeListener() {
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
		        //	Listener
				if(!hasFocus) {
					event();
				}
			}
		});
		//	Set Hint
		v_String.setHint(m_field.Name);
		setEnabled(!m_field.IsReadOnly);
		//	Set Display Type
		v_String.setInputType(DisplayType.getInputType(m_field.DisplayType));
		//	Selected All on Focus
		v_String.setSelectAllOnFocus(true);
		//	Set Multi-line
		if(m_field.DisplayType == DisplayType.TEXT
				|| m_field.DisplayType == DisplayType.TEXT_LONG
				|| m_field.DisplayType == DisplayType.MEMO)
			v_String.setSingleLine(false);
		//	Add to View
		addView(v_String);
	}
	
	/**
	 * Listener
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @return void
	 */
	private void event(){
        //	Listener
        if(m_Listener != null)
        	m_Listener.onFieldEvent(this);
	}

	@Override
	public void setValue(Object value) {
		//	Set Old Value
		m_OldValue = v_String.getText().toString();
		//	
		if(value == null
				|| ((String)value).length() <= 0)
			v_String.setText("");
		else
			v_String.setText((String)value);
	}

	@Override
	public Object getValue() {
		return v_String.getText().toString();
	}
	
	@Override
	public Object getOldValue() {
		return m_OldValue;
	}

	@Override
	public boolean isEmpty() {
		return (v_String.getText() == null 
				|| v_String.getText().length() == 0);
	}

	@Override
	public View getChildView() {
		return v_String;
	}
	
	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		v_String.setEnabled(enabled);
		if(enabled) {
			v_String.setTextColor(
					getResources().getColor(R.color.lookup_text_read_write));
		} else {
			v_String.setTextColor(
					getResources().getColor(R.color.lookup_text_read_only));
		}
	}

	@Override
	public String getDisplayValue() {
		return v_String.getText().toString();
	}

	@Override
	public void setValueAndOldValue(Object value) {
		//	
		if(value == null
				|| ((String)value).length() <= 0)
			v_String.setText("");
		else
			v_String.setText((String)value);
		//	Set Old Value
		m_OldValue = v_String.getText().toString();
	}

}
