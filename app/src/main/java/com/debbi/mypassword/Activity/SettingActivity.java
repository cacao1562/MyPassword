package com.debbi.mypassword.Activity;

import com.debbi.mypassword.CommonApplication;
import com.debbi.mypassword.Drive.DriveServiceHelper;
import com.debbi.mypassword.Drive.MimeType;
import com.debbi.mypassword.R;
import com.debbi.mypassword.Utils.RealmBackupRestore;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;
import com.google.android.gms.drive.OpenFileActivityOptions;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.util.Collections;

/**
 * The main {@link Activity} for the Drive API migration sample app.
 */
public class SettingActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final int REQUEST_CODE_OPEN_DOCUMENT = 2;

    private DriveServiceHelper mDriveServiceHelper;
    private String mOpenFileId;

    private RealmBackupRestore mRealmBackup;

    private File EXPORT_REALM_PATH = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
    private String EXPORT_REALM_FILE_NAME = "backupRealm.realm";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        mRealmBackup = new RealmBackupRestore(this);
        // Store the EditText boxes to be updated when files are opened/created/modified.
//        mFileTitleEditText = findViewById(R.id.file_title_edittext);
//        mDocContentEditText = findViewById(R.id.doc_content_edittext);

        // Set the onClick listeners for the button bar.
