package com.awisogera.votingpostermaker;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    PosterView posterView;
    ImageView imageA, imageB;
    Button addButton, addBButton, addTextButton, deleteTextButton;
    ActivityResultLauncher<Intent> launcherA;
    ActivityResultLauncher<Intent> launcherB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        posterView = findViewById(R.id.posterView);
        imageA = findViewById(R.id.imageA);
        imageB = findViewById(R.id.imageB);
        addButton = findViewById(R.id.addButton);
        addBButton = findViewById(R.id.addBButton);
        addTextButton = findViewById(R.id.addTextButton);
        deleteTextButton = findViewById(R.id.deleteTextButton);

        launcherA = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (imageUri != null && imageA != null && posterView != null) {
                    imageA.setImageURI(imageUri);
                    posterView.setImageA(imageUri);
                }
            }
        });

        launcherB = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                Uri imageUri = result.getData().getData();
                if (imageUri != null && imageB != null && posterView != null) {
                    imageB.setImageURI(imageUri);
                    posterView.setImageB(imageUri);
                }
            }
        });

        if (addButton != null) {
            addButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                launcherA.launch(intent);
            });
        }

        if (addBButton != null) {
            addBButton.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType("image/*");
                launcherB.launch(intent);
            });
        }

        if (addTextButton != null) {
            addTextButton.setOnClickListener(v -> {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Add Poster Text");
                final EditText input = new EditText(this);
                input.setHint("Enter candidate name or title");
                builder.setView(input);
                builder.setPositiveButton("Add", (dialog, which) -> {
                    String text = input.getText().toString();
                    if (!text.isEmpty() && posterView != null) {
                        posterView.addText(text);
                    }
                });
                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
                builder.show();
            });
        }

        if (deleteTextButton != null && posterView != null) {
            deleteTextButton.setOnClickListener(v -> posterView.deleteActiveText());
        }
    }
}
