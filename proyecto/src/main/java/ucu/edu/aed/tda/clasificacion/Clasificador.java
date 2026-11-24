package ucu.edu.aed.tda.clasificacion;
import java.util.Arrays;


public class Clasificador extends TClasificador {
    @Override
    void insercionDirecta(int[] datos) {
        for (int i = 1; i < datos.length; i++) {
            int aux = datos[i];
            int anterior = (i - 1);
            while ((anterior > 0) && (aux < datos[anterior])) {
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
        int[] aux = new int[3];

        for(int valor = 0; valor <= 2; valor++){
            aux[valor] = datos[valor];
        }

        int [] media = Arrays.stream(aux).sorted().toArray();

        return media[1];
    }

    @Override
    protected int particion(int[] datos, int i, int j, int pivote) {
        int izquierda = i;
        int derecha = j;

        do {
            intercambiar(datos, izquierda, derecha);
            while (datos[izquierda] < pivote) {
                izquierda = izquierda + 1;
            }
            while (datos[derecha] >= pivote) {
                derecha = derecha - 1;
            }
        } while (izquierda <= derecha);

        return izquierda;
    }

    @Override
    public void clasificacionDirecta(int[] datos) {
        for (int i = 0; i < datos.length; i++) {
            int indiceDelMenor = i;
            int valorMenor = datos[i];
            for (int k = i + 1; k < datos.length; k++) {
                if (valorMenor < datos[k]) {
                    indiceDelMenor = k;
                    valorMenor = datos[k];
                }
            }
            intercambiar(datos, i, indiceDelMenor);
        }
    }

    @Override
    public void heapsort(int[] datos) {
        int n = datos.length - 1;
        for (int i = n / 2; i >= 1; i--) {
            desplazaElemento(datos, i, n);
        }
        for (int k = n; k >= 2; k--) {
            intercambiar(datos, 1, k);
            desplazaElemento(datos, 1, k - 1);
        }
    }

    @Override
    protected void desplazaElemento(int[] datos, int primero, int ultimo) {
        int actual = primero;
        while (actual <= (ultimo / 2)) {
            if (ultimo == (2 * actual)) { //si actual tiene hijo
                if (datos[actual] > datos[2 * actual]) {
                    intercambiar(datos, actual, actual * 2);
                }
                actual = ultimo;
            } else {
                int menor = menorHijo(datos, (2 * actual), (2 * actual + 1));
                if (datos[actual] > datos[menor]) {
                    intercambiar(datos, actual, menor);
                    actual = menor;
                } else {
                    actual = ultimo;
                }
            }
        }
    }

    @Override
    public void cuentaPorDistribucion(int[] datos) {

    }

    @Override
    public void binsort(int[] datos) {

    }

    @Override
    public void bucketsort(int[] datos, int cantidadUrnas) {

    }

    @Override
    public void radixsort(int[] datos, int base) {

    }

    @Override
    protected void distribuirPorDigito(int[] datos, int exponente, int base) {

    }


    private int menorHijo(int[] datos, int primerHijo, int segundoHijo) {
        return datos[primerHijo] <= datos[segundoHijo] ? primerHijo : segundoHijo;
    }


}