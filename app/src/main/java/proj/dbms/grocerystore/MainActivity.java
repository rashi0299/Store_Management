package proj.dbms.grocerystore;

import android.Manifest;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class MainActivity extends Activity {

    public static boolean isAdmin = false;
    public static MenuItem cart;
    public static MenuItem addCategory;
    boolean allPermissionsGranted = TRUE;
    boolean needExplanation = FALSE;

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        int i;
        boolean b = TRUE;
        for (i = 0; i < grantResults.length; i++) {
            if (grantResults[i] != PackageManager.PERMISSION_GRANTED) b = FALSE;
        }

        // If request is cancelled, the result arrays are empty.
        if (b) {

            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS && GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE > 11000)
                launch();

            else
                Toast.makeText(this, "Please install/update Google Play Services", Toast.LENGTH_LONG).show();

        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseApp.initializeApp(this);

        setContentView(R.layout.activity_main);
        //////////////

        checkPermissions();

    }

    private void checkPermissions() {

        String[] permissions = new String[]{android.Manifest.permission.READ_PHONE_STATE, android.Manifest.permission.READ_SMS, Manifest.permission.READ_EXTERNAL_STORAGE};

        for (int i = 0; i < permissions.length; i++) {
            if (ContextCompat.checkSelfPermission(this,
                    permissions[i])
                    != PackageManager.PERMISSION_GRANTED) {
                allPermissionsGranted = FALSE;

                break;
            }
        }
        for (int i = 0; i < permissions.length; i++) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    permissions[i])) {
                needExplanation = TRUE;
                break;
            }
        }
        //Check if the app has all permissions
        if (allPermissionsGranted == FALSE) {

            // Should we show an explanation?
            if (needExplanation) {
                Toast toast = Toast.makeText(this, "Need permissions for OTP verification", Toast.LENGTH_SHORT);
                toast.show();

                ActivityCompat.requestPermissions(this,
                        permissions,
                        1);

            } else {
                // No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(this,
                        permissions,
                        0);
            }
        } else {

            if (GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this) == ConnectionResult.SUCCESS && GoogleApiAvailability.GOOGLE_PLAY_SERVICES_VERSION_CODE > 11000) {
                launch();

            } else
                Toast.makeText(this, "Please install/update Google Play Services", Toast.LENGTH_LONG).show();
        }

    }

    private void launch() {

        File file = this.getFileStreamPath("myImages");
        if (!file.exists()) {
            file.mkdir();
        }
        file = new File(this.getFileStreamPath("myImages"), "thumbs");
        if (!file.exists()) {
            file.mkdir();
        }

        Firebase.auth = FirebaseAuth.getInstance();

        getFragmentManager().beginTransaction().replace(R.id.fragment, new SplashScreen()).commit();
        Firebase.progressDialog = new ProgressDialog(this);
        Firebase.progressDialog.setCanceledOnTouchOutside(false);
        Firebase.progressDialog.setTitle("Status");
        Firebase.progressDialog.setMessage("Fetching data...");
        //Firebase.progressDialog.show();

        Firebase.LaunchAsyncTask task = new Firebase.LaunchAsyncTask();
        task.execute();

        Firebase.activity = this;

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actionbar_menu, menu);
        cart = menu.findItem(R.id.cart);
        addCategory = menu.findItem(R.id.addCategory);
        if (!isAdmin) {
            cart.setVisible(true);
            addCategory.setVisible(false);

        } else {
            cart.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.myProfile:
                MainProfile mainProfile = new MainProfile();
                getFragmentManager().beginTransaction().replace(R.id.fragment, mainProfile, "MY_PROFILE").addToBackStack("myProfile").commit();
                break;
            case R.id.home:
                HomeScreen homeScreen = new HomeScreen();
                getFragmentManager().beginTransaction().replace(R.id.fragment, homeScreen).addToBackStack(null).commit();
                break;
            case R.id.cart:
                getFragmentManager().beginTransaction().replace(R.id.fragment, new CartFragment()).addToBackStack(null).commit();
                break;
            case R.id.addCategory:
                View dialogView = MainActivity.this.getLayoutInflater().inflate(R.layout.add_category, null);
                final EditText name = dialogView.findViewById(R.id.editName);
                Button add = dialogView.findViewById(R.id.confirm);
                Button cancel = dialogView.findViewById(R.id.dismiss);

                LinearLayout linearLayout = findViewById(R.id.fragment);

                final PopupWindow popup = new PopupWindow(dialogView, 900, 500);
                popup.setBackgroundDrawable(new ColorDrawable(0xff808080));
                popup.setFocusable(TRUE);
                popup.showAtLocation(linearLayout, Gravity.CENTER, 0, 0);
                add.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!name.getText().toString().equals("")) {

                            DBConnection connection = new DBConnection(MainActivity.this);
                            List<String> existingCategories = connection.getCategories();
                            int flag = 0;
                            for (int i = 0; i < existingCategories.size(); i++) {
                                if (existingCategories.get(i).equalsIgnoreCase(name.getText().toString())) {
                                    flag = 1;
                                    break;
                                }
                            }
                            if (flag == 0) connection.addCategory(name.getText().toString());
                            getFragmentManager().beginTransaction().replace(R.id.fragment, new HomeScreen()).commit();
                            popup.dismiss();
                        } else {
                            Toast.makeText(MainActivity.this, "You forgot to enter something", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        popup.dismiss();
                    }
                });
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    public void onBackPressed() {

        MainProfile mainProfile = (MainProfile) getFragmentManager().findFragmentByTag("MY_PROFILE");
        if (mainProfile != null && mainProfile.isVisible()) {

            //HomeScreen homeScreen = new HomeScreen();
            //getFragmentManager().beginTransaction().replace(R.id.fragment,homeScreen).commit();
            this.getFragmentManager().popBackStack("myProfile", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        } else {
            super.onBackPressed();
        }
    }
}
