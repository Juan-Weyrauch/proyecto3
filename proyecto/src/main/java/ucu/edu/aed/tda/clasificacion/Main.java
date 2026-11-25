package ucu.edu.aed.tda.clasificacion;

import java.util.EnumSet;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;
import ucu.edu.aed.tda.clasificacion.datos.GeneradorTransacciones;
import ucu.edu.aed.tda.clasificacion.datos.Medidor;
import ucu.edu.aed.tda.clasificacion.datos.Transaccion;

public class Main {

    private record ResultadoMedicion(double tiempoMs, long comparaciones, long movimientos) {
    }

    public static void main(String[] args) {
        long semilla = 2026;
        int[] demoras = {100, 200, 500, 1_000, 2_000, 5_000, 10_000, 60_000, 600_000, 86_400_000};
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
                int desplazamientoMax = GeneradorTransacciones.desplazamientoMaximo(datos);
                System.out.printf("Elementos: %,d | Desplazamiento máximo: %,d%n", n, desplazamientoMax);

                for (TipoClasificador tipo : TipoClasificador.values()) {
                    if (!tipo.esComparativo()) {
                        continue;
                    }

                    if (cortados.contains(tipo)) {
                        System.out.printf("  %-22s no terminó (corte)%n", tipo.getNombre());
                        continue;
                    }

                    Future<ResultadoMedicion> futuro = ejecutor.submit(() -> {
                        TClasificador clasificador = new Clasificador();
                        double ms = Medidor.medianaMs(
                                copia -> tipo.ordenar(clasificador, copia),
                                datos,
                                repeticiones,
                                calentamientos
                        );
                        return new ResultadoMedicion(ms, clasificador.getComparaciones(), clasificador.getMovimientos());
                    });

                    try {
                        ResultadoMedicion res = futuro.get(limiteMs, TimeUnit.MILLISECONDS);
                        System.out.printf("  %-22s %10.2f ms  | cmp=%d  mov=%d%n",
                                tipo.getNombre(), res.tiempoMs(), res.comparaciones(), res.movimientos());
                    } catch (TimeoutException e) {
                        futuro.cancel(true);
                        cortados.add(tipo);
                        System.out.printf("  %-22s no terminó (> %d ms)%n", tipo.getNombre(), limiteMs);
                    } catch (InterruptedException | ExecutionException e) {
                        cortados.add(tipo);
                        Throwable causa = e.getCause() != null ? e.getCause() : e;
                        System.out.printf("  %-22s error: %s%n", tipo.getNombre(), causa);
                    }
                }
            }
        }

        medirQuicksortIngenuo(ejecutor, semilla, repeticiones, calentamientos, limiteMs);
        verificarEstabilidadTransacciones(semilla, repeticiones, calentamientos);

        ejecutor.shutdownNow();
    }

    private static void medirQuicksortIngenuo(ExecutorService ejecutor, long semilla,
                                              int repeticiones, int calentamientos, long limiteMs) {
        System.out.println();
        System.out.println("=== Quicksort pivote ingenuo (demora: 100 ms) ===");
        int[] tamaniosIngenuo = {10_000, 20_000, 40_000, 10_000_000};
        boolean cortado = false;

        for (int n : tamaniosIngenuo) {
            int[] datos = GeneradorTransacciones.generarTimestamps(n, 100, semilla);
            int desplazamientoMax = GeneradorTransacciones.desplazamientoMaximo(datos);
            System.out.printf("Elementos: %,d | Desplazamiento máximo: %,d%n", n, desplazamientoMax);

            if (cortado) {
                System.out.println("  Quicksort ingenuo      no terminó (corte)");
                continue;
            }

            Future<ResultadoMedicion> futuro = ejecutor.submit(() -> {
                TClasificador clasificador = new Clasificador();
                double ms = Medidor.medianaMs(
                        clasificador::quicksortIngenuo,
                        datos,
                        repeticiones,
                        calentamientos
                );
                return new ResultadoMedicion(ms, clasificador.getComparaciones(), clasificador.getMovimientos());
            });

            try {
                ResultadoMedicion res = futuro.get(limiteMs, TimeUnit.MILLISECONDS);
                System.out.printf("  Quicksort ingenuo      %10.2f ms  | cmp=%d  mov=%d%n",
                        res.tiempoMs(), res.comparaciones(), res.movimientos());
            } catch (TimeoutException e) {
                futuro.cancel(true);
                cortado = true;
                System.out.printf("  Quicksort ingenuo      no terminó (> %d ms)%n", limiteMs);
            } catch (InterruptedException | ExecutionException e) {
                cortado = true;
                Throwable causa = e.getCause() != null ? e.getCause() : e;
                System.out.printf("  Quicksort ingenuo      error: %s%n", causa);
            }
        }
    }

    private static void verificarEstabilidadTransacciones(long semilla, int repeticiones, int calentamientos) {
        System.out.println();
        System.out.println("=== Verificación de estabilidad con Transaccion[] (10.000.000 elementos) ===");
        int[] demorasPrueba = {800, 5_000};

        for (int demora : demorasPrueba) {
            Transaccion[] datos = GeneradorTransacciones.generarTransacciones(10_000_000, 500, demora, semilla);
            System.out.println("Demora: " + demora + " ms");

            medirTransacciones("Inserción directa", datos, c -> c::insercionDirecta, repeticiones, calentamientos);
            medirTransacciones("Quicksort (por id)", datos, c -> c::quicksort, repeticiones, calentamientos);
        }
    }

    private static void medirTransacciones(String nombre, Transaccion[] original,
                                           java.util.function.Function<Clasificador, Consumer<Transaccion[]>> selector,
                                           int repeticiones, int calentamientos) {
        Clasificador clasificador = new Clasificador();
        Consumer<Transaccion[]> algoritmo = selector.apply(clasificador);

        for (int r = 0; r < calentamientos; r++) {
            algoritmo.accept(original.clone());
        }

        long[] tiempos = new long[repeticiones];
        boolean estable = false;
        for (int r = 0; r < repeticiones; r++) {
            Transaccion[] copia = original.clone();
            long inicio = System.nanoTime();
            algoritmo.accept(copia);
            tiempos[r] = System.nanoTime() - inicio;
            if (r == 0) {
                estable = estaOrdenadoYEstable(copia);
            }
        }

        double ms = Medidor.mediana(tiempos) / 1_000_000.0;
        System.out.printf("  %-22s %10.2f ms  | estable=%s  | cmp=%d  mov=%d%n",
                nombre, ms, estable ? "sí" : "no", clasificador.getComparaciones(), clasificador.getMovimientos());
    }

    private static boolean estaOrdenadoYEstable(Transaccion[] datos) {
        for (int i = 1; i < datos.length; i++) {
            int tAnt = datos[i - 1].timestamp();
            int tAct = datos[i].timestamp();
            if (tAnt > tAct) {
                return false;
            }
            if (tAnt == tAct && datos[i - 1].id() >= datos[i].id()) {
                return false;
            }
        }
        return true;
    }
}
