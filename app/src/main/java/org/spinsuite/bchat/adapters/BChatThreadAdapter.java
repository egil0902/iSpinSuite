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
 * Copyright (C) 2012-2015 E.R.P. Consultores y Asociados, S.A. All Rights Reserved. *
 * Contributor(s): Yamel Senih www.erpcya.com                                        *
 *************************************************************************************/
package org.spinsuite.bchat.adapters;

import java.io.File;
import java.util.ArrayList;

import org.spinsuite.base.R;
import org.spinsuite.bchat.util.BCMessageHandle;
import org.spinsuite.bchat.util.BC_ThreadHolder;
import org.spinsuite.bchat.util.DisplayBChatThreadItem;
import org.spinsuite.mqtt.connection.MQTTDefaultValues;
import org.spinsuite.util.AttachmentHandler;
import org.spinsuite.util.Env;
import org.spinsuite.util.ImageCacheLru;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
 *
 */
public class BChatThreadAdapter extends ArrayAdapter<DisplayBChatThreadItem> {

	/**
	 * 
	 * *** Constructor ***
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com 04/02/2014, 17:06:03
	 * @param ctx
	 * @param id_View
	 * @param data
	 * @param p_IsGroup
	 */
	public BChatThreadAdapter(Context ctx, ArrayList<DisplayBChatThreadItem> data, boolean p_IsGroup) {
		super(ctx, R.layout.i_bchat_thread, data);
		this.ctx = ctx;
		this.id_View = R.layout.i_bchat_thread;
		this.data = data;
		this.isGroup = p_IsGroup;
		m_SelectedItems = new SparseBooleanArray();
		inflater = LayoutInflater.from(ctx);
		m_DirectoryApp = Env.getBC_IMG_DirectoryPathName(ctx) + File.separator;
		int memClass = ((ActivityManager)ctx.getSystemService(Context.ACTIVITY_SERVICE)).getMemoryClass();
		int maxSize = 1024 * 1024 * memClass / 8;
		m_ImageCache = new ImageCacheLru(maxSize);
		//	Get Image Size
		loadDefaultValues();
	}

	/**
	 * Load Defaul Values for Adapter
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @return void
	 */
	private void loadDefaultValues() {
		//	Get Image Size
		m_ImageWidth = ctx.getResources().getDimensionPixelSize(R.dimen.bc_thread_imageView_layout_width);
		m_ImageHeight = ctx.getResources().getDimensionPixelSize(R.dimen.bc_thread_imageView_layout_height);
		m_TextViewMaxWidth = ctx.getResources().getDimensionPixelSize(R.dimen.bc_thread_textView_thread_max_size);
	}

	/**	Context						*/
	private Context 								ctx;
	/**	Data						*/
	private ArrayList<DisplayBChatThreadItem> 		data = new ArrayList<DisplayBChatThreadItem>();
	/**	Backup						*/
	private ArrayList<DisplayBChatThreadItem> 		originalData;
	/**	Identifier of View			*/
	private int 									id_View;
	/**	Selected Items IDs			*/
	private SparseBooleanArray 						m_SelectedItems;
	/**	Inflater					*/
	private LayoutInflater 							inflater;
	/**	Is Group					*/
	private boolean 								isGroup = false;
	/**	Directory by Default		*/
	private String									m_DirectoryApp = null;
	/**	Images Cache				*/
	private ImageCacheLru							m_ImageCache = null;
	/**	Default Image Size			*/
	private int										m_ImageWidth = 0;
	private int										m_ImageHeight = 0;
	/**	Max Text View Size			*/
	private int										m_TextViewMaxWidth = 0;
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		final BC_ThreadHolder msgHolder;
		DisplayBChatThreadItem diti = data.get(position);
		//	Image Key
		String imageKey = m_DirectoryApp + diti.getFileName();
		//	Size
		int desiredWidth = 0;
		boolean isImage = false;
		//	Inflate
		if(view == null) {
			msgHolder = new BC_ThreadHolder();
			view = inflater.inflate(id_View, null);
			msgHolder.ll_MainMessage = (LinearLayout) view.findViewById(R.id.ll_MainMessage);
			msgHolder.ll_Message = (LinearLayout) view.findViewById(R.id.ll_Message);
			msgHolder.rl_Conversation = (RelativeLayout) view.findViewById(R.id.rl_Conversation);
			msgHolder.tv_Conversation = (TextView) view.findViewById(R.id.tv_Conversation);
			msgHolder.tv_Time = (TextView)view.findViewById(R.id.tv_Time);
			msgHolder.tv_UserName = (TextView)view.findViewById(R.id.tv_UserName);
			//	
			view.setTag(msgHolder);
		} else {
			msgHolder = (BC_ThreadHolder) view.getTag();
		}
		//	Parameters
		LinearLayout.LayoutParams params = 
				(LinearLayout.LayoutParams) msgHolder.ll_Message.getLayoutParams();

