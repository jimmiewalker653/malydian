package malydian.app;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.opengl.GLSurfaceView;


public class MainActivity extends Activity {
    private GLSurfaceView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mView = new AppSurfaceView(this);
        setContentView(mView);
    }    
}
