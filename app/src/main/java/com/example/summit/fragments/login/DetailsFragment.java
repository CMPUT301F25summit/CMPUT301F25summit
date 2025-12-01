package com.example.summit.fragments.login;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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
    private TextView nameRequired, emailRequired;
    private Button submitButton;

    private int defaultColour;
    private int errorColour = Color.RED;

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

        nameRequired = view.findViewById(R.id.name_required);
        emailRequired = view.findViewById(R.id.email_required);

        submitButton = view.findViewById(R.id.button_submit);

        //store default color
        defaultColour = nameRequired.getCurrentTextColor();
        addLiveValidationListeners();

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

     private boolean validateName() {
        String name = inputName.getText().toString().trim();
        if(name.isEmpty()) {
            nameRequired.setTextColor(errorColour);
            return false;
        } else {
            nameRequired.setTextColor(defaultColour);
            return true;
        }
     }

     private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();
        boolean valid = email.contains("@") &&
                        email.contains(".") &&
                        email.indexOf('@') < email.lastIndexOf('.');
        if(!valid) {
            emailRequired.setTextColor(errorColour);
            return false;
        } else {
            emailRequired.setTextColor(defaultColour);
            return true;
        }
     }

     private void addLiveValidationListeners() {

        inputName.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateName();
            }
        });

        inputEmail.addTextChangedListener(new SimpleTextWatcher() {
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                validateEmail();
            }
        });
     }

    private void handleSubmit() {

        boolean nameOk = validateName();
        boolean emailOk = validateEmail();

        if(!nameOk || !emailOk) {
            Toast.makeText(getContext(), "Please correct the required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();


        String deviceId = android.provider.Settings.Secure.getString(
                requireContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);

        Bundle args = new Bundle();
        args.putString("deviceId", deviceId);
        args.putString("name", name);
        args.putString("email", email);
        args.putString("phone", phone);

        //navigate to event list screen
        NavHostFragment.findNavController(this)
                .navigate(R.id.action_DetailsFragment_to_RoleSelectionFragment, args);
    }

    private abstract class SimpleTextWatcher implements TextWatcher {
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void afterTextChanged(Editable s) {}
    }


}
