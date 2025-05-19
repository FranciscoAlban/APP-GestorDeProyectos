package ec.edu.utn.example.miapp_gestorproyectos;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.utn.example.miapp_gestorproyectos.database.DBHelper;

import java.util.ArrayList;

public class ProyectosActivity extends AppCompatActivity {

    ListView listViewProyectos;
    Button btnAgregar;
    ArrayList<String> proyectosList;
    ArrayAdapter<String> adapter;
    DBHelper dbHelper;
    SQLiteDatabase db;

    int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_proyectos);

        listViewProyectos = findViewById(R.id.listViewProyectos);
        btnAgregar = findViewById(R.id.btnAgregarProyecto);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        userId = getIntent().getIntExtra("USER_ID", -1);
        cargarProyectos();

        btnAgregar.setOnClickListener(v -> mostrarDialogoNuevoProyecto());

        listViewProyectos.setOnItemClickListener((parent, view, position, id) -> {
            String info = proyectosList.get(position);
            String nombreProyecto = info.split("\n")[0];

            Cursor cursorProyecto = db.rawQuery("SELECT id FROM proyectos WHERE nombre = ? AND user_id = ?", new String[]{nombreProyecto, String.valueOf(userId)});
            if (cursorProyecto.moveToFirst()) {
                int proyectoId = cursorProyecto.getInt(0);
                Intent intent = new Intent(this, ActividadesActivity.class);
                intent.putExtra("PROYECTO_ID", proyectoId);
                startActivity(intent);
            }
            cursorProyecto.close();
        });

        listViewProyectos.setOnItemLongClickListener((parent, view, position, id) -> {
            String info = proyectosList.get(position);
            String nombreProyecto = info.split("\n")[0];

            Cursor c = db.rawQuery("SELECT id, descripcion, fecha_inicio, fecha_fin FROM proyectos WHERE nombre = ? AND user_id = ?", new String[]{nombreProyecto, String.valueOf(userId)});
            if (c.moveToFirst()) {
                int proyectoId = c.getInt(0);
                String descripcion = c.getString(1);
                String fechaInicio = c.getString(2);
                String fechaFin = c.getString(3);

                new AlertDialog.Builder(this)
                        .setTitle("Opciones del proyecto")
                        .setItems(new CharSequence[]{"Editar", "Eliminar"}, (dialog, which) -> {
                            if (which == 0) {
                                mostrarDialogoEditarProyecto(proyectoId, nombreProyecto, descripcion, fechaInicio, fechaFin);
                            } else {
                                db.delete("proyectos", "id = ?", new String[]{String.valueOf(proyectoId)});
                                db.delete("actividades", "proyecto_id = ?", new String[]{String.valueOf(proyectoId)});
                                Toast.makeText(this, "Proyecto eliminado", Toast.LENGTH_SHORT).show();
                                cargarProyectos();
                            }
                        })
                        .show();
            }
            c.close();
            return true;
        });
    }

    private void cargarProyectos() {
        proyectosList = new ArrayList<>();
        Cursor cursor = db.rawQuery(
                "SELECT nombre, descripcion, fecha_inicio, fecha_fin FROM proyectos WHERE user_id = ?",
                new String[]{String.valueOf(userId)}
        );
        if (cursor.moveToFirst()) {
            do {
                String nombre = cursor.getString(0);
                String descripcion = cursor.getString(1);
                String fechaInicio = cursor.getString(2);
                String fechaFin = cursor.getString(3);
                proyectosList.add(nombre + "\n" + descripcion + "\n" + fechaInicio + " → " + fechaFin);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, proyectosList);
        listViewProyectos.setAdapter(adapter);
    }

    private void mostrarDialogoNuevoProyecto() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_nuevo_proyecto, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);
        EditText etInicio = dialogView.findViewById(R.id.etFechaInicio);
        EditText etFin = dialogView.findViewById(R.id.etFechaFin);

        new AlertDialog.Builder(this)
                .setTitle("Nuevo Proyecto")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    ContentValues values = new ContentValues();
                    values.put("nombre", etNombre.getText().toString());
                    values.put("descripcion", etDescripcion.getText().toString());
                    values.put("fecha_inicio", etInicio.getText().toString());
                    values.put("fecha_fin", etFin.getText().toString());
                    values.put("user_id", userId);

                    db.insert("proyectos", null, values);
                    cargarProyectos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarProyecto(int id, String nombre, String descripcion, String inicio, String fin) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_nuevo_proyecto, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombre);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcion);
        EditText etInicio = dialogView.findViewById(R.id.etFechaInicio);
        EditText etFin = dialogView.findViewById(R.id.etFechaFin);

        etNombre.setText(nombre);
        etDescripcion.setText(descripcion);
        etInicio.setText(inicio);
        etFin.setText(fin);

        new AlertDialog.Builder(this)
                .setTitle("Editar Proyecto")
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    ContentValues values = new ContentValues();
                    values.put("nombre", etNombre.getText().toString());
                    values.put("descripcion", etDescripcion.getText().toString());
                    values.put("fecha_inicio", etInicio.getText().toString());
                    values.put("fecha_fin", etFin.getText().toString());

                    db.update("proyectos", values, "id = ?", new String[]{String.valueOf(id)});
                    cargarProyectos();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
