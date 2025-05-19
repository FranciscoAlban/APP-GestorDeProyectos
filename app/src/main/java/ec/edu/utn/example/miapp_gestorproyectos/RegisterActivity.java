package ec.edu.utn.example.miapp_gestorproyectos;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.utn.example.miapp_gestorproyectos.R;
import ec.edu.utn.example.miapp_gestorproyectos.database.DBHelper;

public class RegisterActivity extends AppCompatActivity {

    EditText etNewUsername, etNewPassword;
    Button btnRegister;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        etNewUsername = findViewById(R.id.etNewUsername);
        etNewPassword = findViewById(R.id.etNewPassword);
        btnRegister = findViewById(R.id.btnRegister);

        dbHelper = new DBHelper(this);

        btnRegister.setOnClickListener(v -> {
            String user = etNewUsername.getText().toString().trim();
            String pass = etNewPassword.getText().toString().trim();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Llena todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Insertar nuevo usuario
            ContentValues values = new ContentValues();
            values.put("username", user);
            values.put("password", pass);

            long result = db.insert("usuarios", null, values);
            if (result == -1) {
                Toast.makeText(this, "Error: Usuario ya existe", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Usuario registrado correctamente", Toast.LENGTH_SHORT).show();
                finish(); // volver al login
            }
        });
    }
}
