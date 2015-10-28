/*
 *      Copyright (C) 2015 Kevin Haines
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *      You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *      See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 */

package com.swiftkaytech.findme.views.tagview;

import android.graphics.Color;

public class Constants {
	
	
	//use dp and sp, not px
	
	//----------------- separator TagView-----------------//
	public static final float DEFAULT_LINE_MARGIN = 5;
	public static final float DEFAULT_TAG_MARGIN = 5;
	public static final float DEFAULT_TAG_TEXT_PADDING_LEFT = 8;
	public static final float DEFAULT_TAG_TEXT_PADDING_TOP = 5;
	public static final float DEFAULT_TAG_TEXT_PADDING_RIGHT = 8;
	public static final float DEFAULT_TAG_TEXT_PADDING_BOTTOM = 5;
	
	public static final float LAYOUT_WIDTH_OFFSET = 2;
	
	//----------------- separator Tag Item-----------------//
	public static final float DEFAULT_TAG_TEXT_SIZE = 14f;
	public static final float DEFAULT_TAG_DELETE_INDICATOR_SIZE = 14f;
	public static final float DEFAULT_TAG_LAYOUT_BORDER_SIZE = 0f;
	public static final float DEFAULT_TAG_RADIUS = 100;
	public static final int DEFAULT_TAG_LAYOUT_COLOR = Color.parseColor("#00BFFF");
	public static final int DEFAULT_TAG_LAYOUT_COLOR_PRESS = Color.parseColor("#88363636");
	public static final int DEFAULT_TAG_TEXT_COLOR = Color.parseColor("#ffffff");
	public static final int DEFAULT_TAG_DELETE_INDICATOR_COLOR = Color.parseColor("#ffffff");
	public static final int DEFAULT_TAG_LAYOUT_BORDER_COLOR = Color.parseColor("#ffffff");
	public static final String DEFAULT_TAG_DELETE_ICON = "×";
	public static final boolean DEFAULT_TAG_IS_DELETABLE = false;
}
