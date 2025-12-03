package com.example.summit.fragments.login;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import com.example.summit.EntrantActivity;
import com.example.summit.R;
import com.example.summit.model.Entrant;
import com.example.summit.model.Organizer;
import com.example.summit.session.Session;
import com.example.summit.OrganizerActivity;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * A {@link Fragment} that serves as the initial entry point for user authentication.
 *
 * This fragment's view ({@code fragment_device_id}) presents a "Continue" button.
 * When clicked, it checks the user's unique {@link Settings.Secure#ANDROID_ID} against
 * the "entrants" and "organizers" collections in Firestore.
 * <ul>
 * <li>If a matching document is found, the user is logged in, their data is
 * loaded into the {@link Session}, and they are redirected to the
 * appropriate activity ({@link EntrantActivity} or {@link OrganizerActivity}).
 * The current (login) activity is then finished.</li>
 * <li>If no match is found, the user is considered new and is navigated to
 * the {@link DetailsFragment} to register.</li>
 * </ul>
 */
public class DeviceIDFragment extends Fragment {

    /**
     * Inflates the layout for this fragment's view.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state.
     * @return The View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_device_id, container, false);
    }

    /**
     * Called immediately after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     * has returned, but before any saved state has been restored in to the view.
     *
     * This method finds the "continue" button and sets its click listener to
     * trigger the {@link #checkUser()} method.
     *
     * @param view The View returned by {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state.
     */
    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Button continueButton = view.findViewById(R.id.continue_button);
        continueButton.setOnClickListener(v -> checkUser());
    }

    /**
     * Checks the local device ID against the Firestore database to authenticate the user.
     *
     * This method retrieves the {@link Settings.Secure#ANDROID_ID} and performs a
     * series of chained Firestore queries:
     * <ol>
     * <li>It first checks the "entrants" collection for the device ID.</li>
     * <li>If found, the user is logged in as an {@link Entrant}, their data is
     * saved to the {@link Session}, and {@link EntrantActivity} is launched.</li>
     * <li>If not found in "entrants", it then checks the "organizers" collection.</li>
     * <li>If found, the user is logged in as an {@link Organizer}, data is
     * saved to the {@link Session}, and {@link OrganizerActivity} is launched.</li>
     * <li>If the device ID is not found in either collection, the user is navigated
     * to {@link DetailsFragment} to begin registration, passing the device ID
     * in the navigation arguments.</li>
     * <li>If any database query fails, a "Login failed" toast is displayed.</li>
     * </ol>
     * After a successful login (as Entrant or Organizer), the current activity is finished.
     */
    private void checkUser() {

        String deviceId = Settings.Secure.getString(requireContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("entrants")
                .document(deviceId)
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        Entrant e = doc.toObject(Entrant.class);
                        if (e!=null) {
                            Session.setEntrant(e);
                            Toast.makeText(getContext(),
                                    "Signed in as " + e.getName(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        startActivity(new Intent(getActivity(), EntrantActivity.class));


                        requireActivity().finish();

                    } else {
                        db.collection("organizers")
                                .document(deviceId)
                                .get()
                                .addOnSuccessListener(doc2 -> {
                                    if (doc2.exists()) {
                                        Organizer o = doc2.toObject(Organizer.class);
                                        Session.setOrganizer(o);
                                        startActivity(new Intent(getActivity(), OrganizerActivity.class));
                                        requireActivity().finish();

                                    } else {
                                        Bundle args = new Bundle();
                                        args.putString("deviceId", deviceId);

                                        NavHostFragment.findNavController(DeviceIDFragment.this)
                                                .navigate(R.id.action_DeviceIDFragment_to_DetailsFragment, args);
                                    }
                                });
                    }
                })
                .addOnFailureListener(e -> {

                    Bundle args = new Bundle();
                    args.putString("deviceId", deviceId);

                    NavHostFragment.findNavController(DeviceIDFragment.this)
                            .navigate(R.id.action_DeviceIDFragment_to_DetailsFragment, args);});
    }

}


