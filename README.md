NDK vs Java : Raster Operation
![raster](https://lh3.googleusercontent.com/-LFi7l5iSlI0/T4lQ8QC4bGI/AAAAAAAACSs/w98XbDsTORo/s800/rasterNDK.png "rasterNDK")

Raster Operationの演算部分をNDKとJavaで比較。<br>
以下のようにNDKとJavaのraster関数の処理時間を計測し、100回平均している。

    start = System.nanoTime();
    raster(pixels, maskpixels, width, height, 1);// NDK Raster
    stop = System.nanoTime();

    start = System.nanoTime();
    rasterJ(pixels, maskpixels, width, height, 1);// Java Raster
    stop = System.nanoTime();

- ICONIA A500
 - 1回目 NDK:2,133us, Java:14,138us 約7倍性能差<br>
 - 2回目 NDK:2,114us, Java:4,727us 約2倍性能差      

- Galaxy SII
 - 1回目 NDK:2,111us, Java:18,956us 約9倍性能差<br>
 - 2回目 NDK:2,251us, Java:7,742us 約3.5倍性能差

Javaは連続して実行すると、JIT効果で実行時間が半分以下になっているようす。

