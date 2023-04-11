package com.manasi.nearesto.helper;

import android.app.Activity;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.manasi.nearesto.HomeActivity;
import com.manasi.nearesto.R;

public class CSVDataImporter {

    public static void doImport(Activity context, int fileId, String collectionName, String idColumnName) throws IOException {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference collection = db.collection(collectionName);
        String filePath = "android.resource://" + context.getPackageName() + "/" + fileId;

        InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(filePath));

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        String[] headers = null;
        int idColumnIndex = -1;

        while ((line = br.readLine()) != null) {

            if (headers == null) {
                headers = line.split(",");
                idColumnIndex = getColumnIndex(headers, idColumnName);
            } else {
                String[] fields = line.split(",");
                Map<String, Object> data = new HashMap<>();

                for (int i = 0; i < headers.length; i++) {
                    data.put(headers[i], fields[i]);
                }

                if (idColumnIndex >= 0) {
                    String id = fields[idColumnIndex];
                    DocumentReference document = collection.document(id);
                    document.update(data);
                } else {
                    DocumentReference document = collection.document();
                    document.set(data);
                }
            }
        }

        br.close();
    }

    private static int getColumnIndex(String[] headers, String columnName) {
        for (int i = 0; i < headers.length; i++) {
            if (headers[i].equals(columnName)) {
                return i;
            }
        }
        return -1;
    }
}
