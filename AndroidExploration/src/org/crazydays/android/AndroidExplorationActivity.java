/* $Id$ */
package org.crazydays.android;


import org.crazydays.android.sensor.SensorsActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

/**
 * AndroidExplorationActivity
 */
public class AndroidExplorationActivity
    extends Activity
{
    /**
     * On create.
     * 
     * @param state State
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle state)
    {
        super.onCreate(state);
        setContentView(R.layout.main);

        setupSensorsButton();
    }

    /**
     * Setup sensors button.
     */
    protected void setupSensorsButton()
    {
        Button button = (Button) findViewById(R.id.sensorsButton);
        button.setOnClickListener(new OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                startActivity(new Intent(view.getContext(),
                    SensorsActivity.class));
            }
        });
    }
}
