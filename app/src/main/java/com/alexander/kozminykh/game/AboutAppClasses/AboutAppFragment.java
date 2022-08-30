package com.alexander.kozminykh.game.AboutAppClasses;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alexander.kozminykh.game.sounds.ActivitiesControl;
import com.alexander.kozminykh.game.R;

/**
 * Created by Александр on 09.06.2018.
 */

public class AboutAppFragment extends Fragment {
    public static final String TAG = "AboutAppFragment";
    private TextView email;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.about_app_fragment, container, false);
        setHasOptionsMenu(true);
        email = view.findViewById(R.id.email);
        email.setText(Html.fromHtml(getString(R.string.your_string)));
        email.setMovementMethod(LinkMovementMethod.getInstance());

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                Log.i(TAG, "onOptionItemSelected");
                ActivitiesControl.getInstance().goToTheFirstActivity();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

    }

    @Override
    public void onResume() {
        super.onResume();
        ActivitiesControl.getInstance().secondActivityResume(getActivity());
    }

    @Override
    public void onPause() {
        super.onPause();
        ActivitiesControl.getInstance().secondActivityPause(getActivity());
    }


}
