
package com.appbasic.blendcam.opengl;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES20;
import android.opengl.Matrix;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import static com.appbasic.blendcam.opengl.ParticlesRenderer.fixed_environment_program;
import static com.appbasic.blendcam.opengl.ParticlesRenderer.mMVPMatrix;
import static com.appbasic.blendcam.opengl.ParticlesRenderer.mModelMatrix;
import static com.appbasic.blendcam.opengl.ParticlesRenderer.mProjectionMatrix;
import static com.appbasic.blendcam.opengl.ParticlesRenderer.mViewMatrix;

public class RenderScreen {
	//---------------------------------------------------------------------
	// MEMBERS
	//---------------------------------------------------------------------
	private final float[]     mPosMtx = GlUtil.createIdentityMtx();

	private final int mFboTexW;
	private final int mFboTexH;
	private final int mFboTexId;



	private int mProgram         = -1;
	private int cam_progress;
	private int maPositionHandle = -1;
	private int maTexCoordHandle = -1;
	private int muPosMtxHandle   = -1;
	private int muSamplerHandle  = -1;

	private int mScreenW  = -1;
	private int mScreenH  = -1;

	private FloatBuffer vertex_coordinates_beauty;
	                           // One system for all particles
private int images_sequence_program;
	    

		FloatBuffer pVertex,pVertex1,pTexCoord,pTexCoord1,pTexCoord_images,texture1;

	private Context ctx;

	final String vertex_shader =
			"attribute vec4 aPosition;\n" +

					"attribute vec2 aTexCoord;\n" +

					"varying vec2 vTexCoord;\n" +

					"void main() {\n" +
					"vTexCoord = aTexCoord.xy;\n" +
					"gl_Position = aPosition;\n" +

					"}";
	private final String fragment_shader_for_images =
			"precision mediump float;\n" +
					"uniform sampler2D from;\n" +

					"varying vec2  vTexCoord;\n" +
					"void main() {\n" +
					"vec4 fc=texture2D(from,vTexCoord);\n"+
					"  gl_FragColor = fc ;\n" +
					"} \n" ;


	
	//---------------------------------------------------------------------
	// PUBLIC METHODS
	//---------------------------------------------------------------------
	public RenderScreen(int w, int h, int id, Context ctx) {
		mFboTexW  = w;
		mFboTexH  = h;
		mFboTexId = id;
this.ctx=ctx;


	
		initGL();
		
		
		
	        
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

	public void setSize(int w, int h) {
		mScreenW = w;
		mScreenH = h;

		Matrix.setIdentityM(mPosMtx, 0);
		if (mFboTexW * mScreenH < mFboTexH * mScreenW) {
			Matrix.scaleM(mPosMtx, 0,
					(float)mFboTexW*((float)mScreenH/(float)mFboTexH)/(float)mScreenW, 1f, 1f);
		} else {
			Matrix.scaleM(mPosMtx, 0,
					1f, (float)mFboTexH*((float)mScreenW/(float)mFboTexW)/(float)mScreenH, 1f);
		}
	}

	public void draw() {
		if (mScreenW <= 0 || mScreenH <= 0) {
			return;
		}



		GLES20.glViewport(0, 0, mScreenW, mScreenH);


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
//
		GLES20.glEnableVertexAttribArray(maTexCoordHandle);

		Matrix.setIdentityM(mPosMtx, 0);

		GLES20.glUniformMatrix4fv(muPosMtxHandle, 1, false, mPosMtx, 0);
		GLES20.glUniform1i(muSamplerHandle, 0);
		if(!MainActivity.seebarprogresschanged){
			GLES20.glUniform1f(cam_progress,0.5f);
		}
		else{

			GLES20.glUniform1f(cam_progress,MainActivity.progress);
		}
		GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mFboTexId);


		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

		GLES20.glEnable(GLES20.GL_BLEND);
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);

        if(MainActivity.environment){
			fixed_environment_images();
		}
		if(MainActivity.sticker){
			image_animation_on_top();
		}






		GLES20.glDisable(GLES20.GL_BLEND);



	}



    public void image_animation_on_top(){


        GLES20.glUseProgram(images_sequence_program);

        Matrix.setIdentityM(mModelMatrix, 0);


        Matrix.translateM(mModelMatrix, 0, ParticlesRenderer.touchedX, ParticlesRenderer.touchedY, 0.0f);
        Matrix.rotateM(mModelMatrix, 0, ParticlesRenderer.rotate , 0.0f, 0.0f, -1.0f);

        Matrix.scaleM(mModelMatrix,0,ParticlesRenderer.touchedZ,ParticlesRenderer.touchedZ,ParticlesRenderer.touchedZ);

        Matrix.multiplyMM(mMVPMatrix, 0, mViewMatrix, 0, mModelMatrix, 0);  //M * V
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mMVPMatrix, 0);

        int mMVPMatrixHandle = GLES20.glGetUniformLocation(images_sequence_program,"uMVPMatrix");
        int vertexhandle = GLES20.glGetAttribLocation(images_sequence_program, "aPosition");
        int texturehandle = GLES20.glGetAttribLocation(images_sequence_program, "aTexCoord");

        int from_image = GLES20.glGetUniformLocation(images_sequence_program, "from");

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);

        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, ParticlesRenderer.mTextureIds2[0]);

        GLES20.glUniform1i(from_image, 0);

        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mMVPMatrix, 0);

        GLES20.glVertexAttribPointer(vertexhandle, 2, GLES20.GL_FLOAT, false, 4*2, vertex_coordinates_beauty);

        GLES20.glEnableVertexAttribArray(vertexhandle);

        GLES20.glVertexAttribPointer(texturehandle, 2, GLES20.GL_FLOAT, false, 4*2,pTexCoord_images);

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

        GLES20.glVertexAttribPointer(texturehandle, 2, GLES20.GL_FLOAT, false, 4*2,pTexCoord_images);

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

		//String beauty_shader;
		mProgram         = GlUtil.createProgram(vertexShader, fragmentShader);
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		maTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
		muPosMtxHandle   = GLES20.glGetUniformLocation(mProgram, "uPosMtx");
		muSamplerHandle  = GLES20.glGetUniformLocation(mProgram, "uSampler");
		cam_progress =  GLES20.glGetUniformLocation(mProgram,"progress");

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
}
