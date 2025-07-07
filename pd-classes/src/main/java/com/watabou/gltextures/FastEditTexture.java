package com.watabou.gltextures;

import android.opengl.GLES20;

import com.watabou.glwrap.Texture;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class FastEditTexture extends SmartTexture {

    public final IntBuffer pixels;

    public FastEditTexture(int w, int h) {
        this(w, h, null);
    }

    public FastEditTexture(int w, int h, Object cacheKey) {
        super();
        width = w;
        height = h;
        pixels = ByteBuffer
                .allocateDirect(w * h * 4)
                .order(ByteOrder.nativeOrder())
                .asIntBuffer();

        if (cacheKey != null) {
            TextureCache.add(cacheKey, this);
        }
    }

    @Override
    protected void generate() {
        int[] ids = new int[1];
        GLES20.glGenTextures(1, ids, 0);
        id = ids[0];
    }

    @Override
    public void reload() {
        generate();
        update();
    }

    public void update() {
        bind();
        filter(Texture.LINEAR, Texture.LINEAR);
        pixels.position(0);
        GLES20.glTexImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                GLES20.GL_RGBA,
                width,
                height,
                0,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                pixels);
    }

    public void update(int top, int bottom) {
        bind();
        filter(Texture.LINEAR, Texture.LINEAR);
        pixels.position(top * width);
        GLES20.glTexSubImage2D(
                GLES20.GL_TEXTURE_2D,
                0,
                0,
                top,
                width,
                bottom - top,
                GLES20.GL_RGBA,
                GLES20.GL_UNSIGNED_BYTE,
                pixels);
    }
}
