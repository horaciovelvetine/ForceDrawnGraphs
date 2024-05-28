package edu.ForceDrawnGraphs.interfaces;

import java.util.ArrayList;
import java.util.List;

/**
 * The ProcessTimer class represents a timer for measuring the execution time of a process.
 * It implements the Reportable interface.
 */
public class ProcessTimer implements Reportable {
  private long startTime;
  private long endTime;
  private String processName;
  private List<Long> laps = new ArrayList<>();

  /**
   * Constructs a ProcessTimer object with the given process name.
   * The timer starts immediately upon construction.
   *
   * @param processName the name of the process
   */
  public ProcessTimer(String processName) {
    this.startTime = System.currentTimeMillis();
    this.processName = processName;
    // publishProcessTimerStart();
  }

  /**
   * Ends the timer and reports the process timer end.
   */
  public void end() {
    this.endTime = System.currentTimeMillis();
    reportProcessTimerEnd();
  }

  /**
   * Ends the timer with additional name text and reports the process timer end.
   *
   * @param additionalNameText additional text to append to the process name
   */
  public void end(String additionalNameText) {
    if (this.processName != null) {
      this.processName += (" " + additionalNameText);
    } else {
      this.processName = additionalNameText;
    }
    end();
  }

  /**
   * Publishes the start of the process timer to the console.
   */
  public void publishProcessTimerStart() {
    print(this.processName + " started.");
  }

  /**
   * Reports the end of the process timer to the console & process.log.
   */
  public void reportProcessTimerEnd() {
    report(toString() + this.startTime + ", " + this.endTime + ", " + laps);
  }

  /**
   * Helper returns the total time taken by the process timer.
   *
   * @return the total time in milliseconds
   */
  public long getTotalTime() {
    return this.endTime - this.startTime;
  }

  /**
   * Helper returns the elapsed time since the start of the process timer.
   *
   * @return the elapsed time in milliseconds
   */
  public long getElapsedTime() {
    return System.currentTimeMillis() - this.startTime;
  }

  /**
   * Records a 'lap time' in the process timer report, for marking significant events or recurring milestones.
   */
  public void lap() {
    this.laps.add(System.currentTimeMillis());
  }

  /**
   * Records a 'lap time' in the process timer report, for marking significant events or recurring milestones.
   *
   * @param lapStringData the string data to record with the process log
   */
  public void lapData(String lapStringData) {
    this.laps.add(System.currentTimeMillis());
    report(now() + this.processName + "lapData():" + lapStringData);
  }

  /**
   * Returns a string representation of the process timer.
   *
   * @return a string representation of the process timer
   */
  @Override
  public String toString() {
    return this.processName + " finished in " + getTotalTime() + "ms.\n";
  }
}
