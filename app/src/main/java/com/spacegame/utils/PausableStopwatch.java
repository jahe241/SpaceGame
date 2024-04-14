package com.spacegame.utils;

/**
 * A stopwatch that can be paused and resumed. The stopwatch measures elapsed time in milliseconds.
 */
public class PausableStopwatch {
  private long startTime; // The time when the stopwatch was started or resumed
  private long pauseTime; // The time when the stopwatch was paused
  private boolean isRunning; // Whether the stopwatch is currently running

  /** Constructs a new PausableStopwatch. The stopwatch is initially stopped. */
  public PausableStopwatch() {
    this.startTime = 0;
    this.pauseTime = 0;
    this.isRunning = false;
  }

  /**
   * Starts or resumes the stopwatch. If the stopwatch is already running, this method does nothing.
   */
  public void start() {
    if (!isRunning) {
      this.startTime = System.currentTimeMillis() - pauseTime;
      this.isRunning = true;
    }
  }

  /** Pauses the stopwatch. If the stopwatch is already paused, this method does nothing. */
  public void pause() {
    if (isRunning) {
      this.pauseTime = System.currentTimeMillis() - startTime;
      this.isRunning = false;
    }
  }

  /** Resumes the stopwatch. If the stopwatch is already running, this method does nothing. */
  public void resume() {
    if (!isRunning) {
      this.startTime = System.currentTimeMillis() - pauseTime;
      this.isRunning = true;
    }
  }

  /**
   * Resets the stopwatch. After a call to this method, the stopwatch is stopped and the elapsed
   * time is zero.
   */
  public void reset() {
    this.startTime = 0;
    this.pauseTime = 0;
    this.isRunning = false;
  }

  /**
   * Returns the elapsed time formatted as a string. The format of the string is "MM:SS", where MM
   * is the number of minutes and SS is the number of seconds.
   *
   * @return the formatted elapsed time
   */
  public String getFormattedElapsedTime() {
    long elapsedTime = getElapsedTime();
    long seconds = elapsedTime / 1000;
    long minutes = seconds / 60;
    return String.format("%02d:%02d", minutes % 60, seconds % 60);
  }

  /**
   * Returns the elapsed time in milliseconds. If the stopwatch is running, the elapsed time is the
   * current time minus the start time. If the stopwatch is paused, the elapsed time is the pause
   * time.
   *
   * @return the elapsed time
   */
  public long getElapsedTime() {
    if (isRunning) {
      return System.currentTimeMillis() - startTime;
    } else {
      return pauseTime;
    }
  }
}
