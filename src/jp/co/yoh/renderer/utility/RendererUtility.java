package jp.co.yoh.renderer.utility;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;
import javax.microedition.khronos.opengles.GL11Ext;
import javax.microedition.khronos.opengles.GL11ExtensionPack;

import jp.co.yoh.primitive.Vector;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.opengl.GLU;
import android.opengl.GLUtils;
import android.os.Environment;
import android.util.Log;

public class RendererUtility {

    public static Bitmap getBackBuffer(GL10 gl,int w,int h) {

    	byte GL_b[] 	= new byte[w * h * 4];
    	ByteBuffer GL_ib= ByteBuffer.wrap(GL_b);
    	GL_ib.position(0);
    	gl.glReadPixels(0, 0, w, h,  GL11.GL_RGBA,GL11.GL_UNSIGNED_BYTE, GL_ib);

    	int len = GL_b.length / 4;
    	int[] bmp = new int[len];

    	int a,r,g,b;
    	int yy;

    	int k = 0;
    	yy=h-1;
    	for(int i = 0; i < h; i++)
    	{
    		for(int j = 0; j < w; j++)
    		{
    			r = GL_b[k++] & 0xff;
    			g = GL_b[k++] & 0xff;
    			b = GL_b[k++] & 0xff;
    			a = GL_b[k++] & 0xff;
    			bmp[(yy * w) + j] = a<<24 | r<<16 | g<<8 | b;
    		}
    		yy--;
    	}
    	Bitmap bitmap = Bitmap.createBitmap(bmp, w, h, Bitmap.Config.ARGB_8888);
//        GLUtils.texImage2D(GL11.GL_TEXTURE_2D, 0, bitmap, 0);
//    	bitmap.recycle();
    	return bitmap;
    }

    public static byte[] bmp2data(Bitmap src, Bitmap.CompressFormat format, int quality) {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        src.compress(format, quality, os);
        return os.toByteArray();
    }