		//	Set Conversation
		msgHolder.tv_Conversation.setText(diti.getText());

		//	Set Time
		msgHolder.tv_Time.setText(diti.getTimeAsString());
		msgHolder.tv_UserName.setText(diti.getUserName());
		//	Get Size from text
		msgHolder.ll_Message.measure(0, 0);
		msgHolder.tv_Conversation.measure(0, 0);
		msgHolder.tv_UserName.measure(0, 0);
		msgHolder.tv_Time.measure(0, 0);
		//	For Image
		if(diti.getFileName() != null
				&& diti.getFileName().length() > 0) {
			//	Set flag
			isImage = true;
			Bitmap bmimage = m_ImageCache.get(imageKey);
			if(bmimage == null) {
				bmimage = AttachmentHandler.getBitmapFromFile(imageKey, m_ImageWidth, m_ImageHeight);
				//	Re-Check
				if(bmimage != null) {
					m_ImageCache.put(imageKey, bmimage);
					msgHolder.rl_Conversation.setBackgroundDrawable(new BitmapDrawable(ctx.getResources(), bmimage));
				} else {
					msgHolder.rl_Conversation.setBackgroundDrawable(null);
				}
			} else {
				msgHolder.rl_Conversation.setBackgroundDrawable(new BitmapDrawable(ctx.getResources(), bmimage));
			}
		} else {
			msgHolder.rl_Conversation.setBackgroundDrawable(null);
		}
		//	
		desiredWidth = msgHolder.rl_Conversation.getMeasuredWidth();
		//	
		desiredWidth += msgHolder.tv_Conversation.getMeasuredWidth();
		//	Set Visibility
		if(!isGroup
				|| !diti.getType().equals(MQTTDefaultValues.TYPE_IN)) {
			msgHolder.tv_UserName.setVisibility(View.GONE);
		} else {
			msgHolder.tv_UserName.setVisibility(View.VISIBLE);
			//	Change Desired
			desiredWidth = Math.max(desiredWidth, 
					msgHolder.tv_UserName.getMeasuredWidth());
		}
		//	Add Time
		desiredWidth += msgHolder.tv_Time.getMeasuredWidth();
		//	
		int id_att = R.attr.ic_bc_bubble_local;
		int gravity = Gravity.START;
		//	Verify with parent
		//	For Type Message change Background
		if(diti.getType().equals(MQTTDefaultValues.TYPE_IN)) {
			//	Change Position
			msgHolder.tv_UserName.setGravity(Gravity.START);
			//	
			if(m_SelectedItems.get(position)) {
				id_att = R.attr.ic_bc_bubble_remote_selected;
			} else {
				id_att = R.attr.ic_bc_bubble_remote;
			}
		} else {
			if(diti.getStatus().equals(MQTTDefaultValues.STATUS_CREATED)) {
				id_att = R.attr.ic_bc_bubble_local;
			} else if(diti.getStatus().equals(MQTTDefaultValues.STATUS_SENT)) {
				id_att = R.attr.ic_bc_bubble_local_sent;
			} else if(diti.getStatus().equals(MQTTDefaultValues.STATUS_DELIVERED)) {
				id_att = R.attr.ic_bc_bubble_local_delivered;
			} else if(diti.getStatus().equals(MQTTDefaultValues.STATUS_READED)) {
				id_att = R.attr.ic_bc_bubble_local_readed;
			}
			//	
			if(m_SelectedItems.get(position)) {
				id_att = R.attr.ic_bc_bubble_local_selected;
			}
			//	Change Gravity
			gravity = Gravity.END;
		}
		//	
		msgHolder.ll_Message.setBackgroundResource(Env.getResourceID(ctx, id_att));
		//	Change Size
		if(m_TextViewMaxWidth < desiredWidth) {
			desiredWidth = m_TextViewMaxWidth;
		}
		//	Change Width
		if(isImage) {
			params.width = m_ImageWidth;
			params.height = m_ImageHeight;
		} else {
			params.width = desiredWidth;
			params.height = LayoutParams.WRAP_CONTENT;
		}
		//	Change Gravity
		params.gravity = gravity;
		//	Send Status
		if(diti.getType().equals(MQTTDefaultValues.TYPE_IN)
				&& !diti.getStatus().equals(MQTTDefaultValues.STATUS_READED)
				&& !diti.getStatus().equals(MQTTDefaultValues.STATUS_FN_READED)) {
			BCMessageHandle.getInstance(ctx).sendStatusAcknowledgment(
					diti.getSPS_BC_Request_UUID(), diti.getSPS_BC_Message_UUID(), 
					null, MQTTDefaultValues.STATUS_READED);
			//	Change Status for Data
			diti.setStatus(MQTTDefaultValues.STATUS_FN_READED);
			//	Change Data
			data.set(position, diti);
		}
		//	Return
		return view;
	}
	
	@Override
	public Filter getFilter() {
	    return new Filter() {
	        @SuppressWarnings("unchecked")
	        @Override
	        protected void publishResults(CharSequence constraint, FilterResults results) {
	            data = (ArrayList<DisplayBChatThreadItem>) results.values;
	            if (results.count > 0) {
	            	notifyDataSetChanged();
	            } else {
	            	notifyDataSetInvalidated();
	            }  
	        }

	        @Override
	        protected FilterResults performFiltering(CharSequence constraint) {
	            //	Populate Original Data
	        	if(originalData == null)
	            	originalData = data;
	        	//	Get filter result
	        	ArrayList<DisplayBChatThreadItem> filteredResults = getResults(constraint);
	            //	Result
	            FilterResults results = new FilterResults();
	            //	
	            results.values = filteredResults;
	            results.count = filteredResults.size();
	            //	
	            return results;
	        }

	        /**
	         * Search
	         * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com 02/03/2014, 03:19:33
	         * @param constraint
	         * @return
	         * @return ArrayList<DisplayBChatThreadItem>
	         */
	        private ArrayList<DisplayBChatThreadItem> getResults(CharSequence constraint) {
	        	//	Verify
	            if(constraint != null
	            		&& constraint.length() > 0) {
	            	//	new Filter
	            	ArrayList<DisplayBChatThreadItem> filteredResult = new ArrayList<DisplayBChatThreadItem>();
	                for(DisplayBChatThreadItem item : originalData) {
	                    if((item.getText() != null 
	                    		&& item.getText().toLowerCase(Env.getLocate())
	                    					.contains(constraint.toString().toLowerCase(Env.getLocate())))
	                    	|| (item.getUserName() != null 
		                    		&& item.getUserName().toLowerCase(Env.getLocate())
                					.contains(constraint.toString().toLowerCase(Env.getLocate()))))
	                        filteredResult.add(item);
	                }
	                return filteredResult;
	            }
	            //	Only Data
	            return originalData;
	        }
	    };
	}
	
	@Override
	public int getCount() {
		return data.size();
	}
	
	@Override
	public DisplayBChatThreadItem getItem(int position) {
		return data.get(position);
	}
	
	/**
	 * Remove Selections
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @return void
	 */
	public void removeSelection() {
		m_SelectedItems = new SparseBooleanArray();
		notifyDataSetChanged();
	}
	
	/**
	 * Toogle Selection
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @param position
	 * @return void
	 */
	public void toggleSelection(int position) {
		selectView(position, !m_SelectedItems.get(position));
	}

	/**
	 * Select View
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @param position
	 * @param value
	 * @return void
	 */
	public void selectView(int position, boolean value) {
		if (value) {
			m_SelectedItems.put(position, value);
		} else {
			m_SelectedItems.delete(position);
		}
		//	Is Change
		notifyDataSetChanged();
	}

	/**
	 * Get Selected Count
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @return
	 * @return int
	 */
	public int getSelectedCount() {
		return m_SelectedItems.size();
	}

	/**
	 * Get Selected Items
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @return
	 * @return SparseBooleanArray
	 */
	public SparseBooleanArray getSelectedItems() {
		return m_SelectedItems;
	}
	
	@Override
	public void remove(DisplayBChatThreadItem object) {
		data.remove(object);
		notifyDataSetChanged();
	}
	
	/**
	 * Change a Message
	 * @author Yamel Senih, ysenih@erpcya.com, ERPCyA http://www.erpcya.com
	 * @param p_SPS_BC_Message_UUID
	 * @param p_Status
	 * @return void
	 */
	public void changeMsgStatus(String p_SPS_BC_Message_UUID, String p_Status) {
		for(int i = 0; i < data.size(); i++) {
			DisplayBChatThreadItem item = data.get(i);
            if(item.getSPS_BC_Message_UUID() != null
            		&& p_SPS_BC_Message_UUID != null
            		&& item.getSPS_BC_Message_UUID().equals(p_SPS_BC_Message_UUID)) {
            	item.setStatus(p_Status);
            	data.set(i, item);
            	//	Break
            	break;
            }
        }
	}
}
