package ucu.edu.aed.tda.clasificacion;
import java.util.Arrays;
import java.util.ArrayList;

public class Clasificador extends TClasificador {
    @Override
    void insercionDirecta(int[] datos) {
        for (int i = 1; i < datos.length; i++) {
            int aux = datos[i];
            int anterior = (i - 1);
            while ((anterior >= 0) && (aux < datos[anterior])) {
                datos[anterior + 1] = datos[anterior];
                anterior--;
            }
            datos[anterior + 1] = aux;
        }
    }



    @Override
    void shell(int[] datos, Integer[] incrementos) {
        for (int i = 0; i < incrementos.length; i++) {
            int salto = incrementos[i];
            for (int k = salto; k < datos.length; k++) {
                int aux = datos[k];
                int puntero = (k - salto);
                while (puntero >= 0 && (aux < datos[puntero])) {
                    datos[puntero + salto] = datos[puntero];
                    puntero -= salto;
                }
                datos[puntero + salto] = aux;
            }
        }
    }

    @Override
    void burbuja(int[] datos) {
        for (int i = 0; i < datos.length - 1; i++) {
            for (int j = datos.length - 1; j > i; j--) {
                if (datos[j] < datos[j - 1]) {
                    intercambiar(datos, j, j - 1);
                }
            }
        }
    }

    @Override
    protected int obtenerPivote(int[] datos, int i, int j) {
        if (j - i < 2) {
            return i;
        }

        int medio = i + (j - i) / 2;
        int a = datos[i];
        int b = datos[medio];
        int c = datos[j];

        if ((a <= b && b <= c) || (c <= b && b <= a)) {
            return medio;
        } else if ((b <= a && a <= c) || (c <= a && a <= b)) {
            return i;
        } else {
            return medio;
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
            while (datos[izquierda] < pivote) {
                izquierda++;
            }
            while (datos[derecha] > pivote) {
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

    @Override
    public void clasificacionDirecta(int[] datos) {
        for (int i = 0; i < datos.length; i++) {
            int indiceDelMenor = i;
            int valorMenor = datos[i];
            for (int k = i + 1; k < datos.length; k++) {
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
        return datos[primerHijo] >= datos[segundoHijo] ? primerHijo : segundoHijo;
    }


}