    public static boolean saveBitmapToJpgFile(String fileName, Bitmap bmp) {
        byte[] image = bmp2data(bmp, Bitmap.CompressFormat.JPEG, 100);

        String filePath = "/mnt/sdcard/download/" + File.separator + fileName;
        File file = new File(filePath);
        file.getParentFile().mkdir();

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filePath);
            fos.write(image, 0, image.length);
            fos.flush();
            return true;
        }
        catch (Exception e) {
        	e.printStackTrace();
        }
        finally {
            try {
                fos.close();
            }
            catch (IOException e) {}
        }
        return false;
    }


    //スクリーン座標系からオブジェクト座標を求める
    public static Vector calcScreen2ObjectCoord(GL10 gl,int screenHeight,int x, int y) {

    	GL11 gl11 = (GL11)gl;
        float[] model = new float[16];
        gl11.glGetFloatv(GL11.GL_MODELVIEW_MATRIX, model,0);

        float[] project = new float[16];
    	gl11.glGetFloatv(GL11.GL_PROJECTION_MATRIX, project,0);

        int[] view = new int[4];
    	gl11.glGetIntegerv(GL11.GL_VIEWPORT, view,0);

    	float[] xyz = new float[4];

    	GLU.gluUnProject((float)x,(float)(screenHeight - y),0.0f,model,0,project,0,view,0,xyz,0);
    	Vector vector = new Vector();
    	vector.x = xyz[0];
    	vector.y = xyz[1];
    	vector.z = 0.0f;
    	vector.w = 0.0f;
    	return vector;
    }

    public static int loadTexture(GL10 gl,Context context,int resourceID) {
    	if (gl == null || context == null || resourceID == 0) return 0;
        InputStream is = context.getResources().openRawResource(resourceID);
        return loadTexture(gl,is);
    }

    public static int loadTexture(GL10 gl,String fileName) {
    	if (gl == null || fileName == null) return 0;
    	InputStream is = null;
    	try {
    		is = new FileInputStream(fileName);
    	} catch (Exception e) {
        	return 0;
		}
        return loadTexture(gl,is);
    }

    public static int initTexture(GL10 gl) {
    	if (gl == null) return 0;

    	int[] textureID = new int[1];
        gl.glGenTextures(1, textureID, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);	//該当するテクスチャIDを2Dとして紐付け

		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,GL10.GL_REPEAT);	//これを設定しとかないとBindTextureが機能しない
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,GL10.GL_REPEAT);	//これを設定しとかないとBindTextureが機能しない
        gl.glTexParameterx( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR );	//これを設定しとかないとBindTextureが機能しない
        gl.glTexParameterx( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );	//これを設定しとかないとBindTextureが機能しない
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,GL10.GL_REPLACE);

        return textureID[0];
    }

    public static int initTexture(GL10 gl,int width,int height) {
    	if (gl == null || width <= 0 || height <= 0) return 0;

    	int textureID = initTexture(gl);

        gl.glTexImage2D( GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height,
                      0, GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null );

        return textureID;
    }

    public static int initOffscreenTexture(GL10 gl,int width,int height) {
    	if (gl == null || width <= 0 || height <= 0) return 0;

    	GL11ExtensionPack gl11e = (GL11ExtensionPack) gl;

    	int[] offscreenTexture = new int[1];
    	gl.glGenTextures(1, offscreenTexture,0);
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, offscreenTexture[0]);
    	gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height, 0,  GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
    	gl11e.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D, offscreenTexture[0], 0);

        return offscreenTexture[0];
    }

    public static int loadTexture(GL10 gl,InputStream is) {
    	if (gl == null || is == null) return 0;

    	int[] textureID = new int[1];
        gl.glGenTextures(1, textureID, 0);
		gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID[0]);	//該当するテクスチャIDを2Dとして紐付け

		gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_S,GL10.GL_REPEAT);	//これを設定しとかないとBindTextureが機能しない
        gl.glTexParameterx(GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_WRAP_T,GL10.GL_REPEAT);	//これを設定しとかないとBindTextureが機能しない
        gl.glTexParameterx( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MIN_FILTER, GL10.GL_LINEAR );	//これを設定しとかないとBindTextureが機能しない
        gl.glTexParameterx( GL10.GL_TEXTURE_2D, GL10.GL_TEXTURE_MAG_FILTER, GL10.GL_LINEAR );	//これを設定しとかないとBindTextureが機能しない
        gl.glTexEnvf(GL10.GL_TEXTURE_ENV, GL10.GL_TEXTURE_ENV_MODE,GL10.GL_REPLACE);

        Bitmap bitmap;
        try {
            bitmap = BitmapFactory.decodeStream(is);
        } finally {
            try {
                is.close();
            } catch(IOException e) {
            	return 0;
            }
        }
        GLUtils.texImage2D(GL10.GL_TEXTURE_2D, 0, bitmap, 0);	//テクスチャをテクスチャオブジェクトとしてセットする
        bitmap.recycle();
        return textureID[0];
    }

    public static void texture2DChange(GL10 gl,int textureID) {
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, textureID);
    }

    public static int createFrameBuffer( GL10 gl, int width, int height,int[] offscreenTexture)
    {
    	GL11ExtensionPack gl11e = (GL11ExtensionPack) gl;

        int oldFBO[] = new int[1];
        gl.glGetIntegerv(GL11ExtensionPack.GL_FRAMEBUFFER_BINDING_OES, oldFBO,0);

    	//FrameBuffer Init
    	int[] offscreenFBO = new int[1];
        gl11e.glGenFramebuffersOES(1, offscreenFBO,0);
        gl11e.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, offscreenFBO[0]);

        // テクスチャを生成
    	gl.glGenTextures(1, offscreenTexture,0);
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, offscreenTexture[0]);
    	gl.glTexImage2D(GL10.GL_TEXTURE_2D, 0, GL10.GL_RGBA, width, height, 0,  GL10.GL_RGBA, GL10.GL_UNSIGNED_BYTE, null);
    	gl.glBindTexture(GL10.GL_TEXTURE_2D, 0);
    	gl11e.glFramebufferTexture2DOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, GL11ExtensionPack.GL_COLOR_ATTACHMENT0_OES, GL10.GL_TEXTURE_2D, offscreenTexture[0], 0);

        // 深度バッファを作成
    	int[] offscreenDepth = new int[1];
    	gl11e.glGenRenderbuffersOES(1, offscreenDepth,0);
    	gl11e.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, offscreenDepth[0]);
        gl11e.glRenderbufferStorageOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, GL11ExtensionPack.GL_DEPTH_COMPONENT16, width, height);
        gl11e.glBindRenderbufferOES(GL11ExtensionPack.GL_RENDERBUFFER_OES, 0);
        gl11e.glFramebufferRenderbufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, GL11ExtensionPack.GL_DEPTH_ATTACHMENT_OES, GL11ExtensionPack.GL_RENDERBUFFER_OES, offscreenDepth[0]);

        // FramebufferObjectの有効性チェック
        int status = gl11e.glCheckFramebufferStatusOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES);
        if (status != GL11ExtensionPack.GL_FRAMEBUFFER_COMPLETE_OES) {
        	Log.e("RenderUtility","OES is incomplete = " + status);
        	return 0;
        }

        // FBOのbindを復元
        gl11e.glBindFramebufferOES(GL11ExtensionPack.GL_FRAMEBUFFER_OES, oldFBO[0]);

    	return offscreenFBO[0];
	}


}
