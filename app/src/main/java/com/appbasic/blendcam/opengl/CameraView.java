
package com.appbasic.blendcam.opengl;

import android.content.Context;
import android.graphics.PointF;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

class CameraView extends GLSurfaceView {
	//---------------------------------------------------------------------
	// MEMBERS
	//---------------------------------------------------------------------
//	private final VideoParam mVideoParam = VideoParam.getInstance();


	float touchedX;
	float touchedY;

	float[] lastEvent ;


	float old_dist = 0;
	int number;
	private final int zoom = 1;
	private final int drag = 0;
	private static int move_mode;
	private int mActivePointerId = -1;
	private int mlastPointerId;
	public static PointF mid = new PointF();
	static float rajd = 0f;
	static float newRot = 0f;
	public static   float r1;
	private Camera mCamera   = null;
	private ParticlesRenderer mRenderer;
	private MyRecorder  mRecorder = null;
	int count=0;
	public static boolean val=false;
	public static boolean touch_value=false;
	boolean finger_up=false;
//MainActivity context;
	//---------------------------------------------------------------------
	// PUBLIC METHODS
	//---------------------------------------------------------------------
	public CameraView(Context context) {
		super(context);//
		
		    setEGLContextClientVersion(2);
	
	        setEGLConfigChooser( 8, 8, 8, 8, 16, 0 );

		mRenderer = new ParticlesRenderer(context,this);
		setRenderer(mRenderer);

	}



	@Override
	public void onResume() {
		initCamera(0);
		super.onResume();
	}

	@Override
	public void onPause() {
		quitCamera();
		super.onPause();
	}

	public void startVideo() {
		if (mRecorder == null) {
			mRecorder = new MyRecorder();
			mRecorder.prepareEncoder();
			mRenderer.setRecorder(mRecorder);
		}
	}

	public void stopVideo() {
		if (mRecorder != null) {
			mRecorder.stop();

			mRenderer.setRecorder(null);
            mRecorder = null;
		}
	}

	//---------------------------------------------------------------------
	// PRIVATE...
	//---------------------------------------------------------------------
//	private void init( ) {
//		
//	}

	private void initCamera(int count) {
		if (mCamera == null) {
			if(count==0){
				val=false;
			}
			try {
				mCamera = Camera.open(count);
				Camera.Parameters cp = mCamera.getParameters();
//				cp.setPreviewSize(mVideoParam.mSize.width, mVideoParam.mSize.height);
//				cp.setPreviewFpsRange(
//						mVideoParam.mFpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
//						mVideoParam.mFpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
				mCamera.setParameters(cp);
				mRenderer.setCamera(mCamera);
			} catch (Exception e) {
				e.printStackTrace();
			//	throw new RuntimeException("setup camera");
			}
		}
	}

	
	public void switch_camera(){
		
		count=count+1;
		if(count==1){
		
			quitCamera();
			
			this.onPause();
			 
			initCamera(1);
//			
			this.onResume();
			
			val=true;
		//	mCamera = null;
		//	 mRenderer.setCamera(null);
			//mCamera.release();
//			 mCamera = Camera.open(1);
//			 Camera.Parameters cp = mCamera.getParameters();
//				cp.setPreviewSize(mVideoParam.mSize.width, mVideoParam.mSize.height);
//				cp.setPreviewFpsRange(
//						mVideoParam.mFpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
//						mVideoParam.mFpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
//				mCamera.setParameters(cp);
//		
//				 mRenderer.setCamera(mCamera);
//	
				
		
	//		 mCamera.startPreview();
	//			val=true;
		//	 startCamera(texture);
		}
		if(count==2){
			
	
		quitCamera();
		this.onPause();
		
		initCamera(0);
		   
		this.onResume();
		val=false;
		//	mCamera = null;
//			 mCamera = Camera.open(0);
//			 Camera.Parameters cp = mCamera.getParameters();
//				cp.setPreviewSize(mVideoParam.mSize.width, mVideoParam.mSize.height);
//				cp.setPreviewFpsRange(
//						mVideoParam.mFpsRange[Camera.Parameters.PREVIEW_FPS_MIN_INDEX],
//						mVideoParam.mFpsRange[Camera.Parameters.PREVIEW_FPS_MAX_INDEX]);
//				mCamera.setParameters(cp);
//			 mRenderer.setCamera(mCamera);
	//		 mCamera.startPreview();
		//		val=false;
			count=0;
		}
		
	}
	

