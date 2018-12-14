/**
 * Copyright (c) 2018 enerc
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.aeon.aeondaemon.app.Fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import org.aeon.aeondaemon.app.MainActivity;
import org.aeon.aeondaemon.app.R;
import org.aeon.aeondaemon.app.model.Launcher;
import org.aeon.aeondaemon.app.model.SynchronizeThread;

public class LogSlideFragment  extends Fragment {
    private static final String TAG = LogSlideFragment.class.getSimpleName();
    private static long RefreshInterval = 1000;
    private ViewGroup rootView;
    private Context context = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Handler handler = new Handler();

        Runnable r = new Runnable() {
            @Override
            public void run() {
                boolean hasFocus = MainActivity.getmViewPager().getCurrentItem() == MainActivity.FRAGMENT_LOG;
                if (hasFocus) {
                    Launcher launcher = SynchronizeThread.getLauncher();
                    if (launcher == null) {
                        TextView v = (TextView) rootView.findViewById(R.id.logs);
                        v.setText(getString(R.string.daemon_not_running));
                    } else {
                        launcher.updateStatus();
                        TextView v = (TextView) rootView.findViewById(R.id.logs);
                        v.setText(launcher.getLogs());
                    }
                }
                handler.postDelayed(this, RefreshInterval);
            }
        };
        handler.postDelayed(r, RefreshInterval);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = (ViewGroup) inflater.inflate(R.layout.log_fragment, container, false);
        ((TextView) rootView.findViewById(R.id.logs)).setOnLongClickListener(copyListener);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    private View.OnLongClickListener copyListener = new View.OnLongClickListener() {
        public boolean onLongClick(View v) {
            ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("label", ((TextView) v).getText());
            clipboard.setPrimaryClip(clip);

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(context, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(context);
            }
            builder.setTitle("Clipboard")
                    .setMessage(R.string.logs_selected_msg)
                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
            return true;
        }
    };

    @Override
    public void onAttach(Context _context) {
        super.onAttach(_context);
        context = _context;
    }
}