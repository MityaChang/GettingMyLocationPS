package sg.edu.rp.c302.id19034275.gettingmylocationps;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

public class CheckRecord extends AppCompatActivity {
    Button btnRefresh;
    TextView tvNumofRecords;
    ArrayList<String> lines;
    ArrayAdapter aa;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_record);
        btnRefresh = findViewById(R.id.btnRefresh);
        tvNumofRecords = findViewById(R.id.tvNumofRecords);
        lv = findViewById(R.id.lv);
        lines = new ArrayList<>();
        aa = new ArrayAdapter(CheckRecord.this, android.R.layout.simple_list_item_1, lines);
        lv.setAdapter(aa);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lines.clear();
                String folderLocation = getFilesDir().getAbsolutePath() + "/P09PS";
                File targetFile = new File(folderLocation, "locationData.txt");
                if (targetFile.exists()) {
                    String data = "";
                    try {
                        FileReader reader = new FileReader(targetFile);
                        BufferedReader br = new BufferedReader(reader);
                        String line = br.readLine();
                        while (line != null) {
                            Log.i("Line: ", line);
                            lines.add(line);
                            line = br.readLine();
                        }
                        br.close();
                        reader.close();
                    } catch (Exception e) {
                        Toast.makeText(CheckRecord.this, "Failed to read!", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }
                    
                    tvNumofRecords.setText("Number of Records: " + lines.size());
                    aa.notifyDataSetChanged();
                }
            }
        });
        btnRefresh.performClick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        btnRefresh.performClick();
    }
}