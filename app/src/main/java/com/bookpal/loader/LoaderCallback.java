package com.bookpal.loader;

import android.app.Activity;
import android.app.LoaderManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.net.ConnectivityManager;
import android.os.Bundle;

import com.bookpal.activity.MainActivity;
import com.bookpal.model.Model;
import com.bookpal.model.Request;
import com.bookpal.parser.Parser;
import com.bookpal.utility.AppConstants;
import com.bookpal.utility.SharedPreference;
import com.bookpal.utility.Utility;

import java.net.HttpURLConnection;

public class LoaderCallback implements LoaderManager.LoaderCallbacks<Model> {
    public static ProgressDialog pd;
    private Request request;
    private Activity activity;
    DialogInterface.OnCancelListener mOnCancelListener = new DialogInterface.OnCancelListener() {
        @Override
        public void onCancel(DialogInterface dialog) {
            activity.getLoaderManager().destroyLoader(request.getId());
        }
    };
    private Parser parser;
    private APICaller apiCaller;

    public LoaderCallback(Activity activity, Parser parser) {
        this.activity = activity;
        this.parser = parser;
    }

    public void setServerResponse(APICaller apiCaller) {
        this.apiCaller = apiCaller;
    }

    @Override
    public void onLoaderReset(Loader<Model> modelLoader) {
        modelLoader = null;
    }

    @Override
    public void onLoadFinished(Loader<Model> modelLoader, Model model) {
        dismissDialog();
        activity.getLoaderManager().destroyLoader(request.getId());
        onResponseFromServer(model);
    }

    public void onResponseFromServer(Model model) {
        // storing is account verified in shared preferences
        if (!SharedPreference.getBoolean(activity, AppConstants.PREF_KEY_IS_ACCOUNT_VERIFIED))
            SharedPreference.setBoolean(activity, AppConstants.PREF_KEY_IS_ACCOUNT_VERIFIED, model.isAccountVerified());
        if (model.getHttpStatusCode() == HttpURLConnection.HTTP_UNAUTHORIZED) {
            //showMsg(model.getMessage());
            SharedPreference.clearLoggedInInfo(activity);
            Intent startOverIntent = new Intent(activity, MainActivity.class);
            startOverIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            activity.startActivity(startOverIntent);
        }
        apiCaller.onComplete(model);
    }

    @Override
    public Loader<Model> onCreateLoader(int i, Bundle bundle) {
        boolean showDialog = bundle.getBoolean("showDialog", true);
        if (showDialog) {
            // To prevent crash when activity state is finished and app tries to show dialog
            if (activity != null && !activity.isFinishing()) {
                dismissDialog();
                showDialog(activity);

            }
        }
        HttpAsyncTaskLoader httpAsyncTaskLoader = new HttpAsyncTaskLoader(activity, request, parser);
        return httpAsyncTaskLoader;
    }

    public final boolean requestToServer(Request request) {
        /**
         * Checking the network Here. That network connection is available or not.
         */
        if (!hasConnectivity(activity)) {
//            showMsg(activity.getString(R.string.no_internet_connection));
            return false;
        }
        this.request = request;
        Bundle bundle = new Bundle();
        bundle.putBoolean("showDialog", request.isShowDialog());
        activity.getLoaderManager().initLoader(request.getId(), bundle, this);
//        activity.getLoaderManager().restartLoader(request.getId(),bundle,this);
        return true;
    }

    public boolean hasConnectivity(Context context) {
        boolean rc = false;
        if (context != null) {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isAvailable()
                    && cm.getActiveNetworkInfo().isConnected()) {
                rc = true;
            }
        }
        return rc;
    }

    void showDialog(Context context) {
        if (context == null)
            return;
        if (pd != null) {
            dismissDialog();
        }
        try {
            pd = new ProgressDialog(context);
            pd.setMessage(request.getDialogMessage());
            pd.setCancelable(request.isDialogCancelable());
            pd.setIndeterminate(true);
            pd.setCanceledOnTouchOutside(false);
            pd.setOnCancelListener(mOnCancelListener);

            pd.show();
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        }

    }

    void dismissDialog() {
        try {
            if (pd != null && pd.isShowing()) {
                pd.dismiss();
            }
        } catch (final IllegalArgumentException e) {
            // Handle or log or ignore
        } catch (final Exception e) {
            // Handle or log or ignore
        } finally {
            pd = null;
        }
    }
}
