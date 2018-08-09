
package com.appbasic.blendcam.opengl;

import android.opengl.GLES11Ext;
import android.opengl.GLES20;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

public class RenderFbo {
	//---------------------------------------------------------------------
	// MEMBERS
	//---------------------------------------------------------------------
//	private final FloatBuffer mVtxBuf = GlUtil.createSquareVtx();
	private final float[]     mPosMtx = GlUtil.createIdentityMtx();
	private final float[]     mPMtx   = GlUtil.createIdentityMtx();
	private final float[]     mVMtx   = GlUtil.createIdentityMtx();
	private final float[]     mMMtx   = GlUtil.createIdentityMtx();
	private final float[]     mWMtx   = GlUtil.createIdentityMtx();
	private final int[]       mFboId  = new int[1];
	private final int[]       mRboId  = new int[1];
	private final int[]       mTexId  = new int[1];
	private final int mSrfTexW;
	private final int mSrfTexH;
	private final int mSrfTexId;

	private int mProgram         = -1;
	private int maPositionHandle = -1;
	private int maTexCoordHandle = -1;
	private int muPosMtxHandle   = -1;
	private int muTexMtxHandle   = -1;
	private int muSamplerHandle  = -1;



	FloatBuffer pVertex,pTexCoord,pTexCoord1;
	
