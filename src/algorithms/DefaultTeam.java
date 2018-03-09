package algorithms;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static java.util.stream.Collectors.toCollection;

public class DefaultTeam {

    private int edgeThreshold=55;
    private int eTc=edgeThreshold*edgeThreshold;
    private int numero = 2;
    private boolean onlyres = true;
    private ArrayList<Point> points=readFromFile(numero, "input");
    private ArrayList<Point> filescore=readFromFile(numero, "score");
    double scoredufile = scoreEntraineur(filescore, edgeThreshold);

    private int nbInst=points.size();
    private static int count=0;

    public ArrayList<Point> calculEntraineur(ArrayList<Point> inpoints, int edgeThreshold) {
        long time = System.currentTimeMillis();
        System.out.println("Score du file actuel : " + scoredufile);
        if (onlyres) {
            return filescore;
        }
        ArrayList<Point> res = (ArrayList<Point>) filescore.clone();
//        ArrayList<Point> gready = fullGready(points);
        ArrayList<Point> gready = (ArrayList<Point>) filescore.clone();
        ArrayList<Point> swap=swap(gready, numero);


        double scoreducalcul = scoreEntraineur(gready, edgeThreshold);
        if (scoreducalcul < scoredufile) {
            printToFile(numero, gready);
            System.out.println("NEW SCOOOOOOORE : " + scoreducalcul);
        }

        System.out.println("Temps total : "+(double) (System.currentTimeMillis() - time) / 1000.0+" sec");
        return gready;
    }

    private ArrayList<Point> fullGready(ArrayList<Point> inpoints) {
        AtomicReference<Double> scoreBest = new AtomicReference<>(Double.MAX_VALUE);
        AtomicReference<ArrayList<Point>> best = new AtomicReference<>(new ArrayList<>());
        AtomicInteger count = new AtomicInteger();
        AtomicInteger size = new AtomicInteger(inpoints.size());
        points.parallelStream().forEach(p ->
        {
            count.getAndIncrement();
            ArrayList<Point> greadyres = gready(inpoints, points.indexOf(p));
            double v = scoreEntraineur(greadyres, edgeThreshold);
            if (v < scoreBest.get()) {
                scoreBest.set(v);
                best.set(greadyres);
                System.out.println("IMPROVED !");
            }
            System.out.println(100 * count.get() / size.get() + "%... worker " + points.indexOf(p) + "   Score : " + v + "   Best is :" + scoreBest);
//            System.out.println("   Degre du premier point :"+degree(points, p, edgeThreshold)+"    Ratio:"+(v/degree(points, p, edgeThreshold)));
        });
        return best.get();
    }

    private ArrayList<Point> gready(ArrayList<Point> inpoints, int... indexpremier) {
        ArrayList<Point> points = (ArrayList<Point>) inpoints.clone();
        ArrayList<Point> result = new ArrayList<>();

        Point first;
        if (indexpremier.length > 0) {
            first = points.get(indexpremier[0]);
        } else {
            first = points.get(indexleastdegre(points, edgeThreshold));
        }
        result.add(first);
        points.remove(first);

        double currentscore = cutScore(result, points, edgeThreshold);

        while (!points.isEmpty()) {
            Point p = minCut(currentscore, result, points, edgeThreshold);
            result.add(p);
            currentscore += scoreOpt(p, result, inpoints, edgeThreshold);
            points.remove(p);

//            System.out.println("GREADY : " + result.size()*1./inpoints.size()*100.0+"%" );

        }
        return result;
    }

    private Point minCut(double score, ArrayList<Point> alignement, ArrayList<Point> points, int edgeThreshold) {
        int index = 0;
        Point best = points.get(0);
        alignement.add(best);
        double currentScore = score + scoreOpt(best, alignement, points, edgeThreshold);
        alignement.remove(best);

        for (int i = 1; i < points.size(); i++) {
            alignement.add(points.get(i));
            double newS = score + scoreOpt(points.get(i), alignement, points, edgeThreshold);
            if (newS < currentScore) {
                currentScore = newS;
                index = i;
            }
            alignement.remove(points.get(i));
        }
        return points.get(index);
    }

