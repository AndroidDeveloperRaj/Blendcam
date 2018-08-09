package com.appbasic.blendcam.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.opengl.GLES11Ext;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLUtils;
import android.opengl.Matrix;
import android.os.Environment;
import android.util.Log;

//import com.ab.videoshoot.Utils;

import com.appbasic.blendcam.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.GL_DEPTH_BUFFER_BIT;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glEnable;
import static android.opengl.GLES20.glViewport;

//import android.util.Log;


/**
 * Main renderer class.
 */
public class ParticlesRenderer implements GLSurfaceView.Renderer,SurfaceTexture.OnFrameAvailableListener{

	public static File f;
	private File file;
	   int selectImagePosition=0;
	int selectImagePosition1=0;
	public static int[] mTextureIds2;
	public static int[] mTextureIds1;


	   int i=0;
	SurfaceTexture mSrfTex;
	private Camera mCamera       = null;

public static int images_sequence_program,fixed_environment_program;
   
	  Context context;
	  private final float[]    mTexMtx     = GlUtil.createIdentityMtx();
	

    private FloatBuffer pVertex;
    private FloatBuffer pTexCoord,texture1,vertex_coordinates_beauty;
 

    RenderFbo mRenderFbo;
	RenderScreen mRenderScreen;
	RenderSrfTex mRenderSrfTex;

	public static  float[] mModelMatrix = new float[16];
	public  static float[] mViewMatrix = new float[16];
	public static float[] mProjectionMatrix = new float[16];
	public static float[] mMVPMatrix = new float[16]; //combined matrix!

	public static float touchedX = 0;
	public static float touchedY = 0;
	public  static float touchedZ = 1f;
	public static  float rotate;
	
	private int            mSrfTexId     = -1;
	private int             mFboTexId     = -1;