		 int texture;
	//---------------------------------------------------------------------
	// PUBLIC METHODS
	//---------------------------------------------------------------------
	public RenderFbo(int w, int h, int id) {
		mSrfTexW  = w;
		mSrfTexH  = h;
		mSrfTexId = id;
		//this.viewProjectionMatrix=viewProjectionMatrix;
//this.texture=texture;
		initGL();
		initFBO();
		
	
		 final float FULL_QUAD_COORDS[]={
//	                -1.0f,-1.0f,
//	                 1.0f,-1.0f,
//	                -1.0f,1.0f,
//	                 1.0f,1.0f
				 -1 , 1,
				 
				 -1 , -1,
				 
				  1 ,  1,
				  
				  1 ,-1
				 
				
				 
	                 };
		    
	        float[] ttmp = {   0.0f, 1.0f,
					 0.0f, 0.0f,
					 1.0f, 1.0f, 
					 1.0f, 0.0f  };
	        
	        
	        float[] texture_coordinates1={
					 0.0f, 1.0f,
					 0.0f, 0.0f,
					 1.0f, 1.0f, 
					 1.0f, 0.0f 
			};

	        
	        pVertex = ByteBuffer.allocateDirect(FULL_QUAD_COORDS.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		    pVertex.put ( FULL_QUAD_COORDS );
		    pVertex.position(0);
		    
		    pTexCoord = ByteBuffer.allocateDirect(ttmp.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		    pTexCoord.put ( ttmp );
		    pTexCoord.position(0);
		    
		    pTexCoord1 = ByteBuffer.allocateDirect(texture_coordinates1.length*4).order(ByteOrder.nativeOrder()).asFloatBuffer();
		    pTexCoord1.put ( texture_coordinates1 );
		    pTexCoord1.position(0);
		
		 
	}

	public int getFboTexId() {
		return mTexId[0];
	}

//	public int getFboTexId1() {
//		return mTexId[1];
//	}

	public void draw(final float[] tex_mtx) {
	//	GlUtil.checkGlError("draw_S");
    
//		//TODO: for sample.
//		boolean for_sample = true;
//		if (for_sample) {
//			long time = SystemClock.uptimeMillis() % 7200L;
//			float angle = 0.05f * ((int) time);
//			Matrix.setRotateM(mWMtx, 0, angle, 0, 0, 1);
//			Matrix.multiplyMM(mPosMtx, 0, mWMtx, 0, mMMtx, 0);
//			Matrix.multiplyMM(mPosMtx, 0, mVMtx, 0, mPosMtx, 0);
//			Matrix.multiplyMM(mPosMtx, 0, mPMtx, 0, mPosMtx, 0);
//		} else {
//			Matrix.multiplyMM(mPosMtx, 0, mMMtx, 0, mVMtx, 0);
//			Matrix.multiplyMM(mPosMtx, 0, mPMtx, 0, mPosMtx, 0);
//		}

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboId[0]);

		GLES20.glViewport(0, 0, mSrfTexW, mSrfTexH);

		GLES20.glClearColor(0f, 0f, 0f, 1f);
		GLES20.glClear(GLES20.GL_DEPTH_BUFFER_BIT | GLES20.GL_COLOR_BUFFER_BIT);

		GLES20.glUseProgram(mProgram);

		//mVtxBuf.position(0);
		GLES20.glVertexAttribPointer(maPositionHandle,
				2, GLES20.GL_FLOAT, false, 4*2, pVertex);
		GLES20.glEnableVertexAttribArray(maPositionHandle);

	//	mVtxBuf.position(3);
		GLES20.glVertexAttribPointer(maTexCoordHandle,
				2, GLES20.GL_FLOAT, false, 4*2, pTexCoord);
		GLES20.glEnableVertexAttribArray(maTexCoordHandle);

		GLES20.glUniformMatrix4fv(muPosMtxHandle, 1, false, mPosMtx, 0);
		GLES20.glUniformMatrix4fv(muTexMtxHandle, 1, false, tex_mtx, 0);
		GLES20.glUniform1i(muSamplerHandle, 0);

	//	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		GLES20.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, mSrfTexId);

		GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);

		 GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
		 
		// GLES20.glDeleteFramebuffers(1, mFboId, 0);
//		
		 
	//	 GlUtil.checkGlError("draw_E");
		
		/*GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboId[0]);

		GLES20.glViewport(0, 0, mSrfTexW, mSrfTexH);

		
		float currentTime = (System.nanoTime() - globalStartTime) / 1000000000f;        // Set the particle creation time

//        fireworksDeltaTime = System.currentTimeMillis();                                // Set the fireworks delta time

//        // Each frame add to the shooter
        redParticleShooter.addParticles(particleSystem, currentTime, 1);
//        greenParticleShooter.addParticles(particleSystem, currentTime, 1);
//        blueParticleShooter.addParticles(particleSystem, currentTime, 1);

 //      createFireworks(currentTime);
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texture);
        particleProgram.useProgram();
        
//       GLES20.glUniformMatrix4fv(uMatrixLocation, 1, false, viewProjectionMatrix, 0);
//       GLES20.glUniform1f(uTimeLocation, currentTime);
//       GLES20.glActiveTexture(texture);
//       GLES20.glBindTexture(GL_TEXTURE_2D, texture);
//       GLES20.glUniform1i(uTextureLocation, 1);
        
 //       try{
       
  	 particleProgram.setUniforms(viewProjectionMatrix,currentTime,texture);
 //       }catch(Exception e){
  //.printStackTrace();
   //     } 
    //   
        particleSystem.bindData(particleProgram);
        particleSystem.draw();
	//	particleSystem.draw();
      GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0);
//		
*///
	//	GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 1);
		
	}

	
	
/*	private void deleteFrameBuffer() { 
        GLES20.glDeleteFramebuffers(1, mFboId, 0);
    //    GLES20.glDeleteTextures(1, mExportTexture, 0);
    } */
	
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
				"uniform   mat4 uTexMtx;\n" +
				"varying   vec2 vTexCoord;\n" +
				"void main() {\n" +
				"  gl_Position =   uPosMtx * aPosition;\n" +
				
//"gl_Position = uPosMtx * vec4 ( aPosition.x, aPosition.y, 0.0, 1.0 );\n"+
				"  vTexCoord   =  aTexCoord.xy;\n" +
				"}\n";
		final String fragmentShader =
				//
				"#extension GL_OES_EGL_image_external : require\n" +
				"precision mediump float;\n" +
				"uniform samplerExternalOES uSampler;\n" +
			//	"uniform sampler2D hearts;\n"+
				"varying vec2               vTexCoord;\n" +
				"void main() {\n" +
				"  gl_FragColor = texture2D(uSampler, vTexCoord);\n" +
				"}\n";
		mProgram         = GlUtil.createProgram(vertexShader, fragmentShader);
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		maTexCoordHandle = GLES20.glGetAttribLocation(mProgram, "aTexCoord");
		muPosMtxHandle   = GLES20.glGetUniformLocation(mProgram, "uPosMtx");
		muTexMtxHandle   = GLES20.glGetUniformLocation(mProgram, "uTexMtx");
		muSamplerHandle  = GLES20.glGetUniformLocation(mProgram, "uSampler");

//		float ratio = (float)mSrfTexW/(float)mSrfTexH;
//		Matrix.perspectiveM(mPMtx, 0, 270, ratio, 1, 10);
//
//		Matrix.setLookAtM(mVMtx, 0,
//				0f, 0f, 2f,  // eye
//				0f, 0f, 0f,  // center
//				0f, 1f, 0f); // up
//
//		Matrix.setIdentityM(mMMtx, 0);
//		if (mSrfTexW > mSrfTexH) {
//			Matrix.scaleM(mMMtx, 0, 1f, (float)mSrfTexH/(float)mSrfTexW, 1f);
//		} else {
//			Matrix.scaleM(mMMtx, 0, (float)mSrfTexW/(float)mSrfTexH, 1f, 1f);
//		}

