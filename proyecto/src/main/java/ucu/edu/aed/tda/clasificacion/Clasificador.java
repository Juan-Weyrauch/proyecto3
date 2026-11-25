package ucu.edu.aed.tda.clasificacion;
import java.util.Arrays;
import java.util.ArrayList;
import ucu.edu.aed.tda.clasificacion.datos.Transaccion;

public class Clasificador extends TClasificador {
    @Override
    void insercionDirecta(int[] datos) {
        reiniciarContadores();
        for (int i = 1; i < datos.length; i++) {
            int aux = datos[i];
            int anterior = (i - 1);
            while (anterior >= 0) {
                contarComparacion();
                if (aux < datos[anterior]) {
                    datos[anterior + 1] = datos[anterior];
                    contarMovimiento();
                    anterior--;
                } else {
                    break;
                }
            }
            datos[anterior + 1] = aux;
            contarMovimiento();
        }
    }



    @Override
    void shell(int[] datos, Integer[] incrementos) {
        reiniciarContadores();
        for (int i = 0; i < incrementos.length; i++) {
            int salto = incrementos[i];
            for (int k = salto; k < datos.length; k++) {
                int aux = datos[k];
                int puntero = (k - salto);
                while (puntero >= 0) {
                    contarComparacion();
                    if (aux < datos[puntero]) {
                        datos[puntero + salto] = datos[puntero];
                        contarMovimiento();
                        puntero -= salto;
                    } else {
                        break;
                    }
                }
                datos[puntero + salto] = aux;
                contarMovimiento();
            }
        }
    }

    @Override
    void burbuja(int[] datos) {
        reiniciarContadores();
        for (int i = 0; i < datos.length - 1; i++) {
            for (int j = datos.length - 1; j > i; j--) {
                contarComparacion();
                if (datos[j] < datos[j - 1]) {
                    intercambiar(datos, j, j - 1);
                }
            }
        }
    }

    @Override
    protected int obtenerPivote(int[] datos, int i, int j) {
        if (i >= j) {
            return -1;
        }

        int medio = i + (j - i) / 2;
        contarComparacion();
        boolean igualMedio = datos[i] == datos[medio];
        contarComparacion();
        boolean igualFin = datos[i] == datos[j];

        if (igualMedio && igualFin) {
            int distinto = i + 1;
            while (distinto <= j) {
                contarComparacion();
                if (datos[distinto] == datos[i]) {
                    distinto++;
                } else {
                    break;
                }
            }
            if (distinto > j) {
                return -1;
            }
            contarComparacion();
            if (datos[i] > datos[distinto]) {
                return i;
            }
            if (distinto < j) {
                return distinto;
            }
            intercambiar(datos, i, j);
            return i;
        }

        int indiceMediana = medianaDeTres(datos, i, medio, j);
        if (indiceMediana == j) {
            intercambiar(datos, i, j);
            return i;
        }
        return indiceMediana;
    }

    private int medianaDeTres(int[] datos, int a, int b, int c) {
        contarComparacion();
        if (datos[a] < datos[b]) {
            contarComparacion();
            if (datos[b] < datos[c]) {
                return b;
            }
            contarComparacion();
            return datos[a] < datos[c] ? c : a;
        } else {
            contarComparacion();
            if (datos[a] < datos[c]) {
                return a;
            }
            contarComparacion();
            return datos[b] < datos[c] ? c : b;
        }
    }
    @Override
    protected int obtenerPivoteIngenuo(int[] datos, int i, int j) {
        if (i >= j) {
            return -1;
        }
        if (i + 1 == j) {
            return i;
        }
        contarComparacion();
        int maximo = Math.max(datos[i], datos[i + 1]);
        if (maximo == datos[i]) {
            return i;
        }
        return i + 1;
    }


    @Override
    protected int particion(int[] datos, int i, int j, int pivote) {
        int izquierda = i;
        int derecha = j;

        while (izquierda <= derecha) {
            while (true) {
                contarComparacion();
                if (datos[izquierda] < pivote) {
                    izquierda++;
                } else {
                    break;
                }
            }
            while (true) {
                contarComparacion();
                if (datos[derecha] > pivote) {
                    derecha--;
                } else {
                    break;
                }
            }
            if (izquierda <= derecha) {
                intercambiar(datos, izquierda, derecha);
                izquierda++;
                derecha--;
            }
        }

        return izquierda;
    }

    @Override
    public void clasificacionDirecta(int[] datos) {
        reiniciarContadores();
        for (int i = 0; i < datos.length; i++) {
            int indiceDelMenor = i;
            int valorMenor = datos[i];
            for (int k = i + 1; k < datos.length; k++) {
                contarComparacion();
                if (datos[k] < valorMenor) {
                    indiceDelMenor = k;
                    valorMenor = datos[k];
                }
            }
            intercambiar(datos, i, indiceDelMenor);
        }
    }