    private double scoreOpt(Point q, ArrayList<Point> coupe, ArrayList<Point> points, int edgeThreshold) {
        int toAdd = 0;
        int moins = degree(coupe, q, edgeThreshold);
        for (Point p : points) {
            if (distancecarree(q, p) <= eTc) {
                toAdd++;
            }
        }
        return toAdd - moins;
    }

    private double cutScore(ArrayList<Point> cut, ArrayList<Point> points, int edgeThreshold) {
        return cut.parallelStream().mapToLong(p -> points.stream()
                .filter(q -> !cut.contains(q) && distancecarree(p, q) <= eTc)
                .count()).sum();
    }

    private int indexleastdegre(ArrayList<Point> points, int edgeThreshold) {
        AtomicInteger minValue = new AtomicInteger(Integer.MAX_VALUE);
        AtomicInteger index = new AtomicInteger(-1);
        points.forEach(i -> {
            int degre = degree(points, i, edgeThreshold);
            if (degre < minValue.get()) {
                minValue.set(degre);
                index.set(points.indexOf(i));
            }
        });
        if (index.get() == -1)
            System.out.println("PWOBLEM !");
        return index.get();
    }

    private ArrayList<Point> swap(ArrayList<Point> input, int numero){
        ArrayList<Point> result= (ArrayList<Point>) input.clone();

        double scorefile = scoreEntraineur(readFromFile(numero, "score"), edgeThreshold);
        double scoreBefore = scoreEntraineur(result, edgeThreshold);

        if (scoreBefore < scorefile) {
            printToFile(numero, result);
            System.out.println("START WITH BETTER VALUE : " + scoreBefore);
            scorefile = scoreBefore;
        }
        scoreBefore = scoreEntraineur(result, edgeThreshold);
        for (int i = 0; i < result.size(); i++) {
            long timeLoop = System.currentTimeMillis();
            System.out.println("Loop "+i);
            for (int j = i; j < result.size(); j++) {
                Collections.swap(result, i, j);
                double scoreAfter = scoreEntraineur(result, edgeThreshold);
                if (scoreAfter < scoreBefore) {
                    printToFile(numero, result);
                    System.out.println("NEW HIGH SCORE : " + scoreAfter);
                    scoreBefore = scoreAfter;
                    scorefile = scoreAfter;
                }
                else Collections.swap(result, i, j);
            }
            System.out.println("Loop time : " + (double) (System.currentTimeMillis() - timeLoop) / 1000.0 + " sec");
        }
        return result;
    }

    private double scoreEntraineur(ArrayList<Point> alignement, int edgeThreshold) {
        return alignement.parallelStream().mapToDouble(p -> scoreVi(alignement, alignement.indexOf(p), edgeThreshold)).max().getAsDouble();
    }

    private double scoreVi(ArrayList<Point> alignement, int i, int edgeThreshold) {
        int s=0;
        for (int j=0;j<=i;j++){
            for (int k=i+1;k<alignement.size();k++){
//                if (alignement.get(j).distance(alignement.get(k))<=edgeThreshold) s++;
                if (distancecarree(alignement.get(j), alignement.get(k))<=eTc) s++;
            }
        }
        return s;
    }

