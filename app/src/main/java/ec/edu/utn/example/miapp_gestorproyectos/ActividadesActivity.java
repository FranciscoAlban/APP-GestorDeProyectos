package ec.edu.utn.example.miapp_gestorproyectos;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import ec.edu.utn.example.miapp_gestorproyectos.database.DBHelper;

import java.util.ArrayList;

public class ActividadesActivity extends AppCompatActivity {

    int proyectoId;
    DBHelper dbHelper;
    SQLiteDatabase db;
    ArrayList<String> listaActividades;
    ArrayAdapter<String> adapter;
    ListView listView;
    Button btnAgregar;
    TextView tvAvance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_actividades);

        proyectoId = getIntent().getIntExtra("PROYECTO_ID", -1);
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        listView = findViewById(R.id.listViewActividades);
        btnAgregar = findViewById(R.id.btnAgregarActividad);
        tvAvance = findViewById(R.id.tvAvance);

        btnAgregar.setOnClickListener(v -> mostrarDialogoNuevaActividad());
        cargarActividades();
    }

    private void cargarActividades() {
        listaActividades = new ArrayList<>();
        int total = 0;
        int realizadas = 0;

        Cursor cursor = db.rawQuery("SELECT id, nombre, descripcion, fecha_inicio, fecha_fin, estado FROM actividades WHERE proyecto_id = ?", new String[]{String.valueOf(proyectoId)});
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String nombre = cursor.getString(1);
                String descripcion = cursor.getString(2);
                String inicio = cursor.getString(3);
                String fin = cursor.getString(4);
                String estado = cursor.getString(5);

                if (estado.equalsIgnoreCase("Realizado")) realizadas++;
                total++;

                listaActividades.add(nombre + "\n" + descripcion + "\n" + inicio + " → " + fin + "\nEstado: " + estado);
            } while (cursor.moveToNext());
        }
        cursor.close();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, listaActividades);
        listView.setAdapter(adapter);

        if (total > 0) {
            int avance = (realizadas * 100) / total;
            tvAvance.setText("Avance del proyecto: " + avance + "%");
        } else {
            tvAvance.setText("Avance del proyecto: 0%");
        }

        listView.setOnItemLongClickListener((parent, view, position, id) -> {
            String info = listaActividades.get(position);
            String nombreActividad = info.split("\n")[0];

            Cursor c = db.rawQuery("SELECT id, descripcion, fecha_inicio, fecha_fin, estado FROM actividades WHERE nombre = ? AND proyecto_id = ?", new String[]{nombreActividad, String.valueOf(proyectoId)});
            if (c.moveToFirst()) {
                int actividadId = c.getInt(0);
                String descripcion = c.getString(1);
                String inicio = c.getString(2);
                String fin = c.getString(3);
                String estado = c.getString(4);

                new AlertDialog.Builder(this)
                        .setTitle("Opciones de actividad")
                        .setItems(new CharSequence[]{"Editar", "Eliminar"}, (dialog, which) -> {
                            if (which == 0) {
                                mostrarDialogoEditarActividad(actividadId, nombreActividad, descripcion, inicio, fin, estado);
                            } else {
                                db.delete("actividades", "id = ?", new String[]{String.valueOf(actividadId)});
                                Toast.makeText(this, "Actividad eliminada", Toast.LENGTH_SHORT).show();
                                cargarActividades();
                            }
                        })
                        .show();
            }
            c.close();
            return true;
        });
    }

    private void mostrarDialogoNuevaActividad() {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_nueva_actividad, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombreAct);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionAct);
        EditText etInicio = dialogView.findViewById(R.id.etFechaInicioAct);
        EditText etFin = dialogView.findViewById(R.id.etFechaFinAct);
        Spinner spEstado = dialogView.findViewById(R.id.spEstadoAct);

        ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(this,
                R.array.estado_actividad, android.R.layout.simple_spinner_item);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estadoAdapter);

        new AlertDialog.Builder(this)
                .setTitle("Nueva Actividad")
                .setView(dialogView)
                .setPositiveButton("Guardar", (dialog, which) -> {
                    ContentValues values = new ContentValues();
                    values.put("nombre", etNombre.getText().toString());
                    values.put("descripcion", etDescripcion.getText().toString());
                    values.put("fecha_inicio", etInicio.getText().toString());
                    values.put("fecha_fin", etFin.getText().toString());
                    values.put("estado", spEstado.getSelectedItem().toString());
                    values.put("proyecto_id", proyectoId);
                    db.insert("actividades", null, values);
                    cargarActividades();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoEditarActividad(int id, String nombre, String descripcion, String inicio, String fin, String estado) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_nueva_actividad, null);
        EditText etNombre = dialogView.findViewById(R.id.etNombreAct);
        EditText etDescripcion = dialogView.findViewById(R.id.etDescripcionAct);
        EditText etInicio = dialogView.findViewById(R.id.etFechaInicioAct);
        EditText etFin = dialogView.findViewById(R.id.etFechaFinAct);
        Spinner spEstado = dialogView.findViewById(R.id.spEstadoAct);

        etNombre.setText(nombre);
        etDescripcion.setText(descripcion);
        etInicio.setText(inicio);
        etFin.setText(fin);

        ArrayAdapter<CharSequence> estadoAdapter = ArrayAdapter.createFromResource(this,
                R.array.estado_actividad, android.R.layout.simple_spinner_item);
        estadoAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spEstado.setAdapter(estadoAdapter);

        int pos = estadoAdapter.getPosition(estado);
        spEstado.setSelection(pos);

        new AlertDialog.Builder(this)
                .setTitle("Editar Actividad")
                .setView(dialogView)
                .setPositiveButton("Actualizar", (dialog, which) -> {
                    ContentValues values = new ContentValues();
                    values.put("nombre", etNombre.getText().toString());
                    values.put("descripcion", etDescripcion.getText().toString());
                    values.put("fecha_inicio", etInicio.getText().toString());
                    values.put("fecha_fin", etFin.getText().toString());
                    values.put("estado", spEstado.getSelectedItem().toString());

                    db.update("actividades", values, "id = ?", new String[]{String.valueOf(id)});
                    cargarActividades();
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
