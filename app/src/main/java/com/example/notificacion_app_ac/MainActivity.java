package com.example.notificacion_app_ac;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MainActivity extends AppCompatActivity {
    private Button botonNotificacion;
    private Button botonToast;
    private Button botonToastPersonalizado;
    private static final String CANAL_ID = "Notificacion";
    private static final int NOTIFICACION_ID = 0;
    private static final int CODIGO_SOLICITUD_PERMISOS = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        crearCanalNotificacion();

        botonNotificacion = findViewById(R.id.boton_notificacion);
        botonToast = findViewById(R.id.boton_toast);
        botonToastPersonalizado = findViewById(R.id.boton_toast_personalizado);

        botonNotificacion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, CODIGO_SOLICITUD_PERMISOS);
                    } else {
                        crearNotificacion();
                    }
                } else {
                    crearNotificacion();
                }
            }
        });

        botonToast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarToastBasico();
            }
        });

        botonToastPersonalizado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarToastPersonalizado();
            }
        });
    }

    private void crearCanalNotificacion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence nombre = "Notificación Básica";
            String descripcion = "Canal para notificaciones básicas";
            int importancia = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel canal = new NotificationChannel(CANAL_ID, nombre, importancia);
            canal.setDescription(descripcion);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(canal);
        }
    }

    private void crearNotificacion() {
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent intentPendiente = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder constructor = new NotificationCompat.Builder(this, CANAL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground)
                .setContentTitle("Notificación Básica")
                .setContentText("Esta es una notificación básica")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(intentPendiente)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        notificationManager.notify(NOTIFICACION_ID, constructor.build());
        Toast.makeText(this, "Notificación exitosa", Toast.LENGTH_SHORT).show();
    }

    private void mostrarToastBasico() {
        Toast.makeText(this, "Este es un Toast básico", Toast.LENGTH_SHORT).show();
    }

    private void mostrarToastPersonalizado() {
        LayoutInflater inflater = getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast, findViewById(R.id.custom_toast_container));

        Toast toast = new Toast(getApplicationContext());
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CODIGO_SOLICITUD_PERMISOS) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                crearNotificacion();
            } else {
                Toast.makeText(this, "Permiso de notificación denegado", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