    private int degree(ArrayList<Point> points, Point p, int edgeThreshold) {
        int d=-1;
        for (Point point : points) if (distancecarree(point, p) <= eTc) d++;
        return d;
    }

//    private boolean isValid(ArrayList<Point> result, ArrayList<Point> points) {
//        if (!result.isEmpty()){
//            for (Point point : points) {
//                if (!result.contains(point)) {
//                    return true;
//                }
//            }
//            return false;
//        }else{
//            return false;
//        }
//    }


//    private Point getMaxDegreePoint (ArrayList<Point> coupe, ArrayList<Point> points, int edgeThreshold){
//        int degreMax = degree(points,points.indexOf(coupe.get(0)),edgeThreshold);
//        Point degreMaxPoint = coupe.get(0);
//        for (int i = 1; i<coupe.size(); i++) {
//            if (coupe.contains(degreMaxPoint))
//                continue;
//            int currentDegree = degree(points,points.indexOf(coupe.get(i)),edgeThreshold);
//            if ( degreMax < currentDegree) {
//                degreMax = currentDegree;
//                degreMaxPoint = coupe.get(i);
//            }
//        }
//        return  degreMaxPoint;
//    }

    private double distancecarree (Point p1, Point p2) {
        double var1 = p1.getX() - p2.getX();
        double var2 = p1.getY() - p2.getY();
        return var1 * var1 + var2 * var2;
    }

//    private void remainingtime(long debut, int size, int curr){
//        if(curr>1)
//            System.out.println("Remaining time :"+Math.round((((double)(System.currentTimeMillis()-debut))/curr)*(size-curr))/1000.0);
//    }

    private ArrayList<Point> neighbor(Point point, ArrayList<Point> inpoints){
        return inpoints.stream().filter(p -> distancecarree(p, point)<=eTc).collect(toCollection(ArrayList::new));}

    private int diffScore(Point point, ArrayList<Point> res){
        return neighbor(point, points).stream().mapToInt(p -> res.contains(p) ? -1 : 1).sum(); }

//    private ArrayList<Point> near(ArrayList<Point> inpoints) {
//        AtomicInteger ai = new AtomicInteger();
//        ArrayList<Point> res = inpoints.parallelStream().map(p -> {
//            int i = ai.incrementAndGet();
////            System.out.println("worker: " + i);
//            ArrayList<Point> points = new ArrayList<Point>(inpoints);
//            ArrayList<Point> result = new ArrayList<Point>();
//            points.remove(p);
//            result.add(p);
//            while (!points.isEmpty()) {
//                Point point = points.stream().min(Comparator.comparingInt(po -> Math.abs(diffScore(po, points))))
//                        .orElse(null);
//                if (point == null)
//                    System.out.println("NullPointerExceptionKappa");
//                points.remove(point);
//                result.add(point);
//            }
//            count++;
//            System.out.println("Gready : "+(10000*count/nbInst)/100.0+"%");
//            return result;
//        }).min(Comparator.comparingDouble(ar -> scoreEntraineur(ar, edgeThreshold))).orElse(null);
//
//        assert res != null;
//        System.out.println("Resultat entraineur: score = " + scoreEntraineur(res, edgeThreshold));
//        return res;
//    }

