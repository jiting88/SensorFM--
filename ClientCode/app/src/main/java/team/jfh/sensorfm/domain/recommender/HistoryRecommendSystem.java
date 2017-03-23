package team.jfh.sensorfm.domain.recommender;

import android.content.Context;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import team.jfh.sensorfm.data.entity.InputForRecommendation;
import team.jfh.sensorfm.data.entity.MusicRecord;
import team.jfh.sensorfm.data.repository.MusicRecordRepositorySQLite;


/**
 * Created by My on 2016/9/8.
 */
public class HistoryRecommendSystem {

    private  ArrayList<Integer> pulseListSport = new ArrayList<>();
    private  ArrayList<Integer> BPMListSport = new ArrayList<>();
    private  ArrayList<Integer> pulseListNotSport = new ArrayList<>();
    private  ArrayList<Integer> BPMListNotSport = new ArrayList<>();

    private  ArrayList<info> day = new ArrayList<>();
    private  ArrayList<info> evening = new ArrayList<>();
    private  ArrayList<info> night = new ArrayList<>();

    private List<MusicRecord> historyData;
    private InputForRecommendation inputForRecommendation;

    public HistoryRecommendSystem(Context context, InputForRecommendation inputForRecommendation) {
        this.inputForRecommendation = inputForRecommendation;
        this.historyData = new MusicRecordRepositorySQLite(context).getAllRecords();
    }

    //whether the user is doing sport
    public static boolean whetherSport(MusicRecord musicRecord) {
        return musicRecord.getPulse() > 100;
    }

    //split the pulse and bpm into 2 groups of lists to calculate their correlation respectively
    private  void splitSportSleep() {
        for (MusicRecord record : historyData) {
            if (whetherSport(record)) {
                pulseListSport.add(record.getPulse());
                BPMListSport.add(record.getBpm());
            } else {
                pulseListNotSport.add(record.getPulse());
                BPMListNotSport.add(record.getBpm());
            }
        }
    }

    private  double calculateAverage(ArrayList<Integer> list) {
        double sum = 0;
        for (int i = 0; i < list.size(); i++) {
            sum += (double) list.get(i);
        }
        return sum / list.size();
    }

    private  double calculateSDeviation(ArrayList<Integer> list) {
        double average = calculateAverage(list);
        double deviation = 0;
        for (int i = 0; i < list.size(); i++) {
            deviation += Math.pow(((double) list.get(i) - average), 2);
        }
        return Math.sqrt(deviation / list.size());
    }

    private  double calculateCovariance(ArrayList<Integer> listA, ArrayList<Integer> listB) {
        if (listA.size() != listB.size())
            return -1;
        double averageA = calculateAverage(listA);
        double averageB = calculateAverage(listB);
        double sum = 0;
        for (int i = 0; i < listA.size(); i++) {
            sum += ((double) listA.get(i) - averageA) * ((double) listB.get(i) - averageB);
        }
        return sum / listA.size();
    }

    public  double calculateCorrelation(ArrayList<Integer> listA, ArrayList<Integer> listB) {
        double covarianceAB = calculateCovariance(listA, listB);
        double standardDeviationA = calculateSDeviation(listA);
        double standardDeviationB = calculateSDeviation(listB);
        return covarianceAB / (standardDeviationA * standardDeviationB);
    }

    public  double correlationPulseBPMSport() {
        splitSportSleep();
        return calculateCorrelation(pulseListSport, BPMListSport);
    }

    public  double correlationPulseBPMSleep() {
        splitSportSleep();
        return calculateCorrelation(pulseListNotSport, BPMListNotSport);
    }

    public  int crudeBPM(ArrayList<Integer> list, int number) {
        ArrayList<Integer> tmp = new ArrayList<>();
        tmp.addAll(list);
        Collections.sort(tmp);
        int sum = 0;
        for (int i = number; i < number + 15; i++) {
            sum += tmp.get(i);
        }
        return sum / 15;
    }

