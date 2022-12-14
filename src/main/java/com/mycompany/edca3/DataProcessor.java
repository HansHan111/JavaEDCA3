/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.edca3;

import java.util.LinkedList;

import nz.sodium.*;
import java.util.Timer;
import java.util.TimerTask;
import java.lang.Math;

public class DataProcessor {
    public Cell<String> cStrAllEvents;
    public LinkedList<LinkedList<Cell<String>>> cStrTableDatas;

    public Stream<GpsEvent> mergedStream;
    public Stream<GpsEvent> restrictedMergedStream;
    private GpsService gpsServ;
    Stream<GpsEvent>[] streams;
    LinkedList<StreamSink<GpsEvent>> slidedStreams;
    LinkedList<GpsEvent> startPositions;

    private Cell<String> cFromLatitude;
    private Cell<String> cToLatitude;
    private Cell<String> cFromLongitude;
    private Cell<String> cToLongitude;
    private Stream<Unit> sRestricition;

    public Cell<RestrictionRange> cRestrictionRage;
    public Cell<String> cRestrictionLabel;

    public DataProcessor() {
        gpsServ = new GpsService();
        streams = gpsServ.getEventStreams();
        mergedStream = new Stream<>();
        restrictedMergedStream = new Stream<>();
        slidedStreams = new LinkedList<>();
        cStrTableDatas = new LinkedList<>();
        startPositions = new LinkedList<>();
        // this.processStream();
    }

    public void setCRestrictionLabel() {
        cRestrictionRage = sRestricition.snapshot(cFromLatitude, cToLatitude, cFromLongitude, cToLongitude,
                (u, strFromLat, strToLat, strFromLong, strToLong) -> new RestrictionRange(strFromLat, strToLat,
                        strFromLong, strToLong))
                .hold(new RestrictionRange("", "", "", ""));
        cRestrictionLabel = cRestrictionRage.map(u -> u.toString());
        this.processStream();
    }

    private void processStream() {
        for (Stream<GpsEvent> s : streams) {
            // Range Restricted GpsEvent
            Stream<GpsEvent> sRestricted = s.snapshot(cRestrictionRage, (e, r) -> new RestrictionData(e, r))
                    .filter(u -> u.checkInRange()).map(u -> u.data);

            // Merge all stream Events and restricted Events
            mergedStream = mergedStream.orElse(s);
            restrictedMergedStream = restrictedMergedStream.orElse(sRestricted);

            // RowData of Display Table
            LinkedList<Cell<String>> rowData = new LinkedList<>();

            Cell<String> trackerId = s.map(u -> u.name).hold("");
            Cell<String> latitude = s.map(u -> String.valueOf(u.latitude)).hold("");
            Cell<String> longitude = s.map(u -> String.valueOf(u.longitude)).hold("");

            StreamSink<GpsEvent> slidedstream = new StreamSink<>();
            slidedStreams.add(slidedstream);
            Cell<GpsEvent> cellSlidedEvent = slidedstream.hold(new GpsEvent(null, 0, 0, 0));
            s.listen(e -> {
                if (cellSlidedEvent.sample().name == null) {
                    Timer t = new Timer();
                    SlidingInput slidingInput = new SlidingInput(t, slidedstream, e);
                    t.schedule(slidingInput, 100);
                }
                Timer t = new Timer();
                SlidingInput slidingInput = new SlidingInput(t, slidedstream, e);
                t.schedule(slidingInput, 5 * 60 * 1000);
            });

            Cell<String> distance = s.snapshot(cellSlidedEvent, (cur, old) -> distance(cur, old))
                    .hold("0");
            rowData.add(trackerId);
            rowData.add(latitude);
            rowData.add(longitude);
            rowData.add(distance);
            cStrTableDatas.add(rowData);
        }
        cStrAllEvents = restrictedMergedStream.map(u -> (u.name + ", lat:" + u.latitude + ", lon:"
                + u.longitude + ", alt:" + u.altitude)).hold("");
    }

    private String distance(GpsEvent cur, GpsEvent old) {
        if (!cur.name.equals(old.name))
            return "0";

        double ft = 0.3048;
        double deltaLat = (cur.latitude - old.latitude) * Math.PI / 180;
        double deltaLong = (cur.longitude - old.longitude) * Math.PI / 180;
        double curAlt = cur.altitude * ft;
        double oldAlt = old.altitude * ft;
        double deltaAlt = curAlt - oldAlt;
        double distance = Math
                .sqrt(Math.pow(deltaLat * oldAlt, 2) + Math.pow(deltaLong * oldAlt, 2) + Math.pow(deltaAlt, 2));
        return String.format("%f", distance);
    }

    // setter and getter
    public void setFromLatitude(Cell<String> cell) {
        this.cFromLatitude = cell;
    }

    public void setToLatitude(Cell<String> cell) {
        this.cToLatitude = cell;
    }

    public void setFromLongitude(Cell<String> cell) {
        this.cFromLongitude = cell;
    }

    public void setToLongitude(Cell<String> cell) {
        this.cToLongitude = cell;
    }

    public void setRestriction(Stream<Unit> stream) {
        this.sRestricition = stream;
    }

    private class RestrictionRange {
        public Double fromLat;
        public Double toLat;
        public Double fromLong;
        public Double toLong;

        public RestrictionRange(String strFromLat, String strToLat, String strFromLong, String strToLong) {
            this.fromLat = parseDouble(strFromLat, (double) -90);
            this.toLat = parseDouble(strToLat, (double) 90);
            this.fromLong = parseDouble(strFromLong, (double) -180);
            this.toLong = parseDouble(strToLong, (double) 180);
        }

        private Double parseDouble(String str, Double defaultValue) {
            try {
                return Double.parseDouble(str);
            } catch (Exception e) {
                return defaultValue;
            }
        }

        public String toString() {
            return "Restriction Range(Latitude: " + String.format("%.4f", this.fromLat) + "~"
                    + String.format("%.4f", this.toLat)
                    + ", Longitude: " + String.format("%.4f", this.fromLong) + "~" + String.format("%.4f", this.toLong)
                    + ")";
        }
    }

    private class RestrictionData {
        public GpsEvent data;
        public RestrictionRange range;

        public RestrictionData(GpsEvent data, RestrictionRange range) {
            this.data = data;
            this.range = range;
        }

        public Boolean checkInRange() {
            double latitude = this.data.latitude;
            double longitude = this.data.longitude;
            if (latitude < this.range.fromLat)
                return false;
            if (latitude > this.range.toLat)
                return false;
            if (longitude < this.range.fromLong)
                return false;
            if (longitude > this.range.toLong)
                return false;
            return true;
        }
    }

    private class SlidingInput extends TimerTask {
        public Timer timer;
        public GpsEvent data;
        public StreamSink<GpsEvent> stream;

        public SlidingInput(Timer timer, StreamSink<GpsEvent> stream, GpsEvent data) {
            this.timer = timer;
            this.stream = stream;
            this.data = data;
        }

        public void run() {
            stream.send(data);
            timer.cancel();
            timer.purge();
        }
    }
}
