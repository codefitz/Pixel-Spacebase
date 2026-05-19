/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2016 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.watabou.gltextures;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.watabou.glwrap.Texture;

import java.util.HashMap;

import com.watabou.utils.Logger;

public class TextureCache {

	private static Resources resources;
	private static AssetManager assets;
	
	private static HashMap<Object,SmartTexture> all = new HashMap<>();
	
	// No dithering, no scaling, 32 bits per pixel
	private static BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
	static {
		bitmapOptions.inScaled = false;
		bitmapOptions.inDither = false;
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;
	}

	public static synchronized void setContext( Context context ) {
		Context appContext = context.getApplicationContext();
		if (appContext == null) {
			appContext = context;
		}
		resources = appContext.getResources();
		assets = appContext.getAssets();
	}

	public static SmartTexture createSolid( int color ) {
		final String key = "1x1:" + color;
		
		if (all.containsKey( key )) {
			
			return all.get( key );
			
		} else {
		
			Bitmap bmp = Bitmap.createBitmap( 1, 1, Bitmap.Config.ARGB_8888 );
			bmp.eraseColor( color );
			
			SmartTexture tx = new SmartTexture( bmp );
			all.put( key, tx );
			
			return tx;
		}
	}
	
	public static SmartTexture createGradient( int... colors ) {
		
		final String key = "" + colors;
		
		if (all.containsKey( key )) {
			
			return all.get( key );
			
		} else {
		
			Bitmap bmp = Bitmap.createBitmap( colors.length, 1, Bitmap.Config.ARGB_8888 );
			for (int i=0; i < colors.length; i++) {
				bmp.setPixel( i, 0, colors[i] );
			}
			SmartTexture tx = new SmartTexture( bmp );

			tx.filter( Texture.LINEAR, Texture.LINEAR );
			tx.wrap( Texture.CLAMP, Texture.CLAMP );

			all.put( key, tx );
			return tx;
		}
		
	}
	
	public static void add( Object key, SmartTexture tx ) {
		all.put( key, tx );
	}

	public static SmartTexture get( Object src ) {
		
		if (all.containsKey( src )) {
			
			return all.get( src );
			
		} else if (src instanceof SmartTexture) {
			
			return (SmartTexture)src;
			
		} else {

			SmartTexture tx = new SmartTexture( getBitmap( src ) );
			all.put( src, tx );
			return tx;
		}
		
	}
	
	public static void clear() {
		
		for (Texture txt:all.values()) {
			txt.delete();
		}
		all.clear();
		
	}
	
	public static void reload() {
		for (SmartTexture tx:all.values()) {
			tx.reload();
		}
	}
	
	public static Bitmap getBitmap( Object src ) {
		if (resources == null || assets == null) {
			Logger.e("TextureCache context has not been initialized", new IllegalStateException("Call TextureCache.setContext(...) before loading textures"));
			return null;
		}
		
		try {
			if (src instanceof Integer){
				
				return BitmapFactory.decodeResource(
					resources, (Integer)src, bitmapOptions );
				
			} else if (src instanceof String) {
				
				return BitmapFactory.decodeStream(
					assets.open( (String)src ), null, bitmapOptions );
				
			} else if (src instanceof Bitmap) {
				
				return (Bitmap)src;
				
			} else {
				
				return null;
				
			}
               } catch (Exception e) {

                       Logger.e("Failed to load bitmap from " + src, e);
                       return null;

               }
       }
	
	public static boolean contains( Object key ) {
		return all.containsKey( key );
	}
	
}
