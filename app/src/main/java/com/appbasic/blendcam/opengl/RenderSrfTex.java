
package com.appbasic.blendcam.opengl;

import android.opengl.EGL14;
import android.opengl.EGLContext;
import android.opengl.EGLDisplay;
import android.opengl.EGLSurface;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static android.opengl.GLES20.GL_BLEND;
import static android.opengl.GLES20.GL_ONE;
import static android.opengl.GLES20.glBlendFunc;
import static android.opengl.GLES20.glEnable;
import static com.appbasic.blendcam.opengl.ParticlesRenderer.fixed_environment_program;
import static com.appbasic.blendcam.opengl.ParticlesRenderer.images_sequence_program;

public class RenderSrfTex {
	//---------------------------------------------------------------------
	// MEMBERS
	//---------------------------------------------------------------------

	private final int mFboTexW;
	private final int mFboTexH;
	private final int mFboTexId;
	private final MyRecorder mRecorder;

	private int mProgram         = -1;
	private int maPositionHandle = -1;
	private int maTexCoordHandle = -1;
	private int muSamplerHandle  = -1;
	private EGLDisplay mSavedEglDisplay     = null;
	private EGLSurface mSavedEglDrawSurface = null;
	private EGLSurface mSavedEglReadSurface = null;
	private EGLContext mSavedEglContext     = null;
	
	


	FloatBuffer pVertex,pVertex1,pTexCoord,pTexCoord1,pTexCoord_images,texture1,vertex_coordinates_beauty;


