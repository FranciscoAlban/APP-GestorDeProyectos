package ec.edu.utn.example.miapp_gestorproyectos;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.utn.example.miapp_gestorproyectos.database.DBHelper;

public class LoginActivity extends AppCompatActivity {

    EditText usernameInput, passwordInput;
    Button loginButton;
    TextView registerLink;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        usernameInput = findViewById(R.id.etUsername);
        passwordInput = findViewById(R.id.etPassword);
        loginButton = findViewById(R.id.btnLogin);
        registerLink = findViewById(R.id.tvGoToRegister);
        TextView recuperarLink = findViewById(R.id.tvRecuperar);

        dbHelper = new DBHelper(this);

        loginButton.setOnClickListener(v -> {
            String user = usernameInput.getText().toString();
            String pass = passwordInput.getText().toString();

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT * FROM usuarios WHERE username = ? AND password = ?", new String[]{user, pass});
            cursor = db.rawQuery("SELECT * FROM usuarios WHERE username = ? AND password = ?", new String[]{user, pass});
            if (cursor.moveToFirst()) {
                int userId = cursor.getInt(0); // columna ID del usuario
                Toast.makeText(this, "Bienvenido", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(this, ProyectosActivity.class);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
                finish();
        } else {
                Toast.makeText(this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
            }
            cursor.close();
        });

        registerLink.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
        });

        recuperarLink.setOnClickListener(v -> { // 👈 AÑADIDO
            Intent intent = new Intent(this, RecuperarPasswordActivity.class);
            startActivity(intent);
        });
    }
}