	public ParticlesRenderer(Context ctx, CameraView view){
		context = ctx;
		 float[] ttmp = {   0.0f, 0.0f,
				 0.0f, 1.0f ,
				 1.0f, 0.0f,
				  1.0f, 1.0f, };
		 
			float[] texture_coordinates1={
					 0.0f, 1.0f,
					 0.0f, 0.0f,
					 1.0f, 1.0f, 
					 1.0f, 0.0f 
			};

		    
		    final float FULL_QUAD_COORDS[]={
//	                -1.0f,-1.0f,
//	                 1.0f,-1.0f,
//	                -1.0f,1.0f,
//	                 1.0f,1.0f

					-1.0f, 1.0f,
					-1.0f, -1.0f,
					1.0f, 1.0f,
					1.0f, -1.0f
	                 };

		float[] beauty_vertex={

				-0.4f,  0.4f,
				-0.4f, -0.4f,
				0.4f,   0.4f,
				0.4f,  -0.4f,
		};

		    pVertex = ByteBuffer.allocateDirect(FULL_QUAD_COORDS.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		    pVertex.put ( FULL_QUAD_COORDS );
		    pVertex.position(0);
		    
		    pTexCoord = ByteBuffer.allocateDirect(ttmp.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		    pTexCoord.put ( ttmp );
		    pTexCoord.position(0);
		    
		    texture1 = ByteBuffer.allocateDirect(texture_coordinates1.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		    texture1.put ( texture_coordinates1 );
		    texture1.position(0);


		vertex_coordinates_beauty = ByteBuffer.allocateDirect(beauty_vertex.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertex_coordinates_beauty.put ( beauty_vertex );
		vertex_coordinates_beauty.position(0);

	}
	
	
	
	
	    
	    public void save_image(){
			  
			 Bitmap bm = readPixels(MainActivity.screenWidth,MainActivity.screenHeight);
			  // bm=flip_Bitmap(bm);
			  if(bm!=null){
			  f = new File(file.getAbsolutePath(), String
						.valueOf(System.currentTimeMillis())
						+ "Camera.jpg");


				if (!f.exists()) {
					try {
						f.createNewFile();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}    
				}

				try {
					bm.compress(Bitmap.CompressFormat.JPEG, 100,
							new FileOutputStream(f));

			
				
					
				//	refreshGallery(f);

					
				} catch (FileNotFoundException e) {
					
					e.printStackTrace();
				}
				finally{
				//	Video_Activity.takesnap=false;
				}
			  }
		  }
	    

	 
	
	
	public void setCamera(Camera camera) {
		mCamera = camera;
	}

	
	 public void setRecorder(MyRecorder recorder) {
			synchronized(this) {
				if (recorder != null) {
					mRenderSrfTex = new RenderSrfTex(
							MainActivity.screenWidth,MainActivity.screenHeight,
							mFboTexId, recorder);
				} else {
					mRenderSrfTex = null;
				}
				
			}
		}




	public static Bitmap readPixels(int width, int height) {
		ByteBuffer PixelBuffer = ByteBuffer.allocateDirect(4 * width * height);
		PixelBuffer.position(0);
		GLES20.glReadPixels(0, 0, width, height, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, PixelBuffer);

		Bitmap bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		PixelBuffer.position(0);
		bmp.copyPixelsFromBuffer(PixelBuffer);


		Bitmap bmp1=flip_Bitmap(bmp);

		return bmp1;
	}


	public static Bitmap flip_Bitmap(Bitmap bmp){
		android.graphics.Matrix flip = new android.graphics.Matrix();
		flip.preScale(1f, -1f);
		bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), flip, true);
		bmp= Bitmap.createScaledBitmap(bmp,  MainActivity.screenWidth,   MainActivity.screenHeight, true);

		return bmp;
	}

	@Override
	public void onDrawFrame(GL10 gl) {

		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);



		mSrfTex.updateTexImage();
		mSrfTex.getTransformMatrix(mTexMtx);
		mRenderFbo.draw(mTexMtx);



        mRenderScreen.draw();
		load_Bitmap_sequence_of_images_for_environment();
		load_sequence_of_images_for_stickers();
		if(MainActivity.takesnapnow){
			save_image();
			MainActivity.takesnapnow=false;
		}

		if (mRenderSrfTex != null) {
			mRenderSrfTex.draw();
		}


	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		glViewport(0, 0, width, height);
		Matrix.setLookAtM(mViewMatrix, 0,
				0.0f, 0.0f, 1.0f, //eye's location
				0.0f, 0.0f, -1.0f, //direction we're looking at
				0.0f, 1.0f, 0.0f //direction that is "up" from our head
		); //this gets compiled into the proper matrix automatically



		//Set Projection Matrix. We will talk about this more in the future
		final float ratio = (float) width / height; //aspect ratio
		final float left = -ratio;
		final float right = ratio;
		final float bottom = -1.0f;
		final float top = 1.0f;
		final float near = 1.0f;
		final float far = 50.0f;
		Matrix.frustumM(mProjectionMatrix, 0, left, right, bottom, top, near, far);





		mRenderFbo = new RenderFbo(
				width, height,mSrfTexId);
		mFboTexId = mRenderFbo.getFboTexId();

		mRenderScreen = new RenderScreen(
				width,height, mFboTexId,context);
		mRenderScreen.setSize(width, height);

	//	load_Bitmap();

		load_Bitmap_sequence_of_images_for_environment();
		load_sequence_of_images_for_stickers();


	}

	public void load_Bitmap_sequence_of_images_for_environment() {

		if(mTextureIds1!=null){
			GLES20.glDeleteTextures(1,mTextureIds1,0);
			mTextureIds1=null;
		}

		if (mTextureIds1 == null) {
			mTextureIds1 = new int[1];
			GLES20.glGenTextures(1, mTextureIds1, 0);
			for (int textureId : mTextureIds1) {
				// Set texture attributes.
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
						GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
						GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
						GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
						GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			}
		}


		if(Utils.old_photo_movie.length != selectImagePosition){

			if (Utils.old_photo_movie[selectImagePosition] != -1) {
				// Log.e("count","value" + selectImagePosition);

		 	Bitmap	one = BitmapFactory.decodeResource(context.getResources(),  Utils.old_photo_movie[selectImagePosition]);

				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureIds1[0]);
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, one, 0);
				one.recycle();
			}

			selectImagePosition = selectImagePosition + 1;

		}else {

			selectImagePosition = 0;

			if(Utils.old_photo_movie.length != selectImagePosition){

				if (Utils.old_photo_movie[selectImagePosition] != -1) {
					Bitmap	one = BitmapFactory.decodeResource(context.getResources(), Utils.old_photo_movie[selectImagePosition]);
					GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIds1[0]);
					GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, one, 0);
					one.recycle();
				}
			}
		}





	}




	public void load_sequence_of_images_for_stickers(){


		if(mTextureIds2!=null){
			GLES20.glDeleteTextures(1,mTextureIds2,0);
			mTextureIds2=null;
		}

		if (mTextureIds2 == null) {
			mTextureIds2 = new int[1];
			GLES20.glGenTextures(1, mTextureIds2, 0);
			for (int textureId : mTextureIds2) {
				// Set texture attributes.
				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId);
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
						GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
						GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
						GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
				GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
						GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);
			}
		}


		if(Utils.images.length != selectImagePosition1){

			if (Utils.images[selectImagePosition1] != -1) {
				// Log.e("count","value" + selectImagePosition);

				Bitmap	one = BitmapFactory.decodeResource(context.getResources(),  Utils.images[selectImagePosition1]);

				GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTextureIds2[0]);
				GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, one, 0);
				one.recycle();
			}

			selectImagePosition1 = selectImagePosition1 + 1;

		}else {

			selectImagePosition1 = 0;

			if(Utils.images.length != selectImagePosition1){

				if (Utils.images[selectImagePosition1] != -1) {
					Bitmap	one = BitmapFactory.decodeResource(context.getResources(), Utils.images[selectImagePosition1]);
					GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureIds2[0]);
					GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, one, 0);
					one.recycle();
				}
			}
		}





	}



	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {



		file = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/Camera Sample");
		if (!file.exists()) {
			file.mkdirs();
		}


		 final String vertex_shader =
				"attribute vec4 aPosition;\n" +

						"attribute vec2 aTexCoord;\n" +

						"varying vec2 vTexCoord;\n" +

						"void main() {\n" +
						"vTexCoord = aTexCoord.xy;\n" +
						"gl_Position = aPosition;\n" +

						"}";

		final String vertexShader_for_images =
				"uniform mat4 uMVPMatrix; \n" +
						"attribute vec4 aPosition;\n" +
						"attribute vec4 aTexCoord;\n" +
						"varying   vec2 vTexCoord;\n" +
						"void main() {\n" +
						"  vTexCoord =aTexCoord.xy;\n" +
						//	" gl_Position = vec4 ( aPosition.x, aPosition.y, 0.0, 1.0 );\n" +
						"  gl_Position = uMVPMatrix * aPosition;" +


						"}\n";


		 final String fragment_shader_for_images =
				"precision mediump float;\n" +
						"uniform sampler2D from;\n" +

						"varying vec2  vTexCoord;\n" +
						"void main() {\n" +
						"vec4 fc=texture2D(from,vTexCoord);\n"+
						"  gl_FragColor = fc ;\n" +
						"} \n" ;

        
        int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);
		mSrfTexId = textures[0];

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mSrfTexId);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES11Ext.GL_TEXTURE_EXTERNAL_OES,
				GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

		mSrfTex = new SurfaceTexture(mSrfTexId);
		mSrfTex.setOnFrameAvailableListener(this);
		try {
			mCamera.setPreviewTexture(mSrfTex);
		} catch (IOException t) {
		}
//		synchronized(this) {
//			updateSurface = false;
//		}
		mCamera.startPreview();



        fixed_environment_program = GlUtil.createProgram(vertex_shader,fragment_shader_for_images);
		images_sequence_program= GlUtil.createProgram(vertexShader_for_images,fragment_shader_for_images);



    }




   
    
    public void onFrameAvailable(SurfaceTexture surface) {

	}

}
