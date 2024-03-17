package edu.ForceDrawnGraphs.util;

import java.util.ArrayList;
import java.util.List;

public class ProcessTimer implements Reportable {
  private long startTime;
  private long endTime;
  private String processName;
  private List<Long> laps = new ArrayList<>();

  public ProcessTimer(String processName) {
    this.startTime = System.currentTimeMillis();
    this.processName = processName;
    publishProcessTimerStart();
  }

  public void end() {
    this.endTime = System.currentTimeMillis();
    reportProcessTimerEnd();
  }

  public void end(String additionalNameText) {
    if (this.processName != null) {
      this.processName += (" " + additionalNameText);
    } else {
      this.processName = additionalNameText;
    }
    end();
  }

  public void publishProcessTimerStart() {
    print(this.processName + " started.");
  }

  public void reportProcessTimerEnd() {
    report(toString() + this.startTime + ", " + this.endTime + ", " + laps, "process.log");
  }

  public long getTotalTime() {
    return this.endTime - this.startTime;
  }

  public long getElapsedTime() {
    return System.currentTimeMillis() - this.startTime;
  }

  public void lap() {
    this.laps.add(System.currentTimeMillis());
  }

  @Override
  public String toString() {
    return this.processName + " finished in " + getTotalTime() + "ms.\n";
  }
}