		GlUtil.checkGlError("initGL_E");
	}

	private void initFBO() {
		GlUtil.checkGlError("initFBO_S");

		/*GLES20.glGenFramebuffers(1, mFboId, 0);
		GLES20.glGenRenderbuffers(1, mRboId, 0);
		GLES20.glGenTextures(1, mTexId, 0);

		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRboId[0]);
		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,
				GLES20.GL_DEPTH_COMPONENT16, mSrfTexW, mSrfTexH);

		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboId[0]);
		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, mRboId[0]);

		
		GLES20.glGenTextures(1, mTexId, 0);
		for(int i=0;i<mTexId.length;i++){
			
		//	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		//	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, i);

			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexId[i]);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			
			
		}
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
				mSrfTexW, mSrfTexH, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);
		
		
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mTexId[0], 0);*/
//
	
		
		
		GLES20.glGenFramebuffers(1, mFboId, 0);
		GLES20.glGenRenderbuffers(1, mRboId, 0);
		GLES20.glGenTextures(1, mTexId, 0);

		
		
	//	GLES20.glGenTextures(1, mTexId, 0);
		for(int i=0;i<mTexId.length;i++){
			
			
			GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRboId[i]);
			GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,
					GLES20.GL_DEPTH_COMPONENT16, mSrfTexW, mSrfTexH);

			GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboId[i]);
			GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
					GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, mRboId[i]);

			
		//	GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
		//	GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, i);

			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexId[i]);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

			GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGB,
					mSrfTexW, mSrfTexH, 0, GLES20.GL_RGB, GLES20.GL_UNSIGNED_BYTE, null);
			
			
			GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
					GLES20.GL_COLOR_ATTACHMENT0, GLES20.GL_TEXTURE_2D, mTexId[i], 0);
			
		}
	
		
		
		
	/*	GLES20.glActiveTexture(GLES20.GL_TEXTURE1);
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTexId[1]);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE);

		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
				mSrfTexW, mSrfTexH, 0, GLES20.GL_RGBA, GLES20.GL_UNSIGNED_BYTE, null);
		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER,
				GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_TEXTURE_2D, mTexId[1], 0);
		*/
		
		
//		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, 
//				GLES20.GL_TEXTURE_2D, texture,
//				      0);
		
		
//		GLES20.glBindRenderbuffer(GLES20.GL_RENDERBUFFER, mRboId[1]);
//		GLES20.glRenderbufferStorage(GLES20.GL_RENDERBUFFER,
//				GLES20.GL_DEPTH_COMPONENT16, mSrfTexW, mSrfTexH);
//
//		GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, mFboId[1]);
//		GLES20.glFramebufferRenderbuffer(GLES20.GL_FRAMEBUFFER,
//				GLES20.GL_DEPTH_ATTACHMENT, GLES20.GL_RENDERBUFFER, mRboId[1]);

//		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,mTexId[1]);
//		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA, mSrfTexW, mSrfTexH, 0,
//				GLES20. GL_RGBA,GLES20. GL_UNSIGNED_SHORT_5_6_5, null);
//		GLES20. glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE); 
//		GLES20.    glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE); 
//		GLES20.  glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER,GLES20.GL_LINEAR); 
//		GLES20.   glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
//		
//		GLES20.glFramebufferTexture2D(GLES20.GL_FRAMEBUFFER, GLES20.GL_COLOR_ATTACHMENT0, 
//				GLES20.GL_TEXTURE_2D, mTexId[1],
//				      1);

		if (GLES20.glCheckFramebufferStatus(GLES20.GL_FRAMEBUFFER) !=
				GLES20.GL_FRAMEBUFFER_COMPLETE) {
			throw new RuntimeException("glCheckFramebufferStatus()");
		}

		GlUtil.checkGlError("initFBO_E");
	}
}