    private ArrayList<Point> readFromFile(int numerofichier, String folder) {
        String filename=folder+"/"+folder+""+numerofichier+".points";
        String line;
        String[] coordinates;
        ArrayList<Point> points = new ArrayList<>();
        try {
            BufferedReader input = new BufferedReader(
                    new InputStreamReader(new FileInputStream(filename))
            );
            try {
                while ((line=input.readLine())!=null) {
                    coordinates=line.split("\\s+");
                    points.add(new Point(Integer.parseInt(coordinates[0]),
                            Integer.parseInt(coordinates[1])));
                }
            } catch (IOException e) {
                System.err.println("Exception: interrupted I/O.");
            } finally {
                try {
                    input.close();
                } catch (IOException e) {
                    System.err.println("I/O exception: unable to close "+filename);
                }
            }
        } catch (FileNotFoundException e) {
            System.err.println("Input file not found.");
        }
        return points;
    }
    private void printToFile(int numerofichier ,ArrayList<Point> points){
        ArrayList<Point> score = readFromFile(numerofichier, "score");
        if (scoreEntraineur(points, edgeThreshold) > scoreEntraineur(score, edgeThreshold)) {
            System.out.println("Nooooooon l'était mieux avant !!!!!! J'écris pas.");
            return;
        }
        String filename="score/score"+numerofichier+".points";
        try {
            PrintStream output = new PrintStream(new FileOutputStream(filename));
            int x,y;
            for (Point p:points) output.println(Integer.toString((int)p.getX())+" "+Integer.toString((int)p.getY()));
            output.close();
        } catch (FileNotFoundException e) {
            System.err.println("I/O exception: unable to create "+filename);
        }
    }


//    public ArrayList<Point> calculCoupeMax(ArrayList<Point> inpoints, int edgeThreshold) {
//        ArrayList<Point> points = (ArrayList<Point>)inpoints.clone();
//        ArrayList<Point> result = new ArrayList<Point>();
//
//        double maxScore = 0;
//        boolean noBetter = false;
//
//        int nbMaxDegree = 0;
//        Point maxDegreePoint = null;
//        for (int i = 0; i<points.size(); i++) {
//            int currentDegree = degree(points,i,edgeThreshold);
//            if ( nbMaxDegree < currentDegree) {
//                nbMaxDegree = currentDegree;
//                maxDegreePoint = points.get(i);
//            }
//        }
//
//        if (null == maxDegreePoint){
//            System.out.println("ERRRROOOOOORR");
//            return null;
//        }
//
//        result.add(maxDegreePoint);
//        while (!noBetter) {
//            double currentScore = 0;
//            do {
//                ArrayList<Point> pointCoupeNeighbors = new ArrayList<>();
//                for (Point pointCoupe: result) {
//                    ArrayList<Point> neighbors = getNeighborsPointCoupe(pointCoupe,result,points,edgeThreshold);
//                    pointCoupeNeighbors.addAll(neighbors);
//                }
//
//                Point degreMaxPoint = getMaxDegreePoint(pointCoupeNeighbors,points,edgeThreshold);
//                result.add(degreMaxPoint);
//
//            } while (isValid(result,points));
//            System.out.println("COUPE = "+result.toString());
//
//            currentScore = scoreCoupe(result,points,edgeThreshold);
//            System.out.println("score = "+currentScore);
//
//            if (maxScore < currentScore){
//                maxScore = currentScore;
//            }else{
//                System.out.println("Pas meilleur !");
//                noBetter = true;
//            }
//            result.clear();
//        }
//
//        System.out.println("Resultat coupe: score = "+scoreCoupe(result, inpoints, edgeThreshold));
//        return result;
//    }
//    private void swapstream(int numero){
//        int edgeThreshold=55;
//        ArrayList<Point> points=readFromFile(numero, "input");
//        ArrayList<Point> result = readFromFile(numero, "score");
//        IntStream.range(0, result.size()-1).parallel().forEach(i ->
//        {
//            IntStream.range(i, result.size() - 1).parallel().forEach(j ->
//                    {
//                        double scoreBefore = scoreEntraineur(result, edgeThreshold);
//                        Collections.swap(result, i, j);
//                        double scoreAfter = scoreEntraineur(result, edgeThreshold);
//                        if (scoreAfter < scoreBefore) {
//                            printToFile(numero, result);
//                            System.out.println("NEW HIGH SCORE : " + scoreAfter);
//                        } else {
//                            Collections.swap(result, i, j);
//                        }
//                    }
//            );
//        });
//    }
//    private Point getMaxDegreePoint(ArrayList<Point> points, int edgeThreshold){
//        return points.stream()
//                .max(Comparator.comparingInt(point -> degree(points,points.indexOf(point),edgeThreshold)))
//                .get();
//        /*.reduce((point, point2) -> {
//                   if (degree(points,points.indexOf(point),edgeThreshold) <
//                           degree(points,points.indexOf(point2),edgeThreshold))
//                            return point2;
//                   else return point;
//                }).get();*/
//
//    }
}
