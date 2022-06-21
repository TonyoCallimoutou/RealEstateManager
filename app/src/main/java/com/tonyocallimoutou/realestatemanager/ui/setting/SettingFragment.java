package com.tonyocallimoutou.realestatemanager.ui.setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.EditTextPreference;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.tonyocallimoutou.realestatemanager.R;
import com.tonyocallimoutou.realestatemanager.util.Utils;
import com.tonyocallimoutou.realestatemanager.viewmodel.ViewModelUser;

import java.util.Arrays;
import java.util.Locale;


public class SettingFragment extends PreferenceFragmentCompat {

    private EditTextPreference namePreference;
    private EditTextPreference phonePreference;
    private ListPreference languagePreference;
    private ListPreference moneyPreference;
    private Preference removeAccount;


    private SharedPreferences sharedPreferences;
    private ViewModelUser viewModelUser;

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setPreferencesFromResource(R.xml.preferences,rootKey);
        viewModelUser = new ViewModelProvider(requireActivity()).get(ViewModelUser.class);
        initInformation();
        setPreferences();
    }

    private void initInformation() {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getContext());

        String name = sharedPreferences.getString(getString(R.string.shared_preference_username),"");
        String phone = sharedPreferences.getString(getString(R.string.shared_preference_phone_number),"");
        String defaultLanguageKey = sharedPreferences.getString(getString(R.string.shared_preference_language),getResources().getConfiguration().locale.getLanguage());

        String[] languageKey = getResources().getStringArray(R.array.language_key);
        String[] moneyStr = getResources().getStringArray(R.array.money);

        String defaultMoney;
        if (Arrays.asList(languageKey).contains(defaultLanguageKey)) {
            defaultMoney = moneyStr[Arrays.asList(languageKey).indexOf(defaultLanguageKey)];
        }
        else {
            defaultMoney = moneyStr[0];
        }

        String money = sharedPreferences.getString(getString(R.string.shared_preference_money), defaultMoney);


        namePreference = findPreference(getString(R.string.preferences_name));
        namePreference.setSummary(getString(R.string.preferences_your_actual_name)+ " " + name);
        namePreference.setText(name);

        phonePreference = findPreference(getString(R.string.preferences_phone_number));
        phonePreference.setSummary(getString(R.string.preferences_your_actual_phone_number)+ " " + phone);
        phonePreference.setText(phone);
        phonePreference.setOnBindEditTextListener(new EditTextPreference.OnBindEditTextListener() {
            @Override
            public void onBindEditText(@NonNull EditText editText) {
                editText.setInputType(InputType.TYPE_CLASS_PHONE);
            }
        });


        Locale locale = new Locale(defaultLanguageKey);
        String defaultLanguage = locale.getDisplayLanguage(locale);
        defaultLanguage = defaultLanguage.substring(0,1).toUpperCase() + defaultLanguage.substring(1).toLowerCase();

        languagePreference = findPreference(getString(R.string.preferences_languages));
        languagePreference.setSummary(defaultLanguage);
        languagePreference.setValue(defaultLanguage);

        moneyPreference = findPreference(getString(R.string.preferences_money));
        moneyPreference.setSummary(money);
        moneyPreference.setValue(money);

        removeAccount = findPreference(getString(R.string.preferences_remove_account));
    }

    private void setPreferences() {
        namePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                sharedPreferences
                        .edit()
                        .putString(getString(R.string.shared_preference_username), (String) newValue)
                        .apply();

                viewModelUser.setNameOfCurrentUser((String) newValue);

                initInformation();
                return false;
            }
        });

        phonePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                sharedPreferences
                        .edit()
                        .putString(getString(R.string.shared_preference_phone_number), (String) newValue)
                        .apply();

                viewModelUser.setPhoneNumberOfCurrentUser((String) newValue);

                initInformation();
                return false;
            }
        });

        languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {

                String key = Utils.getKeyFromLanguage(getContext(), (String) newValue);

                sharedPreferences
                        .edit()
                        .putString(getString(R.string.shared_preference_language), key)
                        .apply();

                getActivity().finish();
                startActivity(getActivity().getIntent());

                initInformation();
                return false;
            }
        });

        moneyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(@NonNull Preference preference, Object newValue) {
                sharedPreferences
                        .edit()
                        .putString(getString(R.string.shared_preference_money), (String) newValue)
                        .apply();

                initInformation();
                return false;
            }
        });

        removeAccount.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(@NonNull Preference preference) {
                alertDialogRemoveAccount();
                return false;
            }
        });
    }

    private void alertDialogRemoveAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle(R.string.setting_account_title_alert);
        builder.setMessage(R.string.setting_account_message_alert);

        builder.setPositiveButton(getContext().getResources().getString(R.string.setting_account_button_alert), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                viewModelUser.deleteUser().addOnCompleteListener(task -> {
                    getActivity().finish();
                });
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }
}