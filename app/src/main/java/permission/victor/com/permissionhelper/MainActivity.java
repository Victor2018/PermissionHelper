package permission.victor.com.permissionhelper;

import android.Manifest;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Arrays;

import permission.victor.com.library.OnPermissionCallback;
import permission.victor.com.library.PermissionHelper;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnPermissionCallback{
    private PermissionHelper permissionHelper;
    private String[] neededPermission;
    private AlertDialog builder;
    private final static String[] MULTI_PERMISSIONS = new String[]{
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private Button mBtnPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        permissionHelper = PermissionHelper.getInstance(this);
        mBtnPermission = (Button) findViewById(R.id.btn_permission);
        mBtnPermission.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_permission :
                permissionHelper
                        .setForceAccepting(false) // default is false. its here so you know that it exists.
                        .request(MULTI_PERMISSIONS);
                break;
        }
    }

    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {
        Log.d("onPermissionGranted", "Permission(s) " + Arrays.toString(permissionName) + " Granted");
    }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) {
        Log.d("onPermissionDeclined", "Permission(s) " + Arrays.toString(permissionName) + " Declined");
    }

    @Override
    public void onPermissionPreGranted(@NonNull String permissionsName) {
        Log.d("onPermissionPreGranted", "Permission( " + permissionsName + " ) preGranted");
    }

    @Override
    public void onPermissionNeedExplanation(@NonNull String permissionName) {
        neededPermission = PermissionHelper.declinedPermissions(this, MULTI_PERMISSIONS);
        StringBuilder builder = new StringBuilder(neededPermission.length);
        if (neededPermission.length > 0) {
            for (String permission : neededPermission) {
                builder.append(permission).append("\n");
            }
        }
        AlertDialog alert = getAlertDialog(neededPermission, builder.toString());
        if (!alert.isShowing()) {
            alert.show();
        }
    }

    @Override
    public void onPermissionReallyDeclined(@NonNull String permissionName) {
        Log.d("ReallyDeclined", "Permission " + permissionName + " can only be granted from settingsScreen");
    }

    @Override
    public void onNoPermissionNeeded() {
        Log.d("onNoPermissionNeeded", "Permission(s) not needed");
    }

    public AlertDialog getAlertDialog(final String[] permissions, final String permissionName) {
        if (builder == null) {
            builder = new AlertDialog.Builder(this)
                    .setTitle("Permission Needs Explanation")
                    .create();
        }
        builder.setButton(DialogInterface.BUTTON_POSITIVE, "Request", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                permissionHelper.requestAfterExplanation(permissions);
            }
        });
        builder.setMessage("Permissions need explanation (" + permissionName + ")");
        return builder;
    }

    @Override
    protected void onDestroy() {
        builder = null;
        permissionHelper = null;
        super.onDestroy();
    }
}
