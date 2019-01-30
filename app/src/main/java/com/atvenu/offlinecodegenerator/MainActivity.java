package com.atvenu.offlinecodegenerator;

import android.content.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.*;

import org.hashids.*;
import org.w3c.dom.Text;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private Hashids hashids = null;
    private ArrayList<String> generatedCodeList = new ArrayList<>();
    private EditText fakeShowIdField;
    private ToggleButton enableTippingButton;
    private EditText offlineLimitField;
    private Button saveButton;
    private Button generateCodeButton;
    private Button copyButton;
    private Button clearCodesButton;
    private TextView emergencyCodeField;
    private TextView generatedCodesField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fakeShowIdField = findViewById(R.id.fakeShowId);
        fakeShowIdField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus)
                    generateCode();
            }
        });


        enableTippingButton = findViewById(R.id.enableTipping);
        enableTippingButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                generateCode();

            }

        });

        offlineLimitField = findViewById(R.id.offlineLimit);
        offlineLimitField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (!hasFocus)
                    generateCode();
            }
        });


        saveButton = findViewById(R.id.saveCode);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                saveCode();

            }

        });


        generateCodeButton = findViewById(R.id.generateCode);
        generateCodeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                generateCode();

            }

        });


        copyButton = findViewById(R.id.copyCode);
        copyButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String emergencyCode = String.valueOf(emergencyCodeField.getText());
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Copied Text", emergencyCode);
                clipboard.setPrimaryClip(clip);
            }

        });

        clearCodesButton = findViewById(R.id.clearCodes);
        clearCodesButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                TextView generatedCodesField = findViewById(R.id.generatedCodes);
                generatedCodesField.setText("");

            }

        });

        emergencyCodeField = findViewById(R.id.emergencyOfflineCode);
        generatedCodesField = findViewById(R.id.generatedCodes);

        hashids = new Hashids("atvenuOfflineSalt", 0, "abcdefghijkmnpqrstuvwxyz23456789");
        this.generateCode();
        this.readFileInternalStorage();
    }

    void generateCode() {
        String fakeIdFieldText = String.valueOf(fakeShowIdField.getText());
        String offlineLimitFieldText = String.valueOf(offlineLimitField.getText());
        if (fakeIdFieldText.isEmpty() || offlineLimitFieldText.isEmpty()) return;

        long fakeShowId = new Long(fakeIdFieldText);
        boolean enableTipping = enableTippingButton.isChecked();
        long offlineLimit = new Long(offlineLimitFieldText);

        String newCode = hashids.encode(fakeShowId, enableTipping ? 1 : 0, offlineLimit);


        emergencyCodeField.setText(newCode);
    }

    void saveCode() {
        this.generateCode();

        long fakeShowId = new Long(String.valueOf(fakeShowIdField.getText()));
        boolean enableTipping = enableTippingButton.isChecked();
        long offlineLimit = new Long(String.valueOf(offlineLimitField.getText()));

        String[] arr = {String.valueOf(fakeShowId), String.valueOf(enableTipping), String.valueOf(offlineLimit), String.valueOf(emergencyCodeField.getText())};
        String newCode = String.join(",", Arrays.asList(arr));
        if (!generatedCodeList.contains(newCode))
            generatedCodeList.add(0, newCode);


        String generatedCodeListString = String.join("\n", generatedCodeList);
        generatedCodesField.setText(generatedCodeListString);

        createUpdateFile(generatedCodeListString, false);
    }

    private void createUpdateFile(String content, boolean update) {
        String fileName = "offlineCodes";
        FileOutputStream outputStream;

        try {
            if (update) {
                outputStream = openFileOutput(fileName, Context.MODE_APPEND);
            } else {
                outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            }
            outputStream.write(content.getBytes());
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void readFileInternalStorage() {
        String fileName = "offlineCodes";
        try {
            FileInputStream fileInputStream = openFileInput(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(fileInputStream));

            StringBuffer sb = new StringBuffer();
            String line = reader.readLine   ();

            while (line != null) {
                sb.append(line);
                sb.append("\n");
                line = reader.readLine();
            }
            generatedCodesField.setText(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
