package spark;

import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.sql.Row;
import scala.Tuple2;
import utilities.ConstantFields;
import utilities.LoadData;
import utilities.TupleComparator;

import java.util.List;

public class Job3_CommonUsers {

    public static void main(String[] args) {
        // load the csv
        JavaRDD<Row> rdd = LoadData.readCsvToRDD();

        long startTime = System.currentTimeMillis();

        // PairRDD: prodotto, utente
        JavaPairRDD<String, String> product2user =
            rdd.mapToPair(row -> new Tuple2<>((String)row.get(ConstantFields.UserId), (String)row.get(ConstantFields.ProductId))).distinct();

        // PairRDD: utente, (prodotto 1, prodotto 2)
        JavaPairRDD<String, Tuple2<String, String>> user2prodCouple =
                product2user.join(product2user).filter(tuple -> tuple._2._1.compareTo(tuple._2._2)<0);

        // PairRDD: (prodotto 1, prodotto 2), count
        JavaPairRDD<String, Integer> commonUsers =
                user2prodCouple.mapToPair(tuple -> new Tuple2<>(tuple._2.toString(), 1))
                .reduceByKey((a, b) -> a+b).sortByKey();

//        commonUsers.coalesce(1, true).saveAsTextFile("/home/fabrizio/Scaricati/spark_job3_result");

        List<Tuple2<String, Integer>> tuples = commonUsers.collect();

        long endTime = System.currentTimeMillis();
        long totalTime = (endTime-startTime)/1000;
        System.out.println("\nTempo totale di esecuzione: " + totalTime + " sec");


    }
}