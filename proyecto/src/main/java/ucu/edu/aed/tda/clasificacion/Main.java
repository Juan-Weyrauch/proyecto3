package ucu.edu.aed.tda.clasificacion;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import ucu.edu.aed.tda.clasificacion.datos.GeneradorTransacciones;
import ucu.edu.aed.tda.clasificacion.datos.Medidor;

public class Main {

    public static void main(String[] args) {
        TClasificador clasificador = new Clasificador();
        long semilla = 2026;
        int[] demoras = {100, 1_000, 10_000, 600_000};
        int[] tamanios = {10_000, 20_000, 40_000, 80_000, 160_000, 320_000, 640_000, 1_280_000, 2_560_000, 5_120_000, 10_000_000};
        int repeticiones = 3;
        int calentamientos = 1;
        long limiteMs = 60_000;

        ExecutorService ejecutor = Executors.newCachedThreadPool(tarea -> {
            Thread hilo = new Thread(tarea);
            hilo.setDaemon(true);
            return hilo;
        });

        for (int demora : demoras) {
            System.out.println();
            System.out.println("=== Demora: " + demora + " ms ===");
            Set<TipoClasificador> cortados = EnumSet.noneOf(TipoClasificador.class);

            for (int n : tamanios) {
                int[] datos = GeneradorTransacciones.generarTimestamps(n, demora, semilla);
                int k = GeneradorTransacciones.desplazamientoMaximo(datos);
                System.out.printf("n=%d (k=%d)%n", n, k);

                for (TipoClasificador tipo : TipoClasificador.values()) {
                    if (!tipo.esComparativo()) {
                        continue;
                    }

                    if (cortados.contains(tipo)) {
                        System.out.printf("  %-22s no terminó (corte)%n", tipo.getNombre());
                        continue;
                    }

                    Future<Double> futuro = ejecutor.submit(() ->
                            Medidor.medianaMs(copia -> tipo.ordenar(clasificador, copia), datos, repeticiones, calentamientos)
                    );

                    try {
                        double tiempoMs = futuro.get(limiteMs, TimeUnit.MILLISECONDS);
                        System.out.printf("  %-22s %.2f ms%n", tipo.getNombre(), tiempoMs);
                    } catch (TimeoutException e) {
                        futuro.cancel(true);
                        cortados.add(tipo);
                        System.out.printf("  %-22s no terminó (> %d ms)%n", tipo.getNombre(), limiteMs);
                    } catch (InterruptedException | ExecutionException e) {
                        cortados.add(tipo);
                        System.out.printf("  %-22s error: %s%n", tipo.getNombre(), e.getMessage());
                    }
                }
            }
        }

        ejecutor.shutdownNow();
    }
}