	//---------------------------------------------------------------------
	// PUBLIC METHODS
	//---------------------------------------------------------------------
	public RenderSrfTex(int w, int h, int id, MyRecorder recorder) {
		mFboTexW  = w;
		mFboTexH  = h;
		mFboTexId = id;
		mRecorder = recorder;


		final float FULL_QUAD_COORDS[]={
				-1.0f,-1.0f,
				1.0f,-1.0f,
				-1.0f,1.0f,
				1.0f,1.0f
		};

		float[] ttmp_1 = {

				1.0f, 1.0f,
				1.0f, 0.0f ,
				0.0f, 1.0f,
				0.0f, 0.0f,

		};

		float[] texture_coordinates1={
				0.0f, 1.0f,
				0.0f, 0.0f,
				1.0f, 1.0f,
				1.0f, 0.0f

		};

		float[] ttmp = {   0.0f, 0.0f,
				0.0f, 1.0f ,
				1.0f, 0.0f,
				1.0f, 1.0f, };


		pVertex1 = ByteBuffer.allocateDirect(FULL_QUAD_COORDS.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		pVertex1.put ( FULL_QUAD_COORDS );
		pVertex1.position(0);

		pTexCoord = ByteBuffer.allocateDirect(ttmp_1.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		pTexCoord.put ( ttmp_1 );
		pTexCoord.position(0);


		pTexCoord1 = ByteBuffer.allocateDirect(texture_coordinates1.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		pTexCoord1.put ( texture_coordinates1 );
		pTexCoord1.position(0);


		pTexCoord_images = ByteBuffer.allocateDirect(ttmp.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		pTexCoord_images.put ( ttmp );
		pTexCoord_images.position(0);

		texture1 = ByteBuffer.allocateDirect(texture_coordinates1.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		texture1.put ( texture_coordinates1 );
		texture1.position(0);



	}

	public void draw() {
		saveRenderState();
		{
	//		GlUtil.checkGlError("draw_S");

			if (mRecorder.firstTimeSetup()) {
				mRecorder.makeCurrent();
				initGL();
			} else if(MainActivity.record_start) {
				mRecorder.makeCurrent();
			}

			GLES20.glViewport(0, 0, mFboTexW, mFboTexH);

			GLES20.glClearColor(0f, 0f, 0f, 1f);
			GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);




			GLES20.glUseProgram(mProgram);


			
			GLES20.glVertexAttribPointer(maPositionHandle,
					2, GLES20.GL_FLOAT, false, 4*2, pVertex1);
			GLES20.glEnableVertexAttribArray(maPositionHandle);


			
			if(CameraView.val==true){
			    GLES20.glVertexAttribPointer(maTexCoordHandle, 2, GLES20.GL_FLOAT, false, 4*2, pTexCoord1);
			    }
			    else if(CameraView.val==false){
			    //	texture1=null;
			    	GLES20.glVertexAttribPointer(maTexCoordHandle, 2, GLES20.GL_FLOAT, false, 4*2, pTexCoord);
			    }


			GLES20.glEnableVertexAttribArray(maTexCoordHandle);

			GLES20.glUniform1i(muSamplerHandle, 0);

			GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFboTexId);

			GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

            GLES20.glEnable(GLES20.GL_BLEND);

            GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);


            fixed_environment_images();
            image_animation_on_top();

			GLES20.glDisable(GLES20.GL_BLEND);
			


			mRecorder.swapBuffers();

		}
		restoreRenderState();
	}







	public void image_animation_on_top(){

		//first_program = GlUtil.createProgram ( vertex_shader, fragment_shader);
		GLES20.glUseProgram(ParticlesRenderer.images_sequence_program);

		Matrix.setIdentityM(ParticlesRenderer.mModelMatrix, 0);



		Matrix.translateM(ParticlesRenderer.mModelMatrix, 0, ParticlesRenderer.touchedX, ParticlesRenderer.touchedY, 0.0f);
		Matrix.rotateM(ParticlesRenderer.mModelMatrix, 0, ParticlesRenderer.rotate , 0.0f, 0.0f, -1.0f);

		Matrix.scaleM(ParticlesRenderer.mModelMatrix,0,ParticlesRenderer.touchedZ,ParticlesRenderer.touchedZ,ParticlesRenderer.touchedZ);

		Matrix.multiplyMM(ParticlesRenderer.mMVPMatrix, 0, ParticlesRenderer.mViewMatrix, 0, ParticlesRenderer.mModelMatrix, 0);  //M * V
		Matrix.multiplyMM(ParticlesRenderer.mMVPMatrix, 0, ParticlesRenderer.mProjectionMatrix, 0, ParticlesRenderer.mMVPMatrix, 0);

		int mMVPMatrixHandle = GLES20.glGetUniformLocation(ParticlesRenderer.images_sequence_program,"uMVPMatrix");
		int vertexhandle = GLES20.glGetAttribLocation(ParticlesRenderer.images_sequence_program, "aPosition");
		int texturehandle = GLES20.glGetAttribLocation(ParticlesRenderer.images_sequence_program, "aTexCoord");

		int from_image = GLES20.glGetUniformLocation(ParticlesRenderer.images_sequence_program, "from");
		//	int progress= GLES20.glGetUniformLocation(images_program, "progress");
		//	int resolution= GLES20.glGetUniformLocation(images_program, "resolution");

		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ParticlesRenderer.mTextureIds2[0]);

		GLES20.glUniform1i(from_image, 0);

		GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, ParticlesRenderer.mMVPMatrix, 0);

		GLES20.glVertexAttribPointer(vertexhandle, 2, GLES20.GL_FLOAT, false, 4*2, vertex_coordinates_beauty);

		GLES20.glEnableVertexAttribArray(vertexhandle);

		GLES20.glVertexAttribPointer(texturehandle, 2, GLES20.GL_FLOAT, false, 4*2,pTexCoord);

		GLES20.glEnableVertexAttribArray(texturehandle);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

		GLES20.glDisableVertexAttribArray(vertexhandle);
		GLES20.glDisableVertexAttribArray(texturehandle);
	}



	public void fixed_environment_images(){


		GLES20.glUseProgram(fixed_environment_program);


		int vertexhandle = GLES20.glGetAttribLocation(fixed_environment_program, "aPosition");
		int texturehandle = GLES20.glGetAttribLocation(fixed_environment_program, "aTexCoord");

		int from_image = GLES20.glGetUniformLocation(fixed_environment_program, "from");


		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ParticlesRenderer.mTextureIds1[0]);

		GLES20.glUniform1i(from_image, 0);



