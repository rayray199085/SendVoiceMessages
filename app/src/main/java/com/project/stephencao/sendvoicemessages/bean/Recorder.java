package com.project.stephencao.sendvoicemessages.bean;

public class Recorder {
        float duration;
        String filePath;

        public Recorder(float duration, String filePath) {
            this.duration = duration;
            this.filePath = filePath;
        }

        public float getDuration() {
            return duration;
        }

        public void setDuration(float duration) {
            this.duration = duration;
        }

        public String getFilePath() {
            return filePath;
        }

        public void setFilePath(String filePath) {
            this.filePath = filePath;
        }
    }