package com.spacegame.utils;

public class PausableStopwatch {
  private long startTime;
  private long pauseTime;
  private boolean isRunning;

  public PausableStopwatch() {
    this.startTime = 0;
    this.pauseTime = 0;
    this.isRunning = false;
  }

  public void start() {
    if (!isRunning) {
      this.startTime = System.currentTimeMillis() - pauseTime;
      this.isRunning = true;
    }
  }

  public void pause() {
    if (isRunning) {
      this.pauseTime = System.currentTimeMillis() - startTime;
      this.isRunning = false;
    }
  }

  public void resume() {
    if (!isRunning) {
      this.startTime = System.currentTimeMillis() - pauseTime;
      this.isRunning = true;
    }
  }

  public void reset() {
    this.startTime = 0;
    this.pauseTime = 0;
    this.isRunning = false;
  }

  public String getFormattedElapsedTime() {
    long elapsedTime = getElapsedTime();
    long seconds = elapsedTime / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    //    return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    return String.format("%02d:%02d", minutes % 60, seconds % 60);
  }

  public long getElapsedTime() {
    if (isRunning) {
      return System.currentTimeMillis() - startTime;
    } else {
      return pauseTime;
    }
  }
}
