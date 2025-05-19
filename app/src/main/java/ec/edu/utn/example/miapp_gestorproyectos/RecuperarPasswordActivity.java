package ec.edu.utn.example.miapp_gestorproyectos;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.utn.example.miapp_gestorproyectos.database.DBHelper;

public class RecuperarPasswordActivity extends AppCompatActivity {

    EditText etUsuario;
    Button btnRecuperar;
    TextView tvResultado;

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recuperar_password);

        etUsuario = findViewById(R.id.etUsuarioRecuperar);
        btnRecuperar = findViewById(R.id.btnRecuperar);
        tvResultado = findViewById(R.id.tvResultado);
        dbHelper = new DBHelper(this);

        btnRecuperar.setOnClickListener(v -> {
            String user = etUsuario.getText().toString().trim();
            if (user.isEmpty()) {
                Toast.makeText(this, "Ingrese un nombre de usuario", Toast.LENGTH_SHORT).show();
                return;
            }

            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.rawQuery("SELECT password FROM usuarios WHERE username = ?", new String[]{user});
            if (cursor.moveToFirst()) {
                String pass = cursor.getString(0);
                tvResultado.setText("Tu contraseña es: " + pass);
            } else {
                tvResultado.setText("Usuario no encontrado.");
            }
            cursor.close();
        });
    }
}
