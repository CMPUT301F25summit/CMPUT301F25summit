package com.example.summit.fragments.login;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.summit.R;
import com.example.summit.model.Entrant;
import com.example.summit.model.Firebase;
import com.example.summit.model.User;

/**
 * A {@link Fragment} that displays a form for the user to enter their personal details.
 * <p>
 * This fragment collects the user's name, email, and phone number.
 * Upon submission, it validates the required fields (name and email),
 * retrieves the unique device ID, and passes all details to the
 * {@code RoleSelectionFragment} via navigation arguments.
 */
public class DetailsFragment extends Fragment {
    private EditText inputName, inputEmail, inputPhone;
    private Button submitButton;
    private boolean fields_filled;

    /**
     * Inflates the fragment's view, initializes UI components, and sets up event listeners.
     * It also sets the title of the {@link androidx.appcompat.app.ActionBar} if available.
     *
     * @param inflater The LayoutInflater object that can be used to inflate
     * any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's
     * UI should be attached to. The fragment should not add the view itself,
     * but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     * from a previous saved state as given in this Bundle.
     * @return Returns the View for the fragment's UI.
     */
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_enter_details, container, false);

        if(getActivity() != null) {
            ((androidx.appcompat.app.AppCompatActivity) getActivity())
                    .getSupportActionBar()
                    .setTitle("Enter Details");
        }

        inputName = view.findViewById(R.id.input_name);
        inputEmail = view.findViewById(R.id.input_email);
        inputPhone = view.findViewById(R.id.input_phone);

        submitButton = view.findViewById(R.id.button_submit);

        submitButton.setOnClickListener(v-> handleSubmit());
        return view;
    }

    /**
     * Handles the logic when the submit button is clicked.
     *
     * It performs the following actions:
     * 1. Retrieves and trims the input from the name, email, and phone fields.
     * 2. Validates that the name and email fields are not empty. Shows a Toast if they are.
     * 3. Retrieves the device's secure {@link Settings.Secure#ANDROID_ID}.
     * 4. Bundles the device ID, name, email, and phone number.
     * 5. Navigates to the {@code RoleSelectionFragment} (via action
     * {@code R.id.action_DetailsFragment_to_RoleSelectionFragment}), passing the bundle as arguments.
     */
    private void handleSubmit() {
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();

        if(name.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String deviceId = android.provider.Settings.Secure.getString(
                requireContext().getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        Bundle args = new Bundle();
        args.putString("deviceId", deviceId);
        args.putString("name", name);
        args.putString("email", email);
        args.putString("phone", phone);

        //navigate to event list screen
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_DetailsFragment_to_RoleSelectionFragment, args);
    }
}