	public ParticlesRenderer getRenderer(){
		return mRenderer;
		
	}
	
	private void quitCamera() {
		if (mCamera != null) {
			mCamera.stopPreview();
			mCamera.release();
			mCamera = null;
			mRenderer.setCamera(null);
		}
	}



	@Override
	public boolean onTouchEvent(MotionEvent event) {
		number = event.getPointerCount();

		if (event.getActionMasked() == MotionEvent.ACTION_DOWN) {
			touchedX = event.getX();
			touchedY = event.getY();

			mActivePointerId = event.getPointerId(0);

		} else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN)  // basically the same as getaction(), but for multiple pointers
		{
			if (number >= 2) {
				move_mode = zoom;
				//save value of previous pointer
				mlastPointerId = mActivePointerId;
				// Save the ID of this pointer
				mActivePointerId = event.getPointerId(1);
				old_dist = spacing(event);




				lastEvent = new float[4];
				lastEvent[0] = event.getX(mlastPointerId);
				lastEvent[1] = event.getX(mActivePointerId);
				lastEvent[2] = event.getY(mlastPointerId);
				lastEvent[3] = event.getY(mActivePointerId);
				rajd = rotation(event)-r1;
			}
			if (number < 2) {
				move_mode = drag;
			}

		} else if (event.getActionMasked() == MotionEvent.ACTION_MOVE) {
			if (move_mode == drag)  //only occurs when there is not 2 touch gestures present. Can be altered to allow for more than 2 types of motion events
			{


				mRenderer.touchedX += (touchedX - event.getX()) / -500;
				mRenderer.touchedY += (touchedY - event.getY()) / 500;

				touchedX = event.getX();
				touchedY = event.getY();
			}
			if (move_mode == zoom) {


				float change = spacing(event) - old_dist;
				if (change < 0) {
					mRenderer.touchedZ -= 0.01;
					if (mRenderer.touchedZ < 0.1f)
						mRenderer.touchedZ = 0.1f;
				}
				if (change > 0) {
					mRenderer.touchedZ += 0.01;
					if (mRenderer.touchedZ > 2.5f)
						mRenderer.touchedZ = 2.5f;
				}




				if (lastEvent != null) {
					newRot = rotation(event);
					r1 = newRot - rajd;
					getRenderer().rotate = r1;
				}
			}
		} else if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {

			lastEvent=null;
			final int pointerIndex = event.getActionIndex();

			number = event.getPointerCount() - 1;
			if (number == 1) {

				move_mode = drag;
				final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
				touchedX = event.getX(newPointerIndex);
				touchedY = event.getY(newPointerIndex);
				mActivePointerId = event.getPointerId(newPointerIndex);
			}

		} else if (event.getActionMasked() == MotionEvent.ACTION_UP) {
			mActivePointerId = event.getPointerId(0);

			number = 0;
			move_mode = drag;
		}

		return true;
	}

	private int spacing(MotionEvent event) {
		float x = event.getX(mlastPointerId) - event.getX(mActivePointerId);
		float y = event.getY(mlastPointerId) - event.getY(mActivePointerId);
		return Math.round((float) Math.sqrt(x * x + y * y));
	}


	private float rotation(MotionEvent event) {
		double delta_x = (event.getX(mlastPointerId) - event.getX(mActivePointerId));
		double delta_y = (event.getY(mlastPointerId) - event.getY(mActivePointerId));
		double radians = Math.atan2(delta_y, delta_x);

		return (float) Math.toDegrees(radians);
	}




}
