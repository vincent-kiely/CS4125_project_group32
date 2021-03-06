package com.example.cs4125_project.user;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.cs4125_project.logs.LogTags;
import com.example.cs4125_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInFragment extends Fragment implements View.OnClickListener {

    private FirebaseAuth mAuth;
    private EditText mEmailField;
    private EditText mPasswordField;
    private RelativeLayout layout;

    public LogInFragment() {
        // Required empty public constructor
    }

    public static LogInFragment newInstance()
    {
        LogInFragment myFragment = new LogInFragment();
        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_log_in, container, false);

        //Layout
        layout = rootView.findViewById(R.id.fragment_log_in);

        //Text fields
        mEmailField = rootView.findViewById(R.id.fieldEmail);
        mPasswordField = rootView.findViewById(R.id.fieldPassword);

        //Listeners
        rootView.findViewById(R.id.register).setOnClickListener(this);
        rootView.findViewById(R.id.signIn).setOnClickListener(this);

        return rootView;
    }

    //Checks to make sure login credentials are valid
    private boolean validateForm(String email, String password){
        //Initially set to true and if form is invalid, boolean changes to false
        boolean valid = true;

        if(TextUtils.isEmpty(email)) {
            mEmailField.setError("Required");
            valid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            mEmailField.setError("Invalid Format");
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else if(password.length()< 4){
            mPasswordField.setError("Must be greater than 4 characters");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    //Method to register an account
    private void register(String email, String password) {
        //Checks credentials first
        if (!validateForm(email, password)) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(LogTags.REGISTER_ACCOUNT, "Sign-up successful");
                            Toast.makeText(getContext(), "Sign up successful.",
                                    Toast.LENGTH_LONG).show();
                        } else {
                            Log.d(LogTags.REGISTER_ACCOUNT, "Sign-up failed");
                            Toast.makeText(getContext(), "Sign up failed. " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    //Hides fragment
    public void closeFragment(View v) {
        layout.setVisibility(v.INVISIBLE);
    }

    //Firebase sign in method
    private void signIn(String email, String password, final View v) {
        //Checks credentials first
        if (!validateForm(email, password)) {
            return;
        }
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            Log.d(LogTags.LOG_IN, "Logged in with "+user.getEmail());
                            //Close fragment when successfully logged in
                            getActivity().getSupportFragmentManager().popBackStackImmediate();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d(LogTags.LOG_IN, "Failed to log in");
                            Toast.makeText(getContext(), "Authentication failed." + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signIn) {
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString(), v);
        }
        if (i == R.id.register) {
            register(mEmailField.getText().toString(), mPasswordField.getText().toString());
        }
    }
}