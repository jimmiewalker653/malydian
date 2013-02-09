package malydian.app;

import android.opengl.GLSurfaceView;
import android.opengl.GLES20;
import android.content.Context;

import java.nio.FloatBuffer;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.egl.EGLConfig;

class AppRenderer implements GLSurfaceView.Renderer {
	
    Triangle mTriangle;
    Square mSquare;

	public void onSurfaceCreated(GL10 unused, EGLConfig config){
		GLES20.glClearColor(0.1f, 0.1f, 0.1f, 1.0f);

		mTriangle = new Triangle();
		mSquare = new Square();
	}
	public void onDrawFrame(GL10 unused){
		GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
		mTriangle.draw();
	}
	public void onSurfaceChanged(GL10 unused, int width, int height){
		GLES20.glViewport(0, 0, width, height);
	}

	public static int loadShader(int type, String shaderCode){

    // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
    // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
    int shader = GLES20.glCreateShader(type);

    // add the source code to the shader and compile it
    GLES20.glShaderSource(shader, shaderCode);
    GLES20.glCompileShader(shader);

    return shader;
}
}

class AppSurfaceView extends GLSurfaceView {
    public AppSurfaceView(Context context) {
        super(context);
        setEGLContextClientVersion(2);
        setRenderer(new AppRenderer());
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }
}

class Triangle {
    private final String vertexShaderCode =
    "attribute vec4 vPosition;" +
    "void main() {" +
    "  gl_Position = vPosition;" +
    "}";

    private final String fragmentShaderCode =
    "precision mediump float;" +
    "uniform vec4 vColor;" +
    "void main() {" +
    "  gl_FragColor = vColor;" +
    "}";
    
    private FloatBuffer vertexBuffer;
   	private final int mProgram; 
   	private int mPositionHandle;
    private int mColorHandle;

    static final int COORDS_PER_VERTEX = 3;
    static float triangleCoords[] = {
        0.0f,   0.622008459f,   0.0f,
        -0.5f,  -0.311004243f,  0.0f,
        0.5f,   -0.311004243f,  0.0f
    };
    private final int vertexCount = triangleCoords.length / COORDS_PER_VERTEX;
    private final int vertexStride = COORDS_PER_VERTEX * 4; // bytes per vertex

    float color[] = { 0.63671875f, 0.7693125f, 0.22265625f, 1.0f}; 
    
    public Triangle() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
                // (number of coordinate values * 4 bytes per float)
                triangleCoords.length * 4);
        // use the device hardware's native byte order
        bb.order(ByteOrder.nativeOrder());

        // create a floating point buffer from the ByteBuffer
        vertexBuffer = bb.asFloatBuffer();
        // add the coordinates to the FloatBuffer
        vertexBuffer.put(triangleCoords);
        // set the buffer to read the first coordinate
        vertexBuffer.position(0);

        int vertexShader = AppRenderer.loadShader(
        	GLES20.GL_VERTEX_SHADER, vertexShaderCode);
        int fragmentShader = AppRenderer.loadShader(
        	GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();
        GLES20.glAttachShader(mProgram, vertexShader);
        GLES20.glAttachShader(mProgram, fragmentShader);
        GLES20.glLinkProgram(mProgram);
    }
    public void draw() {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(mPositionHandle, COORDS_PER_VERTEX,
                                     GLES20.GL_FLOAT, false,
                                     vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // Draw the triangle
        GLES20.glDrawArrays(GLES20.GL_TRIANGLES, 0, vertexCount);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }
}