//        findViewById(R.id.open_btn).setOnClickListener(view -> openFilePicker());
//        findViewById(R.id.create_btn).setOnClickListener(view -> createFile());
//        findViewById(R.id.save_btn).setOnClickListener(view -> saveFile());
//        findViewById(R.id.query_btn).setOnClickListener(view -> query());
        findViewById(R.id.uplaod_btn).setOnClickListener(view -> upload());
        findViewById(R.id.restore_btn).setOnClickListener(view -> openFilePicker());

        // Authenticate the user. For most apps, this should be done when the user performs an
        // action that requires Drive access rather than in onCreate.
        requestSignIn();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        switch (requestCode) {
            case REQUEST_CODE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && resultData != null) {
                    handleSignInResult(resultData);
                }
                break;

            case REQUEST_CODE_OPEN_DOCUMENT:
                if (resultCode == Activity.RESULT_OK && resultData != null) {

                    DriveId driveId = resultData.getParcelableExtra(OpenFileActivityOptions.EXTRA_RESPONSE_DRIVE_ID);
                    Log.d(TAG, "onActivityResult  drive id  = " + driveId);

                    if (driveId != null) {
                        DriveFile file = driveId.asDriveFile();
                    }

//                    Log.i(TAG, "Selected folder's ID: " + driveId2.encodeToString());
//                    Log.i(TAG, "Selected folder's Resource ID: " + driveId2.getResourceId());// this is the id of the actual file
//                    Toast.makeText(getApplicationContext(), " my id: " + driveId.getResourceId(), Toast.LENGTH_LONG).show();

//                    DriveFile file = Drive.DriveApi.getFile(googleApiClient, driveId);

//                    downloadFromDrive(file);
//                    this.getContentResolver().openOutputStream(resultData.getData());
                    Uri uri = resultData.getData();

                    if (uri != null) {

                        Log.d(TAG, "onActivityResult  uri  = " + uri.toString());
//                        openFileFromFilePicker(uri);

                        mDriveServiceHelper.getFileName(getContentResolver(), uri).addOnSuccessListener(name -> {

                            Log.d(TAG, "getFileName  name  = " + name);
                            if (!name.equals(EXPORT_REALM_FILE_NAME)) {
                                CommonApplication.getContext().showAlert("", " 파일 이름이 일치하지 않습니다. ", this);
                                return;
                            }

                            mDriveServiceHelper.getFileID(name, "*/*").addOnSuccessListener(id -> {

                                Log.d(TAG, "getFileID  id  = " + id);

//                                java.io.File file = new java.io.File("/sdcard/Download/DownFromDrive/" , name);
//                                java.io.File file = new java.io.File("/data/data/com.debbi.mypassword/files/" , name);
                                java.io.File file = new java.io.File(EXPORT_REALM_PATH, EXPORT_REALM_FILE_NAME);
                                mDriveServiceHelper.downloadFile(file, id).addOnSuccessListener(v -> {

                                    Log.d(TAG, "downloadFile  success  = ");
                                    mRealmBackup.restore();
                                    CommonApplication.setRestoreMode(true);

                                }).addOnFailureListener(err -> {

                                    Log.d(TAG, "downloadFile  fail  = " + err);
                                });

                            }).addOnFailureListener(err -> {

                                Log.d(TAG, "getFileID  fail  = " + err);
                            });

                        }).addOnFailureListener(err -> {

                            Log.d(TAG, "getFileName  fail  = " + err);
                        });
                    }
                }
                break;
        }

        super.onActivityResult(requestCode, resultCode, resultData);
    }

    /**
     * Starts a sign-in activity using {@link #REQUEST_CODE_SIGN_IN}.
     */
    private void requestSignIn() {
        Log.d(TAG, "Requesting sign-in");

        GoogleSignInOptions signInOptions =
                new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .requestScopes(new Scope(DriveScopes.DRIVE_FILE))
                        .build();
        GoogleSignInClient client = GoogleSignIn.getClient(this, signInOptions);

        // The result of the sign-in Intent is handled in onActivityResult.
        startActivityForResult(client.getSignInIntent(), REQUEST_CODE_SIGN_IN);
    }

    /**
     * Handles the {@code result} of a completed sign-in activity initiated from {@link
     * #requestSignIn()}.
     */
    private void handleSignInResult(Intent result) {
        GoogleSignIn.getSignedInAccountFromIntent(result)
                .addOnSuccessListener(googleAccount -> {
                    Log.d(TAG, "Signed in as " + googleAccount.getEmail());

                    // Use the authenticated account to sign in to the Drive service.
                    GoogleAccountCredential credential =
                            GoogleAccountCredential.usingOAuth2(
                                    this, Collections.singleton(DriveScopes.DRIVE_FILE));
                    credential.setSelectedAccount(googleAccount.getAccount());
                    Drive googleDriveService =
                            new Drive.Builder(
                                    AndroidHttp.newCompatibleTransport(),
                                    new GsonFactory(),
                                    credential)
                                    .setApplicationName("Drive API Migration")
                                    .build();

                    // The DriveServiceHelper encapsulates all REST API and SAF functionality.
                    // Its instantiation is required before handling any onClick actions.
                    mDriveServiceHelper = new DriveServiceHelper(googleDriveService);
                })
                .addOnFailureListener(exception -> Log.e(TAG, "Unable to sign in.", exception));
    }

    /**
     * Opens the Storage Access Framework file picker using {@link #REQUEST_CODE_OPEN_DOCUMENT}.
     */
    private void openFilePicker() {
        if (mDriveServiceHelper != null) {
            Log.d(TAG, "Opening file picker.");

            Intent pickerIntent = mDriveServiceHelper.createFilePickerIntent();

            // The result of the SAF Intent is handled in onActivityResult.
            startActivityForResult(pickerIntent, REQUEST_CODE_OPEN_DOCUMENT);
        }
    }

    /**
     * Opens a file from its {@code uri} returned from the Storage Access Framework file picker
     * initiated by {@link #openFilePicker()}.
     */
//    private void openFileFromFilePicker(Uri uri) {
//        if (mDriveServiceHelper != null) {
//            Log.d(TAG, "Opening " + uri.getPath());
//
//            mDriveServiceHelper.openFileUsingStorageAccessFramework(getContentResolver(), uri)
//                    .addOnSuccessListener(nameAndContent -> {
//                        String name = nameAndContent.first;
//                        String content = nameAndContent.second;
//
//                        mFileTitleEditText.setText(name);
//                        mDocContentEditText.setText(content);
//
//                        // Files opened through SAF cannot be modified.
//                        setReadOnlyMode();
//                    })
//                    .addOnFailureListener(exception ->
//                            Log.e(TAG, "Unable to open file from picker.", exception));
//        }
//    }

    /**
     * Creates a new file via the Drive REST API.
     */
