package proj.dbms.grocerystore;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.File;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.blurry.Blurry;

import static java.lang.Boolean.TRUE;

/**
 * Created by rutvora (www.github.com/rutvora)
 */

public class MainProfile extends Fragment implements View.OnClickListener {

    String profilePicPath = null;
    RelativeLayout relativeLayout;
    CircleImageView profilePic;
    TextView displayName;
    ImageButton editProfile;
    Bitmap originalPic;
    File f;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_new, container, false);

        relativeLayout = rootView.findViewById(R.id.relativeLayout);

        profilePic = rootView.findViewById(R.id.profile_photo);
        profilePic.setOnClickListener(this);
        displayName = rootView.findViewById(R.id.displayName);
        displayName.setText(Firebase.displayName);
        try {
            File file = new File(getActivity().getFileStreamPath("myImages"), "thumbs");
            File[] fileArray = file.listFiles();
            File profileThumb = null;
            for (int i = 0; i < fileArray.length; i++) {

                if (fileArray[i].toString().contains("profile.thumb")) {
                    profileThumb = fileArray[i];

                }

                file = getActivity().getFileStreamPath("myImages");
                fileArray = file.listFiles();
                File profile;
                for (i = 0; i < fileArray.length; i++) {

                    if (fileArray[i].toString().contains("profile.png")) {
                        profile = fileArray[i];
                        profilePicPath = profile.toString();
                    }
                }
            }
            profilePic.setImageDrawable(Drawable.createFromPath(profileThumb.toString()));

        } catch (Exception e) {
            e.printStackTrace();
        }
        editProfile = rootView.findViewById(R.id.editProfile);
        editProfile.setVisibility(View.GONE);
        editProfile.setOnClickListener(this);
        editProfile.setVisibility(View.VISIBLE);
        return rootView;
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        switch (viewId) {
            case R.id.editProfile:
                EditProfile editProfile = new EditProfile();
                editProfile.editMode = TRUE;
                android.app.FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment, editProfile).addToBackStack(null).commit();
                break;
            case R.id.profile_photo:
                popupPicture(profilePicPath);
                break;
        }

    }

    private void popupPicture(String path) {
        View dialogView = getActivity().getLayoutInflater().inflate(R.layout.expanded_image, null);
        ImageView image = dialogView.findViewById(R.id.expandedImage);
        image.setImageDrawable(Drawable.createFromPath(path));
        image.setScaleType(ImageView.ScaleType.CENTER_CROP);

        PopupWindow popup = new PopupWindow(dialogView, 900, 900);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                MainProfile mainProfile = (MainProfile) getFragmentManager().findFragmentByTag("MY_PROFILE");
                getFragmentManager().beginTransaction().detach(mainProfile).attach(mainProfile).commit();
            }
        });
        Blurry.with(getActivity()).radius(10).sampling(1).color(Color.argb(80, 0, 0, 0)).onto(relativeLayout);
        popup.setBackgroundDrawable(new ColorDrawable(0x80000000));
        popup.setFocusable(TRUE);
        popup.showAtLocation(relativeLayout, Gravity.CENTER, 0, 0);
    }
}