    public  double[] linearRegression(ArrayList<Integer> listA, ArrayList<Integer> listB) {
        double aveA = calculateAverage(listA);
        double aveB = calculateAverage(listB);
        double sumxy = calculateCovariance(listA, listB) * listA.size();
        double sumx2 = Math.pow(calculateSDeviation(listA), 2) * listA.size();
        double A = sumxy / sumx2;
        double B = aveB - A * aveA;
        double[] arr = new double[]{A, B};
        return arr;
    }

    public  int crudeRecommendation() {
        double theta = 0.0001;
        int bpm;
        int i = 0;
        boolean status = inputForRecommendation.getMode().equals("sport");
        if (status) {
            double previousCorrelation = correlationPulseBPMSport();
            pulseListSport.add(inputForRecommendation.getHeartRate());
            BPMListSport.add(0);
            do {
                bpm = crudeBPM(BPMListSport, i);
                BPMListSport.set(BPMListSport.size() - 1, bpm);
                i += 2;
            } while (Math.abs(calculateCorrelation(pulseListSport, BPMListSport) - previousCorrelation) > theta);
            return bpm;
        } else {
            double previousCorrelation = correlationPulseBPMSleep();
            pulseListNotSport.add(inputForRecommendation.getHeartRate());
            BPMListNotSport.add(0);
            do {
                bpm = crudeBPM(BPMListNotSport, i);
                BPMListNotSport.set(BPMListNotSport.size() - 1, bpm);
                i += 2;
            } while (Math.abs(calculateCorrelation(pulseListNotSport, BPMListNotSport) - previousCorrelation) > theta);
            return bpm;
        }
    }

    public  void splitAccordingTime() {
        for (MusicRecord record : historyData) {
            info dataPoint = new info(record.getPulse(), record.getBpm());
            int tmp = record.getTime().getHours();
            if (tmp >= 7 && tmp < 17) {
                day.add(dataPoint);
            } else if (tmp >= 17 && tmp < 23) {
                evening.add(dataPoint);
            } else {
                night.add(dataPoint);
            }
        }
    }

    public  ArrayList<Integer>[] splitInfo(ArrayList<info> list) {
        ArrayList<Integer> pulse = new ArrayList<>();
        ArrayList<Integer> bpm = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            pulse.add(list.get(i).getPulse());
            bpm.add(list.get(i).getBPMSong());
        }
        ArrayList[] returnValue = new ArrayList[]{pulse, bpm};
        return returnValue;
    }

    public  int preciseRecommendation() {
        int bpm;
        splitAccordingTime();
        int presentHour = new Date().getHours();
        if (presentHour >= 7 && presentHour < 17) {
            ArrayList[] splitResult = splitInfo(day);
            double[] regressionResult = linearRegression(splitResult[0], splitResult[1]);
            bpm = (int) (regressionResult[0] * inputForRecommendation.getHeartRate() + regressionResult[1]);
        } else if (presentHour >= 17 && presentHour < 23) {
            ArrayList[] splitResult = splitInfo(evening);
            double[] regressionResult = linearRegression(splitResult[0], splitResult[1]);
            bpm = (int) (regressionResult[0] * inputForRecommendation.getHeartRate() + regressionResult[1]);
        } else {
            ArrayList[] splitResult = splitInfo(night);
            double[] regressionResult = linearRegression(splitResult[0], splitResult[1]);
            bpm = (int) (regressionResult[0] * inputForRecommendation.getHeartRate() + regressionResult[1]);
        }
        return bpm;
    }

    private class info {
        private int pulse;
        private int BPMSong;

        public info(int pulse, int BPMSong) {
            this.BPMSong = BPMSong;
            this.pulse = pulse;
        }

        public int getPulse() {
            return pulse;
        }

        public int getBPMSong() {
            return BPMSong;

        }
    }
}
