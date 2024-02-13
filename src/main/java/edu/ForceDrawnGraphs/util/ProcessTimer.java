package edu.ForceDrawnGraphs.util;

public class ProcessTimer implements Reportable {
  private long startTime;
  private long endTime;
  private String processName;

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