//    private void createFile() {
//        if (mDriveServiceHelper != null) {
//            Log.d(TAG, "Creating a file.");
//
//            mDriveServiceHelper.createFile()
//                    .addOnSuccessListener(fileId -> readFile(fileId))
//                    .addOnFailureListener(exception ->
//                            Log.e(TAG, "Couldn't create file.", exception));
//        }
//    }

    /**
     * Retrieves the title and content of a file identified by {@code fileId} and populates the UI.
     */
//    private void readFile(String fileId) {
//        if (mDriveServiceHelper != null) {
//            Log.d(TAG, "Reading file " + fileId);
//
//            mDriveServiceHelper.readFile(fileId)
//                    .addOnSuccessListener(nameAndContent -> {
//                        String name = nameAndContent.first;
//                        String content = nameAndContent.second;
//
//                        mFileTitleEditText.setText(name);
//                        mDocContentEditText.setText(content);
//
//                        setReadWriteMode(fileId);
//                    })
//                    .addOnFailureListener(exception ->
//                            Log.e(TAG, "Couldn't read file.", exception));
//        }
//    }

    /**
     * Saves the currently opened file created via {@link #createFile()} if one exists.
     */
//    private void saveFile() {
//        if (mDriveServiceHelper != null && mOpenFileId != null) {
//            Log.d(TAG, "Saving " + mOpenFileId);
//
//            String fileName = mFileTitleEditText.getText().toString();
//            String fileContent = mDocContentEditText.getText().toString();
//
//            mDriveServiceHelper.saveFile(mOpenFileId, fileName, fileContent)
//                    .addOnFailureListener(exception ->
//                            Log.e(TAG, "Unable to save file via REST.", exception));
//        }
//    }

    /**
     * Queries the Drive REST API for files visible to this app and lists them in the content view.
     */
//    private void query() {
//        if (mDriveServiceHelper != null) {
//            Log.d(TAG, "Querying for files.");
//
//            mDriveServiceHelper.queryFiles()
//                    .addOnSuccessListener(fileList -> {
//                        StringBuilder builder = new StringBuilder();
//                        for (File file : fileList.getFiles()) {
//                            builder.append(file.getName()).append("\n");
//                        }
//                        String fileNames = builder.toString();
//
//                        mFileTitleEditText.setText("File List");
//                        mDocContentEditText.setText(fileNames);
//
//                        setReadOnlyMode();
//                    })
//                    .addOnFailureListener(exception -> Log.e(TAG, "Unable to query files.", exception));
//        }
//    }
    private void upload() {

        mRealmBackup.backup();


        if (mDriveServiceHelper != null) {
//            java.io.File file = new java.io.File("/sdcard/DCIM/Camera/20190807_084706_002.jpg");
//            java.io.File file = new java.io.File("/data/data/com.debbi.mypassword/files/default.realm");
            java.io.File file = new java.io.File(EXPORT_REALM_PATH, EXPORT_REALM_FILE_NAME);
//            java.io.File sdCard = Environment.getExternalStorageDirectory();
            Log.d(TAG, "upload file exists = " + file.exists());
//            Log.d(TAG, "upload file sd path= " + sdCard.getAbsolutePath() );

            mDriveServiceHelper.deleteFile(EXPORT_REALM_FILE_NAME, "*/*")
                    .addOnSuccessListener(v -> {
                        Log.d(TAG, "deleteFile success  ");

                        mDriveServiceHelper.uploadFile(file, "*/*", null)
                                .addOnSuccessListener(res -> {
                                    Log.d(TAG, "upload success = " + res);
                                }).addOnFailureListener(exception -> {
                            Log.d(TAG, "upload fail = " + exception);
                        });

                    }).addOnFailureListener(err -> {

                Log.d(TAG, "deleteFile fail = " + err  );

            });

        }


    }

    /**
     * Updates the UI to read-only mode.
     */
//    private void setReadOnlyMode() {
//        mFileTitleEditText.setEnabled(false);
//        mDocContentEditText.setEnabled(false);
//        mOpenFileId = null;
//    }

    /**
     * Updates the UI to read/write mode on the document identified by {@code fileId}.
     */
//    private void setReadWriteMode(String fileId) {
//        mFileTitleEditText.setEnabled(true);
//        mDocContentEditText.setEnabled(true);
//        mOpenFileId = fileId;
//    }
}
