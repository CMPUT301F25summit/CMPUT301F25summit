package com.example.summit.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.summit.R;

public class DetailsFragment extends Fragment {
    private EditText inputName, inputEmail, inputPhone;
    private Button submitButton;
    private boolean fields_filled;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enterdetails, container, false);

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

    private void handleSubmit() {
        String name = inputName.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String phone = inputPhone.getText().toString().trim();

        if(name.isEmpty() || email.isEmpty()) {
            Toast.makeText(getContext(), "Please fill in all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

    }
}
