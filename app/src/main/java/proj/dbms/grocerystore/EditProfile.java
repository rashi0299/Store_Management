package proj.dbms.grocerystore;

import android.app.Fragment;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class EditProfile extends Fragment implements View.OnClickListener, OnSuccessListener<UploadTask.TaskSnapshot>, OnCompleteListener<Void> {

    public boolean editMode = FALSE;
    boolean proPicSet = FALSE;
    Uri profilePic;
    private int count = 0;
    private ImageButton profilePicture;
    private Button submit;
    private EditText name;
    private Uri fullSizeProfilePicture;
    private Uri thumbnailProfilePicture;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_creation, container, false);

        profilePicture = rootView.findViewById(R.id.profilePic);
        profilePicture.setOnClickListener(this);

        submit = rootView.findViewById(R.id.submit);
        submit.setOnClickListener(this);

        name = rootView.findViewById(R.id.name);

        if (editMode) {
            proPicSet = TRUE;
            Bitmap thumb = null;
            try {
                File file = new File(getActivity().getFileStreamPath("myImages"), "thumbs/profile.thumb");
                thumb = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), Uri.fromFile(file));
                thumbnailProfilePicture = Uri.fromFile(file);
                file = new File(getActivity().getFileStreamPath("myImages"), "profile.png");
                fullSizeProfilePicture = Uri.fromFile(file);

            } catch (IOException e) {
                e.printStackTrace();
            }
            profilePicture.setImageBitmap(thumb);
            name.setText(Firebase.displayName);
        }


        return rootView;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.submit:
                if (name.getText().toString().equals("")) {
                    if (name.getText().toString().equals(""))
                        Toast.makeText(getActivity(), "Please enter your name", Toast.LENGTH_SHORT).show();
                    if (!proPicSet)
                        Toast.makeText(getActivity(), "Please select a Profile Picture", Toast.LENGTH_SHORT).show();
                } else {
                    uploadFile();
                }
                break;
            case R.id.profilePic:
                proPicSet = FALSE;
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                //intent.setData(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.w("resultCode", resultCode + "");
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            performCrop(data.getData());
        }
        if (requestCode == 2 && resultCode == RESULT_OK && data != null && data.getExtras() != null) {

            Bitmap originalPicture = data.getExtras().getParcelable("data");
            Bitmap thumb = ImageRelated.createThumb(originalPicture, 450, true);
            profilePicture.setImageBitmap(thumb);

            //Save Full Sized Image
            File file = new File(getActivity().getFileStreamPath("myImages"), "profile.png");
            fullSizeProfilePicture = ImageRelated.writeBitmapToFile(originalPicture, file);

            //Save Thumbnail
            file = new File(getActivity().getFileStreamPath("myImages"), "thumbs/profile.thumb");
            thumbnailProfilePicture = ImageRelated.writeBitmapToFile(thumb, file);
            proPicSet = TRUE;
        }
    }

    private void performCrop(Uri picUri) {
        try {
            Log.w("performCrop", "Working");

            Intent cropIntent = new Intent("com.android.camera.action.CROP");
            // indicate image type and Uri
            cropIntent.setDataAndType(picUri, "image/*");
            // set crop properties here
            cropIntent.putExtra("crop", true);
            // indicate aspect of desired crop
            cropIntent.putExtra("aspectX", 1);
            cropIntent.putExtra("aspectY", 1);
            // indicate output X and Y
            cropIntent.putExtra("outputX", 128);
            cropIntent.putExtra("outputY", 128);
            // retrieve data on return
            cropIntent.putExtra("return-data", true);
            cropIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Log.w("performCrop Uri", picUri.toString());
            // start the activity - we handle returning in onActivityResult
            startActivityForResult(cropIntent, 2);
        }
        // respond to users whose devices do not support the crop action
        catch (ActivityNotFoundException anfe) {
            // display an error message
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast toast = Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_SHORT);
            toast.show();
        }
    }


    private void submit(Uri thumbnail, Uri profilePicture) {
        Firebase.progressDialog.setMessage("Submitting...");
        //Firebase.progressDialog.show();
        //TODO
        Firebase.activity.getFragmentManager().beginTransaction().replace(R.id.fragment, new HomeScreen()).commit();
    }

    private void uploadFile() {
        //if there is a file to upload
        if (fullSizeProfilePicture != null) {
            UploadTask task = Firebase.uploadFile(getActivity(), fullSizeProfilePicture, "profilePic", true, false);
            task.addOnSuccessListener(this);
        }
        //if there is not any file
        else {
            Toast.makeText(getActivity(), "No file selected", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
        if (count % 2 == 0) {
            profilePic = taskSnapshot.getDownloadUrl();
            count++;
            Firebase.progressDialog.dismiss();
            UploadTask task = Firebase.uploadFile(getActivity(), thumbnailProfilePicture, "profilePic", true, true);
            task.addOnSuccessListener(this);
        } else {
            count = 0;
            Firebase.progressDialog.dismiss();
            submit(taskSnapshot.getDownloadUrl(), profilePic);
        }
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            Firebase.displayName = name.getText().toString();
            Firebase.progressDialog.dismiss();
            HomeScreen homeScreen = new HomeScreen();
            getActivity().getFragmentManager().beginTransaction().replace(R.id.fragment, homeScreen).commit();
        } else {
            Toast.makeText(getActivity(), task.getException().toString(), Toast.LENGTH_SHORT).show();
        }
    }

}
