package edu.ForceDrawnGraphs.util;

public class ProcessTimer implements Reportable {
  private long startTime;
  private long endTime;
  private String processName;

  public ProcessTimer() {
    this.startTime = System.currentTimeMillis();
  }

  public ProcessTimer(String processName) {
    this.startTime = System.currentTimeMillis();
    this.processName = processName;
  }

  public void stop() {
    this.endTime = System.currentTimeMillis();
    publishReport();
  }

  public void stop(String additionalNameText) {
    if (this.processName != null) {
      this.processName += (" " + additionalNameText);
    } else {
      this.processName = additionalNameText;
    }
    stop();
  }

  public void publishReport() {
    report(toString() + this.startTime + " " + this.endTime, "process.log");
  }

  public long getTotalTime() {
    return this.endTime - this.startTime;
  }

  public long getElapsedTime() {
    return System.currentTimeMillis() - this.startTime;
  }

  @Override
  public String toString() {
    return "Completed: " + this.processName + "in " + getTotalTime() + "ms.\n";
  }
}