    @Override
    public void heapsort(int[] datos) {
        reiniciarContadores();
        int n = datos.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            desplazaElemento(datos, i, n - 1);
        }
        for (int k = n - 1; k >= 1; k--) {
            intercambiar(datos, 0, k);
            desplazaElemento(datos, 0, k - 1);
        }
    }

    @Override
    protected void desplazaElemento(int[] datos, int primero, int ultimo) {
        int actual = primero;
        while (2 * actual + 1 <= ultimo) {
            int hijoIzquierdo = 2 * actual + 1;
            int hijoDerecho = hijoIzquierdo + 1;
            int mayor = (hijoDerecho <= ultimo)
                    ? mayorHijo(datos, hijoIzquierdo, hijoDerecho)
                    : hijoIzquierdo;

            contarComparacion();
            if (datos[actual] < datos[mayor]) {
                intercambiar(datos, actual, mayor);
                actual = mayor;
            } else {
                break;
            }
        }
    }

    @Override
    public void cuentaPorDistribucion(int[] datos) {
        if (datos.length < 2) {
            return;
        }

        int[] rango = obtenerRango(datos);
        int minimo = rango[0];
        long amplitud = (long) rango[1] - minimo + 1;

        if (amplitud > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("El rango de claves es demasiado grande");
        }

        int[] cuenta = new int[(int) amplitud];
        int[] salida = new int[datos.length];

        for (int i = 0; i < datos.length; i++) {
            cuenta[datos[i] - minimo]++;
        }

        for (int i = 1; i < cuenta.length; i++) {
            cuenta[i] = cuenta[i] + cuenta[i - 1];
        }

        for (int i = datos.length - 1; i >= 0; i--) {
            int clave = datos[i] - minimo;
            int posicion = cuenta[clave] - 1;
            salida[posicion] = datos[i];
            cuenta[clave]--;
        }

        for (int i = 0; i < datos.length; i++) {
            datos[i] = salida[i];
        }
    }

    @Override
    public void binsort(int[] datos) {
        if (datos.length < 2) {
            return;
        }

        int[] rango = obtenerRango(datos);
        int minimo = rango[0];
        long amplitud = (long) rango[1] - minimo + 1;

        if (amplitud > Integer.MAX_VALUE) {
            throw new IllegalArgumentException("El rango de claves es demasiado grande");
        }

        int cantidadUrnas = (int) amplitud;
        ArrayList<ArrayList<Integer>> urnas = new ArrayList<>();

        for (int i = 0; i < cantidadUrnas; i++) {
            urnas.add(new ArrayList<>());
        }

        for (int i = 0; i < datos.length; i++) {
            int indice = datos[i] - minimo;
            urnas.get(indice).add(datos[i]);
        }

        int posicion = 0;

        for (int i = 0; i < cantidadUrnas; i++) {
            ArrayList<Integer> urna = urnas.get(i);

            for (int j = 0; j < urna.size(); j++) {
                datos[posicion] = urna.get(j);
                posicion++;
            }
        }
    }

    @Override
    public void bucketsort(int[] datos, int cantidadUrnas) {
        if (cantidadUrnas <= 0) {
            throw new IllegalArgumentException("La cantidad de urnas debe ser positiva");
        }

        if (datos.length < 2) {
            return;
        }

        int[] rango = obtenerRango(datos);
        int minimo = rango[0];
        long amplitud = (long) rango[1] - minimo + 1;
        ArrayList<ArrayList<Integer>> urnas = new ArrayList<>();

        for (int i = 0; i < cantidadUrnas; i++) {
            urnas.add(new ArrayList<>());
        }

        for (int i = 0; i < datos.length; i++) {
            long distancia = (long) datos[i] - minimo;
            int indice = (int) ((distancia * cantidadUrnas) / amplitud);
            urnas.get(indice).add(datos[i]);
        }

        int posicion = 0;

        for (int i = 0; i < cantidadUrnas; i++) {
            ArrayList<Integer> urna = urnas.get(i);

            for (int j = 1; j < urna.size(); j++) {
                int aux = urna.get(j);
                int anterior = j - 1;

                while (anterior >= 0 && aux < urna.get(anterior)) {
                    urna.set(anterior + 1, urna.get(anterior));
                    anterior--;
                }

                urna.set(anterior + 1, aux);
            }

            for (int j = 0; j < urna.size(); j++) {
                datos[posicion] = urna.get(j);
                posicion++;
            }
        }
    }

    @Override
    public void radixsort(int[] datos, int base) {
        if (base < 2) {
            throw new IllegalArgumentException("La base debe ser mayor o igual a 2");
        }

        if (datos.length == 0) {
            return;
        }

        int[] rango = obtenerRango(datos);

        if (rango[0] < 0) {
            throw new IllegalArgumentException("Los valores deben ser no negativos");
        }

        int maximo = rango[1];
        int exponente = 1;

        while (maximo / exponente > 0) {
            distribuirPorDigito(datos, exponente, base);

            if (exponente > maximo / base) {
                break;
            }

            exponente = exponente * base;
        }
    }

    @Override
    protected void distribuirPorDigito(int[] datos, int exponente, int base) {
        ArrayList<ArrayList<Integer>> urnas = new ArrayList<>();

        for (int i = 0; i < base; i++) {
            urnas.add(new ArrayList<>());
        }

        for (int i = 0; i < datos.length; i++) {
            int digito = (datos[i] / exponente) % base;
            urnas.get(digito).add(datos[i]);
        }

        int posicion = 0;

        for (int i = 0; i < base; i++) {
            ArrayList<Integer> urna = urnas.get(i);

            for (int j = 0; j < urna.size(); j++) {
                datos[posicion] = urna.get(j);
                posicion++;
            }
        }
    }


    private int mayorHijo(int[] datos, int primerHijo, int segundoHijo) {
        contarComparacion();
        return datos[primerHijo] >= datos[segundoHijo] ? primerHijo : segundoHijo;
    }

    public void insercionDirecta(Transaccion[] datos) {
        reiniciarContadores();
        for (int i = 1; i < datos.length; i++) {
            Transaccion aux = datos[i];
            int anterior = i - 1;
            while (anterior >= 0) {
                contarComparacion();
                if (aux.timestamp() < datos[anterior].timestamp()) {
                    datos[anterior + 1] = datos[anterior];
                    contarMovimiento();
                    anterior--;
                } else {
                    break;
                }
            }
            datos[anterior + 1] = aux;
            contarMovimiento();
        }
    }

    public void quicksort(Transaccion[] datos) {
        reiniciarContadores();
        quicksort(datos, 0, datos.length - 1);
    }

    private void quicksort(Transaccion[] datos, int i, int j) {
        if (i <= j) {
            int indicePivote = obtenerPivote(datos, i, j);
            if (indicePivote >= i && indicePivote < j) {
                Transaccion pivote = datos[indicePivote];
                int k = particion(datos, i, j, pivote);
                quicksort(datos, i, k - 1);
                quicksort(datos, k, j);
            }
        }
    }

    private int obtenerPivote(Transaccion[] datos, int i, int j) {
        if (i >= j) {
            return -1;
        }

        int medio = i + (j - i) / 2;
        if (compararTransacciones(datos[i], datos[medio]) == 0
                && compararTransacciones(datos[i], datos[j]) == 0) {
            int distinto = i + 1;
            while (distinto <= j && compararTransacciones(datos[distinto], datos[i]) == 0) {
                distinto++;
            }
            if (distinto > j) {
                return -1;
            }
            if (compararTransacciones(datos[i], datos[distinto]) > 0) {
                return i;
            }
            if (distinto < j) {
                return distinto;
            }
            intercambiar(datos, i, j);
            return i;
        }

        int indiceMediana = medianaDeTres(datos, i, medio, j);
        if (indiceMediana == j) {
            intercambiar(datos, i, j);
            return i;
        }
        return indiceMediana;
    }

    private int medianaDeTres(Transaccion[] datos, int a, int b, int c) {
        if (compararTransacciones(datos[a], datos[b]) < 0) {
            if (compararTransacciones(datos[b], datos[c]) < 0) {
                return b;
            }
            return compararTransacciones(datos[a], datos[c]) < 0 ? c : a;
        } else {
            if (compararTransacciones(datos[a], datos[c]) < 0) {
                return a;
            }
            return compararTransacciones(datos[b], datos[c]) < 0 ? c : b;
        }
    }

    private int particion(Transaccion[] datos, int i, int j, Transaccion pivote) {
        int izquierda = i;
        int derecha = j;
        while (izquierda <= derecha) {
            while (compararTransacciones(datos[izquierda], pivote) < 0) {
                izquierda++;
            }
            while (compararTransacciones(datos[derecha], pivote) > 0) {
                derecha--;
            }
            if (izquierda <= derecha) {
                intercambiar(datos, izquierda, derecha);
                izquierda++;
                derecha--;
            }
        }
        return izquierda;
    }

    private int compararTransacciones(Transaccion a, Transaccion b) {
        contarComparacion();
        if (a.timestamp() != b.timestamp()) {
            return Integer.compare(a.timestamp(), b.timestamp());
        }
        return Integer.compare(a.id(), b.id());
    }

    private void intercambiar(Transaccion[] datos, int i, int j) {
        Transaccion aux = datos[i];
        datos[i] = datos[j];
        datos[j] = aux;
        contarMovimiento();
    }

}