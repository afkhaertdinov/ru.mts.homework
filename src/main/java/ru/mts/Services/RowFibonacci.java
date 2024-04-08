package ru.mts.Services;

import java.math.BigInteger;
import java.util.concurrent.Callable;

/**
 * Класс строит ряд Фибоначчи в диапазоне [startNumber:endNumber]. <br/>
 * Через конструктор необходимо инициализировать начальную и конечную позиции ряда Фибоначчи.<br/>
 * Имплементирует Callable возвращая BigInteger[] - рассчитанный ряд Фибоначчи.
 */
public class RowFibonacci implements Callable<BigInteger[]> {
    int startNumber, endNumber;

    /**
     * Конструктор, через который задаём диапазон расчётного ряда Фибоначчи.
     * @param startNumber Начальная (включительно) позиция ряда Фибоначчи;
     * @param endNumber Конечная (включительно) позиция ряда Фибоначчи.
     */
    public RowFibonacci(int startNumber, int endNumber) {
        this.startNumber = startNumber;
        this.endNumber = endNumber;
    }

    @Override
    public BigInteger[] call() {
        if (endNumber <= startNumber)
            return new BigInteger[0]; // При неверных границах возвращаем пустой массив

        BigInteger[] bigIntegers = new BigInteger[endNumber - startNumber + 1];
        bigIntegers[0] = fibonacci(startNumber);
        bigIntegers[1] = fibonacci(startNumber + 1);
        for (int i = 2; i < (endNumber - startNumber + 1); i++)
            bigIntegers[i] = bigIntegers[i - 2].add(bigIntegers[i - 1]);
        return bigIntegers;
    }

    /**
     * Метод рассчитывающий методом перемножения матриц число Фибоначчи для n-го элемента.
     * @param n номер элемента, для которого рассчитывается число Фибоначчи;
     * @return число Фибоначчи.
     */
    public static BigInteger fibonacci(int n) {
        if (n == 0) {
            return BigInteger.ZERO;
        }
        BigInteger[][] a = {
                {BigInteger.ONE, BigInteger.ONE},
                {BigInteger.ONE, BigInteger.ZERO}
        };
        BigInteger[][] powerOfA = matrixPowerFast(a, n - 1);
        // nthFibonacci = powerOfA[0][0] * F_1 + powerOfA[0][0] * F_0 = powerOfA[0][0] * 1 + powerOfA[0][0] * 0
        return powerOfA[0][0];
    }

    /**
     * Возведения матрицы 2 на 2 в степень n.
     * @param a матрицы 2 на 2;
     * @param n степень, в которую возводим матрицу;
     * @return рассчитанная матрица.
     */
    public static BigInteger[][] matrixPowerFast(BigInteger[][] a, int n) {
        if (n == 0) {
            // любая матрица в нулевой степени равна единичной матрице
            return new BigInteger[][]{
                    {BigInteger.ONE, BigInteger.ZERO},
                    {BigInteger.ZERO, BigInteger.ONE}
            };
        } else if (n % 2 == 0) {
            // a ^ (2k) = (a ^ 2) ^ k
            return matrixPowerFast(matrixMultiplication(a, a), n / 2);
        } else {
            // a ^ (2k + 1) = (a) * (a ^ 2k)
            return matrixMultiplication(matrixPowerFast(a, n - 1), a);
        }
    }


    /**
     * Матричное умножение двух матриц размера 2 на 2.
     * @param a первая матрица 2 на 2;
     * @param b вторая матрица 2 на 2;
     * @return рассчитанная матрица 2 на 2.
     */
    static BigInteger[][] matrixMultiplication(BigInteger[][] a, BigInteger[][] b) {
        // [a00 * b00 + a01 * b10, a00 * b01 + a01 * b11]
        // [a10 * b00 + a11 * b10, a10 * b01 + a11 * b11]
        return new BigInteger[][]{
                {
                        a[0][0].multiply(b[0][0]).add(a[0][1].multiply(b[1][0])),
                        a[0][0].multiply(b[0][1]).add(a[0][1].multiply(b[1][1]))
                },
                {
                        a[1][0].multiply(b[0][0]).add(a[1][1].multiply(b[1][0])),
                        a[1][0].multiply(b[0][1]).add(a[1][1].multiply(b[1][1]))
                },
        };
    }
}