		GLES20.glVertexAttribPointer(vertexhandle, 2, GLES20.GL_FLOAT, false, 4*2,pVertex );

		GLES20.glEnableVertexAttribArray(vertexhandle);

		GLES20.glVertexAttribPointer(texturehandle, 2, GLES20.GL_FLOAT, false, 4*2,pTexCoord);

		GLES20.glEnableVertexAttribArray(texturehandle);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

		GLES20.glDisableVertexAttribArray(vertexhandle);
		GLES20.glDisableVertexAttribArray(texturehandle);
	}

	//---------------------------------------------------------------------
	// PRIVATE...
	//---------------------------------------------------------------------
	private void initGL() {
		GlUtil.checkGlError("initGL_S");


		final String vertexShader =
				//
				"attribute vec4 aPosition;\n" +
						"attribute vec4 aTexCoord;\n" +
						"uniform   mat4 uPosMtx;\n" +
						"varying   vec2 vTexCoord;\n" +
						"void main() {\n" +
						//	 "  gl_Position = aPosition;\n"+

						"gl_Position = vec4 ( aPosition.x, aPosition.y, 0.0, 1.0 );\n"+
						"  vTexCoord   = aTexCoord.xy;\n" +
						"}\n";


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
		final String fragmentShader =

				"precision mediump float;\n" +
						"uniform sampler2D uSampler;\n" +
						"uniform sampler2D uSampler1;\n" +
						"varying vec2      vTexCoord;\n" +
						"uniform float progress;\n" +
						"void main() {\n" +
						" vec4 f1 = texture2D(uSampler, vTexCoord);\n" +
						"gl_FragColor = vec4(f1.r, f1.g, f1.b,progress);"+
						"}\n";


		final String vertex_shader =
				"attribute vec4 aPosition;\n" +

						"attribute vec2 aTexCoord;\n" +

						"varying vec2 vTexCoord;\n" +

						"void main() {\n" +
						"vTexCoord = aTexCoord.xy;\n" +
						"gl_Position = aPosition;\n" +

						"}";
		 final String fragment_shader_for_images =
				"precision mediump float;\n" +
						"uniform sampler2D from;\n" +

						"varying vec2  vTexCoord;\n" +
						"void main() {\n" +
						"vec4 fc=texture2D(from,vTexCoord);\n"+
						"  gl_FragColor = fc ;\n" +
						"} \n" ;



		//String beauty_shader;
		mProgram         = GlUtil.createProgram(vertexShader, fragmentShader);
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		maTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
	//	muPosMtxHandle   = GLES20.glGetUniformLocation(mProgram, "uPosMtx");
		muSamplerHandle  = GLES20.glGetUniformLocation(mProgram, "uSampler");

		float[] ttmp = {   0.0f, 0.0f,
				0.0f, 1.0f ,
				1.0f, 0.0f,
				1.0f, 1.0f, };


		final float FULL_QUAD_COORDS[]={

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



		vertex_coordinates_beauty = ByteBuffer.allocateDirect(beauty_vertex.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		vertex_coordinates_beauty.put ( beauty_vertex );
		vertex_coordinates_beauty.position(0);




		fixed_environment_program = GlUtil.createProgram(vertex_shader,fragment_shader_for_images);
		images_sequence_program= GlUtil.createProgram(vertexShader_for_images,fragment_shader_for_images);
		
		GlUtil.checkGlError("initGL_E");
	}

	private void saveRenderState() {
		mSavedEglDisplay     = EGL14.eglGetCurrentDisplay();
		mSavedEglDrawSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_DRAW);
		mSavedEglReadSurface = EGL14.eglGetCurrentSurface(EGL14.EGL_READ);
		mSavedEglContext     = EGL14.eglGetCurrentContext();
	}

	private void restoreRenderState() {
		if (!EGL14.eglMakeCurrent(
				mSavedEglDisplay,
				mSavedEglDrawSurface,
				mSavedEglReadSurface,
				mSavedEglContext)) {
			throw new RuntimeException("eglMakeCurrent failed");
		}
	}
}
